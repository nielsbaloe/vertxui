package live.connector.vertxui.client.samples.chatSockjs;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.MessageEvent;
import elemental.events.UIEvent;
import elemental.json.Json;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.samples.AllExamples;
import live.connector.vertxui.client.samples.chatEventBus.Dto;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.client.transport.SockJS;

/**
 * @author Niels Gorisse
 */

public class Client implements EntryPoint {

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		SockJS socket = SockJS.create("http://localhost/chatSockjs");
		socket.setOnopen(e -> {
			socket.send(name + ": Ola, I'm " + name + ".");
		});
		socket.setOnmessage(e -> {
			// extra: pojo example
			if (Pojofy.socketReceive(urlPojo, e, AllExamples.dto, d -> console.log("Received pojo color=" + d.color))) {
				return;
			}

			messages.li(((MessageEvent) e).getData().toString());
		});
		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				socket.send(name + ": " + input.value());
				input.value(""); // clear the inputfield

				// extra: pojo example
				Pojofy.socketSend(socket, urlPojo, new Dto("violet"), AllExamples.dto,
						Json.parse("{\"action\":\"save\"}"));
			}
		});
	}

	public static String urlPojo = "/pojo";

	@Override
	public void onModuleLoad() {
	}
}
