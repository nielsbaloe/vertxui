package live.connector.vertxui.client.samples.chatEventBus;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import elemental.events.UIEvent;
import elemental.json.Json;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.transport.EventBus;

/**
 * @author Niels Gorisse
 *
 */

public class Client implements EntryPoint {

	// EventBus-address of text messages to everyone
	public static final String freeway = "freeWayAddress";

	// EventBus-address of myDto objects
	public static final String serviceAddress = "serviceForDto";

	// EventBus mapper interface for the myDto object, for json-object
	public interface DtoMapper extends ObjectMapper<Dto> {
	}

	// EventBus mapper object for the Dto object, for json-object
	private DtoMapper dtoMapper = GWT.create(DtoMapper.class);

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

			// extra example: object publish
			eventBus.publish(serviceAddress, new Dto("blue by " + name), Json.parse("{\"action\":\"save\"}"),
					dtoMapper);

			// extra example: object consume
			eventBus.consumer(serviceAddress, null, dtoMapper, a -> console.log("Received an object: " + a.color));
		});

		input.keydown(evt -> {
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
