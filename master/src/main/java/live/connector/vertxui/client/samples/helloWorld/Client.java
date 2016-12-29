package live.connector.vertxui.client.samples.helloWorld;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Document;
import elemental.dom.Element;
import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	private Document document = Fluent.getDocument();

	private Element button;
	private Element response;
	private Element thinking;

	public Client() {
		Element body = document.getBody();

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.setOnclick(evt -> clicked());
		body.appendChild(button);

		response = document.createElement("div");
		body.appendChild(response);

		thinking = document.createElement("div");
		thinking.setInnerHTML("The server waits as demonstration");
		thinking.getStyle().setProperty("display", "none");
		body.appendChild(thinking);
	}

	private void clicked() {
		button.setAttribute("disabled", "");
		thinking.getStyle().setProperty("display", "");

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
		System.out.println("received: " + text);
		button.removeAttribute("disabled");

		Element responseElem = document.createElement("div");
		responseElem.appendChild(document.createTextNode(text));
		response.appendChild(responseElem);

		thinking.getStyle().setProperty("display", "none");
	}

	@Override
	public void onModuleLoad() {
	}
}
