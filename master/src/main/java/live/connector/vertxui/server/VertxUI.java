package live.connector.vertxui.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
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

	private boolean withHtml;
	private Class<?> classs;
	private boolean debug;

	/**
	 * Adjust the location of your source files if they are not in /src,
	 * /src/main or /src/main/java .
	 */
	public String sourceLocation = null;

	static {
		librariesGwt = new ArrayList<>();
		addLibrariesGwt("elemental.Elemental");
		addLibrariesGwt("com.github.nmorel.gwtjackson.GwtJackson");
	}

	public static void addLibrariesGwt(String... gwts) {
		for (String gwt : gwts) {
			librariesGwt.add(gwt);
		}
	}

	private static List<String> librariesGwt;

	/**
	 * Convert the class to html/javascript at runtime. With debugging, you can
	 * make changes in your IDE, save, and refresh your browser which will take
	 * the latest .class files that your IDE has just compiled.
	 * 
	 * @param classs
	 *            the given class. make a public static String[] libraries or a
	 *            public static String[] stylesheets with javascript and
	 *            stylesheets you want to include in the generated html file.
	 * @param debug
	 *            whether we output debug information, minimise output, and
	 *            recompile again on each browser request.
	 * @param withHtml
	 *            whether we want to the result to be wrapped inside
	 * 
	 *            <!DOCTYPE html><html><head> <script>...</script> </head>
	 *            <body> <script>main()</script> </body></html>
	 * @param clientJSUrl
	 * @throws IOException
	 * 
	 */
	public VertxUI(Class<?> classs, boolean withHtml, boolean debug, String url) {
		this.classs = classs;
		this.withHtml = withHtml;
		this.debug = debug;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Context context = Vertx.currentContext();
				if (context == null) {
					return;
				}
				Vertx vertx = context.owner();
				vertx.deploymentIDs().forEach(vertx::undeploy);
				vertx.close();
			}
		});

		if (FigWheely.started) {
			this.debug = true;
			this.withHtml = true;
			String classFile = FigWheely.buildDir + "/" + classs.getCanonicalName().replace(".", "/") + ".class";
			File file = new File(classFile);
			if (!file.exists()) {
				throw new IllegalArgumentException("please set FigWheely.buildDir, failed to load " + classFile);
			}
			// TODO: GWT: find out where the real .js is and reload that one
			FigWheely.addVertX(file, url + "a/a.nocache.js", this);
		}

		if (this.withHtml) {
			try {
				FileUtils.writeStringToFile(new File("war/index.html"),
						"<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
								+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
								+ "border-top: 2px solid #3498db; width:30px; height:30px; -webkit-animation: spin 1.0s linear infinite;"
								+ "animation:spin 1.0s linear infinite; } "
								+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
								+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
								+ "</style></head><body><div class=loader></div></body></html>");
			} catch (IOException ie) {
				throw new IllegalArgumentException("Could not write war/index.html: ", ie);
			}
		}

		Vertx.currentContext().executeBlocking(future -> {
			try {
				translate();
				future.complete();
			} catch (IOException | InterruptedException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				if (!FigWheely.started) {
					System.exit(0); // stop on startup errors when not in debug
				}
			}
		}, result -> {
		});
	}

	public static Handler<RoutingContext> with(Class<?> classs, boolean withHtml, boolean debug, String url) {
		new VertxUI(classs, withHtml, debug, url);
		return StaticHandler.create("war").setCachingEnabled(false);
	}

	public void sychronousReTranslate() throws IOException, InterruptedException {
		translate();
	}

	public void translate() throws IOException, InterruptedException {
		long start = System.currentTimeMillis();

		// Figure out source location
		Stream.of("src", "src/main", "src/main/java", sourceLocation).forEach(location -> {
			if (location != null && new File(location).exists()) {
				sourceLocation = location;
			}
		});
		// log.info("sourceLocation=" + sourceLocation);

		String className = classs.getName();
		String xmlFile = "gwtTemp";
		String path = "live/connector/vertxui/client"; // TODO dynamic (examples
														// outside)
		File gwtXml = new File(sourceLocation + "/" + xmlFile + ".gwt.xml");
		try {
			// Generate temporary gwt file
			StringBuilder content = new StringBuilder("<module rename-to='a'>");
			librariesGwt.forEach(l -> content.append("<inherits name='" + l + "'/>"));
			content.append("<entry-point class='" + className + "'/><source path='" + path + "'/></module>");
			FileUtils.writeStringToFile(gwtXml, content.toString());

			// Compile to javacript
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
		} finally {
			gwtXml.delete();
		}

		System.out.println("Compiling done in " + (System.currentTimeMillis() - start) + " ms.");
		if (withHtml) {
			StringBuilder content = new StringBuilder("<!DOCTYPE html><head>");
			content.append(
					"</head><body><script src='a/a.nocache.js?time=" + Math.random() + "'></script></body></html>");
			Vertx.currentContext().owner().fileSystem().writeFile("war/index.html", Buffer.buffer(content.toString()),
					a -> {
						if (a.failed()) {
							throw new IllegalArgumentException("Could not create html file", a.cause());
						}
					});

		}

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

	public static <A, B> void bind(String service, Class<A> inputType, Function<A, B> handler) {
		Vertx.currentContext().owner().eventBus().consumer(service, in -> {
			A input = (A) Json.decodeValue((String) in.body(), inputType);
			B output = handler.apply(input);
			in.reply(Json.encode(output));
		});
	}
}
