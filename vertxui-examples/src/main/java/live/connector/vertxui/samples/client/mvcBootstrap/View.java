package live.connector.vertxui.samples.client.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.*;
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

import java.util.Collections;
import java.util.Date;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.InputElement;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Css;
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

	public static String[] css = new String[] { "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css",
			"https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" };
	public static String[] scripts = new String[] { "https://code.jquery.com/jquery-1.12.4.js",
			"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js",
			"https://code.jquery.com/ui/1.12.1/jquery-ui.js" };

	private DateTimeFormat dater = new InnerDateTimeFormat("dd/MM/yyyy");

	class InnerDateTimeFormat extends DateTimeFormat {
		protected InnerDateTimeFormat(String pattern) {
			super(pattern, new DefaultDateTimeFormatInfo());
		}
	}

	public View() {

		// Initialise models
		String url = Fluent.window.getLocation().getHref();
		int start = url.indexOf("#");
		String menuStart = "home";
		if (start != -1) {
			menuStart = url.substring(start + 1, url.length());
		}
		Fluent.console.log(menuStart);

		// Head
		head.meta().att(Att.charset, "utf-8");
		head.meta().att(Att.name_, "viewport", Att.content, "width=device-width, initial-scale=1");

		// Header
		Fluent container = body.div("container");
		container.h1(null, "Bills").classs("jumbotron text-center").id("titlerForJunitTest");

		// Menu
		menu = container.nav("navbar navbar-inverse").add(menuStart, selected -> {
			Fluent result = Ul("nav navbar-nav");
			result.li(selected.equals("home") ? "active" : null).a(null, "Home", "#home", this::onMenuHome);
			result.li(selected.equals("bills") ? "active" : null).a(null, "Bills", "#bills", this::onMenuBills);
			result.li(selected.equals("grocery") ? "active" : null).a(null, "Grocery", "#grocery", this::onMenuGrocery);
			return result;
		});

		// Totals on entering
		totals = container.add(null, totals -> {
			if (totals == null) {
				return null;
			}
			Fluent result = Div("row", Div("col-sm-3"));
			result.div("col-sm-3", Name.Niels.name() + " ").span("badge", totals.all.get(Name.Niels) + "");
			result.div("col-sm-3", Name.Linda.name() + " ").span("badge", totals.all.get(Name.Linda) + "");
			result.div("col-sm-3");
			return result;
		});

		// a detached view on a bills form
		billsForm = new ViewOn<Boolean>(false, opened -> {
			if (opened == false) {
				return Button("btn btn-success", "Add").att(Att.type, "button").click(e -> {
					billsForm.state(true);
					Fluent.eval("$('#datepicker').datepicker({ dateFormat:'dd/mm/yy'});");
				});
			}

			Fluent result = Form();

			// three input fields
			Fluent name = Select("form-control", Option(null, Name.Niels.name()), Option(null, Name.Linda.name()));
			Fluent amount = Input("form-control", "number").att(Att.min, "0", Att.max, "2000", Att.value, "0")
					.keypress(this::onBillOnlyNumeric);
			Fluent when = Input("form-control", "text").id("datepicker");

			Fluent text = Span("input-group-addon").css(Css.width, "100px");

			result.div("input-group", text.clone().txt("Name"), name).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("Amount"), amount).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("When"), when).css(Css.width, "80%");

			result.button("btn btn-success", "OK").att(Att.type, "button").click(event -> {
				try {
					addBill(name.domValue(), amount.domValue(), when.domValue());
				} catch (IllegalArgumentException e) {
					Fluent.window.alert(e.getMessage());
					return;
				}
				billsForm.state(false);
			});
			return result;
		});

		bills = container.add(null, bills -> {
			if (bills == null) {
				return null;
			}
			Fluent result = Div();
			result.add(billsForm);
			Fluent table = result.table("table table-condensed table-striped").tbody();
			for (Bill bill : bills.all) {
				table.tr(Td(null, bill.who.name()), Td(null, bill.amount + ""), Td(null, dater.format(bill.date)));
			}
			return result;
		});

		grocery = container.add(null, dto -> {
			if (dto == null) {
				return null;
			}
			Fluent form = Div().form("form");

			form.div("form-group", Label(null, "Name ").att(Att.for_, "n"),
					Input("form-control", "text", "n").css(Css.maxWidth, "200px").keyup(this::onGroceryAdd));
			form.ul(dto.all.stream().map(s -> Div("checkbox",
					Li(Input().att(Att.type, "checkbox", Att.value, s).click(this::delGrocery), Label(null, s)))));

			return form; // Fluent winds it back to the origin.
		});

		// Init
		switch (menu.state()) {
		case "home":
			onMenuHome(null);
			break;
		case "bills":
			onMenuBills(null);
			break;
		case "grocery":
			onMenuGrocery(null);
			break;
		}
	}

	protected void onBillOnlyNumeric(KeyboardEvent event) {
		int code = event.getCharCode();
		if ((code >= 48 && code <= 57) || code == 0) {
			return; // numeric or a not-a-character is OK
		}
		event.preventDefault();
	}

	protected void onGroceryAdd(KeyboardEvent event) {
		if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
			InputElement element = (InputElement) event.getTarget();
			if (!element.getValue().isEmpty()) {
				addGrocery(element.getValue());
				element.setValue("");
			}
		}
	}

	public void addGrocery(String text) {
		grocery.state().all.add(text);
		grocery.sync();

		Pojofy.ajax("PUT", groceryUrl, text, null, null, null);
	}

	public void delGrocery(MouseEvent evt) {
		Element element = ((Element) evt.getTarget());
		String text = element.getAttribute("value");
		((InputElement) element).setChecked(false);
		grocery.state().all.remove(text);
		grocery.sync();

		Pojofy.ajax("DELETE", groceryUrl, text, null, null, null);
	}

	// No DOM specific elements in callbacks from the GUI means easy junit
	// testing
	public void addBill(String name, String amount, String when) {
		Date date = null;
		try {
			date = dater.parse(when);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("This is not a valid date.", e);
		}
		// Create a new bill
		Bill bill = new Bills.Bill(Name.valueOf(name), Integer.parseInt(amount), date);

		// Get the state, add the bill, and sort
		Bills dto = bills.state();
		dto.all.add(bill);
		Collections.sort(dto.all);

		// set the new state (which resyncs too)
		bills.state(dto);

		Pojofy.ajax("PUT", billsUrl, bill, billMap, null, null);
	}

	public void onMenuHome(Event evt) {
		menu.state("home");
		totals.unhide();
		bills.hide();
		grocery.hide();

		// Note that old available data is already shown before any answer
		Pojofy.ajax("GET", totalsUrl, null, null, totalsMap, this::setTotals);
	}

	public void onMenuBills(Event evt) {
		menu.state("bills");
		totals.hide();
		bills.unhide();
		grocery.hide();

		// Note that old available data is already shown before any answer
		Pojofy.ajax("GET", billsUrl, null, null, billsMap, this::setBills);
	}

	public void onMenuGrocery(Event evt) {
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
