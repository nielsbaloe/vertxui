package live.connector.vertxui.server.transport;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.RoutingContext;

public class Pojofy {

	/**
	 * Create a web-handler which gets a pojo of type inputType. Note: please
	 * use vertx-jersey if you have a lot of ajax calls (if you don't use vert.x
	 * as microservice), that is probably faster and better to use due to clean
	 * code.
	 * 
	 * @param inputType
	 *            a pojo type, or String.class for a string
	 * @param handler
	 *            a handler for this call, which returns a POJO back to the
	 *            browser, or a string, or null if you response something
	 *            yourself using the provided context.
	 * @return a web-handler which can receive and send POJO's through ajax.
	 */
	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, Reply<A, RoutingContext> handler) {
		return context -> {
			context.request().bodyHandler(body -> {
				A input = in(inputType, body.toString());
				String output = out(handler.reply(input, context));
				if (output == null) {
					// do nothing
				} else {
					context.response().end(output);
				}
			});
		};
	}

	public static <A> void eventbus(String urlOrAddress, Class<A> inputType, Reply<A, MultiMap> handler) {
		Vertx.currentContext().owner().eventBus().consumer(urlOrAddress, message -> {
			A input = in(inputType, (String) message.body());
			String output = out(handler.reply(input, message.headers()));
			if (output != null && message.replyAddress() != null) {
				message.reply(output);
			}
		});
	}

	// Note: replies at the same address!
	public static <A, S extends WriteStream<Buffer>> boolean socket(S socket, String url, Buffer in, Class<A> inputType,
			Reply<A, JsonObject> handler) {
		String start = ("{\"url\":\"" + url);
		if (in.length() < start.getBytes().length || !in.getString(0, start.length()).equals(start)) {
			return false;
		}

		JsonObject json = in.toJsonObject();
		A input = in(inputType, json.getString("body"));

		String output = out(handler.reply(input, json.getJsonObject("headers")));
		if (output == null) {
			return false;
		}
		json.put("body", output);
		json.remove("headers");
		socket.write(Buffer.buffer(json.toString()));
		return true;
	}

	public static interface Reply<A, B> {
		public Object reply(A pojo, B context);
	}

	private static String out(Object output) {
		String result = null;
		if (output == null) { // do nothing
		} else if (output instanceof java.lang.String) { // pass
			result = (String) output;
		} else {
			result = Json.encode(output);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <I> I in(Class<I> inputType, String in) {
		I result = null;
		// no input type or null? use string
		if (in.isEmpty() || inputType == null || inputType.getClass().equals(String.class)) {
			result = (I) in;
		} else {
			result = (I) Json.decodeValue(in, inputType);
		}
		return result;
	}

}
