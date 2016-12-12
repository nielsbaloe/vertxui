package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

public class FigWheely extends AbstractVerticle {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * Override target-dir when necessary, default is "target/classes/".
	 */
	public static String buildDir = "target/classes/";

	private static class Watchable {
		long lastModified;
		File file;
		VertxUI vertxUI;
		String url;
	}

	private static List<Watchable> watchables = new ArrayList<>();

	public static void add(Router router, String url, VertxUI vertxUI, Vertx vertx) {
		router.route(url).handler(vertxUI);

		String location = buildDir + vertxUI.classs.getCanonicalName().replace(".", "/") + ".class";
		File file = new File(location);
		if (!file.exists()) {
			throw new IllegalArgumentException(
					"buildDir is probably not '" + buildDir + "', tried to load " + location);
		}
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.vertxUI = vertxUI;
		watchable.url = url;
		watchables.add(watchable);

		if (started == false) {
			vertx.deployVerticle(FigWheely.class.getName());
		}
		vertxUI.debug = true;
	}

	protected static boolean started = false;

	@Override
	public void start() {
		started = true;

		final String browserIds = "figwheelyEventBus";
		vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {
			@Override
			public void handle(final ServerWebSocket ws) {
				if (!ws.path().equals("/")) {
					ws.reject();
					return;
				}
				final String id = ws.textHandlerID();
				vertx.sharedData().getLocalMap(browserIds).put(id, "whatever");
				ws.closeHandler(data -> {
					vertx.sharedData().getLocalMap(browserIds).remove(id);
				});
			}
		}).listen(8090, listenHandler -> {
			if (listenHandler.failed()) {
				LOGGER.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0); // stop on startup error
			}
		});

		vertx.executeBlocking(future -> {
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				for (Watchable watchable : watchables) {
					if (watchable.file.lastModified() != watchable.lastModified) {
						watchable.lastModified = watchable.file.lastModified();
						try {
							watchable.vertxUI.translate();
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								vertx.eventBus().send((String) obj, "reload: " + watchable.url);
							}
						} catch (IOException | TeaVMToolException e) {
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								vertx.eventBus().send((String) obj, "error: " + e.getMessage());
							}
						}
					}
				}
			}
		}, result -> {
		});
	}

	public static final String script = "var figwheely=new WebSocket('ws://localhost:8090');"
			+ "figwheely.onmessage = function(m){console.log(m.data);}";

}
