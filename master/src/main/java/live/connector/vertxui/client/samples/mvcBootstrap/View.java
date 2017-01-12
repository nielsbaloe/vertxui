package live.connector.vertxui.client.samples.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.*;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.State;

public class View implements EntryPoint {

	// private List<Model> bills;

	private State<String> mvMenu;

	// Note: THIS IS HEAVILY work-in-progress, get back in a few weeks!
	public View() {
		// Head
		head.style("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css").scriptSync(
				"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");
		head.meta().attr(Att.charset, "utf-8");
		head.meta().attr(Att.name_, "viewport").attr(Att.content, "width=device-width, initial-scale=1");

		// Header
		body.div().h1("Bills").classs("jumbotron text-center");

		// Menu
		Fluent container = body.nav("navbar navbar-inverse").div("container-fluid");
		container.div("navbar-header").a("Bills menu", "#").classs("navbar-brand");

		// http://www.w3schools.com/bootstrap/bootstrap_navbar.asp
		mvMenu = container.div().add("home", s -> {
			Fluent result = Ul("nav navbar-nav");
			result.li(s.equals("home") ? "active" : null).a("Home", "#").click(this::menuHome);
			result.li(s.equals("bills") ? "active" : null).a("Bills", "#").click(this::menuBills);
			result.li(s.equals("grocery") ? "active" : null).a("Grocery", "#").click(this::menuGrocery);
			return result;
		});

		// EXAMPLES AND TESTS
		// EXAMPLES AND TESTS
		// EXAMPLES AND TESTS
		// TODO re-add the stream functionality
		// response.add(Arrays.asList("aaa", "a").stream().filter(e ->
		// e.length() > 1).map(t -> Li(t)));

	}

	public void menuHome(Event evt) {
		mvMenu.state("home");
	}

	public void menuBills(Event evt) {
		mvMenu.state("bills");
	}

	public void menuGrocery(Event evt) {
		mvMenu.state("grocery");
	}

	@Override
	public void onModuleLoad() {
	}

}
