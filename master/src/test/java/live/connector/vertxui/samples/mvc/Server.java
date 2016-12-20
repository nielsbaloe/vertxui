package live.connector.vertxui.samples.mvc;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.samples.ServerOnePage;

public class Server extends ServerOnePage {

	public Server() {
		super(View.class, true);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		super.start();

	}

}
