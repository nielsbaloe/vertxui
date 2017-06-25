package live.connector.vertxui.samples.server.mvcBootstrap;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.samples.client.mvcBootstrap.Store;
import live.connector.vertxui.samples.client.mvcBootstrap.View;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.transport.Pojofy;

public class ServerBootstrap extends AbstractVerticle {

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
		// Fake initial data
		bills.all = new ArrayList<>();
		Bill bill = new Bill(Name.Niels, 55, "Weekend shopping", new Date());
		bills.all.add(bill);
		grocery.all = new ArrayList<>();
		grocery.all.add("Chocolate milk");

		// Route all URLs
		Router router = Router.router(vertx);
		router.get(Store.totalsUrl).handler(Pojofy.ajax(null, this::getTotals));
		router.get(Store.billsUrl).handler(Pojofy.ajax(null, this::getBills));
		router.post(Store.billsUrl).handler(Pojofy.ajax(Bill.class, this::addBill));

		router.get(Store.groceryUrl).handler(Pojofy.ajax(null, this::getGrocery));
		router.post(Store.groceryUrl).handler(Pojofy.ajax(null, this::addGrocery));
		router.delete(Store.groceryUrl).handler(Pojofy.ajax(null, this::delGrocery));

		AllExamplesServer.start(View.class, router);
	}

	public Totals getTotals(String __, RoutingContext context) {
		Totals result = new Totals();
		result.all = new HashMap<>();
		result.all.put(Name.Niels, bills.all.stream().mapToDouble(t -> t.who == Name.Niels ? t.amount : 0.0).sum());
		result.all.put(Name.Linda, bills.all.stream().mapToDouble(t -> t.who == Name.Linda ? t.amount : 0.0).sum());
		return result;
	}

	public Grocery getGrocery(String __, RoutingContext context) {
		return grocery;
	}

	public void addGrocery(String text, RoutingContext context) {
		grocery.all.add(text);
	}

	public void delGrocery(String text, RoutingContext context) {
		grocery.all.remove(text);
	}

	public Bills getBills(String empty, RoutingContext context) {
		Collections.sort(bills.all);
		return bills;
	}

	public long addBill(Bill bill, RoutingContext context) {
		bill.id = bills.all.size();
		bills.all.add(bill);
		return bill.id;
	}

}