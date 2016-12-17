package live.connector.vertxui.core;

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

	public static Handler<RoutingContext> create(String root) {
		if (FigWheelyVertX.started) {
			registerFolder(Vertx.factory.context().owner().fileSystem(), root);
		}
		return io.vertx.ext.web.handler.StaticHandler.create(root);
	}

	private static void registerFolder(FileSystem fileSystem, String root) {
		fileSystem.readDir(root, items -> {
			if (items.result() == null) {
				return;
			}
			for (String item : items.result()) {
				File file = new File(item);
				if (file.isFile()) {
					String url = item.substring(item.indexOf(root)).replace("\\", "/");
					// log.info("adding " + url + " for file=" + file);
					FigWheelyVertX.addFile(file, url);
				} else {
					registerFolder(fileSystem, item);
				}
			}
		});
	}

}
