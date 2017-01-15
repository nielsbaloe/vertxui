package live.connector.vertxui.server;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class VertxUITester {

	// private static final JBrowserDriver driver = new JBrowserDriver();

	public static void create(Class<?> classs) throws ScriptException, FileNotFoundException {
		// Vertx vertx = Vertx.factory.vertx();
		// Router router = Router.router(vertx);
		// HttpServer server = vertx.createHttpServer(new
		// HttpServerOptions().setCompressionSupported(true));
		// router.get("/*").handler(VertxUI.with(classs, "/"));
		// server.requestHandler(router::accept).listen(80);
		//
		// driver.get("http://localhost/");
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		// public void run() {
		// Context context = Vertx.currentContext();
		// if (context == null) {
		// return;
		// }
		// Vertx vertx = context.owner();
		// vertx.deploymentIDs().forEach(vertx::undeploy);
		// vertx.close();
		//
		// driver.quit();
		// }
		// });

		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine engine = engineManager.getEngineByName("nashorn");
		engine.eval(new FileReader("war/a/a.nocache.js"));
		engine.eval("function sum(a, b) { return a + b; }");
		System.out.println(engine.eval("sum(1, 2);"));
	}

}
