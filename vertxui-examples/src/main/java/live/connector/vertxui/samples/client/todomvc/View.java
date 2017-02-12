package live.connector.vertxui.samples.client.todomvc;

import static live.connector.vertxui.client.fluent.Fluent.Footer;
import static live.connector.vertxui.client.fluent.Fluent.H1;
import static live.connector.vertxui.client.fluent.Fluent.Input;
import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.FluentBase.body;

import java.util.List;
import java.util.function.Predicate;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.fluent.ViewOnBoth;
import live.connector.vertxui.samples.client.todomvc.State.Buttons;

public class View implements EntryPoint {

	// Views
	private ViewOnBoth<State, List<Model>> list;
	private ViewOnBoth<State, List<Model>> footer;
	private ViewOn<List<Model>> toggle;

	// Controller
	private Controller controller;

	public static String[] css = { "css/base.css", "css/index.css" };

	public View() {

		// Initialise controller
		controller = new Controller(this);

		// Initialise models
		List<Model> models = controller.getModels();
		State state = controller.getState();

		// Initialise views:

		// Static upper part

		Fluent container = Fluent.dom("startpoint"); // search for id=startpoint
		if (container == null) { // if the existing index.html is not found
			// Note: todomvc is also the extend-an-existing-index.html example
			// for vertxui.
			body.classs("learn-bar").aside();
			container = body.section("todoapp");
		}
		container.header("header", H1(null, "todos"),
				Input("new-todo").att(Att.placeholder, "What needs to be done?").keydown(controller::onInput));

		// List of items
		Fluent main = container.section("main").css(Css.display, "block");

		toggle = main.add(models, m -> {

			boolean allChecked = m.size() != 0 && m.stream().filter(x -> !x.isCompleted()).count() == 0;

			// TODO the css.display is incorrect, but I don't know what css
			// todomvc uses to do that.
			return Input("toggle-all", "checkbox").att(Att.checked, allChecked ? "1" : null)
					.css(Css.display, m.size() == 0 ? "none" : null).click(controller::onSelectAll);

		});

		list = main.add(state, models, (s, m) -> {

			// Which models should be displayed?
			Predicate<Model> filter = p -> (s.getButtons() == Buttons.All)
					|| (p.isCompleted() == (s.getButtons() == Buttons.Completed));

			// Return a list with items
			return Ul("todo-list", m.stream().filter(filter).map(t -> {
				Fluent li = Li(t.isCompleted() ? "completed" : null).div("view");

				Fluent input = li.input("toggle", "checkbox");
				input.click(event -> controller.onSelect(input, t));
				if (t.isCompleted()) {
					input.att(Att.checked, "!");
				}

				li.label(null, t.getText());

				li.button("destroy", "button", null).click(e -> controller.onDestroy(t));

				return li;
			}));
		});

		// Footer
		footer = main.add(state, models, (s, m) -> {
			if (m.size() == 0) {
				return null;
			}
			Fluent result = Footer("footer").css(Css.display, "block");

			// Counter
			Fluent counter = result.span("todo-count");
			long completedCount = m.stream().filter(t -> !t.isCompleted()).count();
			counter.strong(null, completedCount + "");
			if (completedCount == 1) {
				// this is exceptional and bad style, combining plain text and
				// html. however this is how the todomvc example should work
				counter.text(" item left");
			} else {
				// this is exceptional and bad style, combining plain text and
				// html. however this is how the todomvc example should work
				counter.text(" items left");
			}

			// Buttons
			Fluent buttons = result.ul("filters");
			buttons.li().a((s.getButtons() == Buttons.All ? "selected" : null), "All", "#", controller::onAll);
			buttons.li().a((s.getButtons() == Buttons.Active ? "selected" : null), "Active", "#", controller::onActive);
			buttons.li().a((s.getButtons() == Buttons.Completed ? "selected" : null), "Completed", "#",
					controller::onCompleted);

			// "Clear Completed" button
			if (completedCount != models.size()) {
				result.button("clear-completed", "button", "Clear completed").css(Css.display, "block")
						.click(controller::onClearCompleted);
			}
			return result;
		});
	}

	public void syncModel() {
		list.sync();
		footer.sync();
		toggle.sync();
	}

	public void syncState() {
		list.sync();
		footer.sync();
	}

	@Override
	public void onModuleLoad() {
	}

}
