package live.connector.vertxui.samples.client.todomvc;

import static live.connector.vertxui.client.fluent.Fluent.*;
import static live.connector.vertxui.client.fluent.Fluent.H1;
import static live.connector.vertxui.client.fluent.Fluent.Input;
import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Ul;

import java.util.List;
import java.util.function.Predicate;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOnBoth;
import live.connector.vertxui.samples.client.todomvc.State.Buttons;

public class View implements EntryPoint {

	// Views
	private ViewOnBoth<State, List<Model>> list;
	private ViewOnBoth<State, List<Model>> footer;

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

		Fluent container = Fluent.dom("startpoint");
		if (container == null) { // this is not the pre-defined index.html!!
			body.classs("learn-bar").aside();
			container = body.section("todoapp");
		}
		container.header("header", H1(null, "todos"),
				Input("new-todo").placeholder("What needs to be done?").keydown(controller::onInput));

		// List of items
		Fluent main = container.section("main").css(Css.display, "block");
		main.input("toggle-all", "checkbox").click(controller::onSelectAll);
		list = main.add(state, models, (s, m) -> {

			// Which models should be displayed?
			Predicate<Model> filter = p -> (s.getButtons() == Buttons.All)
					|| (p.isCompleted() == (s.getButtons() == Buttons.Completed));

			// Return a list with items
			return Ul("todo-list", m.stream().filter(filter).map(t -> {
				Fluent result = Li().div("view");
				result.input("toggle", "checkbox").att(Att.checked, t.isCompleted() ? "1" : null)
						.click(e -> controller.onSelect(t));
				result.label(null, t.getText());
				result.button("destroy").click(e -> controller.onDestroy(t));
				return result;
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
				result.button("clear-completed", "Clear completed").css(Css.display, "block")
						.click(controller::onClearCompleted);
			}
			return result;
		});
	}

	public void syncModel() {
		list.sync();
		footer.sync();
	}

	public void syncState() {
		list.sync();
		footer.sync();
	}

	@Override
	public void onModuleLoad() {
	}

}
