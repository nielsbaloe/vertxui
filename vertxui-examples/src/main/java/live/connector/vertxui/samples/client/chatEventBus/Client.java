package live.connector.vertxui.samples.client.chatEventBus;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import elemental.json.Json;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.transport.EventBus;
import live.connector.vertxui.samples.client.AllExamplesClient;

/**
 * @author Niels Gorisse
 *
 */

public class Client implements EntryPoint {

	// EventBus-address of text messages to everyone
	public static final String freeway = "freeWayAddress";

	// EventBus-address of myDto objects
	public static final String addressPojo = "serviceForDto";

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input(null, null, "text", null);
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("http://localhost/chatEventbus", null);
		eventBus.onopen(evt -> {
			eventBus.publish(freeway, name + ": Ola, I'm " + name + ".", null);
			eventBus.registerHandler(freeway, null, (error, in) -> { // onmessage
				messages.li(null, in.get("body").asString());
			});

			// extra example: pojo consume
			eventBus.registerHandler(addressPojo, null, AllExamplesClient.dto,
					a -> console.log("Received pojo: " + a.color));
		});

		input.keydown(event -> {
			if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
				eventBus.publish(freeway, name + ": " + input.value(), null);
				input.value(""); // clear the inputfield

				// extra example: object publish
				eventBus.publish(addressPojo, new Dto("blue by " + name), Json.parse("{\"action\":\"save\"}"),
						AllExamplesClient.dto);
			}
		});
	}

	
	@Override
	public void onModuleLoad() {
	}

}
