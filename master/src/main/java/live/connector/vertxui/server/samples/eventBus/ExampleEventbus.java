package live.connector.vertxui.server.samples.eventBus;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleEventbus extends AllExamplesServer {

	public ExampleEventbus() {
		super(live.connector.vertxui.client.samples.eventBus.Client.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
