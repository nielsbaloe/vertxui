package live.connector.vertxui.samples.client.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.Button;
import static live.connector.vertxui.client.fluent.Fluent.Div;
import static live.connector.vertxui.client.fluent.Fluent.Form;
import static live.connector.vertxui.client.fluent.Fluent.Input;
import static live.connector.vertxui.client.fluent.Fluent.Label;
import static live.connector.vertxui.client.fluent.Fluent.Option;
import static live.connector.vertxui.client.fluent.Fluent.Select;
import static live.connector.vertxui.client.fluent.Fluent.Span;
import static live.connector.vertxui.client.fluent.Fluent.Td;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.head;
import static live.connector.vertxui.client.fluent.FluentBase.window;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;

public class View implements EntryPoint {

	// Views
	private ViewOn<String> menu;
	private ViewOn<Totals> totals;
	private ViewOn<Bills> bills;
	private ViewOn<Boolean> billsForm;
	private ViewOn<Grocery> grocery;

	public static String[] css = new String[] { "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css",
			"https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" };

	public static String[] scripts = new String[] { "https://code.jquery.com/jquery-1.12.4.js",
			"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js",
			"https://code.jquery.com/ui/1.12.1/jquery-ui.js" };

	protected DateTimeFormat dater = new InnerDateTimeFormat("dd/MM/yyyy");

	class InnerDateTimeFormat extends DateTimeFormat {
		protected InnerDateTimeFormat(String pattern) {
			super(pattern, new DefaultDateTimeFormatInfo());
		}
	}

	@Override
	public void onModuleLoad() {
		Store store = new Store();
		Controller controller = new Controller(store, this);
		start(controller);
	}

	public void start(Controller controller) {

		// Initialise models:
		String menuStart = "home";
		if (GWT.isClient()) { // in junit tests, there is no Fluent.window
			String url = Fluent.window.getLocation().getHref();
			int start = url.indexOf("#");
			if (start != -1) {
				menuStart = url.substring(start + 1, url.length());
			}
			if (!menuStart.equals("home") && !menuStart.equals("bills") && !menuStart.equals("grocery")) {
				menuStart = "home";
			}
		}

		// Initialise view:

		// Page head
		head.meta().att(Att.name_, "viewport", Att.content, "width=device-width, initial-scale=1");

		// Header
		Fluent container = body.div("container");
		container.h1("jumbotron text-center", "Bills").id("titlerForJunitTest");

		// Menu
		menu = container.nav("navbar navbar-inverse").add(menuStart, selected -> {
			Fluent result = Ul("nav navbar-nav");
			result.li(selected.equals("home") ? "active" : null).a(null, "Home", "#home", controller::onMenuHome);
			result.li(selected.equals("bills") ? "active" : null).a(null, "Bills", "#bills", controller::onMenuBills);
			result.li(selected.equals("grocery") ? "active" : null).a(null, "Grocery", "#grocery",
					controller::onMenuGrocery);
			return result;
		});

		// Totals
		totals = container.add(controller.getTotals(), totals -> {
			if (totals.all.isEmpty()) {
				return null;
			}
			Fluent result = Div("row");
			result.div("col-sm-3");

			double niels = totals.all.get(Name.Niels);
			double linda = totals.all.get(Name.Linda);

			result.div("col-sm-3").h4().span("label label-" + (niels > linda ? "success" : "warning"),
					Name.Niels.name() + ": " + niels + "");
			result.div("col-sm-3").h4().span("label label-" + (linda > niels ? "success" : "warning"),
					Name.Linda.name() + ": " + linda + "");
			result.div("col-sm-3");
			return result;
		});

		// a detached view on a bills form
		billsForm = new ViewOn<Boolean>(false, opened -> {
			if (opened == false) {
				return Button("btn btn-primary", "button", "Add").click((fluent, event) -> {

					// change the state of myself, which causes redrawing of
					// myself!
					billsForm.state(true);
				});
			}

			Fluent result = Form();

			// input fields in bootstrap: a div/input-group with a
			// span/input-group-addon and a form element/form-control, pfff
			Fluent name = Select("form-control", Option(null, Name.Niels.name()), Option(null, Name.Linda.name()));
			Fluent amount = Input("form-control", "number").att(Att.min, "0", Att.max, "2000", Att.value, "")
					.keypress(controller::onBillOnlyNumeric);
			Fluent when = new ComponentDatePicker();
			Fluent what = Input("form-control", "text");

			Fluent text = Span("input-group-addon").css(Css.width, "100px");

			result.div("input-group", text.clone().txt("Name"), name).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("Amount"), amount).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("When"), when).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("What"), what).css(Css.width, "80%");

