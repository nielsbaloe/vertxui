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

public class FigWheelyVertX extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * Override target-dir when necessary, default is "target/classes/".
	 */
	public static String buildDir = "target/classes";
	public static int port = 8090;
	public static boolean started = false;
	protected static Router router;
	private static List<Watchable> watchables = new ArrayList<>();

	private static class Watchable {
		long lastModified;
		File file;
		String url;
		VertxUI handler;
		int urlNumber;

		@Override
		public String toString() {
			return new Gson().toJson(this).toString();
		}
	}

	protected static void addFile(File file, String url) {
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.url = url;
		watchables.add(watchable);
	}

	protected static void addVertX(VertxUI handler) {
		if (!started) {
			return;
		}
		handler.debug = true;
		if (handler.withHtml) {
			log.warning(
					"It does not make sense to use Figwheely when withHtml=true is set, because bootstrap (like shown html) is not hot swappable.");
		}
		String classFile = FigWheelyVertX.buildDir + "/" + handler.classs.getCanonicalName().replace(".", "/")
				+ ".class";
		File file = new File(classFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("please set FigWheelyVertX.buildDir, failed to load " + classFile);
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
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
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
							String url = null;
							if (watchable.handler != null) {
								watchable.handler.translate();
								url = router.getRoutes().get(watchable.urlNumber).getPath();
							} else {
								url = watchable.url;
							}
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								// LOGGER.info("reload: " + url);
								vertx.eventBus().send((String) obj, "reload: " + url);
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
