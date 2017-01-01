package live.connector.vertxui.server.samples.chatEventBus;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.client.samples.chatEventBus.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleChatEventbus extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// chat with EventBus
		final String freeway = live.connector.vertxui.client.samples.chatEventBus.Client.freeway;
		PermittedOptions adresser = new PermittedOptions().setAddress(freeway);
		BridgeOptions firewall = new BridgeOptions().addInboundPermitted(adresser).addOutboundPermitted(adresser);
		router.route("/chatEventbus/*").handler(SockJSHandler.create(vertx).bridge(firewall, be -> {
			final String id = be.socket().writeHandlerID();
			if (be.getRawMessage() == null) { // connected
			} else if (be.type() == BridgeEventType.REGISTER) { // entering
			} else if (be.type() == BridgeEventType.SOCKET_CLOSED) { // leaving
				vertx.eventBus().publish(freeway, "Goodbye id=" + id); // broadcast-example
			}
			// broadcasting to everyone is done automaticly by the Bridge!
			be.complete(true);
		}));
		vertx.eventBus().consumer(freeway, message -> { // receiving
			if (message.replyAddress() != null) {
				message.reply("I received so I reply!"); // extra: reply example
			}
		});

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
