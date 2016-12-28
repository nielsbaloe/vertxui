package live.connector.vertxui.samples.helloWorld;

import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.client.Browser;
import elemental.dom.Document;
import elemental.dom.Element;
import elemental.html.Window;

public class Client {

	private Document document = Browser.getDocument();

	private Element button;
	private Element response;
	private Element thinking;

	public Client() {
		Element body = document.getBody();

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.setOndblclick(evt -> clicked());
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

		Element responseElem = document.createElement("div");
		responseElem.appendChild(document.createTextNode(text));
		response.appendChild(responseElem);

		thinking.getStyle().setProperty("display", "none");
	}

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

}
