package live.connector.vertxui.server.transport;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class Binders {

	public static <A, B> Handler<RoutingContext> ajax(Class<A> inputType, ReplyHandler<A, B> handler) {
		return context -> {
			A input = (A) Json.decodeValue(context.getBody().toString(), inputType);
			B output = handler.reply((MultiMap) context.pathParams(), input);
			context.response().end(Json.encode(output));
		};
	}

	public interface ReplyHandler<A, B> {
		public B reply(MultiMap headers, A input);
	}

	public static <A, B> void eventbus(String service, Class<A> inputType, ReplyHandler<A, B> handler) {
		Vertx.currentContext().owner().eventBus().consumer(service, message -> {
			A input = (A) Json.decodeValue((String) message.body(), inputType);
			B output = handler.reply(message.headers(), input);
			message.reply(Json.encode(output));
		});
	}

}
