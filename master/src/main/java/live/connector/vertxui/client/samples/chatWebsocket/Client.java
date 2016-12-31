package live.connector.vertxui.client.samples.chatWebsocket;

import com.google.gwt.core.client.EntryPoint;

import elemental.client.Browser;
import elemental.events.MessageEvent;
import elemental.events.UIEvent;
import elemental.html.WebSocket;
import elemental.html.Window;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * @author Niels Gorisse
 */

public class Client implements EntryPoint {

	public Client() {
		Window window = Browser.getWindow();

		String name = window.prompt("What is your name?", "");

		Fluent body = Fluent.getBody();
		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		WebSocket webSocket = window.newWebSocket("ws://localhost/chatWebsocket");
		webSocket.setOnopen(e -> {
			webSocket.send(name + ": Ola, I'm " + name + ".");
		});
		webSocket.setOnmessage(e -> {
			messages.li(((MessageEvent) e).getData().toString());
		});
		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				webSocket.send(name + ": " + input.value());
				input.value(""); // clear the inputfield
			}
		});
	}

	@Override
	public void onModuleLoad() {
	}
}
