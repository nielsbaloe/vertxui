package live.connector.vertxui.samples.server.helloWorld;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.helloWorld.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.VertxUI;

public class ExampleHelloWorld extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Wait and do some server stuff for AJAX
		Router router = Router.router(vertx);
		router.post(Client.url).handler(handle -> {
			vertx.setTimer(1000, l -> {
				handle.response().putHeader("Content-Type", "text/plain; charset=" + VertxUI.charset);
				handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
			});
		});

		AllExamplesServer.start(Client.class, router);
	}

}
