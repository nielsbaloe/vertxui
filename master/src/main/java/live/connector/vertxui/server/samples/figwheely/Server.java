package live.connector.vertxui.server.samples.figwheely;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.figwheely.Client;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.StaticHandlery;
import live.connector.vertxui.server.VertxUI;

public class Server extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		FigWheely.with(router);
		try {
			router.route(Client.figLocation).handler(a -> {
				a.response().end(FigWheely.script);
			});
			router.route("/client/*").handler(VertxUI.with(Client.class));
			router.route("/sources/*").handler(StaticHandlery.create("sources", "/sources/"));
		} catch (IOException | IllegalArgumentException e) {
			// Error on first-time java-2-javascript translation: stop deploying
			log.log(Level.SEVERE, "Startup error", e);
			// stop on startup error
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					vertx.deploymentIDs().forEach(vertx::undeploy);
					vertx.close();
				}
			});
			System.exit(0);
		}
		HttpServerOptions serverOptions = new HttpServerOptions().setCompressionSupported(true);
		HttpServer server = vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen(80,
				listenHandler -> {
					if (listenHandler.failed()) {
						log.log(Level.SEVERE, "Startup error", listenHandler.cause());
						// stop on startup error
						Runtime.getRuntime().addShutdownHook(new Thread() {
							public void run() {
								vertx.deploymentIDs().forEach(vertx::undeploy);
								vertx.close();
							}
						});
						System.exit(0);
					}
				});
		log.info("Initialised:" + router.getRoutes().stream().map(a -> {
			return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
		}).collect(Collectors.joining()));
	}

}