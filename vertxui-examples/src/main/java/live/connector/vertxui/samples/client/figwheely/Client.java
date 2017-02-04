package live.connector.vertxui.samples.client.figwheely;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	/**
	 * Please, run the server and change some text in the constructor below,
	 * save, and then look at your browser, press the button or see what
	 * happens. Don't forget to edit the /sources/sample.css file, save, and
	 * look at your browser at the same time. Do NOT reload your browser.
	 */

	public static String figLocation = "/figwheely.js";

	public Client() {
		head.stylesheet("/sourcez/sample.css?" + System.currentTimeMillis()).script(figLocation);

		body.div().id("picture");
		Fluent button = body.button(null, "Look at the css, and change something WITHOUT reloading.");
		button.click(e -> {
			button.txt("Something else");
			body.button(null, "sdfsdf!");
		});
	}

	@Override
	public void onModuleLoad() {
	}
}
