package live.connector.vertxui.server.samples.chatEventBus;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleEventbus extends AllExamplesServer {

	public ExampleEventbus() {
		super(live.connector.vertxui.client.samples.chatEventBus.Client.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
