package live.connector.vertxui.server;

import java.io.File;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.RoutingContext;

/**
 * A thin wrapper around StaticHandler that will reload files (like css and jpg)
 * in the browser when FigWheely was turned on first.
 * 
 * @author ng
 *
 */
public class StaticHandlery {

	public static Handler<RoutingContext> create(String root, String url) {
		if (FigWheelyVertX.started) {
			registerFolder(Vertx.factory.context().owner().fileSystem(), root, url);
		}
		return io.vertx.ext.web.handler.StaticHandler.create(root);
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
					FigWheelyVertX.addFile(file, url + file.getName());
				} else {
					registerFolder(fileSystem, item, url + file.getName() + "/");
				}
			}
		});
	}

}
