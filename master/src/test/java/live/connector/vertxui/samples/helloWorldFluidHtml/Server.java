package live.connector.vertxui.samples.helloWorldFluidHtml;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.core.VertxUI;

public class Server extends AbstractVerticle {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) throws InterruptedException {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() throws IOException, TeaVMToolException {
		Router router = Router.router(vertx);
		router.route("/client").handler(new VertxUI(Client.class, false, true));
		router.route("/server").handler(handle -> {
			vertx.setTimer(1000, l -> {
				handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
			});
		});
		HttpServerOptions serverOptions = new HttpServerOptions().setCompressionSupported(true);
		HttpServer server = vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen(80,
				listenHandler -> {
					if (listenHandler.failed()) {
						LOGGER.log(Level.SEVERE, "Startup error", listenHandler.cause());
						System.exit(0); // stop on startup error
					}
				});
		LOGGER.info("Initialised:" + router.getRoutes().stream().map(a -> {
			return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
		}).collect(Collectors.joining()));
	}

}