package live.connector.vertxui.samples.sockjs;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.core.FigWheely;
import live.connector.vertxui.core.StaticHandlery;
import live.connector.vertxui.core.VertxUI;

public class Server extends AbstractVerticle {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) throws InterruptedException {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);

		// Boot figwheely first, so that VertX'es don't get their first
		// java2javascript translation with debug=false. This single line
		// enables/disables FigWheely.
		FigWheely.with(router);

		// Sockjs handler
		PermittedOptions adresser = new PermittedOptions().setAddress(Client.eventBusAddress);
		BridgeOptions firewall = new BridgeOptions().addInboundPermitted(adresser).addOutboundPermitted(adresser);
		router.route("/sockjs/*").handler(SockJSHandler.create(vertx).bridge(firewall, be -> {
			if (be.type() == BridgeEventType.REGISTER) {
				LOGGER.info("Connected: " + be.socket().writeHandlerID());
				vertx.eventBus().publish(Client.eventBusAddress,
						"Hey all, new subscriber " + be.socket().writeHandlerID());
			} else if (be.type() == BridgeEventType.SOCKET_CLOSED) {
				LOGGER.info("Leaving: " + be.socket().writeHandlerID());
			}
			be.complete(true);
		}));

		// Client with Figwheely support
		String clientUrl = "/client.js";
		String cssUrl = "/sources/sample.css";
		router.route("/client").handler(requestHandler -> {
			requestHandler.response()
					.end("<!DOCTYPE html><html><head><link rel=stylesheet href=" + cssUrl + "?"
							+ System.currentTimeMillis() + ">" + "<script type=text/javascript src=" + clientUrl
							+ "></script></head><body><script>" + FigWheely.script
							+ "main()</script><div id='picture'/></body></html>");
		});
		try {
			// router.route("/figwheely.js").handler(new
			// VertxUI(FigWheelyJs.class, false));
			router.route(clientUrl).handler(new VertxUI(Client.class, false));
			router.route("/sources/*").handler(StaticHandlery.create("sources"));
		} catch (TeaVMToolException | IOException e) {
			// Error on first-time java-2-javascript translation: stop deploying
			LOGGER.log(Level.SEVERE, "Startup error", e);
			System.exit(0); // stop on startup error
		}

		// eventbus example
		vertx.eventBus().consumer(Client.eventBusAddress, message -> {
			LOGGER.info("received: " + message.body() + " replyAddress=" + message.replyAddress());
			if (message.replyAddress() != null) {
				LOGGER.info("sending: I received so I reply");
				message.reply("I received so I reply to " + message.replyAddress());
			}
		});

		// HTTP server
		HttpServerOptions serverOptions = new HttpServerOptions().setCompressionSupported(true);
		HttpServer server = vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen(80,
				listenHandler -> {
					if (listenHandler.failed()) {
						LOGGER.log(Level.SEVERE, "Startup error", listenHandler.cause());
						System.exit(0); // stop on startup error
					}
				});

		LOGGER.info("Initialised:" + router.getRoutes().stream().map(a -> {
			return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
		}).collect(Collectors.joining()));
	}

}