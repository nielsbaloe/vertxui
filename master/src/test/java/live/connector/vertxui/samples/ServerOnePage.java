package live.connector.vertxui.samples;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.core.VertxUI;

public class ServerOnePage extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private Class<?> classs;

	public ServerOnePage(Class<?> classs) {
		this.classs = classs;
	}

	@Override
	public void start() {
		try {
			Router router = Router.router(vertx);
			router.route("/client").handler(new VertxUI(classs, true));
			router.route("/server").handler(handle -> {
				vertx.setTimer(1000, l -> {
					handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
				});
			});
			HttpServerOptions serverOptions = new HttpServerOptions().setCompressionSupported(true);
			HttpServer server = vertx.createHttpServer(serverOptions).requestHandler(router::accept).listen(80,
					listenHandler -> {
						if (listenHandler.failed()) {
							log.log(Level.SEVERE, "Startup error", listenHandler.cause());
							System.exit(0); // stop on startup error
						}
					});
			log.info("Initialised:" + router.getRoutes().stream().map(a -> {
				return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
			}).collect(Collectors.joining()));
		} catch (IOException | TeaVMToolException e) {
			e.printStackTrace();
			System.exit(0); // stop on startup error
		}

	}

}