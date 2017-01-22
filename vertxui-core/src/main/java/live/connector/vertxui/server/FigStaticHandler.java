package live.connector.vertxui.server;

import java.io.File;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * A thin wrapper around StaticHandler that will reload files (like css and jpg)
 * in the browser when FigWheely is turned on, otherwise it is just a regular
 * StaticHandler.
 * 
 * @author ng
 *
 */
public class FigStaticHandler {

	// private final static Logger log =
	// Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static Handler<RoutingContext> create(String root, String url) {
		// log.info("creating figwheely static handler, started=" +
		// FigWheely.started);
		if (FigWheely.started) {
			registerFolder(Vertx.factory.context().owner().fileSystem(), root, url);
		}
		return StaticHandler.create(root);
	}

	private static void registerFolder(FileSystem fileSystem, String root, String url) {
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
					registerFolder(fileSystem, item, url + file.getName() + "/");
				}
			}
		});
	}

}
