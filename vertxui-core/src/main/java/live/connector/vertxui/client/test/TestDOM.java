package live.connector.vertxui.client.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GwtIncompatible;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

import live.connector.vertxui.server.VertxUI;

/**
 * A junit testrunner which starts as a normal java junit test, but underneath
 * converts the class to a javascript page, fires up a headless browser.
 * Register with registerJS() and run your tests with runJS().
 * 
 * @author Niels Gorisse
 *
 */
public abstract class TestDOM implements EntryPoint {

	@GwtIncompatible
	private JBrowserDriver jBrowser;

	@GwtIncompatible
	@Before
	public void before() {
		// for correct stacktrace decoding, we need the debug info.
		boolean debug = true;

		// Convert to javascript
		VertxUI.with(this.getClass(), null, debug, true);

		// Start the headless browser
		jBrowser = new JBrowserDriver(Settings.builder().logJavascript(true).build());
		jBrowser.get("file:///" + new File(VertxUI.getTargetFolder(debug) + "/index.html").getAbsolutePath());
	}

	@GwtIncompatible
	@After
	public void after() {
		if (jBrowser != null) {
			jBrowser.quit();
		}
	}

	public abstract Map<Integer, Runnable> registerJS();

	@Override
	public void onModuleLoad() {
		Asserty.asserty(registerJS());
	}

	@GwtIncompatible
	public void runJS(int which) throws Exception {
		try {
			String error = (String) jBrowser.executeScript("return window.asserty(" + which + ");");
			if (error == null) {
				return; // OK
			}

			// find symbolmap-file
			Optional<File> symbolMap = Stream
					.of(new File(VertxUI.getTargetFolder(true) + "/WEB-INF/deploy/a/symbolMaps").listFiles())
					.filter(f -> f.getName().endsWith(".symbolMap")).findFirst();
			if (symbolMap.isPresent() == false) {
				// no symbolmap file, is OK but less readable
				throw new Exception(error);
			}

			// Read it
			List<String> maps = IOUtils.readLines(new FileReader(symbolMap.get()));

			// Apply
			String[] errors = error.split("\n");
			Exception exception = new Exception(errors[0]);
			List<StackTraceElement> stacks = new ArrayList<>();
			boolean skippingStart = true;
			for (int x = 1; x < errors.length; x++) {
				int find = errors[x].indexOf("@");
				if (find == -1) { // garbage
					continue;
				}
				String fileAndMethod = errors[x].substring(0, find);
				Optional<String> line = maps.stream().filter(l -> l.startsWith(fileAndMethod)).findFirst();
				if (line.isPresent() == false) { // garbage
					continue;
				}
				String[] lne = line.get().split(",");

				// skipping the first few 'getting outside javascript'
				if (lne[2].equals("java.lang.AssertionError")) {
					skippingStart = false;
					continue;
				} else if (skippingStart) {
					continue;
				}
				StackTraceElement stack = new StackTraceElement(lne[2], lne[3], lne[4], Integer.parseInt(lne[5]));
				stacks.add(stack);
			}
			exception.setStackTrace(stacks.toArray(new StackTraceElement[0]));
			throw exception;
		} finally {
			// unfortunately jBrowser doesn't react on a runtime shutdown hook,
			// otherwise we could just recycle the jBrowser....
		}
	}

}
