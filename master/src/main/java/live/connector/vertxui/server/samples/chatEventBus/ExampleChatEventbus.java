package live.connector.vertxui.server.samples.chatEventBus;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import com.google.gson.Gson;

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
import live.connector.vertxui.client.samples.chatEventBus.MyDto;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleChatEventbus extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// chat with EventBus
		PermittedOptions freewayOK = new PermittedOptions().setAddress(Client.freeway);
		PermittedOptions myDtoOK = new PermittedOptions().setAddress(MyDto.class.getName());
		BridgeOptions firewall = new BridgeOptions().addInboundPermitted(freewayOK).addOutboundPermitted(freewayOK)
				.addInboundPermitted(myDtoOK).addOutboundPermitted(myDtoOK);
		router.route("/chatEventbus/*").handler(SockJSHandler.create(vertx).bridge(firewall, be -> {
			final String id = be.socket().writeHandlerID();
			if (be.getRawMessage() == null) { // connected
			} else if (be.type() == BridgeEventType.REGISTER) { // entering
			} else if (be.type() == BridgeEventType.SOCKET_CLOSED) { // leaving
				vertx.eventBus().publish(Client.freeway, "Goodbye id=" + id); // broadcast-example
			}
			be.complete(true); // broadcasting is done automaticly!
		}));
		vertx.eventBus().consumer(Client.freeway, message -> { // receiving
			if (message.replyAddress() != null) {
				message.reply("I received so I reply!"); // extra: reply example
			}
		});
		// extra: receiving and replying objects
		vertx.eventBus().consumer(MyDto.class.getName(), in -> {
			MyDto received = new Gson().fromJson((String) in.body(), MyDto.class);
			log.info("Extra: received a dto with color=" + received.color);
			MyDto send = new MyDto();
			send.color = "red";
			in.reply(new Gson().toJson(send));
		});

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
