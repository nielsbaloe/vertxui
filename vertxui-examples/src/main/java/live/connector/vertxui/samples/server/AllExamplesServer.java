package live.connector.vertxui.samples.server;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.server.FigWheelyServer;
import live.connector.vertxui.server.VertxUI;

/**
 * @author Niels Gorisse
 *
 */
public class AllExamplesServer {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void start(Class<?> classs, Router router) {
		Vertx vertx = Vertx.currentContext().owner();
		HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));
		start(classs, router, httpServer);
	}

	public static void start(Class<?> classs, Router router, HttpServer httpServer) {

		boolean debug = true;

		// Serve the javascript for figwheely (and turn it on too)
		if (debug) {
			router.get(FigWheelyClient.urlJavascript).handler(FigWheelyServer.create());
		}

		// The main compiled js
		router.get("/*").handler(VertxUI.with(classs, "/", debug, true));

		// Start the server
		httpServer.requestHandler(router::accept).listen(80, listenHandler -> {
			if (listenHandler.failed()) {
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0);// stop on startup error
			}
			log.info("Initialised:" + router.getRoutes().stream().map(a -> {
				return "\n\thttp://localhost:" + httpServer.actualPort() + a.getPath();
			}).distinct().collect(Collectors.joining()));
		});
	}

}