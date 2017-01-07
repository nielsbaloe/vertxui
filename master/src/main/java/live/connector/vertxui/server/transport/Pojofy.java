package live.connector.vertxui.server.transport;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.RoutingContext;

public class Pojofy {

	public static interface Reply<A, B> {
		public Object reply(A pojo, B context);
	}

	/**
	 * Create a web-handler which gets a pojo of type inputType.
	 * 
	 * @param inputType
	 *            a pojo type, or String.class for a string
	 * @param handler
	 *            a handler for this call, which returns a POJO back to the
	 *            browser, or a string, or null if you response something
	 *            yourself using the provided context.
	 * @return a web-handler which can receive and send POJO's through ajax.
	 */
	@SuppressWarnings("unchecked")
	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, Reply<A, RoutingContext> handler) {
		return context -> {
			context.request().bodyHandler(body -> {
				String in = body.toString();
				A input = null;
				// no input type or null? use string
				if (in.isEmpty() || inputType.getClass().equals(String.class)) {
					input = (A) in;
				} else {
					input = (A) Json.decodeValue(in, inputType);
				}

				Object output = handler.reply(input, context);
				if (output == null) { // do nothing
				} else if (output instanceof java.lang.String) { // pass
					context.response().end((String) output);
				} else {
					context.response().end(Json.encode(output));
				}
			});
		};
	}

	@SuppressWarnings("unchecked")
	public static <A> void eventbus(String urlOrAddress, Class<A> inputType, Reply<A, MultiMap> handler) {
		Vertx.currentContext().owner().eventBus().consumer(urlOrAddress, message -> {

			A input = null;
			String in = (String) message.body();
			// no input type or null? use string
			if (in.isEmpty() || inputType.getClass().equals(String.class)) {
				input = (A) in;
			} else {
				input = (A) Json.decodeValue(in, inputType);
			}

			Object output = handler.reply(input, message.headers());
			if (output == null) { // do nothing
			} else if (output instanceof java.lang.String) { // pass
				message.reply(output);
			} else {
				message.reply(Json.encode(output));
			}
		});
	}

	// TODO test
	public static <A, S extends ReadStream<Buffer> & WriteStream<Buffer>> void socket(String urlOrAddress, Buffer in,
			S socket, Class<A> inputType, Reply<A, JsonObject> handler) {
		if (!in.getString(0, urlOrAddress.length()).equals("{\"url\":\"" + urlOrAddress)) {
			return;
		}

		JsonObject json = in.toJsonObject();
		JsonObject body = json.getJsonObject("body");
		A input = null;
		if (!body.isEmpty()) {
			input = (A) Json.decodeValue(body.encode(), inputType);
		}

		Object output = handler.reply(input, json.getJsonObject("headers"));
		if (output == null) { // do nothing
		} else if (output instanceof java.lang.String) { // pass
			socket.write(Buffer.buffer((String) output));
		} else {
			socket.write(Buffer.buffer(Json.encode(output)));
		}
	}

}
