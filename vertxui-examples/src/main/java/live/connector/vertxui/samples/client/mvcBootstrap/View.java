package live.connector.vertxui.samples.client.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.Div;
import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Td;
import static live.connector.vertxui.client.fluent.Fluent.*;
import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Style;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;

public class View implements EntryPoint {

	private ViewOn<String> menu;
	private ViewOn<Totals> totals;
	private ViewOn<Bills> bills;
	private ViewOn<Boolean> billsForm;
	private ViewOn<Grocery> grocery;

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
		body.css(Style.maxWidth, "600px", Style.margin, "0 auto 0 auto");
		body.h1(null, "Bills").classs("jumbotron text-center").id("titlerForJunitTest");

		// Menu
		Fluent container = body.nav("navbar navbar-inverse").div("container-fluid");
		menu = container.add("home", selected -> {
			Fluent result = Ul("nav navbar-nav");
			result.li(selected.equals("home") ? "active" : null).a("Home", "#").click(this::menuHome);
			result.li(selected.equals("bills") ? "active" : null).a("Bills", "#").click(this::menuBills);
			result.li(selected.equals("grocery") ? "active" : null).a("Grocery", "#").click(this::menuGrocery);
			return result;
		});

		// Content
		Fluent content = body.div("center-block").css(Style.maxWidth, "500px");
		totals = content.add(null, totals -> {
			if (totals == null) {
				return null;
			}
			Fluent result = Div("row", Div("col-sm-3"));
			result.div("col-sm-3", Name.Niels.name() + " ").span("badge", totals.totals.get(Name.Niels) + "");
			result.div("col-sm-3", Name.Linda.name() + " ").span("badge", totals.totals.get(Name.Linda) + "");
			result.div("col-sm-3");
			return result;
		});

		// a detached view on a bills form
		billsForm = new ViewOn<Boolean>(false, opened -> {
			if (opened == false) {
				return Button("btn btn-success", "Add").attr(Att.type, "button").click(e -> {
					billsForm.state(true);
				});
			}
			Fluent result = Form("form-inline");

			Fluent name = Input("form-control", null, "text", "n");
			Fluent amount = Input("form-control", null, "text", "a");
			Fluent when = Input("form-control", null, "text", "w");

			result.div("form-group", Label(null, "Name ").attr(Att.for_, "n"), name);
			result.div("form-group", Label(null, "Amount ").attr(Att.for_, "a"), amount);
			result.div("form-group", Label(null, "When ").attr(Att.for_, "w"), when);

			result.button("btn btn-success", "OK").attr(Att.type, "button").click(e -> {
				setNewBill(name.value(), amount.value(), when.value());
				billsForm.state(false);
			});
			return result;
		});

		bills = content.add(null, bills -> {
			if (bills == null) {
				return null;
			}
			Fluent result = Div();
			result.add(billsForm);
			Fluent table = result.table("table table-condensed table-striped").tbody();
			for (Bill bill : bills.bills) {
				table.tr(Td(null, bill.who.name()), Td(null, bill.amount + ""), Td(null, bill.notes),
						Td(null, bill.date.toString()));
			}
			return result;
		});
		grocery = content.add(null, grocery -> {
			if (grocery == null) {
				return null;
			}
			return Ul().add(grocery.things.stream().map(s -> Li(null, s)));
		});

		// Init
		menuHome(null);
	}

	public void setNewBill(String name, String amount, String when) {
		console.log("TODO");
	}

	public void menuHome(Event evt) {
		menu.state("home");
		totals.unhide();
		bills.hide();
		grocery.hide();

		Pojofy.ajax("GET", totalsUrl, null, null, totalsMap, this::setTotals);
	}

	public void menuBills(Event evt) {
		menu.state("bills");
		totals.hide();
		bills.unhide();
		grocery.hide();

		// Note that old available data is already shown to the client

		Pojofy.ajax("GET", billsUrl, null, null, billsMap, this::setBills);
	}

	public void menuGrocery(Event evt) {
		menu.state("grocery");
		totals.hide();
		bills.hide();
		grocery.unhide();

		// Note that old available data is already shown to the client

		Pojofy.ajax("GET", groceryUrl, null, null, groceryMap, this::setGrocery);
	}

	public void setTotals(int responseCode, Totals pojo) {
		totals.state(pojo);
	}

	public void setBills(int responseCode, Bills pojo) {
		bills.state(pojo);
	}

	public void setGrocery(int responseCode, Grocery pojo) {
		grocery.state(pojo);
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
