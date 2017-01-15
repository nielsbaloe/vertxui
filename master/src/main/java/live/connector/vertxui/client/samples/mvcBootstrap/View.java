package live.connector.vertxui.client.samples.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class View implements EntryPoint {

	// private List<Model> bills;

	private ViewOn<String> mvMenu;

	// Note: THIS IS HEAVILY work-in-progress, get back in a few weeks!
	public View() {
		// Head
		head.style("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css").scriptSync(
				"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");
		head.meta().attr(Att.charset, "utf-8");
		head.meta().attr(Att.name_, "viewport").attr(Att.content, "width=device-width, initial-scale=1");

		// Header
		body.div().h1("Bills").classs("jumbotron text-center").id("titlerForJunitTest");

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

		// Use example of stream
		// body.ul().add(Stream.of("aaa", "a").filter(e -> e.length() > 1).map(t
		// -> Li(null, t)));

	}

	public void menuHome(Event evt) {
		mvMenu.state("home");
	}

	public void menuBills(Event evt) {
		mvMenu.state("bills");
		// Element el = mvMenu.getTheCurrentViewForDebugPurposesOnly();
		// printStructure(el);
	}

	// private void printStructure(Element element) {
	// console.log("<" + element.getNodeName() + "... >");
	// NodeList children = element.getChildNodes();
	// for (int x = 0; x < children.getLength(); x++) {
	// printStructure((Element) children.at(x));
	// }
	// console.log("</" + element.getNodeName() + ">");
	// }

	public void menuGrocery(Event evt) {
		mvMenu.state("grocery");
	}

	@Override
	public void onModuleLoad() {
	}

}
