package live.connector.vertxui.server.samples.helloWorld;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.helloWorld.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleHelloWorld extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Wait and do some server stuff for AJAX
		router.post(Client.url).handler(handle -> {
			vertx.setTimer(1000, l -> {
				handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
			});
		});

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
