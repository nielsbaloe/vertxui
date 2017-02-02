package live.connector.vertxui.samples.server.todomvc;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.todomvc.View;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.FigStaticHandler;

public class Server extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		String url = "/css/";
		router.get(url + "*").handler(FigStaticHandler.create("assets/todos", url));

		AllExamplesServer.startWarAndServer(View.class, router, server);
	}
}