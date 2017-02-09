package live.connector.vertxui.samples.server.todomvc;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import live.connector.vertxui.samples.client.figwheely.Client;
import live.connector.vertxui.samples.client.todomvc.View;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.VertxUI;

public class Server extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Serve the javascript for FigWheely

		// This application is also an example for using an existing index.html.
		// The only difference is here at startup time: call VertxUI.with()
		// with url=null (2nd parameter) so it only compiles, give false
		// as last parameter so that there is no index.html generated (not
		// necessary), and then server folder /a/ manually
		boolean debug = true;
		VertxUI.with(View.class, null, debug, false);
		router.get("/a/*").handler(StaticHandler.create(VertxUI.getTargetFolder(debug) + "/a"));
		router.get("/*").handler(FigWheely.staticHandler("assets/todos", "/"));
		router.get(Client.figLocation).handler(FigWheely.create());
		AllExamplesServer.startWarAndServer2(router, server);
	}

}