package live.connector.vertxui.samples.client.chatSockjs;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import elemental.events.MessageEvent;
import elemental.json.Json;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.client.transport.SockJS;
import live.connector.vertxui.samples.client.AllExamplesClient;
import live.connector.vertxui.samples.client.chatEventBus.Dto;

/**
 * @author Niels Gorisse
 */

public class Client implements EntryPoint {

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input(null, "text");
		Fluent messages = body.div();

		SockJS socket = SockJS.create("http://localhost/chatSockjs");
		socket.setOnopen(e -> {
			socket.send(name + ": Ola, I'm " + name + ".");
		});
		socket.setOnmessage(e -> {
			// extra: pojo example
			if (Pojofy.socketReceive(urlPojo, e, AllExamplesClient.dto,
					d -> console.log("Received pojo color=" + d.color))) {
				return;
			}

			messages.li(null, ((MessageEvent) e).getData().toString());
		});
		input.keydown(event -> {
			if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
				socket.send(name + ": " + input.domValue());
				input.att(Att.value, "");

				// extra: pojo example
				Pojofy.socketSend(socket, urlPojo, new Dto("violet"), AllExamplesClient.dto,
						Json.parse("{\"action\":\"save\"}"));
			}
		});
	}

	public static String urlPojo = "/pojo";

	@Override
	public void onModuleLoad() {
	}
}
