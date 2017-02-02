package live.connector.vertxui.samples.client.todomvc;

import static live.connector.vertxui.client.fluent.Fluent.*;
import static live.connector.vertxui.client.fluent.Fluent.H1;
import static live.connector.vertxui.client.fluent.Fluent.Input;
import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.Fluent.body;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.fluent.ViewOnBoth;
import live.connector.vertxui.samples.client.todomvc.State.Buttons;

public class View implements EntryPoint {

	// Models
	private List<Model> models;
	private State state;

	// Views
	private ViewOnBoth<State, List<Model>> list;
	private ViewOn<List<Model>> buttons;

	// Controller
	private Controller controller;

	public static String[] css = { "css/base.css", "css/index.css" };

	public View() {

		// Initialise controller
		controller = new Controller(this);

		// Initialise models
		models = controller.getModels();
		state = new State(Buttons.All);

		// Initialise views:

		// Static upper part
		body.classs("learn-bar").add(getInfo());
		Fluent container = body.section("todoapp");
		container.header("header", H1(null, "todos"),
				Input("new-todo").placeholder("What needs to be done?").keydown(controller::onInput));

		// List of items
		Fluent main = container.section("main").css(Css.display, "block");
		main.input("toggle-all", "checkbox");// TODO
		list = main.add(state, models, (s, m) -> {

			// When should we display a model?
			Predicate<Model> filter = p -> (s.getButtons() == Buttons.All)
					|| (p.isCompleted() == (s.getButtons() == Buttons.Completed));

			// Filter on it
			Stream<Model> show = m.stream().filter(filter);

			// Return a list with items
			return Ul("todo-list", show.map(t -> {
				Fluent result = Li().div("view");
				result.input("toggle", "checkbox").att(Att.checked, t.isCompleted() ? "1" : null)
						.click(e -> controller.onSelect(t));
				result.label(null, t.getText());
				return result;
			}));
		});

		// Buttons
		buttons = main.add(models, m -> {
			if (models.size() == 0) {
				return null;
			}
			Fluent result = Div();

			// Counter
			long completedCount = models.stream().filter(t -> !t.isCompleted()).count();
			if (completedCount == 1) {
				result.div(null, "1 item left");
			} else {
				result.div(null, completedCount + " items left");
			}

			// Buttons
			result.button(null, "All").click(controller::onAll);
			result.button(null, "Active").click(controller::onActive);
			result.button(null, "Completed").click(controller::onCompleted);

			if (completedCount != models.size()) {
				result.button(null, "Clear completed").click(controller::onClearCompleted);
			}
			return result;
		});
	}

	private Fluent getInfo() {
		// TODO Auto-generated method stub
		return Aside("learn");
	}

	public void setModelAdded(Model model) {
		models.add(model);

		list.sync();
		buttons.sync();
	}

	public void setState(Buttons buttonsState) {
		state.setButtons(buttonsState);

		list.sync();
	}

	public void setModelCompletedChanged() {
		list.sync();
		buttons.sync();
	}

	public void setClearCompleted() {
		models.stream().filter(t -> t.isCompleted()).forEach(models::remove);

		list.sync();
		buttons.sync();
	}

	@Override
	public void onModuleLoad() {
	}

}
