package live.connector.vertxui.client;

import elemental.js.html.JsWebSocket;
import live.connector.vertxui.client.fluent.Fluent;

public class SockJS extends JsWebSocket {

	protected SockJS() {
	}

	static {
		Fluent.scriptSyncEval("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js");
	}

	protected static void ensureStaticLoading() {
	}
	
	public final native static SockJS create(String url) /*-{ return new SockJS(url); }-*/;

}
