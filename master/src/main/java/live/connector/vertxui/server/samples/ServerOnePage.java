package live.connector.vertxui.server.samples;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.figwheely.Client;
import live.connector.vertxui.server.FigStaticHandler;
import live.connector.vertxui.server.FigWheely;
import live.connector.vertxui.server.VertxUI;

/**
 * This server serves one VertxUI page, and replies something as a server -
 * ideal as example and for testing purposes.
 * 
 * @author ng
 *
 */
public class ServerOnePage extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	// public static void main(String[] args) {
	// Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	// }

	private Class<?> classs;

	public ServerOnePage(Class<?> classs) {
		this.classs = classs;
	}

	@Override
	public void start() {
		try {
			Router router = Router.router(vertx);
			HttpServerOptions serverOptions = new HttpServerOptions().setCompressionSupported(true);
			HttpServer server = vertx.createHttpServer(serverOptions);

			// Start figwheely and server the javascript file
			FigWheely.with(server, router);
			router.get(Client.figLocation).handler(a -> {
				a.response().end(FigWheely.script);
			});

			// Serve folder sources (for figwheely examples)
			router.get("/sources/*").handler(FigStaticHandler.create("sources", "/sources/"));

			// Server the main application class with a fancy failure handler.
			router.get("/*").handler(VertxUI.with(classs)).failureHandler(fail -> {
				fail.response().end(
						"<html style='width:100%;height:100%'><iframe src=//codepen.io/waddington/full/cdzuB style='width:100%;height:100%'></html>");
			});

			// Do some server stuff for the hello world examples
			router.post("/server").handler(handle -> {
				vertx.setTimer(1000, l -> {
					handle.response().end("Hello, " + handle.request().getHeader("User-Agent"));
				});
			});

			// Start the server
			server.requestHandler(router::accept).listen(80, listenHandler -> {
				if (listenHandler.failed()) {
					log.log(Level.SEVERE, "Startup error", listenHandler.cause());
					// stop on startup error
					Runtime.getRuntime().addShutdownHook(new Thread() {
						public void run() {
							vertx.deploymentIDs().forEach(vertx::undeploy);
							vertx.close();
						}
					});
					System.exit(0);
				}
			});
			log.info("Initialised:" + router.getRoutes().stream().map(a -> {
				return "\n\thttp://localhost:" + server.actualPort() + a.getPath();
			}).collect(Collectors.joining()));

		} catch (IOException e) {
			e.printStackTrace();
			vertx.close(); // stop on startup error
		}
	}

}