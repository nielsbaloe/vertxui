package live.connector.vertxui.server.samples.helloWorld;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.client.samples.helloWorld.Client;
import live.connector.vertxui.server.samples.ServerOnePage;

public class Server extends ServerOnePage {

	public Server() {
		super(Client.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
