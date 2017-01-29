package live.connector.vertxui.samples.server;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.figwheely.Client;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.VertxUI;

/**
 * @author ng
 *
 */
public class AllExamplesServer {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void startWarAndServer(Class<?> classs, Router router, HttpServer server) {

		// Make sure that when we exit, we do it properly.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Context context = Vertx.currentContext();
				if (context == null) {
					return;
				}
				Vertx vertx = context.owner();
				vertx.deploymentIDs().forEach(vertx::undeploy);
				vertx.close();
			}
		});

		// All examples: in debug mode, so with figwheely: serve the javascript
		router.get(Client.figLocation).handler(FigWheely.create());

		// All examples: the main compiled js
		router.get("/*").handler(VertxUI.with(classs, "/", true));

		// A fancy 404
		// .failureHandler(fail -> {
		// fail.response().end(
		// "<html style='width:100%;height:100%'><iframe
		// src=//codepen.io/waddington/full/cdzuB
		// style='width:100%;height:100%'></html>");
		// });

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