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
		Store transport = new Store();
		View view = new View();
		Controller controller = new Controller(transport, view);
		view.start(controller);
	}

	public void start(Controller controller) {

		// Initialise models:
		String menuStart = "home";
		if (GWT.isClient()) {
			String url = Fluent.window.getLocation().getHref();
			int start = url.indexOf("#");
			if (start != -1) {
				menuStart = url.substring(start + 1, url.length());
			}
		}

		// Initialise view:

		// Page head
		head.meta().att(Att.charset, "utf-8");
		head.meta().att(Att.name_, "viewport", Att.content, "width=device-width, initial-scale=1");

		// Header
		Fluent container = body.div("container");
		container.h1(null, "Bills").classs("jumbotron text-center").id("titlerForJunitTest");

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
					.keypress(controller::onBillOnlyNumeric);
			Fluent when = Input("form-control", "text").id("datepicker");

			Fluent text = Span("input-group-addon").css(Css.width, "100px");

			result.div("input-group", text.clone().txt("Name"), name).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("Amount"), amount).css(Css.width, "80%");
			result.div("input-group", text.clone().txt("When"), when).css(Css.width, "80%");

			result.button("btn btn-success", "OK").att(Att.type, "button").click(event -> {
				Date date = null;
				try {
					date = dater.parse(when.domValue());
				} catch (IllegalArgumentException e) {
					Fluent.window.alert("This is not a valid date.");
					return;
				}
				controller.onAddBill(name.domValue(), amount.domValue(), date);
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
				table.tr(Td(null, bill.who.name()), Td(null, bill.amount + ""), Td(null, dater.format(bill.date)));
			}
			return result;
		});

		grocery = container.add(controller.getGrocery(), grocery -> {
			if (grocery.all == null) {
				return null;
			}
			Fluent form = Div().form("form");

			form.div("form-group", Label(null, "Name ").att(Att.for_, "n"),
					Input("form-control", "text", "n").css(Css.maxWidth, "200px").keyup(controller::onGroceryAdd));
			form.ul(grocery.all.stream()
					.map(s -> Div("checkbox",
							Li(Input().att(Att.type, "checkbox", Att.value, s).click(controller::onGroceryDelete),
									Label(null, s)))));

			return form; // Fluent winds it back to the origin.
		});

		// Init data
		switch (menuStart) {
		case "home":
			controller.onMenuHome(null);
			break;
		case "bills":
			controller.onMenuBills(null);
			break;
		case "grocery":
			controller.onMenuGrocery(null);
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
			totals.unhide();
			bills.hide();
			grocery.hide();
			break;
		case "bills":
			totals.hide();
			bills.unhide();
			grocery.hide();
			break;
		case "grocery":
			totals.hide();
			bills.hide();
			grocery.unhide();
			break;
		}
	}

}
