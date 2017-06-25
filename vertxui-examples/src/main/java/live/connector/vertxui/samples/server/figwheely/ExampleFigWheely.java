package live.connector.vertxui.samples.server.figwheely;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.FigWheelyClient;
import live.connector.vertxui.samples.client.figwheely.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.FigWheelyServer;

public class ExampleFigWheely extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Start figwheely
		// Serve the javascript for figwheely (and turn it on too)
		Router router = Router.router(vertx);
		router.get(FigWheelyClient.urlJavascript).handler(FigWheelyServer.create());

		// Serve folder assets-figwheely, and notify
		// clients of changes if figwheely is started (otherwise it is just a
		// normal StaticHandler).
		String url = "/sourcez/";
		router.get(url + "*").handler(FigWheelyServer.staticHandler("assets/figwheely", url));

		AllExamplesServer.start(Client.class, router);
	}

}
