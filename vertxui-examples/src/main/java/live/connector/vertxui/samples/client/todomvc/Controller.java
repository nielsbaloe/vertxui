package live.connector.vertxui.samples.client.todomvc;

import java.util.ArrayList;
import java.util.List;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.samples.client.todomvc.State.Buttons;

public class Controller {

	// Models
	private State state = new State(Buttons.All);
	private List<Model> models = new ArrayList<>();

	// View
	private View view;

	public Controller(View view) {
		this.view = view;
	}

	public List<Model> getModels() {
		// AJAX CALL WOULD BE HERE
		return models;
	}

	public State getState() {
		return state;
	}

	public void addModel(String value) {
		Model model = new Model(value, false);
		models.add(model); // AJAX CALL WOULD BE THIS
		view.syncModel();
	}

	public void onInput(Fluent fluent, KeyboardEvent event) {
		if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
			String value = fluent.domValue();
			if (!value.isEmpty()) {
				addModel(value);
				fluent.att(Att.value, "");
			}
		}
	}

	public void onSelectAll(Fluent fluent, MouseEvent __) {
		boolean checked = fluent.domChecked();

		// AJAX CALL WOULD BE THIS
		models.stream().forEach(m -> m.setCompleted(checked));

		view.syncModel();
	}

	public void onSelect(Fluent fluent, Model model) {
		// It is -unfortunately- very important that we call fluent.domChecked()
		// here, because this also synchronizes the latest visual state into the
		// virtual DOM. If we would not call it (or use it), we would have
		// checkboxes that were still checked when switching from view. This is
		// because Fluent changes as less as possible, and it simply doesn't
		// know that something is changed in the DOM.

		model.setCompleted(fluent.domChecked());

		// AJAX CALL WOULD BE HERE

		view.syncModel();
	}

	public void onAll(Fluent __, MouseEvent ___) {
		state.setButtons(Buttons.All);
		view.syncState();
	}

	public void onActive(Fluent __, MouseEvent ___) {
		state.setButtons(Buttons.Active);
		view.syncState();
	}

	public void onCompleted(Fluent __, MouseEvent ___) {
		state.setButtons(Buttons.Completed);
		view.syncState();
	}

	public void onDestroy(Model model) {
		models.remove(model);
		view.syncModel();
	}

	public void onClearCompleted(Fluent __, MouseEvent ___) {

		// AJAX CALL WOULD BE THIS
		for (int x = models.size() - 1; x != -1; x--) {
			if (models.get(x).isCompleted()) {
				models.remove(x);
			}
		}
		view.syncModel();
	}

}
