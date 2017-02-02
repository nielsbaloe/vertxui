package live.connector.vertxui.samples.client.todomvc;

import java.util.ArrayList;
import java.util.List;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.InputElement;
import live.connector.vertxui.samples.client.todomvc.State.Buttons;

public class Controller {

	private View view;

	public Controller(View view) {
		this.view = view;
	}

	public List<Model> getModels() {
		return new ArrayList<>(); // TODO AJAX
	}

	public void addModel(String value) {
		Model model = new Model(value, false);
		// TODO AJAX
		view.setModelAdded(model);
	}

	public void onInput(KeyboardEvent event) {
		if (event.getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
			InputElement inputElement = (InputElement) event.getTarget();
			addModel(inputElement.getValue());
			inputElement.setValue("");
		}
	}

	public void onSelect(Model model) {
		model.setCompleted(!model.isCompleted());
		// TODO AJAX

		// only for synching the click with the checked sign.
		view.setModelCompletedChanged();
	}

	public void onAll(MouseEvent event) {
		view.setState(Buttons.All);
	}

	public void onActive(MouseEvent event) {
		view.setState(Buttons.Active);
	}

	public void onCompleted(MouseEvent event) {
		view.setState(Buttons.Completed);
	}

	public void onClearCompleted(MouseEvent event) {
		// TODO ajax
		
		view.setClearCompleted();
	}
}
