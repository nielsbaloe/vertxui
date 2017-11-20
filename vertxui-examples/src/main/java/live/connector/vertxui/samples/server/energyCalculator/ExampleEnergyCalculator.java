package live.connector.vertxui.samples.server.energyCalculator;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.energyCalculator.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;

public class ExampleEnergyCalculator extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		AllExamplesServer.start(Client.class, router);
	}

}
