package live.connector.vertxui.samples.client.todomvc;

import java.util.ArrayList;
import java.util.List;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.InputElement;
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

	public void onInput(KeyboardEvent event) {
		if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
			InputElement inputElement = (InputElement) event.getTarget();
			addModel(inputElement.getValue());
			inputElement.setValue("");
		}
	}

	public void onSelectAll(MouseEvent event) {
		boolean checked = ((InputElement) event.getTarget()).isChecked();

		// AJAX CALL WOULD BE THIS
		models.stream().forEach(m -> m.setCompleted(checked));

		view.syncModel();
	}

	public void onSelect(Model model) {
		model.setCompleted(!model.isCompleted());

		// AJAX CALL WOULD BE HERE

		view.syncModel();
	}

	public void onAll(MouseEvent event) {
		state.setButtons(Buttons.All);
		view.syncState();
	}

	public void onActive(MouseEvent event) {
		state.setButtons(Buttons.Active);
		view.syncState();
	}

	public void onCompleted(MouseEvent event) {
		state.setButtons(Buttons.Completed);
		view.syncState();
	}

	public void onDestroy(Model model) {
		models.remove(model);
		view.syncModel();
	}

	public void onClearCompleted(MouseEvent event) {

		// AJAX CALL WOULD BE THIS
		for (int x = models.size() - 1; x != -1; x--) {
			if (models.get(x).isCompleted()) {
				models.remove(x);
			}
		}
		view.syncModel();
	}

}
