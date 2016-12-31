package live.connector.vertxui.client.samples.chatEventBus;

import com.google.gwt.core.client.EntryPoint;

import elemental.client.Browser;
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
		String name = Browser.getWindow().prompt("What is your name?", "");

		Fluent body = Fluent.getBody();
		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("http://localhost/chatEventbus", null);
		eventBus.onopen(evt -> {
			eventBus.publish(freeway, name + ": Ola, I'm " + name + ".", null);
			eventBus.registerHandler(freeway, null, (error, in) -> { // onmessage
				messages.li(in.get("body").asString());
			});
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
