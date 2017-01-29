package live.connector.vertxui.client.transport;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.events.Event;
import elemental.events.MessageEvent;
import elemental.html.ArrayBuffer;
import elemental.html.WebSocket;
import elemental.json.Json;
import elemental.json.JsonObject;

public class Pojofy {

	public static <I, O> void ajax(String protocol, String url, I model, ObjectMapper<I> inMapper,
			ObjectMapper<O> outMapper, BiConsumer<Integer, O> handler) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if (handler == null || xhr.getReadyState() != 4) {
				return;
			}
			O result = null;
			if (xhr.getStatus() == 200) {
				result = out(xhr.getResponseText(), outMapper);
			}
			handler.accept(xhr.getStatus(), result);
		});
		xhr.open(protocol, url);
		xhr.send(in(model, inMapper));
	}

	private final native static String socketToString(ArrayBuffer buf)/*-{
																		return String.fromCharCode.apply(null, new Uint8Array(buf));
																		}-*/;

	public static <O> boolean socketReceive(String url, Event e, ObjectMapper<O> outMapper, Consumer<O> handler) {
		Object me = ((MessageEvent) e).getData();
		String meString = me.toString();
		if (meString.equals("[object ArrayBuffer]")) { // websockets
			meString = socketToString((ArrayBuffer) me);
		}
		if (!meString.startsWith("{\"url\":\"" + url + "\"")) {
			return false;
		}
		JsonObject json = Json.parse(meString);
		handler.accept(out(json.getString("body"), outMapper));
		return true;
	}

	protected static <I> String in(I model, ObjectMapper<I> inMapper) {
		if (model == null) {
			return null;
		} else if (model instanceof String || inMapper == null) {
			return (String) model;
		} else {
			return inMapper.write(model);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <O> O out(String message, ObjectMapper<O> outMapper) {
		if (message == null) {
			return null;
		} else if (outMapper == null) { // outMapper null: string
			return (O) message;
		} else {
			return outMapper.read(message);
		}
	}

	public static <I> void socketSend(WebSocket socket, String url, I model, ObjectMapper<I> inMapper,
			JsonObject headers) {
		JsonObject object = Json.createObject();
		object.put("url", url);
		object.put("body", in(model, inMapper));
		object.put("headers", headers);
		socket.send(object.toJson());
	}

}
