package live.connector.vertxui.samples.client.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.Button;
import static live.connector.vertxui.client.fluent.Fluent.Div;
import static live.connector.vertxui.client.fluent.Fluent.Form;
import static live.connector.vertxui.client.fluent.Fluent.Input;
import static live.connector.vertxui.client.fluent.Fluent.Label;
import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Option;
import static live.connector.vertxui.client.fluent.Fluent.Select;
import static live.connector.vertxui.client.fluent.Fluent.Span;
import static live.connector.vertxui.client.fluent.Fluent.Td;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import java.util.Date;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;

import elemental.events.Event;
import elemental.events.UIEvent;
import elemental.html.InputElement;
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

	public static String[] scripts = new String[] { "https://code.jquery.com/jquery-1.12.4.js",
			"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js",
			"https://code.jquery.com/ui/1.12.1/jquery-ui.js" };

	public View() {
		// Head
		head.style("https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css");
		head.meta().att(Att.charset, "utf-8");
		head.meta().att(Att.name_, "viewport", Att.content, "width=device-width, initial-scale=1");

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
				return Button("btn btn-success", "Add").att(Att.type, "button").click(e -> {
					billsForm.state(true);
					Fluent.natively("$('#datepicker').datepicker();");
				});
			}
			Fluent result = Form();

			Fluent name = Select("form-control", Option(null, Name.Niels.name()), Option(null, Name.Linda.name()));
			Fluent amount = Input("form-control", null, "number").att(Att.min, "0", Att.max, "2000", Att.value, "0");
			Fluent when = Input("form-control", null, "text").id("datepicker");

			result.div("input-group", Span("input-group-addon", "Name"), name);
			result.div("input-group", Span("input-group-addon", "Amount"), amount);
			result.div("input-group date", Span("input-group-addon", "When"), when);

			result.button("btn btn-success", "OK").att(Att.type, "button").click(event -> {
				try {
					addBill(name.value(), amount.value(), when.value());
				} catch (IllegalArgumentException e) {
					Fluent.window.alert(e.getMessage());
					return;
				}
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
				table.tr(Td(null, bill.who.name()), Td(null, bill.amount + ""), Td(null, bill.date.toString()));
			}
			return result;
		});

		grocery = content.add(null, grocery -> {
			if (grocery == null) {
				return null;
			}
			Fluent result = Div();
			result.form("form-inline").div("form-group", Label(null, "Name ").att(Att.for_, "n"),
					Input("form-control", null, "text", "n").css(Style.width, "50%").keyup(event -> {
						if (((UIEvent) event).getKeyCode() == 13) {
							InputElement element = (InputElement) event.getTarget();
							addGrocery(element.getValue());
							element.setValue("");
						}
					}));

			return result.ul().add(grocery.things.stream().map(s -> Li(null, s)));
		});

		// Init
		menuHome(null);
	}

	// No DOM specific elements in callbacks from the GUI means easy junit
	// testing
	public void addGrocery(String text) {
		grocery.state().things.add(text);
		grocery.sync();

		Pojofy.ajax("PUT", groceryUrl, text, null, null, null);
	}

	// No DOM specific elements in callbacks from the GUI means easy junit
	// testing
	public void addBill(String name, String amount, String when) {
		Date date = null;
		try {
			date = DateTimeFormat.getFormat("MM/dd/yyyy").parse(when);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("This is not a valid date.", e);
		}
		// Create a new bill
		Bill bill = new Bills.Bill(Name.valueOf(name), Integer.parseInt(amount), date);
		// get the state, and add the bill
		bills.state().bills.add(bill);
		// ask the GUI to resync
		bills.sync();

		Pojofy.ajax("PUT", billsUrl, bill, billMap, null, null);
	}

	public void menuHome(Event evt) {
		menu.state("home");
		totals.unhide();
		bills.hide();
		grocery.hide();

		// Note that old available data is already shown before any answer
		Pojofy.ajax("GET", totalsUrl, null, null, totalsMap, this::setTotals);
	}

	public void menuBills(Event evt) {
		menu.state("bills");
		totals.hide();
		bills.unhide();
		grocery.hide();

		// Note that old available data is already shown before any answer
		Pojofy.ajax("GET", billsUrl, null, null, billsMap, this::setBills);
	}

	public void menuGrocery(Event evt) {
		menu.state("grocery");
		totals.hide();
		bills.hide();
		grocery.unhide();

		// Note that old available data is already shown before any answer
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

	public interface BillMap extends ObjectMapper<Bills.Bill> {
	}

	public static TotalsMap totalsMap = null;
	public static GroceryMap groceryMap = null;
	public static BillsMap billsMap = null;
	public static BillMap billMap = null;

	static {
		// thanks to this construction, we can read the URL's in the servercode
		if (GWT.isClient()) {
			totalsMap = GWT.create(TotalsMap.class);
			groceryMap = GWT.create(GroceryMap.class);
			billsMap = GWT.create(BillsMap.class);
			billMap = GWT.create(BillMap.class);
		}
	}

	@Override
	public void onModuleLoad() {
	}

}
