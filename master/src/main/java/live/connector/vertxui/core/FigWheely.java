package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teavm.tooling.TeaVMToolException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.samples.sockjs.Client;

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

	public static Handler<RoutingContext> add(String url, Class<Client> classs, boolean withHtml) {
		if (withHtml) {
			LOGGER.warning(
					"It does not make sense to use Figwheely when withHtml=true is set, because state (like shown html) is not hot swappable.");
		}
		VertxUI vertxUI = new VertxUI(classs, withHtml, true);

		addItem(url, buildDir + classs.getCanonicalName().replace(".", "/") + ".class", vertxUI);

		if (started == false) {
			Vertx.currentContext().owner().deployVerticle(FigWheely.class.getName());
		}
		return vertxUI;
	}

	public static Handler<RoutingContext> add(String url, String file) {
		addItem(url, file, null);
		return a -> {
			try {
				a.response().end(new String(Files.readAllBytes(new File(file).toPath())));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error while serving " + file, e);
			}
		};
	}

	private static void addItem(String url, String fileLocation, VertxUI vertxUI) {
		File file = new File(fileLocation);
		if (!file.exists()) {
			throw new IllegalArgumentException("failed to load " + fileLocation);
		}
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.vertxUI = vertxUI;
		watchable.url = url;
		watchables.add(watchable);
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
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				for (Watchable watchable : watchables) {
					if (watchable.file.lastModified() != watchable.lastModified) {
						watchable.lastModified = watchable.file.lastModified();
						try {
							if (watchable.vertxUI != null) {
								watchable.vertxUI.translate();
							}
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

	public static final String script = "new WebSocket('ws://localhost:8090').onmessage = function(m)"
			+ "{removejscssfile(m.data.substr(8));};                                          "
			+ "function removejscssfile(filename){                \n"
			+ "if (filename.endsWith('js')) filetype='js'; else filetype='css';         "
			+ "var el = (filetype=='js')? 'script':'link';                                             \n"
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
			+ "       parent.appendChild(script);      console.log('yes',filename,script);                     \n"
			+ "  }  } };                           ";

}
