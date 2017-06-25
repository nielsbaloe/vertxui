package live.connector.vertxui.samples.server.chatSockjs;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import live.connector.vertxui.samples.client.Dto;
import live.connector.vertxui.samples.client.chatSockjs.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.transport.Pojofy;

public class ExampleChatSockjs extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Chat with SockJS
		Router router = Router.router(vertx);
		List<String> ids = new ArrayList<>();
		router.route("/chatSockjs/*").handler(SockJSHandler.create(vertx).socketHandler(socket -> {
			final String id = socket.writeHandlerID();
			ids.add(id); // entering
			socket.endHandler(data -> {
				ids.remove(id); // leaving
			});
			socket.handler(buffer -> { // receiving

				// extra: pojo example
				if (Pojofy.socket(socket, Client.urlPojo, buffer, Dto.class, this::serviceDoSomething)) {
					return;
				}

				ids.forEach(i -> vertx.eventBus().send(i, buffer)); // broadcasting
				// to reply to one: socket.write()

			});
		}));

		AllExamplesServer.start(Client.class, router);
	}

	public Dto serviceDoSomething(Dto received, JsonObject headers) {
		log.info("Extra example: received a dto with action=" + headers.getString("action") + " and color="
				+ received.color);
		return new Dto("brown");
	}

}
