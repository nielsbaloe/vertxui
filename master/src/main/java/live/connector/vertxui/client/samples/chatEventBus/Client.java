package live.connector.vertxui.client.samples.chatEventBus;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.UIEvent;
import live.connector.vertxui.client.EventBus;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * @author Niels Gorisse
 *
 */

public class Client implements EntryPoint {

	public static final String freeway = "twoWayAddressOpenedAtServerBridge";

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("http://localhost/chatEventbus", null);
		eventBus.onopen(evt -> {
			eventBus.publish(freeway, name + ": Ola, I'm " + name + ".", null);
			eventBus.registerHandler(freeway, null, (error, in) -> { // onmessage
				messages.li(in.get("body").asString());
			});

			// extra: send example
			eventBus.send(freeway, name + ": I want a reply", null, (error, message) -> {
				console.log("server said: " + message.get("body"));
			});

			eventBus.onclose(e -> {
				console.log("close");
			});
			eventBus.onerror(e -> {
				console.log(e);
			});
		});
		input.keydown(evt ->

		{
			if (((UIEvent) evt).getKeyCode() == 13) {
				eventBus.publish(freeway, name + ": " + input.value(), null);
				input.value(""); // clear the inputfield
			}
		});
	}

	@Override
	public void onModuleLoad() {

	}

}
