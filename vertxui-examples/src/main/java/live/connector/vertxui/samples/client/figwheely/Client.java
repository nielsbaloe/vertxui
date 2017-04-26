package live.connector.vertxui.samples.client.figwheely;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.head;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	/**
	 * Please, run the server and change some text in the constructor below,
	 * save, and then look at your browser, press the button or see what
	 * happens. Don't forget to edit the /sources/sample.css file, save, and
	 * look at your browser at the same time. Do NOT reload your browser.
	 */

	public Client() {
		head.script(FigWheelyClient.urlJavascript).stylesheet("/sourcez/sample.css?" + System.currentTimeMillis());

		body.div().id("picture");
		Fluent button = body.button(null, "button", "Look at the css, and change something WITHOUT reloading.");
		button.click((fluent, event) -> {
			button.txt("Something else");
			body.button(null, "button", "sdfsdf!");
		});
	}

	@Override
	public void onModuleLoad() {
	}
}
