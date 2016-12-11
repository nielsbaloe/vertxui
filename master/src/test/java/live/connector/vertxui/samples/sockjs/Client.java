package live.connector.vertxui.samples.sockjs;

import live.connector.vertxui.fluidhtml.Html;

/**
 * TODO
 * 
 * Wrap the client for SockJS here
 * https://github.com/konsoletyper/teavm/wiki/Interacting-with-JavaScript
 * http://teavm.org/javadoc/0.4.x/jso/core/
 * http://teavm.org/javadoc/0.4.x/jso/apis/
 * 
 * Fluidhtml: perhaps use http://j2html.com
 * 
 * Demonstrate the vertx service-proxy, perhaps before sockjs also a plain
 * websocket example
 * 
 * Announce at vertx-awesome github list.
 * 
 * @author ng
 *
 */
public class Client {

	public static final String address = "someAddressBothWays";

	public static String[] libraries = { "https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
			"https://raw.githubusercontent.com/vert-x3/vertx-examples/master/web-examples/src/main/java/io/vertx/example/web/chat/webroot/vertx-eventbus.js" };

	// Do not run this one, run the server
	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		Html.body().div().inner("kiekeboe");

	}

}
