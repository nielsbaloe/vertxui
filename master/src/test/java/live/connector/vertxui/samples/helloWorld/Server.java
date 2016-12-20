package live.connector.vertxui.samples.helloWorld;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.samples.ServerOnePage;

public class Server extends ServerOnePage {

	public Server() {
		super(Client.class, false);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
