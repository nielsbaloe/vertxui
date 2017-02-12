package live.connector.vertxui.samples.server.helloWorldFluent;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.Dto;
import live.connector.vertxui.samples.client.helloWorldFluentHtml.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;
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

		// Wait and do some server stuff for AJAX
		router.post(Client.url).handler(Pojofy.ajax(String.class, (dto, context) -> {
			// Without timer, write 'return "Hello,".....' because strings are
			// returned as is.
			vertx.setTimer(1000, l -> {
				context.response().end("Hello, " + context.request().getHeader("User-Agent"));
			});
			return null; // null means: we take care of the request() ourselves
		}));

		// extra: pojo example. Here the Pojofy.ajax() makes more sense!
		router.post(Client.urlPojo).handler(Pojofy.ajax(Dto.class, (dto, context) -> {
			log.info("Received a pojo from the client: color=" + dto.color);
			return new Dto("purple");
		}));

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}
