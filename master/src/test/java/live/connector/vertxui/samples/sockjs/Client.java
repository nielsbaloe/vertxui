package live.connector.vertxui.samples.sockjs;

import live.connector.vertxui.core.EventBus;
import live.connector.vertxui.fluidhtml.Button;
import live.connector.vertxui.fluidhtml.FluidHtml;

/**
 * TODO development:
 * 
 * - finish FigWheely: show errors at client, reload other resources than
 * js/css, refactor for easy removal.
 * 
 * - figure out how to simulate react.js and replace fluid html with something
 * lighter which PUSHes. perhaps use http://j2html.com and look at groovy
 * markupbuilder... - *
 * https://facebook.github.io/react/docs/rendering-elements.html
 * https://vuejs.org/v2/examples/todomvc.html
 * https://www.codeproject.com/articles/265364/first-adventures-in-google-closure
 * 
 * - finish Wrapping the client for SockJS
 * https://github.com/konsoletyper/teavm/wiki/Interacting-with-JavaScript
 * http://teavm.org/javadoc/0.4.x/jso/core/
 * http://teavm.org/javadoc/0.4.x/jso/apis/
 * 
 * - an example with a plain websocket
 * 
 * - an example with vertx service-proxy or a much better way to interact with
 * services (but strong binded!) - a la GWT? - eventbus-subscription both ways?
 * js: eventBus.subscribe(DatabaseVertX::onCreate);
 * 
 * - implement TODOMVC http://todomvc.com/
 * https://github.com/tastejs/todomvc/tree/gh-pages/examples/gwt/gwttodo
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
		FluidHtml.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
		// Html.getHead().stylesheet("http://localhost/my.css");

		FluidHtml body = FluidHtml.getBody();
		Button button = body.button("kiekeboo");
		button.onClick(e -> {
			System.out.println("Hello *insert your name* !!!");
			button.inner("something else");
			body.button("sdfsdff!");
		});

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
