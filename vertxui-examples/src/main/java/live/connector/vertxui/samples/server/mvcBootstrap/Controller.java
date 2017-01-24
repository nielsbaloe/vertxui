package live.connector.vertxui.samples.server.mvcBootstrap;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.samples.client.mvcBootstrap.View;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.transport.Pojofy;

public class Controller extends AbstractVerticle {

	// private final static Logger log =
	// Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		router.route(View.totalsUrl).handler(Pojofy.ajax(null, this::getTotals));
		router.route(View.groceryUrl).handler(Pojofy.ajax(null, this::getGrocery));
		router.route(View.billsUrl).handler(Pojofy.ajax(null, this::getBills));

		AllExamplesServer.startWarAndServer(View.class, router, server);
	}

	public Totals getTotals(String empty, RoutingContext context) {
		Totals result = new Totals();
		result.totals = new HashMap<>();
		result.totals.put(Bills.Name.Linda, 0.0);
		result.totals.put(Bills.Name.Niels, 0.0);
		return result;
	}

	public Grocery getGrocery(String empty, RoutingContext context) {
		Grocery result = new Grocery();

		return result;
	}

	public Bills getBills(String empty, RoutingContext context) {
		Bills result = new Bills();

		return result;
	}

}