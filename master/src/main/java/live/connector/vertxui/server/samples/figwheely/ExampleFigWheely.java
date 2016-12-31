package live.connector.vertxui.server.samples.figwheely;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.client.samples.figwheely.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleFigWheely extends AllExamplesServer {

	public ExampleFigWheely() {
		super(Client.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
