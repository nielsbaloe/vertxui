package live.connector.vertxui.client.samples.chatSockjs;

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

	public static final String freeway = "freeway";

	public Client() {
		EventBus.scripts();

		String name = Browser.getWindow().prompt("What is your name?", "");

		Fluent body = Fluent.getBody();
		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("localhost/sockjs", null);
		eventBus.onopen(() -> {
			eventBus.send(freeway, name + ": Ola, I'm " + name + ".", null, null);
		});
		eventBus.registerHandler(freeway, null, (status, message) -> {
			messages.li(message);
		});
		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				eventBus.send("freeway", name + ": " + input.value(), null, null);
				input.value("");
			}
		});
	}

	@Override
	public void onModuleLoad() {
	}
}
