package live.connector.vertxui.samples.helloWorldFluentHtml;

import org.teavm.jso.ajax.XMLHttpRequest;

import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.Button;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;

public class Client {

	private Button button;
	private Div response;
	private Div thinking;

	// Please don't run this class but run the Server instead.
	public static void main(String[] args) {
		try {
			new Client();
		} catch (Error ule) {
			// This looks weird but teaVM does not know UnsatisfiedLinkError....
			if (ule.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
				System.out.println("Please don't run this class but run the Server instead.");
			} else {
				ule.printStackTrace();
			}
		}
	}

	public Client() {
		Body body = FluentHtml.getBody();
		button = body.div().button("Click me").id("hello-button").click(evt -> clicked());
		response = body.div();
		thinking = body.div().inner("The server waits as demonstration").id("thinking-panel").css("display", "none");
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
