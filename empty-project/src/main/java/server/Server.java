package server;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.Client;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.VertxUI;

public class Server extends AbstractVerticle {
	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		boolean debug = true;

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

		Router router = Router.router(vertx);
		router.get(Client.url)
				.handler(handle -> handle.response().putHeader("Content-Type", "text/html").end("Hi from server"));
		if (debug) {
			router.get("/figwheely.js").handler(FigWheely.create());
		}
		router.get("/*").handler(VertxUI.with(Client.class, "/", debug, true));

		vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true)).requestHandler(router::accept)
				.listen(80, listenHandler -> {
					if (listenHandler.failed()) {
						log.log(Level.SEVERE, "Startup error", listenHandler.cause());
						System.exit(0); // stop on startup error
					}
				});
	}

}
