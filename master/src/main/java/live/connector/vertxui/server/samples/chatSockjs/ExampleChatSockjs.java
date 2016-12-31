package live.connector.vertxui.server.samples.chatSockjs;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleChatSockjs extends AllExamplesServer {

	public ExampleChatSockjs() {
		super(live.connector.vertxui.client.samples.chatSockjs.ExampleChatSockjs.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}
