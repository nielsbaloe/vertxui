package live.connector.vertxui.server.samples.chatWebsocket;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.chatWebsocket.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleChatWebsocket extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Chat with websocket
		List<String> ids = new ArrayList<>();
		server.websocketHandler(socket -> {
			if (!socket.path().equals("/chatWebsocket")) {
				socket.reject();
				return;
			}
			final String id = socket.textHandlerID();
			ids.add(id); // entering
			socket.closeHandler(data -> {
				ids.remove(id); // leaving
			});
			socket.handler(buffer -> { // receiving
				String message = buffer.toString();
				ids.forEach(i -> vertx.eventBus().send(i, message)); // broadcasting
				// to reply to one: socket.writeFinalTextFrame(...);
			});
		});

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
