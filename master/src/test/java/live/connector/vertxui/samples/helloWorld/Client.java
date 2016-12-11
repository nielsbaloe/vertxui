package live.connector.vertxui.samples.helloWorld;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLBodyElement;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class Client {

	private final HTMLDocument document = Window.current().getDocument();

	private HTMLButtonElement helloButton;
	private HTMLElement responsePanel;
	private HTMLElement thinkingPanel;

	// Do not run this one, run the server
	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		HTMLBodyElement body = document.getBody();

		helloButton = document.createElement("button").cast();
		helloButton.setAttribute("id", "hello-button");
		helloButton.setInnerHTML("Click me");
		helloButton.listenClick(evt -> clicked());
		body.appendChild(helloButton);

		responsePanel = document.createElement("response-panel");
		body.appendChild(responsePanel);

		thinkingPanel = document.createElement("thinking-panel");
		thinkingPanel.setInnerHTML("The server waits as demonstration");
		thinkingPanel.getStyle().setProperty("display", "none");
		body.appendChild(thinkingPanel);
	}

	private void clicked() {
		helloButton.setDisabled(true);
		thinkingPanel.getStyle().setProperty("display", "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.onComplete(() -> responsed(xhr.getResponseText()));
		xhr.open("GET", "/server");
		xhr.send();
	}

	private void responsed(String text) {
		System.out.println("received: " + text);
		helloButton.setDisabled(false);

		HTMLElement responseElem = document.createElement("div");
		responseElem.appendChild(document.createTextNode(text));
		responsePanel.appendChild(responseElem);

		thinkingPanel.getStyle().setProperty("display", "none");
	}

}
