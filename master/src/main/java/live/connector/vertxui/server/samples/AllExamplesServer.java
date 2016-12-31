package live.connector.vertxui.server.samples;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.client.samples.figwheely.Client;
import live.connector.vertxui.server.FigStaticHandler;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.VertxUI;

/**
 * @author ng
 *
 */
public class AllExamplesServer extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	// public static void main(String[] args) {
	// Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	// }

	private Class<?> classs;

	public AllExamplesServer(Class<?> classs) {
		this.classs = classs;
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Start figwheely and serve the javascript file
		FigWheely.with(router);
		router.get(Client.figLocation).handler(a -> {
			a.response().end(FigWheely.script);
		});

		// Figwheely example: serve sources folder (if figwheely is off, it's
		// just a normal statichandler)
		router.get("/sourcez/*").handler(FigStaticHandler.create("sources", "/sourcez/"));

		// Hello world examples: wait and do some server stuff for AJAX
		router.post("/server").handler(handle -> {
			vertx.setTimer(1000, l -> {
				handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
			});
		});

		// Chat by websocket
		if (classs.getName().equals(live.connector.vertxui.client.samples.chatWebsocket.Client.class.getName())) {
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
		}

		// Chat by SockJS (find the differences)
		if (classs.getName().equals(live.connector.vertxui.client.samples.chatSockjs.Client.class.getName())) {
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
		}

		// EventBus example
		if (classs.getName().equals(live.connector.vertxui.client.samples.eventBus.Client.class.getName())) {
			// or locally shared in a map:
			// vertx.sharedData().getLocalMap("chatRoom." + room).put(id, "_");
			final String freeway = "freeway";
			PermittedOptions adresser = new PermittedOptions().setAddress(freeway);
			BridgeOptions firewall = new BridgeOptions().addInboundPermitted(adresser).addOutboundPermitted(adresser);
			router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(firewall, be -> {
				if (be.type() == BridgeEventType.REGISTER) {
					log.info("Connected: " + be.socket().writeHandlerID());
					vertx.eventBus().publish(freeway, "Hey all, new subscriber " + be.socket().writeHandlerID());
				} else if (be.type() == BridgeEventType.SOCKET_CLOSED) {
					log.info("Leaving: " + be.socket().writeHandlerID());
				}
				be.complete(true);
			}));
			vertx.eventBus().consumer(freeway, message -> {
				log.info("received: " + message.body() + " replyAddress=" + message.replyAddress());
				if (message.replyAddress() != null) {
					log.info("sending: I received so I reply");
					message.reply("I received so I reply to " + message.replyAddress());
				}
			});
		}

		// All examples: the main compiled js and html at /war with a fancy 404.
		router.get("/*").handler(VertxUI.with(classs)).failureHandler(fail -> {
			fail.response().end(
					"<html style='width:100%;height:100%'><iframe src=//codepen.io/waddington/full/cdzuB style='width:100%;height:100%'></html>");
		});

		// Start the server
		server.requestHandler(router::accept).listen(80, listenHandler -> {
			if (listenHandler.failed()) {
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0);// stop on startup error
			}
		});
		log.info("Initialised:" + router.getRoutes().stream().map(a -> {
			return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
		}).collect(Collectors.joining()));
	}

}