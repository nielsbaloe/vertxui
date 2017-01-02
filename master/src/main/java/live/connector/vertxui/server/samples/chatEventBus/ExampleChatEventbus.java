package live.connector.vertxui.server.samples.chatEventBus;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.client.samples.chatEventBus.Client;
import live.connector.vertxui.client.samples.chatEventBus.MyDto;
import live.connector.vertxui.server.VertxUI;
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
		PermittedOptions myDtoOK = new PermittedOptions().setAddress(Client.serviceAddressMyDto);
		BridgeOptions firewall = new BridgeOptions().addInboundPermitted(freewayOK).addOutboundPermitted(freewayOK)
				.addInboundPermitted(myDtoOK).addOutboundPermitted(myDtoOK);
		router.route("/chatEventbus/*").handler(SockJSHandler.create(vertx).bridge(firewall, be -> {
			// id=be.socket().writeHandlerID() and type=be.type().name());
			// broadcasting to everyone is done automaticly!
			be.complete(true);
		}));
		// to broadcast: vertx.eventBus().publish(Client.freeway,"Bla");
		vertx.eventBus().consumer(Client.freeway, message -> { // receiving
			if (message.replyAddress() != null) {
				message.reply("I received so I reply!"); // extra example: reply
			}
		});

		// extra example: receiving and replying objects
		VertxUI.bind(Client.serviceAddressMyDto, MyDto.class, this::serviceDoSomething);

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

	public MyDto serviceDoSomething(MyDto received) {
		log.info("Extra example: received a dto with color=" + received.color);
		return new MyDto("red");
	}

}
