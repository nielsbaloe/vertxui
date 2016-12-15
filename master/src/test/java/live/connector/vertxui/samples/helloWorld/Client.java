package live.connector.vertxui.samples.helloWorld;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLBodyElement;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class Client {

	private final HTMLDocument document = Window.current().getDocument();

	private HTMLButtonElement button;
	private HTMLElement response;
	private HTMLElement thinking;

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
		HTMLBodyElement body = document.getBody();

		button = document.createElement("button").cast();
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.listenClick(evt -> clicked());
		body.appendChild(button);

		response = document.createElement("div");
		body.appendChild(response);

		thinking = document.createElement("div");
		thinking.setInnerHTML("The server waits as demonstration");
		thinking.getStyle().setProperty("display", "none");
		body.appendChild(thinking);
	}

	private void clicked() {
		button.setDisabled(true);
		thinking.getStyle().setProperty("display", "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.onComplete(() -> responsed(xhr.getResponseText()));
		xhr.open("GET", "/server");
		xhr.send();
	}

	private void responsed(String text) {
		System.out.println("received: " + text);
		button.setDisabled(false);

		HTMLElement responseElem = document.createElement("div");
		responseElem.appendChild(document.createTextNode(text));
		response.appendChild(responseElem);

		thinking.getStyle().setProperty("display", "none");
	}

}
