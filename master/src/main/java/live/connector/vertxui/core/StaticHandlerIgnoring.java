package live.connector.vertxui.core;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.StaticHandler;

/**
 * A StaticHandler that cuts the "?...." part of a file location, interpreting
 * it as a clear-cache signal.
 * 
 * @author ng
 *
 */
public class StaticHandlerIgnoring extends StaticHandler {

	private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	// TODO refactor: create this class extending StaticHandler instead of the
	// creatort() call below

	public StaticHandlerIgnoring(io.vertx.ext.web.handler.StaticHandler delegate) {
		super(delegate);
	}

	/**
	 * Local file handler, replacement for StaticHandler (more or less). Only
	 * use in fluid notation.
	 * 
	 * @param file
	 *            a local file
	 * @return a handler
	 */
	public static Handler<RoutingContext> creatort(String file) {
		Handler<RoutingContext> reply = a -> {
			try {
				a.response().end(new String(Files.readAllBytes(new File(file).toPath())));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error while serving " + file, e);
			}
		};
		FigWheelyVertX.add(file, reply);
		return reply;
	}

}
