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

public class ExampleChatSockjs implements EntryPoint {

	public static final String freeway = "freeway";

	public ExampleChatSockjs() {
		Fluent.scriptSyncEval("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");

		String name = Browser.getWindow().prompt("What is your name?", "");

		Fluent body = Fluent.getBody();
		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("http://localhost/sockjs", null);
		eventBus.onopen(evt -> {
			eventBus.send(freeway, name + ": Ola, I'm " + name + ".", null, null);
		});
		// eventBus.registerHandler(freeway, null, (status, message) -> {
		// messages.li(message);
		// });
		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				eventBus.send("freeway", name + ": " + input.value(), null, null);
				input.value(""); // clear the inputfield
			}
		});
	}

	@Override
	public void onModuleLoad() {

	}

}
