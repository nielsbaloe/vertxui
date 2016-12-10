package live.connector.vertxui.samples.helloWorldFluidHtml;

import org.teavm.jso.ajax.XMLHttpRequest;

import live.connector.vertxui.fluidhtml.Button;
import live.connector.vertxui.fluidhtml.Div;
import live.connector.vertxui.fluidhtml.Html;

public class Client {

	private Button button;
	private Div responsePanel;
	private Div thinkingPanel;

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		Html body = Html.body();
		button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		responsePanel = body.div();
		thinkingPanel = body.div("The server waits as demonstration").id("thinking-panel").css("display", "none");
	}

	private void clicked() {
		button.disable();
		thinkingPanel.css("display", "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.onComplete(() -> responsed(xhr.getResponseText()));
		xhr.open("GET", "/server");
		xhr.send();
	}

	private void responsed(String text) {
		System.out.println("received: " + text);
		button.enable();

		responsePanel.div(text);
		thinkingPanel.css("display", "none");
	}

}
