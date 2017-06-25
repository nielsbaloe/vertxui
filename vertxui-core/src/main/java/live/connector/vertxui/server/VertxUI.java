package live.connector.vertxui.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Runtime java-to-javascript compilation inside vertX, see
 * https://github.com/nielsbaloe/vertx-ui .
 * 
 * @author Niels Gorisse
 *
 */
public class VertxUI {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private Class<?> classs;
	private boolean debug;
	private boolean withHtml;

	/**
	 * Set the location of your source files if not /src, /src/main or
	 * /src/main/java .
	 */
	public static String folderSource = null;

	/**
	 * Set the charset of your application, if not utf-8; it will be put into
	 * the generated index.html.
	 */
	public static String charset = "utf-8";

	private static String folderBuild = null;

	/**
	 * Add GWT libraries to the path if you need to do so.
	 */
	public static List<String> librariesGwt;

	static {
		librariesGwt = new ArrayList<>();
		librariesGwt.add("live.connector.vertxui.Vertxui");
	}

	/**
	 * Get the target folder of the build. If overwritten, you get that one,
	 * otherwise you get one of the defaults: build/development or
	 * build/production depending on debug or not.
	 * 
	 * @param debugMode
	 *            whether we are in debug mode or not.
	 * @return the target folder of the java to javascript build
	 */
	public static String getTargetFolder(boolean debugMode) {
		if (folderBuild != null) {
			return folderBuild;
		}
		if (debugMode) {
			return "build/development";
		} else {
			return "build/production";
		}
	}

	/**
	 * Set the location of your target build folder, in case you do not want it
	 * in build/development (when debug=true) or build/production (when
	 * debug=false).
	 * 
	 * @param targetFolder
	 *            the target folder
	 */
	public static void setTargetFolder(String targetFolder) {
		VertxUI.folderBuild = targetFolder;
	}

	private VertxUI(Class<?> classs, boolean debug, boolean withHtml) {
		this.classs = classs;
		this.debug = debug;
		this.withHtml = withHtml;

		// do a first translation
		if (Vertx.currentContext() != null) {
			Vertx.currentContext().executeBlocking(future -> {
				try {
					translate();
					future.complete();
				} catch (IOException | InterruptedException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
					System.exit(0); // stop on startup errors
				}
			}, result -> {
			});
		} else { // only translating
			try {
				translate();
			} catch (IOException | InterruptedException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				System.exit(0); // stop on startup errors
			}
		}
	}

	/**
	 * Create a VertXUI static-handler at the target folder and translate the
	 * given class from java to javascript. Give url:null for only translating.
	 * 
	 * @param classs
	 *            the class that will be compiled to javascript
	 * @param urlWithoutAsterix
	 *            the url but without asterix for the static file handler; set
	 *            to null if you only want compiling.
	 * @param debug
	 *            debug or not
	 * @param withHtml
	 *            with a generated .html file or not.
	 * @return the static file handler.
	 */
	public static Handler<RoutingContext> with(Class<?> classs, String urlWithoutAsterix, boolean debug,
			boolean withHtml) {

		// If no sourceLocation, then we are in production so we don't do
		// anything at all.
		String sourceFilePath = classs.getName().replace(".", "/") + ".java";
		Stream.of("src", "src/main", "src/main/java", "src/test", "src/test/java", folderSource).forEach(location -> {
			if (location != null && new File(location + "/" + sourceFilePath).exists()) {
				folderSource = location;
			}
		});
		if (folderSource == null) {
			if (debug) {
				throw new IllegalArgumentException(
						"Sourcefolder not found but debug is still true, did you compile with debug=false?");
			}
			if (urlWithoutAsterix == null) {
				throw new IllegalArgumentException(
						"Sourcefolder not found, but urlWithoutAsterix is null, so unable to server files.");
			}
			log.info("Production mode: no source folder found, not translating from java to javascript.");
		} else {
			VertxUI translated = new VertxUI(classs, debug, withHtml);

			if (FigWheelyServer.started) {
				String clientFolder = (folderSource + "/" + classs.getName()).replace(".", "/");
				clientFolder = clientFolder.substring(0, clientFolder.lastIndexOf("client") + 6);
				FigWheelyServer.addFromVertX(Vertx.currentContext().owner().fileSystem(),
						urlWithoutAsterix + "a/a.nocache.js", clientFolder, translated, clientFolder);
			}
		}
		if (urlWithoutAsterix != null) {
			return StaticHandler.create(VertxUI.getTargetFolder(debug)).setCachingEnabled(false);
		} else {
			return null;
		}
	}

