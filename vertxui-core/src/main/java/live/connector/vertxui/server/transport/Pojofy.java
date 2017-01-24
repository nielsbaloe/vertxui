package live.connector.vertxui.server.transport;

import java.lang.invoke.MethodHandles;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.RoutingContext;

public class Pojofy {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

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
	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, BiFunction<A, RoutingContext, Object> handler) {
		return context -> {
			context.request().bodyHandler(body -> {
				A input = in(inputType, body.toString());
				String output = out(handler.apply(input, context));
				if (output == null) {
					// do nothing
				} else {
					context.response().end(output);
				}
			});
		};
	}

	public static <A> void eventbus(String urlOrAddress, Class<A> inputType, BiFunction<A, MultiMap, Object> handler) {
		Vertx.currentContext().owner().eventBus().consumer(urlOrAddress, message -> {
			A input = in(inputType, (String) message.body());
			String output = out(handler.apply(input, message.headers()));
			if (output != null) {
				if (message.replyAddress() != null) {
					message.reply(output);
				} else {
					log.warning("reply not send, the client is not using send() but publish(), lost output=" + output);
				}
			}
		});
	}

	// Note: replies at the same address!
	public static <A, S extends WriteStream<Buffer>> boolean socket(S socket, String url, Buffer in, Class<A> inputType,
			BiFunction<A, JsonObject, Object> handler) {
		String start = ("{\"url\":\"" + url);
		if (in.length() < start.getBytes().length || !in.getString(0, start.length()).equals(start)) {
			return false;
		}

		JsonObject json = in.toJsonObject();
		A input = in(inputType, json.getString("body"));

		String output = out(handler.apply(input, json.getJsonObject("headers")));
		if (output == null) {
			return false;
		}
		json.put("body", output);
		json.remove("headers");

		// TODO
		// ((WebSocket)socket).writeFinalTextFrame("");
		// ((SockJSSocket)socket).writeFinalTextFrame("");

		socket.write(Buffer.buffer(json.toString()));
		return true;
	}

	private static String out(Object output) {
		if (output == null) {
			return null;
		} else if (output instanceof java.lang.String) { // pass
			return (String) output;
		} else {
			return Json.encode(output);
		}
	}

	@SuppressWarnings("unchecked")
	private static <I> I in(Class<I> inputType, String in) {
		// no input type or null? use string
		if (in == null || in.isEmpty() || inputType == null || inputType.getClass().equals(String.class)) {
			return (I) in;
		} else {
			return (I) Json.decodeValue(in, inputType);
		}
	}

}
