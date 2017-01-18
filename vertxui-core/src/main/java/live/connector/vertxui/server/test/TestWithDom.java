package live.connector.vertxui.server.test;

import java.io.File;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

import live.connector.vertxui.server.VertxUI;

public class TestWithDom {

	public static void runwithJunit(Class<?> classs) throws Exception {
		VertxUI.with(classs, null); // Convert to javascript

		JBrowserDriver jBrowser = new JBrowserDriver(Settings.builder().logJavascript(true).build());
		try { // headless run
			jBrowser.get("file:///" + new File("war/index.html").getAbsolutePath());
			String errors = (String) jBrowser.executeScript("return window.asserty();");
			if (errors != null) {
				throw new Exception(errors);
			}
		} finally {
			jBrowser.quit();
		}
	}

}