	protected void translate() throws IOException, InterruptedException {

		// Write index.html file which autoreloads
		if (withHtml) {
			FileUtils.writeStringToFile(new File(VertxUI.getTargetFolder(debug) + "/index.html"),
					"<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
							+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
							+ "border-top: 2px solid #3498db; width:30px; height:30px; -webkit-animation: spin 1.0s linear infinite;"
							+ "animation:spin 1.0s linear infinite; } "
							+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
							+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
							+ "</style></head><body><div class=loader></div></body></html>");
		}

		// Write the .gml.xml file
		String className = classs.getName();
		String xmlFile = "gwtTemp";
		// as path: the "client" package in the name of the classpath of the
		// given classname
		String path = className.replace(".", "/");
		path = path.substring(0, path.lastIndexOf("client") + 6);
		File gwtXml = new File(folderSource + "/" + xmlFile + ".gwt.xml");
		StringBuilder content = new StringBuilder("<module rename-to='a'>");
		librariesGwt.forEach(l -> content.append("<inherits name='" + l + "'/>"));
		content.append("<entry-point class='" + className + "'/><source path='" + path + "'/>");
		if (debug) {
			content.append("<set-property name='compiler.stackMode' value='emulated' />");
			content.append(
					"<set-configuration-property name='compiler.emulatedStack.recordLineNumbers' value='true' />");
			content.append("<set-configuration-property name='compiler.emulatedStack.recordFileNames' value='true'/>");
		}
		content.append("</module>");
		FileUtils.writeStringToFile(gwtXml, content.toString());

		// Compile to javascript
		String options = "-strict -XdisableUpdateCheck -war " + getTargetFolder(debug);
		if (debug) {
			options += " -draftCompile -optimize 0 -style DETAILED"; // -incremental
		} else {
			options += " -XnoclassMetadata -nodraftCompile -optimize 9 -noincremental";
		}

		// Extract and extend the classpath
		String classpath = System.getProperty("java.class.path");
		String separator = (System.getenv("path.separator") == null) ? (classpath.contains(";") ? ";" : ":")
				: System.getenv("path.separator");
		classpath = "\"" + classpath + separator + folderSource + "\"";

		// Run GWT
		Process process = Runtime.getRuntime()
				.exec("java -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + xmlFile);
		StringBuilder info = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader erput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		Context context = Vertx.currentContext();
		if (context == null) { // for TestDOM
			translateContinue(gwtXml, process, info, input, erput);
		} else {
			Vertx.currentContext().owner().setTimer(100, __ -> translateContinue(gwtXml, process, info, input, erput));
		}

		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// List<File> list = new ArrayList<>();
		// list.add(new File("src"));
		// ModuleDef module = new ModuleDef(classs.getName());// ,
		// module.addResourcePath("src");
		// module.addEntryPointTypeName(classs.getName());
		// // module.addisInherited("elemental.Elemental");
		// // module.addPublicPackage("elemental.Elemental", new String[0], new
		// // String[0], new String[0], false, false);
		//
		// // Directly (works, but classpath does not contain 'src'
		// Memory.initialize();
		// SpeedTracerLogger.init();
		// CompilerOptions options = new CompilerOptionsImpl();
		// options.addModuleName(thisClass.getName());
		// options.setStrict(true);
		// options.setClassMetadataDisabled(true);
		// options.setDisableUpdateCheck(true);
		// if (debug) {
		// options.setIncrementalCompileEnabled(true);
		// } else {
		// options.setOptimizationLevel(CompilerOptions.OPTIMIZE_LEVEL_MAX);
		// options.setIncrementalCompileEnabled(false);
		// }
		// com.google.gwt.dev.Compiler.compile(new PrintWriterTreeLogger(),
		// options, module);
		// System.exit(0);
	}

	private void translateContinue(File gwtXml, Process process, StringBuilder info, BufferedReader input,
			BufferedReader erput) {
		// Read input
		try {
			if (input.ready()) {
				String line = input.readLine();
				info.append(line + "\n");
				if (line.contains("[ERROR]")) {
					System.err.print(".");
				} else {
					System.out.print(".");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Read error
		try {
			if (erput.ready()) {
				String line = erput.readLine();
				info.append("[ERROR]" + line + "\n");
				System.err.print(".");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Break
		if (!process.isAlive()) {
			gwtXml.delete();
			writeHtml();
			String result = info.toString();
			if (result.contains("[ERROR]")) {
				System.err.println("Compile error(s): " + info);
			} else {
				System.out.println("*");
			}
			try {
				input.close();
			} catch (IOException ___) {
			}
			try {
				erput.close();
			} catch (IOException ___) {
			}
		} else { // continue

			Context context = Vertx.currentContext();
			if (context == null) { // for TestDOM
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				translateContinue(gwtXml, process, info, input, erput);
			} else {
				Vertx.currentContext().owner().setTimer(100,
						__ -> translateContinue(gwtXml, process, info, input, erput));
			}

		}
	}

	private void writeHtml() {
		if (!withHtml) {
			return;
		}
		StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head><meta charset=\"" + charset + "\">");
		try {
			for (String script : (String[]) classs.getDeclaredField("scripts").get(null)) {
				html.append("<script src='");
				html.append(script);
				html.append("'></script>");
			}
		} catch (NoSuchFieldException e) {
			// is OK, does not exist
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw new IllegalArgumentException("Could not access public static String scripts[]", e);
		}
		try {
			for (String css : (String[]) classs.getDeclaredField("css").get(null)) {
				html.append("<link rel=stylesheet href='");
				html.append(css);
				html.append("'/>");
			}
		} catch (NoSuchFieldException e) {
			// is OK, does not exist
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw new IllegalArgumentException("Could not access public static String css[]", e);
		}
		html.append("</head><body><script src='a/a.nocache.js?time=" + Math.random() + "'></script></body></html>");

		// Write to file (not using vertx because this is also done with
		// non-vertx inside TestDOM)
		try (FileWriter fileWriter = new FileWriter(VertxUI.getTargetFolder(debug) + "/index.html")) {
			fileWriter.write(html.toString());
		} catch (IOException ie) {
			throw new IllegalArgumentException("Could not write index.html file", ie);
		}
	}

}
