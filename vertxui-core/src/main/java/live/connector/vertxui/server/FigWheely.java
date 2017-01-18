package live.connector.vertxui.server;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.RoutingContext;

public class FigWheely extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static String buildDir = "target/classes";
	public static String url = "figwheelySocket";
	public static int port = 8090;

	protected static boolean started = false;

	private static final String browserIds = "figwheelyEventBus";
	private static List<Watchable> watchables = new ArrayList<>();

	private static class Watchable {
		long lastModified;
		File file;
		String url;
		VertxUI handler;

		@Override
		public String toString() {
			return new Gson().toJson(this).toString();
		}
	}

	protected static Watchable addFile(File file, String url) {
		// log.info("Adding: file=" + file + " url=" + url);
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.url = url;
		watchables.add(watchable);
		return watchable;
	}

	protected static void addVertX(File file, String url, VertxUI handler) {
		addFile(file, url).handler = handler;
	}

	@Override
	public void start() {
		HttpServer server = vertx.createHttpServer();
		server.websocketHandler(webSocket -> {
			if (!webSocket.path().equals("/" + url)) {
				webSocket.reject();
				return;
			}
			final String id = webSocket.textHandlerID();
			// log.info("welcoming " + id);
			vertx.sharedData().getLocalMap(browserIds).put(id, "whatever");
			webSocket.closeHandler(data -> {
				vertx.sharedData().getLocalMap(browserIds).remove(id);
			});
		});
		server.listen(port, listenHandler -> {
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
						log.info("Changed: " + watchable.url);
						watchable.lastModified = watchable.file.lastModified();
						try {
							if (watchable.handler != null) {
								watchable.handler.sychronousReTranslate();
							}
							// log.info("url=" + url);
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								log.info("reload: " + url);
								vertx.eventBus().send((String) obj, "reload: " + watchable.url);
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

	public static final String script = "new WebSocket('ws://localhost:" + port + "/" + url
			+ "').onmessage = function(m) {console.log(m.data);removejscssfile(m.data.substr(8));};                                         \n "
			+ "console.log('FigWheely started');                                                                                \n "
			+ "function removejscssfile(filename){                 \n"
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

	public static Handler<RoutingContext> create() {
		if (started) {
			throw new IllegalArgumentException("Can only start once");
		}
		started = true;
		Vertx.currentContext().owner().deployVerticle(FigWheely.class.getName());
		return a -> {
			a.response().end(FigWheely.script);
		};
	}

}
