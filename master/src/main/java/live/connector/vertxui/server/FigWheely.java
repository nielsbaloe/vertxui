package live.connector.vertxui.server;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

public class FigWheely extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * Override target-dir when necessary.
	 */
	public static String buildDir = "target/classes";
	public static boolean started = false;
	public static String url = "figwheelySocket";

	private static Router router;
	private static final String browserIds = "figwheelyEventBus";
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
		// log.info("add file " + file + " url="+url);
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.url = url;
		watchables.add(watchable);
	}

	public static boolean addVertX(File file, VertxUI handler) {
		// log.info("add vertx " + file);
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.handler = handler;
		watchable.urlNumber = router.getRoutes().size();
		watchables.add(watchable);
		return true;
	}

	/**
	 * Server bootstrap.
	 * 
	 * @param router
	 *            the router which will be watched
	 */
	public static void with(HttpServer server, Router router) {
		if (started) {
			throw new IllegalArgumentException("Can only start once");
		}
		started = true;
		FigWheely.router = router;

		Vertx vertx = Vertx.currentContext().owner();
		server.websocketHandler(new Handler<ServerWebSocket>() {
			@Override
			public void handle(final ServerWebSocket ws) {
				if (!ws.path().equals("/" + url)) {
					ws.reject();
					return;
				}
				final String id = ws.textHandlerID();
				// log.info("welcoming " + id);
				vertx.sharedData().getLocalMap(browserIds).put(id, "whatever");
				ws.closeHandler(data -> {
					vertx.sharedData().getLocalMap(browserIds).remove(id);
				});
			}
		});
		vertx.deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		vertx.executeBlocking(future -> {
			while (true) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				for (Watchable watchable : watchables) {
					if (watchable.file.lastModified() != watchable.lastModified) {
						// log.info("Changed: " + watchable.url);
						watchable.lastModified = watchable.file.lastModified();
						try {
							String url = null;
							if (watchable.handler != null) {
								watchable.handler.sychronousReTranslate();
								url = router.getRoutes().get(watchable.urlNumber).getPath();
								url += "a/a.nocache.js"; // GWT
							} else {
								url = watchable.url;
							}
							// log.info("url=" + url);
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								log.info("reload: " + url);
								vertx.eventBus().send((String) obj, "reload: " + url);
							}
						} catch (IOException | InterruptedException e) {
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

	public static final String script = "new WebSocket('ws://localhost:80/" + url
			+ "').onmessage = function(m) {console.log(m.data);removejscssfile(m.data.substr(8));};                                          "
			+ "console.log('FigWheely started');                                                                                 "
			+ "function removejscssfile(filename){                 "
			+ "if (filename.endsWith('js')) filetype='js'; else filetype='css';         "
			+ "var el = (filetype=='js')? 'script':'link';                                              "
			+ "var attr = (filetype=='js')? 'src':'href';                                  \n"
			+ "var all =document.getElementsByTagName(el);                               \n"
			+ "for (var i=all.length; i>=0; i--) {                                        \n"
			+ "   if (all[i] && all[i].getAttribute(attr)!=null && all[i].getAttribute(attr).indexOf(filename)!=-1) {"
			+ "       var parent=all[i].parentNode;                                          \n"
			+ "       parent.removeChild(all[i]);                                        \n"
			+ "       var script = document.createElement(el);                         \n"
			+ "		  if (filetype=='js') {                                                  \n"
			+ "      	  script.type='text/javascript'; script.src=filename;"
			+ "       } else {                                                        "
			+ "      	  script.rel='stylesheet'; script.href=filename+'?'+(new Date().getTime());"
			+ "       }                                                                     "
			+ "       parent.appendChild(script);   	                 \n" + "  }  } };                           ";

}
