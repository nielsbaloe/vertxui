package live.connector.vertxui.client.transport;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

import elemental.js.html.JsWebSocket;
import elemental.json.Json;
import elemental.json.JsonObject;
import live.connector.vertxui.client.transport.EventBus.Handler;

public class MessageSocket extends JsWebSocket {

	protected MessageSocket() {
	}

	public static MessageSocket createSockJs(String url) {
		SockJS.ensureStaticLoadingJsFile();
		return createSockjsNative(url);
	}

	private final native static MessageSocket createSockjsNative(String url) /*-{ return new SockJS(url); }-*/;

	public final native static MessageSocket create(String url) /*-{ return new WebSocket(url); }-*/;

	public final <T, R> void send(String address, T model, JsonObject headers, ObjectMapper<T> inMapper,
			ObjectMapper<R> outMapper, Handler<R> replyHandler) {
		JsonObject all = Json.createObject();
		all.put("address", address);
		all.put("data", inMapper.write(model));
		all.put("headers", headers);
		// TODO
		// send(address, inMapper.write(model), headers, (error, message) -> {
		// if (error != null) {
		// throw new IllegalArgumentException(error.asString());
		// }
		// R result = outMapper.read(message.get("body"));
		// replyHandler.handle(result);
		// });
	}

}
