package live.connector.vertxui.server.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

import live.connector.vertxui.server.VertxUI;

public class TestWithDom {

	public static void runwithJunit(Class<?> classs) throws Exception {
		VertxUI.with(classs, null); // Convert to javascript

		JBrowserDriver jBrowser = new JBrowserDriver(Settings.builder().logJavascript(true).build());
		try { // headless run
			jBrowser.get("file:///" + new File("war/index.html").getAbsolutePath());
			String error = (String) jBrowser.executeScript("return window.asserty();");
			if (error == null) {
				return; // OK
			}

			// find symbolmap-file
			Optional<File> symbolMap = Stream.of(new File("war/WEB-INF/deploy/a/symbolMaps").listFiles())
					.filter(f -> f.getName().endsWith(".symbolMap")).findFirst();
			if (symbolMap.isPresent() == false) {
				throw new Exception(error); // no symbolmap file, is OK
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
			jBrowser.quit();
		}
	}

}
