package live.connector.vertxui.components.server;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertx.components.bootstrap.client.example.ExampleClient;
import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.server.FigWheelyServer;
import live.connector.vertxui.server.VertxUI;

public class ExampleServer extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {

		boolean debug = true;

		// Serve the javascript for figwheely (and turn it on too)
		Router router = Router.router(vertx);
		if (debug) {
			router.get(FigWheelyClient.urlJavascript).handler(FigWheelyServer.create());
		}

		// The main compiled js
		router.get("/*").handler(VertxUI.with(ExampleClient.class, "/", debug, true));

		// Start the server
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));
		server.requestHandler(router::accept).listen(80, listenHandler -> {
			if (listenHandler.failed()) {
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0);// stop on startup error
			}
			log.info("Initialised:" + router.getRoutes().stream().map(a -> {
				return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
			}).distinct().collect(Collectors.joining()));
		});
	}

}
