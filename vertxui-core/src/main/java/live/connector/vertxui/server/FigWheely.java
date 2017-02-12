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
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Figwheely is for detecting javascript or file changes, and then triggering a
 * recompilation (if it is java for javascript compilation) and notify the
 * browser of these changes.
 * 
 * @author ng
 *
 */
public class FigWheely extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

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

	protected static void addFromVertX(FileSystem fileSystem, String url, String sourcePath, VertxUI handler) {
		fileSystem.readDir(sourcePath, files -> {
			for (String file : files.result()) {
				File jfile = new File(file);
				if (jfile.isDirectory()) {
					addFromVertX(fileSystem, url, jfile.getAbsolutePath(), handler);
				} else {
					// note that url is NOT changed in the loop
					addFile(jfile, url).handler = handler;
				}
			}
		});
	}

	protected static void addFromStaticHandler(FileSystem fileSystem, String root, String url) {
		fileSystem.readDir(root, items -> {
			if (items.result() == null) {
				return;
			}
			for (String item : items.result()) {
				File file = new File(item);
				if (file.isFile()) {
					// log.info("adding " + url + " for file=" + file);
					FigWheely.addFile(file, url + file.getName());
				} else {
					addFromStaticHandler(fileSystem, item, url + file.getName() + "/");
				}
			}
		});
	}

	/**
	 * Serve the root folder as static handler, but with notifications to the
	 * browser if folder change. Does not work when figwheely wasn't started
	 * before, so there is no performance loss if you leave this on.
	 * 
	 */
	public static Handler<RoutingContext> staticHandler(String root, String urlWithoutAsterix) {
		// log.info("creating figwheely static handler, started=" +
		// FigWheely.started);
		if (FigWheely.started) {
			FigWheely.addFromStaticHandler(Vertx.factory.context().owner().fileSystem(), root, urlWithoutAsterix);
		}
		return StaticHandler.create(root);
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
						log.info("Changed: target-url=" + watchable.url + "  file=" + watchable.file.getName());
						watchable.lastModified = watchable.file.lastModified();
						try {
							if (watchable.handler != null) {
								watchable.handler.translate();
							}
							// log.info("url=" + url);
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								vertx.eventBus().send((String) obj, "reload: " + watchable.url);
							}
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
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

	// Internet Explorer 11 does not have .endsWith()
	private static final String script = "function endsWith(str, suffix) {return str.indexOf(suffix, str.length - suffix.length) !== -1;}"
			+ "new WebSocket('ws://localhost:" + port + "/" + url
			+ "').onmessage = function(m) {console.log(m.data);removejscssfile(m.data.substr(8));};                                         \n "
			+ "console.log('FigWheely started');                                                                                \n "
			+ "function removejscssfile(filename){                 \n"
			+ "if (endsWith(filename,'js')) filetype='js'; else filetype='css';         "
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

	/**
	 * Create handler which serves the figwheely javascript. Also turns on the
	 * wheel of figwheely.
	 */
	public static Handler<RoutingContext> create() {
		if (!started) {
			started = true;
			Vertx.currentContext().owner().deployVerticle(FigWheely.class.getName());
		}
		return context -> {
			context.response().end(FigWheely.script);
		};
	}

}
