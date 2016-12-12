package live.connector.vertxui.samples.helloWorldFluidHtml;

import org.teavm.jso.ajax.XMLHttpRequest;

import live.connector.vertxui.fluidhtml.Body;
import live.connector.vertxui.fluidhtml.Button;
import live.connector.vertxui.fluidhtml.Div;
import live.connector.vertxui.fluidhtml.Html;

public class Client {

	private Button button;
	private Div response;
	private Div thinking;

	// Do not run this one, run the server
	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		Body body = Html.getBody();
		button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		response = body.div();
		thinking = body.div().inner("The server waits as demonstration").id("thinking-panel").css("display",
				"none");
	}

	private void clicked() {
		button.disable();
		thinking.css("display", "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.onComplete(() -> responsed(xhr.getResponseText()));
		xhr.open("GET", "/server");
		xhr.send();
	}

	private void responsed(String text) {
		System.out.println("received: " + text);
		button.enable();

		response.div().inner(text);
		thinking.css("display", "none");
	}

}
