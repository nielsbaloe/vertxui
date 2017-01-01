package live.connector.vertxui.server.samples.chatSockjs;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.client.samples.chatSockjs.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleChatSockjs extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Chat with SockJS
		List<String> ids = new ArrayList<>();
		router.route("/chatSockjs/*").handler(SockJSHandler.create(vertx).socketHandler(socket -> {
			final String id = socket.writeHandlerID();
			ids.add(id); // entering
			socket.endHandler(data -> {
				ids.remove(id); // leaving
			});
			socket.handler(buffer -> { // receiving
				ids.forEach(i -> vertx.eventBus().send(i, buffer)); // broadcasting
				// to reply to one: socket.write()
			});
		}));

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
