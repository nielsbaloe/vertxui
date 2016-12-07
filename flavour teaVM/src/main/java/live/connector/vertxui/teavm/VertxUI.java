package live.connector.vertxui.teavm;

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
 * Runtime java-to-javascript compilation inside vertX.
 * 
 * Very very very incomplete: TODO finish: add access to the eventbus (!!!),
 * show sharing of model-classes.
 * 
 * Docs TeaVM: http://teavm.org https://github.com/konsoletyper/teavm
 * 
 * Git: https://github.com/nielsbaloe/vertx-ui
 * 
 * @author ng
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
	 *            the given class
	 * @param debug
	 *            whether we output debug information, minimise output, and
	 *            recompile again on each browser request.
	 * @param withHtml
	 *            whether we want to the result to be html with the javascript
	 *            in there.
	 */
	public VertxUI(Class<?> classs, boolean debug, boolean withHtml) {
		this.classs = classs;
		this.debug = debug;
		this.withHtml = withHtml;
		Vertx.currentContext().executeBlocking(future -> {
			future.complete(getJavascript());
		}, b -> {
			cache = (String) b.result();
		});
	}

	@Override
	public void handle(RoutingContext event) {
		if (debug) {
			cache = getJavascript();
		}
		event.response().end(cache);
	}

	private String getJavascript() {
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
			teaVmTool.setBytecodeLogging(false);
			teaVmTool.setDebugInformationGenerated(debug);
			teaVmTool.setMinifying(!debug);
			teaVmTool.generate();
			ProblemProvider problemProvider = teaVmTool.getProblemProvider();
			List<Problem> severes = problemProvider.getSevereProblems();
			for (Problem problem : problemProvider.getProblems()) {
				LOGGER.warning("Warning: " + problem.getClass() + ":" + problem.getText());
			}
			for (Problem problem : problemProvider.getSevereProblems()) {
				LOGGER.severe(problem.getClass() + ":" + problem.getText());
			}
			if (!severes.isEmpty()) {
				throw new TeaVMToolException("Severe build error(s) occurred");
			}
			String result = FileUtils.readFileToString(temp, "UTF-8");

			if (withHtml) {
				// javascript is not in the <head> to prevent onload errors
				result = "<!DOCTYPE html><html><head/><body onload='main()'><script>" + result
						+ "</script></body></html>";
			}
			return result;
		} catch (IOException | TeaVMToolException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return "";
		} finally {
			if (temp.exists()) { // just in case
				temp.delete();
			}
		}
	}

}
