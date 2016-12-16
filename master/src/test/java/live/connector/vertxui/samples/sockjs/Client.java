package live.connector.vertxui.samples.sockjs;

import live.connector.vertxui.core.EventBus;
import live.connector.vertxui.fluentHtml.FluentHtml;

/**
 * @author Niels Gorisse
 *
 */
public class Client {

	public static final String eventBusAddress = "someAddressBothWays";

	// Please don't run this class but run the Server instead.
	public static void main(String[] args) {
		try {
			new Client();
		} catch (Error ule) {
			// This looks weird but teaVM does not know UnsatisfiedLinkError....
			if (ule.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
				System.out.println("Please don't run this class but run the Server instead.");
			} else {
				ule.printStackTrace();
			}
		}
	}

	public Client() {
		FluentHtml.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");

		// TODO: not implemented yet!
		EventBus eb = new EventBus("http://localhost/client");

		eb.onOpen(() -> {
			// eb.registerHandler('some-address', function(error, message) {
			// System.out.println('received: ', error, JSON.stringify(message));
			// });
			//
			// System.out.println('publish: Im new here');
			// eb.publish('some-address', {
			// name : "I'm new here"
			// }, null);
			//
			// System.out.println('send: sending to...');
			// eb.send('some-address', {
			// name : 'sending to...'
			// }, null, function(a, message) {
			// if (message == null) {
			// System.out.println("ERROR: response null ", a);
			// } else {
			// System.out.println('response: ', message, a);
			// }
			// });
		});
		eb.onClose(() -> {
			System.out.println("disconnected");
		});
		eb.onError(a -> {
			// argumenten nog niet helder
			// System.out.println("onError: " + a); a, b, c);
		});

	}

}
