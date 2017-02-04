package live.connector.vertxui.samples.client.helloWorld;

import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.document;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Element;
import elemental.events.Event;

public class Client implements EntryPoint {

	public final static String url = "/ajax";

	private Element button;
	private Element response;
	private Element thinking;

	public Client() {
		Element body = document.getBody();

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setTextContent("Click me");
		button.setOnclick(this::clicked);
		body.appendChild(button);

		response = document.createElement("div");
		body.appendChild(response);

		thinking = document.createElement("div");
		thinking.setTextContent("The server waits as demonstration");
		thinking.getStyle().setProperty("display", "none");
		body.appendChild(thinking);
	}

	// It is advisable to write callbacks into methods, so you can easily write
	// jUnit tests.
	private void clicked(Event e) {
		button.setAttribute("disabled", "");
		thinking.getStyle().setProperty("display", "");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if (xhr.getReadyState() == 4 && xhr.getStatus() == 200) {
				responsed(xhr.getResponseText());
			}
		});
		xhr.open("POST", url);
		xhr.send();
	}

	private void responsed(String text) {
		console.log("received: " + text);
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
