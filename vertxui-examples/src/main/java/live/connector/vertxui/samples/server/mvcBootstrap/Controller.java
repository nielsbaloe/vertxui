package live.connector.vertxui.samples.server.mvcBootstrap;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.samples.client.mvcBootstrap.View;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
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

	// Fake data
	private Bills bills = new Bills();
	private Grocery grocery = new Grocery();

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		router.get(View.totalsUrl).handler(Pojofy.ajax(null, this::getTotals));
		router.get(View.groceryUrl).handler(Pojofy.ajax(null, this::getGrocery));
		router.put(View.groceryUrl).handler(Pojofy.ajax(null, this::addGrocery));
		router.get(View.billsUrl).handler(Pojofy.ajax(null, this::getBills));
		router.put(View.billsUrl).handler(Pojofy.ajax(Bill.class, this::addBill));

		AllExamplesServer.startWarAndServer(View.class, router, server);

		// Fake data
		bills.bills = new ArrayList<>();
		for (int x = 0; x < 10; x++) {
			Bill bill = new Bill(Name.Niels, 2300, new Date());
			bills.bills.add(bill);
		}
		grocery.things = new ArrayList<>();
		grocery.things.add("Chocolate milk");
		grocery.things.add("Banana's");
	}

	public Totals getTotals(String __, RoutingContext context) {
		Totals result = new Totals();
		result.totals = new HashMap<>();
		result.totals.put(Name.Niels,
				bills.bills.stream().mapToDouble(t -> t.who == Name.Niels ? t.amount : 0.0).sum());
		result.totals.put(Name.Linda,
				bills.bills.stream().mapToDouble(t -> t.who == Name.Linda ? t.amount : 0.0).sum());
		return result;
	}

	public Grocery getGrocery(String __, RoutingContext context) {
		return grocery;
	}

	public void addGrocery(String text, RoutingContext context) {
		grocery.things.add(text);
	}

	public Bills getBills(String empty, RoutingContext context) {
		return bills;
	}

	public void addBill(Bill bill, RoutingContext context) {
		bills.bills.add(bill);
	}

}