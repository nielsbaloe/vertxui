package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
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
 * Runtime java-to-javascript compilation inside vertX, using TeaVM.
 * 
 * Very incomplete: TODO finish: add access to the eventbus (!!!), show sharing
 * of model-classes. TODO optimise: make a vert.x-workerthread of the
 * translation.
 *
 * Docs TeaVM: http://teavm.org https://github.com/konsoletyper/teavm
 * 
 * Git: https://github.com/nielsbaloe/vertx-ui
 * 
 * @author ng
 *
 */
public class VertxUIT implements Handler<RoutingContext> {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String result;

	@Override
	public void handle(RoutingContext event) {
		event.response().end(result);
	}

	public VertxUIT(Class<?> classs, boolean optimise) throws IOException, TeaVMToolException {
		File temp = File.createTempFile("prefix", "suffix");
		try {
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
			teaVmTool.setDebugInformationGenerated(!optimise);
			teaVmTool.setMinifying(optimise);
			teaVmTool.generate();
			ProblemProvider problemProvider = teaVmTool.getProblemProvider();
			List<Problem> severes = problemProvider.getSevereProblems();
			for (Problem problem : problemProvider.getProblems()) {
				LOGGER.warning("Warning: " + problem.getClass() + ":" + problem.getText());
				// + "- " + problem.getLocation());
			}
			for (Problem problem : problemProvider.getSevereProblems()) {
				LOGGER.severe(problem.getClass() + ":" + problem.getText());
				// + "- " + problem.getLocation());
			}
			if (!severes.isEmpty()) {
				throw new TeaVMToolException("Severe build error(s) occurred");
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<!DOCTYPE html><html><head></head><body onload='main()'><script>");
			stringBuilder.append(FileUtils.readFileToString(temp, "UTF-8"));
			stringBuilder.append("</script></body></html>");
			result = stringBuilder.toString();
		} finally {
			if (temp.exists()) { // just in case
				temp.delete();
			}
		}
	}

}
