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
import org.teavm.tooling.RuntimeCopyOperation;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
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

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String cache;
	protected boolean withHtml;
	protected Class<?> classs;
	protected boolean debug;

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

		FigWheelyVertX.addVertX(this);

		if (FigWheelyVertX.started) {
			Vertx.currentContext().executeBlocking(future -> {
				try {
					translate();
				} catch (TeaVMToolException | IOException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
				future.complete();
			}, result -> {
			});
		} else {
			// When not in debug mode, we want to run this synchronous. It means
			// that the server will have a small delay when starting up, however
			// it does NOT startup when there are severe errors (which is a good
			// thing).
			translate();
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
				log.warning(problem.getClass() + ":" + problem.getText());
			}
			List<Problem> severes = problemProvider.getSevereProblems();
			if (!severes.isEmpty()) {
				// Collect errors and throw
				StringBuilder allSeveres = new StringBuilder("Severe build error(s) occurred: ");
				for (Problem severe : severes) {
					allSeveres.append("\n\tat ");
					allSeveres.append(severe.getLocation().getMethod());
					allSeveres.append("::");
					allSeveres.append(severe.getLocation().getSourceLocation().getLine());
					allSeveres.append(": ");
					allSeveres.append(severe.getText());
					allSeveres.append(": ");
					allSeveres.append(Arrays.toString(severe.getParams()));
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
