package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.tooling.RuntimeCopyOperation;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.figwheely.FigWheelyVertX;

/**
 * Runtime java-to-javascript compilation inside vertX, see
 * https://github.com/nielsbaloe/vertx-ui .
 * 
 * Docs TeaVM: http://teavm.org https://github.com/konsoletyper/teavm
 * 
 * @author Niels Gorisse
 *
 */
public class VertxUI implements Handler<RoutingContext> {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String cache;
	private boolean withHtml;
	private Class<?> classs;
	private boolean debug;

	/**
	 * Create a VertxUI with debug false.
	 * 
	 * @throws IOException
	 * @throws TeaVMToolException
	 * 
	 */
	public VertxUI(Class<?> classs, boolean withHtml) throws TeaVMToolException, IOException {
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
	 * @throws TeaVMToolException
	 * 
	 */
	public VertxUI(Class<?> classs, boolean withHtml, boolean debug) throws TeaVMToolException, IOException {
		this.classs = classs;
		this.withHtml = withHtml;
		this.debug = debug;

		if (FigWheelyVertX.started) {
			debug = true;

			String classFile = FigWheelyVertX.buildDir + "/" + classs.getCanonicalName().replace(".", "/")
					+ ".class";
			File file = new File(classFile);
			if (!file.exists()) {
				throw new IllegalArgumentException("please set FigWheelyVertX.buildDir, failed to load " + classFile);
			}
			FigWheelyVertX.addVertX(file,this);
		}
		Vertx.currentContext().executeBlocking(future -> {
			try {
				future.complete(translate());
			} catch (TeaVMToolException | IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				if (!FigWheelyVertX.started) {
					System.exit(0); // stop on startup errors
				}
			}
		}, result -> {
			cache = (String) result.result();
		});
	}

	public void sychronousReTranslate() throws TeaVMToolException, IOException {
		cache = translate();
	}

	@Override
	public void handle(RoutingContext event) {
		String reply = cache;
		if (reply == null) {
			reply = "<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
					+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
					+ "border-top: 2px solid #3498db; width:33px; height:33px; -webkit-animation: spin 0.8s linear infinite;"
					+ "animation:spin 0.8s linear infinite; } "
					+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
					+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
					+ "</style></head><body><div class=loader></div></body></html>";
		}
		event.response().end(reply);
	}

	public String translate() throws TeaVMToolException, IOException {
		File temp = null;
		try {
			temp = File.createTempFile("vertxui", "js");

			// Documentation:
			// https://github.com/konsoletyper/teavm/wiki/Building-JavaScript-with-Maven-and-TeaVM
			TeaVMTool teaVmTool = new TeaVMTool();
			teaVmTool.setMainClass(classs.getCanonicalName());
			teaVmTool.setTargetDirectory(temp.getParentFile());
			teaVmTool.setTargetFileName(temp.getName());
			teaVmTool.setCacheDirectory(temp.getParentFile());
			teaVmTool.setRuntime(RuntimeCopyOperation.MERGED);
			teaVmTool.setMainPageIncluded(false);
			teaVmTool.setBytecodeLogging(false);
			teaVmTool.setDebugInformationGenerated(debug);
			teaVmTool.setMinifying(!debug);
			teaVmTool.generate();

			// Warnings
			ProblemProvider problemProvider = teaVmTool.getProblemProvider();
			StringBuilder allWarnings = new StringBuilder();
			List<Problem> severes = problemProvider.getSevereProblems();
			problemProvider.getProblems().stream().filter(s -> !severes.contains(s)).forEach(problem -> {
				if (allWarnings.length() == 0) {
					allWarnings.append("TeaVM warnings:");
				}
				getProblemString(allWarnings, problem);
			});
			if (allWarnings.length() != 0) {
				log.warning(allWarnings.toString());
			}

			// Errors
			if (!severes.isEmpty()) {
				StringBuilder allSeveres = new StringBuilder("Severe build error(s) occurred: ");
				severes.forEach(problem -> {
					getProblemString(allSeveres, problem);
				});
				throw new TeaVMToolException(allSeveres.toString());
			}
			String result = FileUtils.readFileToString(temp, "UTF-8");
			if (withHtml) {
				// main in script so we can dynamicly load scripts
				result = "<!DOCTYPE html><html><head><script>" + result
						+ "</script></head><body><script>main()</script></body></html>";
			}
			return result;
		} finally {
			if (temp.exists()) { // just in case
				temp.delete();
			}
		}
	}

	private void getProblemString(StringBuilder allSeveres, Problem problem) {
		allSeveres.append("\n\tat ");
		CallLocation where = problem.getLocation();
		allSeveres.append(where.getMethod());
		allSeveres.append("::");
		if (where.getSourceLocation() != null) {
			allSeveres.append(where.getSourceLocation().getLine());
		}
		allSeveres.append(": ");
		allSeveres.append(problem.getText());
		allSeveres.append(": ");
		allSeveres.append(Arrays.toString(problem.getParams()));
	}

}
