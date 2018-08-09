package live.connector.vertxui.samples.client.chatEventBus;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.console;
import static live.connector.vertxui.client.fluent.FluentBase.head;
import static live.connector.vertxui.client.fluent.FluentBase.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import elemental.json.Json;
import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.transport.EventBus;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.AllExamplesClient;
import live.connector.vertxui.samples.client.Dto;

/**
 * @author Niels Gorisse
 *
 */

public class Client implements EntryPoint {

	// EventBus-address of text messages to everyone
	public static final String freeway = "freeWayAddress";

	// EventBus-address of myDto objects
	public static final String addressPojo = "serviceForDto";

	public static final String url = "/chatEventbus";

	public Client() {
		head.script(FigWheelyClient.urlJavascript);
		EventBus.importJs((Void) -> {

			String name = window.prompt("What is your name?", "");
			Fluent input = body.input(null, "text");
			Fluent messages = body.div();

			EventBus eventBus = EventBus.create(url, null);
			eventBus.onopen(event -> {
				eventBus.publish(freeway, name + ": Ola, I'm " + name + ".", null);
				eventBus.registerHandler(freeway, null, (error, in) -> { // onmessage
					messages.li(null, in.get("body").asString());
				});

				// extra example: pojo consume
				Pojofy.eventbusReceive(eventBus, addressPojo, null, AllExamplesClient.dto,
						a -> console.log("Received pojo: " + a.color));
			});

			input.keydown((fluent, event) -> {
				if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
					eventBus.publish(freeway, name + ": " + input.domValue(), null);
					input.att(Att.value, null);

					// extra example: object publish
					Pojofy.eventbusPublish(eventBus, addressPojo, new Dto("blue by " + name),
							Json.parse("{\"action\":\"save\"}"), AllExamplesClient.dto);
				}
			});
			input.focus();
		});
	}

	@Override
	public void onModuleLoad() {
	}

}