			result.button("btn btn-success", "button", "OK").click((fluent, event) -> {
				if (amount.domValue().isEmpty()) {
					Fluent.window.alert("Please fill in an amount.");
					return;
				}
				Date date = null;
				try {
					date = dater.parse(when.domValue());
				} catch (IllegalArgumentException e) {
					Fluent.window.alert("Please fill in the date.");
					return;
				}
				controller.onAddBill(name.domValue(), amount.domValue(), what.domValue(), date);
				billsForm.state(false);
			});
			return result;
		});

		bills = container.add(controller.getBills(), bills -> {
			if (bills.all == null) {
				return null;
			}
			Fluent result = Div();
			result.add(billsForm);
			Fluent table = result.table("table table-condensed table-striped").tbody();
			for (Bill bill : bills.all) {
				String when = dater.format(bill.date);
				table.tr(Td(null, bill.who.name()), Td(null, bill.amount + ""), Td(null, bill.what), Td(null, when));
			}
			return result;
		});

		grocery = container.add(controller.getGrocery(), grocery -> {
			if (grocery.all == null) {
				return null;
			}
			Fluent form = Div().form("form");

			form.div("form-group", Label(null, "Name ").att(Att.for_, "n"), Input("form-control", "text").id("n")
					.css(Css.maxWidth, "200px").keypress(controller::onGroceryAdd));
			form.div(grocery.all.stream().map(s -> {
				Fluent result = Div().css(Css.marginTop, "20px");
				result.input(null, "checkbox").att(Att.value, s).click((fluent, event) -> {

					// Reset checkbox:
					fluent.domChecked(); // synchronize the virtual DOM
					fluent.att(Att.checked, null); // before adjusting it

					if (window.confirm("Delete '" + s + "'?")) {
						controller.onGroceryDelete(fluent, event);
					}
				});
				result.span(null, s).css(Css.fontSize, "140%", Css.marginLeft, "20px");
				return result;
			}));
			return form;
		});

		// Init data
		switch (menuStart) {
		case "home":
			controller.onMenuHome(null, null);
			break;
		case "bills":
			controller.onMenuBills(null, null);
			break;
		case "grocery":
			controller.onMenuGrocery(null, null);
			break;
		}
	}

	public void syncGrocery() {
		grocery.sync();
	}

	public void syncBills() {
		bills.sync();
	}

	public void syncTotals() {
		totals.sync();
	}

	public void stateAndSyncMenu(String state) {
		menu.state(state);

		switch (state) {
		case "home":
			totals.hide(false);
			bills.hide(true);
			grocery.hide(true);
			break;
		case "bills":
			totals.hide(true);
			bills.hide(false);
			grocery.hide(true);
			break;
		case "grocery":
			totals.hide(true);
			bills.hide(true);
			grocery.hide(false);
			break;
		}
	}

	public void errorBillAdd() {
		window.alert("Could not add the new bill");
	}

	public void errorGroceryDelete(String text) {
		window.alert("Could not delete grocery item '" + text + "'.");
	}

	public void errorGroceryAdd(String text) {
		window.alert("Could not add grocery item '" + text + "'.");
	}

}
