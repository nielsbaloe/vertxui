package live.connector.vertxui.client.transport;

import java.util.function.Consumer;

import elemental.js.html.JsWebSocket;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * A wrapper around websocket with lots of shockwave-less fallback methods -
 * called SockJS.
 * 
 * 
 * @author ng
 *
 */
public class SockJS extends JsWebSocket {

	protected SockJS() {
	}

	public final native static SockJS create(String url) /*-{ return new window.top.SockJS(url); }-*/;

	/**
	 * You have to load "https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js" first.
	 * You can do this here with scriptSync() or with EntryPoint::getScripts().
	 * 
	 * @param then what to do when the script is loaded
	 */
	public static void importJs(Consumer<Void> then) {
		Fluent.head.scriptSync(then, "https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js");
	}

}
