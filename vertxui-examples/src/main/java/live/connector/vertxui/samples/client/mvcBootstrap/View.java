package live.connector.vertxui.samples.client.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.Div;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;

public class View implements EntryPoint {

	/**
	 * A view on a string. Actually, an enum is cleaner, but just to demonstrate
	 * that a ViewOn a primitive doesn't need much more than this.
	 */
	private ViewOn<String> menuView;
	private ViewOn<Totals> totalsView;
	private ViewOn<Bills> billsView;
	private ViewOn<Grocery> groceryView;

	public static String totalsUrl = "/totals";
	public static String billsUrl = "/bills";
	public static String groceryUrl = "/grocery";

	public View() {
		// Head
		head.style("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css").scriptSync(
				"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");
		head.meta().attr(Att.charset, "utf-8");
		head.meta().attr(Att.name_, "viewport").attr(Att.content, "width=device-width, initial-scale=1");

		// Header
		body.h1(null, "Bills").classs("jumbotron text-center").id("titlerForJunitTest");

		// Menu
		Fluent container = body.nav("navbar navbar-inverse").div("container-fluid");
		menuView = container.div().add("home", s -> {
			Fluent result = Ul("nav navbar-nav");
			result.li(s.equals("home") ? "active" : null).a("Home", "#").click(this::menuHome);
			result.li(s.equals("bills") ? "active" : null).a("Bills", "#").click(this::menuBills);
			result.li(s.equals("grocery") ? "active" : null).a("Grocery", "#").click(this::menuGrocery);
			return result;
		});

		// Home
		totalsView = body.add(null, t -> {
			if (t == null) {
				return null;
			}
			Fluent result = Div("row");
			result.div("col-sm-5"); // left spacing

			// Middle column size 2, with a table in it.
			Fluent tbody = result.div("col-sm-2").table("table table-condensed").tbody();

			// two td's in one tr
			Fluent tr = null;
			for (Name key : t.totals.keySet()) {
				if (tr == null) {
					tr = tbody.tr();
					tr.td().inner(key.name() + " ").span("badge", t.totals.get(key) + "");
				} else {
					tr.td().inner(key.name() + " ").span("badge", t.totals.get(key) + "");
					tr = null;
				}
			}
			result.div("col-sm-5"); // right spacing
			return result;
		});

		billsView = body.add(null, t -> {
			if (t == null) {
				return null;
			}
			Fluent result = Div();

			return result;
		});

		groceryView = body.add(null, t -> {
			if (t == null) {
				return null;
			}
			Fluent result = Div();

			return result;
		});

		// Init
		menuHome(null);

		// TODO
		// bills: input fields (date, person, amount, notes), list of all
		// entries.
		// grocery: list of items to buy, removable too
		// layout: http://www.w3schools.com/bootstrap

		// should include an example of a stream
		// body.ul().add(Stream.of("aaa", "a").filter(e -> e.length() > 1).map(t
		// -> Li(null, t)));

	}

	public void menuHome(Event evt) {
		menuView.state("home");
		totalsView.unhide();
		billsView.hide();
		groceryView.hide();

		Pojofy.ajax("GET", totalsUrl, null, null, totalsMap, this::setTotals);
	}

	public void menuBills(Event evt) {
		menuView.state("bills");
		totalsView.hide();
		billsView.unhide();
		groceryView.hide();

		Pojofy.ajax("GET", billsUrl, null, null, billsMap, this::setBills);
	}

	public void menuGrocery(Event evt) {
		menuView.state("grocery");
		totalsView.hide();
		billsView.hide();
		groceryView.unhide();

		Pojofy.ajax("GET", groceryUrl, null, null, groceryMap, this::setGrocery);
	}

	public void setTotals(int responseCode, Totals pojo) {
		totalsView.state(pojo);
	}

	public void setBills(int responseCode, Bills pojo) {
		billsView.state(pojo);
	}

	public void setGrocery(int responseCode, Grocery pojo) {
		groceryView.state(pojo);
	}

	// POJO MAPPERS
	public interface TotalsMap extends ObjectMapper<Totals> {
	}

	public interface GroceryMap extends ObjectMapper<Grocery> {
	}

	public interface BillsMap extends ObjectMapper<Bills> {
	}

	public static TotalsMap totalsMap = null;
	public static GroceryMap groceryMap = null;
	public static BillsMap billsMap = null;

	static {
		// thanks to this construction, we can read the URL's in the View
		if (GWT.isClient()) {
			totalsMap = GWT.create(TotalsMap.class);
			groceryMap = GWT.create(GroceryMap.class);
			billsMap = GWT.create(BillsMap.class);
		}
	}

	@Override
	public void onModuleLoad() {
	}

}
