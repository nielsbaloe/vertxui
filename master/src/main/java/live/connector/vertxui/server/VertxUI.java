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
import io.vertx.core.buffer.Buffer;
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

	/**
	 * Set the location of your source files if not /src, /src/main or
	 * /src/main/java .
	 */
	public static String sourceLocation = null;

	static {
		librariesGwt = new ArrayList<>();
		addLibrariesGwt("elemental.Elemental");
		addLibrariesGwt("com.github.nmorel.gwtjackson.GwtJackson");
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
	 * If figwheely is started, debugging is always set to true.
	 * 
	 */
	private VertxUI(Class<?> classs, boolean debug, String url) {
		this.classs = classs;
		this.debug = debug;

		if (FigWheely.started) {
			this.debug = true;
			String classFile = FigWheely.buildDir + "/" + classs.getCanonicalName().replace(".", "/") + ".class";
			File file = new File(classFile);
			if (!file.exists()) {
				throw new IllegalArgumentException("please set FigWheely.buildDir, failed to load " + classFile);
			}
			// TODO: GWT: find out where the real .js is and reload that one
			FigWheely.addVertX(file, url + "a/a.nocache.js", this);
		}

		// do a first translation
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
	}

	public static Handler<RoutingContext> with(Class<?> classs, boolean debug, String url) {

		// If no sourceLocation, then we are in production so we don't do
		// anything at all.
		Stream.of("src", "src/main", "src/main/java", sourceLocation).forEach(location -> {
			if (location != null && new File(location).exists()) {
				sourceLocation = location;
			}
		});
		if (sourceLocation != null) {
			new VertxUI(classs, debug, url);
		}

		return StaticHandler.create("war").setCachingEnabled(false);
	}

	public void sychronousReTranslate() throws IOException, InterruptedException {
		translate();
	}

	public void translate() throws IOException, InterruptedException {
		long start = System.currentTimeMillis();

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
		String path = "live/connector/vertxui/client";
		// TODO dynamic (examples in other project)
		File gwtXml = new File(sourceLocation + "/" + xmlFile + ".gwt.xml");
		StringBuilder content = new StringBuilder("<module rename-to='a'>");
		librariesGwt.forEach(l -> content.append("<inherits name='" + l + "'/>"));
		content.append("<entry-point class='" + className + "'/><source path='" + path + "'/></module>");
		FileUtils.writeStringToFile(gwtXml, content.toString());

		// Compile to javacript
		try {
			String options = "-strict -XnoclassMetadata -XdisableUpdateCheck";
			if (debug) {
				options += " -draftCompile -optimize 0 -incremental";
			} else {
				options += " -nodraftCompile -optimize 9 -noincremental";
			}
			String classpath = System.getProperty("java.class.path") + ";" + sourceLocation;
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
							System.out.println(line);
							error = true;
						}
					}
					while ((line = bre.readLine()) != null) {
						info.append(line + "\n");
						System.err.println(line);
						error = true;
					}
				}
			}
			if (error) {
				throw new IOException("Compile error(s): " + info);
			}
			System.out.println("Compiling done in " + (System.currentTimeMillis() - start) + " ms.");
		} finally {
			gwtXml.delete();
		}

		// Write the final index.html file
		StringBuilder html = new StringBuilder("<!DOCTYPE html><head>");
		html.append("</head><body><script src='a/a.nocache.js?time=" + Math.random() + "'></script></body></html>");
		Vertx.currentContext().owner().fileSystem().writeFile("war/index.html", Buffer.buffer(html.toString()), a -> {
			if (a.failed()) {
				throw new IllegalArgumentException("Could not create html file", a.cause());
			}
		});

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
