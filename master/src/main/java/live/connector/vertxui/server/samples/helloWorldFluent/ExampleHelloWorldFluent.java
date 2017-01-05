package live.connector.vertxui.server.samples.helloWorldFluent;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.chatEventBus.Dto;
import live.connector.vertxui.client.samples.helloWorldFluentHtml.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;
import live.connector.vertxui.server.transport.Pojofy;

public class ExampleHelloWorldFluent extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Hello world examples: wait and do some server stuff for AJAX
		router.post(Client.url).handler(Pojofy.ajax(String.class, (r, m) -> {
			// TODO use setTimeOut
			// vertx.setTimer(1000, l -> {
			return "Hello, " + r.getHeader("User-Agent");
			// });
		}));

		// extra: pojo example
		router.post(Client.urlPojo).handler(Pojofy.ajax(Dto.class, (r, m) -> {
			log.info("received a pojo from the client: color=" + m.color);
			return new Dto("purple");
		}));

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
