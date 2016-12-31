package live.connector.vertxui.client.samples.helloWorldFluentHtml;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.xhr.client.XMLHttpRequest;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Style;
import live.connector.vertxui.client.fluent.console;

public class Client implements EntryPoint {

	private Fluent button;
	private Fluent response;
	private Fluent thinking;

	public Client() {
		Fluent body = Fluent.getBody();
		button = body.div().button("Click me!").id("hello-button").click(evt -> clicked());
		response = body.div();
		thinking = body.div().inner("The server waits as demonstration!").id("thinking-panel").css(Style.display,
				"none");
	}

	private void clicked() {
		button.disabled(true);
		thinking.css(Style.display, "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if (xhr.getReadyState() == 4 && xhr.getStatus() == 200) {
				responsed(xhr.getResponseText());
			}
		});
		xhr.open("POST", "/server");
		xhr.send();
	}

	private void responsed(String text) {
		console.log("received: " + text);
		button.disabled(false);

		response.div().inner(text);
		thinking.css(Style.display, "none");
	}

	@Override
	public void onModuleLoad() {
	}

}
