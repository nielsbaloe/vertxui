package live.connector.vertxui.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

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
	private boolean debug = false;

	/**
	 * Set the location of your source files if not /src, /src/main or
	 * /src/main/java .
	 */
	public static String folderSourceMain = null;

	static {
		librariesGwt = new ArrayList<>();
		addLibrariesGwt("live.connector.vertxui.Vertxui");
	}

	/**
	 * Add gwt libraries if you need to do so.
	 */
	public static void addLibrariesGwt(String... gwts) {
		for (String gwt : gwts) {
			librariesGwt.add(gwt);
		}
	}

	private static List<String> librariesGwt;

	/**
	 * Serve the /war folder. Also, if a source folder is found, convert the
	 * class to html+javascript .
	 * 
	 * If figwheely is started or if the URL is null, debugging is set to true.
	 * 
	 */
	private VertxUI(Class<?> classs, String url) {
		this.classs = classs;
		if (FigWheely.started || url == null) {
			debug = true;
		}

		if (FigWheely.started) {
			String classFile = FigWheely.buildDir + "/" + classs.getCanonicalName().replace(".", "/") + ".class";
			File file = new File(classFile);
			if (!file.exists()) {
				throw new IllegalArgumentException("please set FigWheely.buildDir, failed to load " + classFile);
			}
			FigWheely.addVertX(file, url + "a/a.nocache.js", this); // useless,
																	// needs
																	// refactoring
		}

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
	 * Create a VertXUI statichandler at /war and compile the given class from
	 * java to javascript. Give url:null for compiling only.
	 * 
	 */
	public static Handler<RoutingContext> with(Class<?> classs, String url) {

		// If no sourceLocation, then we are in production so we don't do
		// anything at all.
		String sourceFilePath = classs.getName().replace(".", "/") + ".java";
		Stream.of("src", "src/main", "src/main/java", "src/test", "src/test/java", folderSourceMain)
				.forEach(location -> {
					if (location != null && new File(location + "/" + sourceFilePath).exists()) {
						folderSourceMain = location;
					}
				});
		if (folderSourceMain == null) {
			throw new IllegalArgumentException("Please define your sourcefolder location at Vertxui.folderSourceMain");
		}
		new VertxUI(classs, url);
		if (url != null) {
			return StaticHandler.create("war").setCachingEnabled(false);
		} else {
			return null;
		}
	}

	public void sychronousReTranslate() throws IOException, InterruptedException {
		translate();
	}

	/**
	 * Debug is false by default; enable Figwheely to set debug to true.
	 */
	public void translate() throws IOException, InterruptedException {
		// Write index.html file which autoreloads
		FileUtils.writeStringToFile(new File("war/index.html"),
				"<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
						+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
						+ "border-top: 2px solid #3498db; width:30px; height:30px; -webkit-animation: spin 1.0s linear infinite;"
						+ "animation:spin 1.0s linear infinite; } "
						+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
						+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
						+ "</style></head><body><div class=loader></div></body></html>");

		// Write the .gml.xml file
		String className = classs.getName();
		String xmlFile = "gwtTemp";

		// as path: the "client" package in the name of the classpath of the
		// given classname
		String path = className.replace(".", "/");
		path = path.substring(0, path.lastIndexOf("client") + 6);
		File gwtXml = new File(folderSourceMain + "/" + xmlFile + ".gwt.xml");
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

		// Compile to javacript
		try {
			String options = "-strict -XdisableUpdateCheck";
			if (debug) {
				options += " -draftCompile -optimize 0 -style DETAILED"; // -incremental
			} else {
				options += " -XnoclassMetadata -nodraftCompile -optimize 9 -noincremental";
			}
			String classpath = System.getProperty("java.class.path") + ";" + folderSourceMain;

			String separator = ";"; // windows
			if (!classpath.contains(separator)) {
				separator = ":"; // unix
			}
			classpath = Stream.of(classpath.split(separator)).map(c -> "\"" + c + "\";").reduce("", String::concat);

			String line = null;
			Process p = Runtime.getRuntime()
					.exec("java -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + xmlFile);
			StringBuilder info = new StringBuilder();
			boolean error = false;
			try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
				while (p.isAlive()) {
					while ((line = bri.readLine()) != null) {
						info.append(line + "\n");
						if (line.contains("[ERROR]")) {
							System.err.print(".");
							error = true;
						} else {
							System.out.print(".");
						}
					}
					while ((line = bre.readLine()) != null) {
						info.append(line + "\n");
						System.err.print(".");
						error = true;
					}
				}
			}
			if (error) {
				throw new IOException("Compile error(s): " + info);
			}
			System.out.println("*");
			// log.info("compiled in " + (System.currentTimeMillis() - start) +
			// " ms.");
		} finally {
			gwtXml.delete();
		}

		// Write the final index.html file
		StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
		try {
			for (String script : (String[]) classs.getDeclaredField("scripts").get(null)) {
				html.append("<script src='");
				html.append(script);
				html.append("'></script>");
			}
		} catch (NoSuchFieldException e) {
			// is OK, does not exist
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not access public static String scripts[]", e);
		}
		html.append("</head><body><script src='a/a.nocache.js?time=" + Math.random() + "'></script></body></html>");

		// Not using vertx filesystem() because there is no speed gain and is
		// called by tests outside vertx
		FileUtils.writeStringToFile(new File("war/index.html"), html.toString());
		// Vertx.currentContext().owner().fileSystem().writeFile(,
		// Buffer.buffer(), a -> {
		// if (a.failed()) {
		// throw new IllegalArgumentException("Could not create html file",
		// a.cause()); } });

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

}
