package live.connector.vertxui.server.transport;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class Pojofy {

	public static interface ReplyAjax<A> {
		public Object reply(HttpServerRequest request, A input);
	}

	@SuppressWarnings("unchecked")
	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, ReplyAjax<A> handler) {
		return context -> {
			context.request().bodyHandler(body -> {
				String in = body.toString();
				A input = null;
				// no input type or null? use string
				if (in == null || in.isEmpty() || inputType.getClass().equals(String.class)) {
					input = (A) in;
				} else {
					input = (A) Json.decodeValue(in, inputType);
				}
				Object output = handler.reply(context.request(), input);

				// no output or type String? -> use string
				if (output == null || output instanceof java.lang.String) {
					context.response().end((String) output);
				} else {
					context.response().end(Json.encode(output));
				}
			});
		};
	}

	public interface ReplyEventBus<A, B> {
		public B reply(MultiMap headers, A input);
	}

	public static <A> void eventbus(String service, Class<A> inputType, ReplyEventBus<A, Object> handler) {
		Vertx.currentContext().owner().eventBus().consumer(service, message -> {
			A input = (A) Json.decodeValue((String) message.body(), inputType);
			Object output = handler.reply(message.headers(), input);
			message.reply(Json.encode(output));
		});
	}

}
