package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.tooling.RuntimeCopyOperation;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

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

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String cache;
	private boolean withHtml;
	protected Class<?> classs;
	protected boolean debug;

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
	 * 
	 */
	public VertxUI(Class<?> classs, boolean withHtml) {
		this.classs = classs;
		this.withHtml = withHtml;
		debug = false;

		try {
			// Below here is asynchronous generation of javascript, however we
			// want this to be synchronous for two reasons:
			// 1. we want to stop the startupprocess when there are severe
			// errors to prevent deploying erronymous vertx'es.
			// 2. when the server says 'started up' this should mean that
			// everything is good to go, so this should include translation
			// to the client code.
			// Vertx.currentContext().executeBlocking(future -> {
			// future.complete(getJavascript(classs, debug, withHtml));
			// }, result -> { cache = (String) result.result(); });
			translate();
		} catch (IOException | TeaVMToolException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			// Errors should prevent going live at runtime
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void handle(RoutingContext event) {
		event.response().end(cache);
	}

	public void translate() throws TeaVMToolException, IOException {
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
			ProblemProvider problemProvider = teaVmTool.getProblemProvider();
			for (Problem problem : problemProvider.getProblems()) {
				LOGGER.warning(problem.getClass() + ":" + problem.getText());
			}
			List<Problem> severes = problemProvider.getSevereProblems();
			if (!severes.isEmpty()) {
				// Collect errors and throw
				StringBuilder allSeveres = new StringBuilder("Severe build error(s) occurred: ");
				for (Problem severe : severes) {
					allSeveres.append("\n\t");
					allSeveres.append(severe.getClass());
					allSeveres.append(":");
					allSeveres.append(severe.getText());
				}
				String allSeveresString = allSeveres.toString();
				throw new TeaVMToolException(allSeveresString);
			}
			cache = FileUtils.readFileToString(temp, "UTF-8");
			if (withHtml) {
				// main in script so we can dynamicly load scripts
				cache = "<!DOCTYPE html><html><head><script>" + cache
						+ "</script></head><body><script>main()</script></body></html>";
			}
		} finally {
			if (temp.exists()) { // just in case
				temp.delete();
			}
		}
	}

}
