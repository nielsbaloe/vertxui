package live.connector.vertxui.samples.sockjs;

import live.connector.vertxui.core.EventBus;
import live.connector.vertxui.fluidhtml.Html;

/**
 * TODO
 * 
 * - check: fluidhtml: perhaps use http://j2html.com and look at groovy
 * markupbuilder...
 * 
 * - finish Wrapping the client for SockJS here
 * https://github.com/konsoletyper/teavm/wiki/Interacting-with-JavaScript
 * http://teavm.org/javadoc/0.4.x/jso/core/
 * http://teavm.org/javadoc/0.4.x/jso/apis/
 * 
 * - finish FigWheely: reload or show error at client, also add support for
 * other files.
 * 
 * - multiline in java https://github.com/benelog/multiline to simulate react.js
 * https://facebook.github.io/react/docs/rendering-elements.html +>>
 * 
 * - an example with a plain websocket
 * 
 * - an example with vertx service-proxy
 * 
 * - announce at vertx-awesome github list!
 * 
 * @author Niels Gorisse
 *
 */
public class Client {

	public static final String eventBusAddress = "someAddressBothWays";

	// Do not run this one, run the server
	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		Html.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");

		Html.getBody().div().inner("kiekeboo");

		EventBus eb = new EventBus("http://localhost/client");
		eb.onOpen(() -> {
			System.out.println("connected!!");
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
			System.out.println("onError: " + a);// TODO , a, b, c);
		});

	}

}
