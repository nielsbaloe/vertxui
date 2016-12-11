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

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String cache;
	private Class<?> classs;
	private boolean debug;
	private boolean withHtml;

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
	 *            whether we want to the result to be wrapped inside <!DOCTYPE
	 *            html><html><head><script>...</script></head><body onload=
	 *            'main()'></body></html>
	 * @param jsLibraries
	 *            other .js files that can be included inside the HTML head.
	 *            Only makes sense when withHtml is set to true.
	 */
	public VertxUI(Class<?> classs, boolean debug, boolean withHtml) {
		this.classs = classs;
		this.debug = debug;
		this.withHtml = withHtml;
		Vertx.currentContext().executeBlocking(future -> {
			future.complete(getJavascript(classs, debug, withHtml));
		}, result -> {
			cache = (String) result.result();
		});
	}

	@Override
	public void handle(RoutingContext event) {
		if (debug) {
			cache = getJavascript(classs, debug, withHtml);
		}
		event.response().end(cache);
	}

	public static String getJavascript(Class<?> classs, boolean debug, boolean withHtml) {
		File temp = null;
		try {
			temp = File.createTempFile("prefix", "suffix");

			// Documentation:
			// https://github.com/konsoletyper/teavm/wiki/Building-JavaScript-with-Maven-and-TeaVM
			TeaVMTool teaVmTool = new TeaVMTool();
			teaVmTool.setMainClass(classs.getCanonicalName());
			teaVmTool.setTargetDirectory(temp.getParentFile());
			teaVmTool.setTargetFileName(temp.getName());
			teaVmTool.setCacheDirectory(temp.getParentFile());
			teaVmTool.setRuntime(RuntimeCopyOperation.MERGED);
			teaVmTool.setMainPageIncluded(false);
			teaVmTool.setBytecodeLogging(debug);
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
			String result = FileUtils.readFileToString(temp, "UTF-8");

			if (withHtml) {
				StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
				try {
					for (String library : (String[]) classs.getDeclaredField("libraries").get(null)) {
						html.append("<script type='text/javascript' src='" + library + "'></script>");
					}

				} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException
						| SecurityException ne) {
					// consciously ignore, is OK if it does not exist
				}
				try {
					for (String stylesheet : (String[]) classs.getDeclaredField("stylesheets").get(null)) {
						html.append("<link rel='stylesheet' href='" + stylesheet + "'>");
					}
				} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException
						| SecurityException ne) {
					// consciously ignore, is OK if it does not exist
				}
				html.append("<script>");
				html.append(result);
				html.append("</script></head><body onload='main()'></body></html>");
				result = html.toString();
			}
			return result;
		} catch (IOException |

				TeaVMToolException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			// Errors should prevent going live at runtime
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (temp.exists()) { // just in case
				temp.delete();
			}
		}
	}

}
