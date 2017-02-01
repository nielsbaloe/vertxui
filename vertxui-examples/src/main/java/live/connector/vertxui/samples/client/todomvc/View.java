package live.connector.vertxui.samples.client.todomvc;

import static live.connector.vertxui.client.fluent.Fluent.*;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.fluent.Fluent.body;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import elemental.html.InputElement;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.fluent.ViewOnBoth;

public class View implements EntryPoint {

	private List<Model> models;
	private State state;
	private ViewOn<List<Model>> list;
	private ViewOnBoth<State, List<Model>> buttons;

	public View() {
	}

	public View(Controller controller) {

		// Initial empty state
		models = new ArrayList<>();
		state = State.All;

		// Input field
		body.input().keyup(t -> {
			if (t.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
				InputElement element = (InputElement) t.getTarget();
				controller.addModel(element.getValue());
				element.setValue("");
			}
		});

		// List of items
		list = body.add(models, m -> {
			return Ul(m.stream().map(t -> Li(Input(null, "checkbox"), Span(null, t.getText()))));
		});

		// Buttons
		buttons = body.add(state, models, (s, m) -> {
			return null;
		});
	}

	public void setModels(List<Model> models) {
		this.models = models; // TODO: is not necessary... sync should work
		list.state(models);
		buttons.state2(models);
	}

	@Override
	public void onModuleLoad() {
		Controller controller = new Controller();
		View view = new View(controller);
		controller.setView(view);
		view.setModels(controller.getModels());
	}

}
