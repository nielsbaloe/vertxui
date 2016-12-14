package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teavm.tooling.TeaVMToolException;

import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class FigWheelyVertX extends AbstractVerticle {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * Override target-dir when necessary, default is "target/classes/".
	 */
	public static String buildDir = "target/classes/";
	public static int port = 8090;
	protected static boolean started = false;
	protected static Router router;
	private static List<Watchable> watchables = new ArrayList<>();

	private static class Watchable {
		long lastModified;
		File file;
		Handler<RoutingContext> handler;
		int urlNumber;

		@Override
		public String toString() {
			return new Gson().toJson(this).toString();
		}
	}

	protected static void add(String localFile, Handler<RoutingContext> handler) {
		File file = new File(localFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("failed to load " + localFile);
		}
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.handler = handler;
		watchable.urlNumber = router.getRoutes().size();
		watchables.add(watchable);
	}

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
		}).listen(port, listenHandler -> {
			if (listenHandler.failed()) {
				LOGGER.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0); // stop on startup error
			}
		});

		vertx.executeBlocking(future -> {
			while (true) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				for (Watchable watchable : watchables) {
					if (watchable.file.lastModified() != watchable.lastModified) {
						watchable.lastModified = watchable.file.lastModified();
						try {
							if (watchable.handler != null && watchable.handler instanceof VertxUI) {
								((VertxUI) watchable.handler).translate();
							}
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								vertx.eventBus().send((String) obj,
										"reload: " + router.getRoutes().get(watchable.urlNumber).getPath());
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

}
