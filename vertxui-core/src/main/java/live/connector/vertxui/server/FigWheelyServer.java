package live.connector.vertxui.server;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * You can also send a message to the browser, for example:
 * vertx.eventBus().publish(FigWheely.figNotify, "bla die bla");
 * 
 * @author ng
 *
 */
public class FigWheelyServer extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static String urlSocket = "figwheelySocket";
	public static int portSocket = 8090;

	protected static boolean started = false;

	private final static String browserIds = "figwheelyEventBus";
	public final static String figNotify = "figNotify";
	private static List<Watchable> watchables = new ArrayList<>();

	private static class Watchable {

		public Watchable(String url, String root, VertxUI handler) {
			this.url = url;
			this.root = root;
			this.handler = handler;
			this.lastState = getCurrentState();
		}

		public Watchable(String url, String root) {
			this.url = url;
			this.root = root;
			this.lastState = getCurrentState();
		}

		String url;
		String root;
		VertxUI handler;
		String lastState;

		public String getCurrentState() {

			// file
			File file = new File(root);
			if (file.isFile()) {
				return file.getAbsolutePath() + file.lastModified();
			}

			// folder
			StringBuilder result = new StringBuilder();
			getContent(Vertx.currentContext().owner().fileSystem(), root, result);
			return result.toString();
		}

		private void getContent(FileSystem fileSystem, String path, StringBuilder result) {
			fileSystem.readDirBlocking(path).forEach(file -> {
				File jfile = new File(file);
				if (jfile.isDirectory()) {
					getContent(fileSystem, jfile.getAbsolutePath(), result);
				} else {
					result.append(jfile.getAbsolutePath());
					result.append(jfile.lastModified());
				}
			});
		}

	}

	/**
	 * Serve the root folder as static handler, but with notifications to the
	 * browser if folder change. Does not work when figwheely wasn't started before,
	 * so there is no performance loss if you leave this on.
	 * 
	 * @param root              the root folder
	 * @param urlWithoutAsterix the url but without the asterix at the end
	 * @return a static file handler with figwheely support
	 */
	public static Handler<RoutingContext> staticHandler(String root, String urlWithoutAsterix) {
		// log.info("creating figwheely static handler, started=" +
		// FigWheely.started);
		if (FigWheelyServer.started) {
			FigWheelyServer.addFromStaticHandler(Vertx.factory.context().owner().fileSystem(), root, urlWithoutAsterix,
					root);
		}

		return StaticHandler.create(root).setCachingEnabled(false).setDefaultContentEncoding(VertxUI.charset);
	}

	private static void addFromStaticHandler(FileSystem fileSystem, String sourcePath, String url, String rootroot) {
		fileSystem.readDir(sourcePath, files -> {
			if (files.result() == null) {
				return;
			}
			for (String item : files.result()) {
				File file = new File(item);
				if (file.isFile()) {
					watchables.add(new Watchable(url + file.getName(), item));
				} else {
					addFromStaticHandler(fileSystem, item, url + file.getName() + "/", rootroot);
				}
			}
		});
	}

	@Override
	public void start() {
		HttpServer server = vertx.createHttpServer();
		server.websocketHandler(webSocket -> {
			if (!webSocket.path().equals("/" + urlSocket)) {
				webSocket.reject();
				return;
			}
			final String id = webSocket.textHandlerID();
			// log.info("welcoming " + id);
			vertx.sharedData().getLocalMap(browserIds).put(id, "whatever");
			webSocket.closeHandler(data -> {
				vertx.sharedData().getLocalMap(browserIds).remove(id);
			});
			webSocket.handler(buffer -> {
				log.info(buffer.toString());
			});
		});
		server.listen(portSocket, listenHandler -> {
			if (listenHandler.failed()) {
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
				System.exit(0); // stop on startup error
			}
		});

		// Ensure that even outside this class people can push messages to the
		// browser
		vertx.eventBus().consumer(figNotify, message -> {
			for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
				vertx.eventBus().send((String) obj, message.body());
			}
		});

		vertx.setPeriodic(250, id -> {
			if (VertxUI.isCompiling()) {
				return;
			}
			for (Watchable watchable : watchables) {
				String currentState = watchable.getCurrentState();
				if (watchable.lastState.equals(currentState)) {
					continue;
				}
				log.info("Changed: " + watchable.root);
				watchable.lastState = currentState;
				if (watchable.handler == null) {
					log.info("Skipping recompile, no handler found.");
				} else {
					// Recompile
					boolean succeeded = watchable.handler.translate();
					if (succeeded == false) {
						vertx.eventBus().publish(figNotify,
								"unsuccessfull rebuild for url=" + watchable.url + " root=" + watchable.root);
					}
				}
				vertx.eventBus().publish(figNotify, "reload: " + watchable.url);
			}
		});
	}

	// Internet Explorer 11 does not have .endsWith()
	private static final String script = "function endsWith(str, suffix) {return str.indexOf(suffix, str.length - suffix.length) !== -1;}"
			+ "var _fig = new WebSocket('ws://localhost:" + portSocket + "/" + urlSocket + "'); "
			+ "_fig.onmessage = function(m) {console.log(m.data);removejscssfile(m.data.substr(8));};                                         \n "
			+ "console.log('FigWheely loaded');                                                                                \n "
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
	 * Create handler which serves the figwheely javascript. Also turns on the wheel
	 * of figwheely.
	 * 
	 * @return the static handler which servers the necessary javascript.
	 */
	public static Handler<RoutingContext> create() {
		if (!started) {
			started = true;
			Vertx.currentContext().owner().deployVerticle(FigWheelyServer.class.getName());
		}
		return context -> {
			context.response().putHeader("Content-Type", "text/javascript; charset=utf-8").end(FigWheelyServer.script);
		};
	}

	public static void addWatchable(String url, String root, VertxUI handler) {
		watchables.add(new Watchable(url, root, handler));
	}

}
