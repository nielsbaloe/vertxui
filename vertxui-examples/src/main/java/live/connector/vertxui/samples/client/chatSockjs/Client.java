package live.connector.vertxui.samples.client.chatSockjs;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.console;
import static live.connector.vertxui.client.fluent.FluentBase.head;
import static live.connector.vertxui.client.fluent.FluentBase.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import elemental.events.MessageEvent;
import elemental.json.Json;
import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.client.transport.SockJS;
import live.connector.vertxui.samples.client.AllExamplesClient;
import live.connector.vertxui.samples.client.Dto;

/**
 * @author Niels Gorisse
 */

public class Client implements EntryPoint {

	public static final String url = "/chatSockjs";

	public Client() {
		head.script(FigWheelyClient.urlJavascript);
		SockJS.importJs((Void) -> {

			String name = window.prompt("What is your name?", "");
			Fluent input = body.input(null, "text");
			Fluent messages = body.div();

			SockJS socket = SockJS.create(url);
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
			input.keydown((fluent, event) -> {
				if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
					socket.send(name + ": " + input.domValue());
					input.att(Att.value, null);

					// extra: pojo example
					Pojofy.socketSend(socket, urlPojo, new Dto("violet"), AllExamplesClient.dto,
							Json.parse("{\"action\":\"save\"}"));
				}
			});
			input.focus();

		});
	}

	public static String urlPojo = "/pojo";

	@Override
	public void onModuleLoad() {
	}
}
