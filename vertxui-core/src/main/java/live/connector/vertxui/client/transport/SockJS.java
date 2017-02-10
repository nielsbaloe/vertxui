package live.connector.vertxui.client.transport;

import elemental.js.html.JsWebSocket;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * A wrapper around websocket with lots of shockwave-less fallback methods -
 * called SockJS.
 * 
 * @author ng
 *
 */
public class SockJS extends JsWebSocket {

	protected SockJS() {
	}

	static {
		Fluent.head.scriptSync("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js");
	}

	protected static void ensureStaticLoadingJsFile() {
	}

	public final native static SockJS create(String url) /*-{ return new window.top.SockJS(url); }-*/;

}
