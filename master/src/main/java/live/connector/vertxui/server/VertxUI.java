package live.connector.vertxui.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private boolean withHtml;
	private Class<?> classs;
	private boolean debug;

	/**
	 * Create a VertxUI with html-file false and debug false
	 * 
	 * @param classs
	 * @throws IOException
	 */
	public VertxUI(Class<?> classs) throws IOException {
		this(classs, false, false);
	}

	/**
	 * Create a VertxUI with debug false.
	 * 
	 * @throws IOException
	 * 
	 */
	public VertxUI(Class<?> classs, boolean withHtml) throws IOException {
		this(classs, withHtml, false);
	}

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
	public VertxUI(Class<?> classs, boolean withHtml, boolean debug) throws IOException {
		this.classs = classs;
		this.withHtml = withHtml;
		this.debug = debug;

		if (FigWheelyVertX.started) {
			this.debug = true;
			this.withHtml = true;
			String classFile = FigWheelyVertX.buildDir + "/" + classs.getCanonicalName().replace(".", "/") + ".class";
			File file = new File(classFile);
			if (!file.exists()) {
				throw new IllegalArgumentException("please set FigWheelyVertX.buildDir, failed to load " + classFile);
			}
			FigWheelyVertX.addVertX(file, this);
		}

		if (this.withHtml) {
			FileUtils.writeStringToFile(new File("war/index.html"),
					"<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
							+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
							+ "border-top: 2px solid #3498db; width:30px; height:30px; -webkit-animation: spin 1.0s linear infinite;"
							+ "animation:spin 1.0s linear infinite; } "
							+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
							+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
							+ "</style></head><body><div class=loader></div></body></html>");
		}
		Vertx.currentContext().executeBlocking(future -> {
			try {
				translate();
				future.complete();
			} catch (IOException | InterruptedException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				if (!FigWheelyVertX.started) {
					// stop on startup errors when not in debug
					// stop on startup error
					Runtime.getRuntime().addShutdownHook(new Thread() {
						public void run() {
							Vertx vertx = Vertx.currentContext().owner();
							vertx.deploymentIDs().forEach(vertx::undeploy);
							vertx.close();
						}
					});
					System.exit(0);
				}
			}
		}, result -> {
		});
	}

	public static Handler<RoutingContext> with(Class<?> classs) throws IOException {
		new VertxUI(classs);
		return StaticHandler.create("war").setCachingEnabled(false);
	}

	public void sychronousReTranslate() throws IOException, InterruptedException {
		translate();
	}

	// public String translate() throws IOException {
	// File temp = null;
	// try {
	// temp = File.createTempFile("vertxui", "js");
	// TeaVMTool teaVmTool = new TeaVMTool();
	// teaVmTool.setMainClass(classs.getCanonicalName());
	// teaVmTool.setTargetDirectory(temp.getParentFile());
	// teaVmTool.setTargetFileName(temp.getName());
	// teaVmTool.setCacheDirectory(temp.getParentFile());
	// teaVmTool.setRuntime(RuntimeCopyOperation.MERGED);
	// teaVmTool.setMainPageIncluded(false);
	// teaVmTool.setBytecodeLogging(debug);
	// teaVmTool.setDebugInformationGenerated(debug);
	// teaVmTool.setMinifying(!debug);
	// teaVmTool.generate();
	//
	// // Warnings
	// ProblemProvider problemProvider = teaVmTool.getProblemProvider();
	// StringBuilder allWarnings = new StringBuilder();
	// List<Problem> severes = problemProvider.getSevereProblems();
	// problemProvider.getProblems().stream().filter(s ->
	// !severes.contains(s)).forEach(problem -> {
	// if (allWarnings.length() == 0) {
	// allWarnings.append("TeaVM warnings:");
	// }
	// getProblemString(allWarnings, problem);
	// });
	// if (allWarnings.length() != 0) {
	// log.warning(allWarnings.toString());
	// }
	//
	// // Errors
	// if (!severes.isEmpty()) {
	// StringBuilder allSeveres = new StringBuilder("Severe build error(s)
	// occurred: ");
	// severes.forEach(problem -> {
	// getProblemString(allSeveres, problem);
	// });
	// throw new TeaVMToolException(allSeveres.toString());
	// }
	// String result = FileUtils.readFileToString(temp, "UTF-8");
	// if (withHtml) {
	// // main in script so we can dynamicly load scripts
	// result = "<!DOCTYPE html><html><head><script>" + result
	// + "</script></head><body><script>main()</script></body></html>";
	// }
	// return result;
	// } finally {
	// if (temp.exists()) { // just in case
	// temp.delete();
	// }
	// }
	// }

	public void translate() throws IOException, InterruptedException {

		// List<File> list = new ArrayList<>();
		// list.add(new File("src"));
		// ModuleDef module = new ModuleDef(thisClass.getName());// ,
		// module.addResourcePath("src");
		// module.addEntryPointTypeName(thisClass.getName());
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

		String src = "src/main/java/"; // TODO do automatic search and set
		String className = classs.getName();
		String xmlFile = "live/connector/vertxui/client/Whatever"; // TODO
																	// dynamic
		File gwtXml = new File(src + xmlFile + ".gwt.xml");
		try {
			FileUtils.writeStringToFile(gwtXml,
					"<module rename-to='a'><inherits name='elemental.Elemental'/><entry-point class='" + className
							+ "'/><source path=''/></module>");
			String options = "-strict -XnoclassMetadata -XdisableUpdateCheck";
			if (debug) {
				options += " -draftCompile -optimize 0 -incremental";
			} else {
				options += " -nodraftCompile -optimize 9 -noincremental";
			}
			String classpath = System.getProperty("java.class.path") + ";" + src;
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
						System.out.println(line);
						if (line.contains("[ERROR]")) {
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
		if (withHtml) {
			FileUtils.writeStringToFile(new File("war/index.html"),
					"<!DOCTYPE html><html><body><script type='text/javascript' src='a/a.nocache.js?time="
							+ Math.random() + "'></script></body></html>");
		}
	}

}
