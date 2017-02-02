package live.connector.vertxui.samples.client.todomvc;

/**
 * The buttons-state of the application. If extra states arrise, we can put them
 * in this class, or create a new state class, or just use a primite and call
 * .state() on our ViewOn... objects.
 *
 */
public class State {

	private Buttons buttons;

	public static enum Buttons {
		All, Active, Completed;
	}

	public State() {
	}

	public State(Buttons buttons) {
		this.buttons = buttons;
	}

	public Buttons getButtons() {
		return buttons;
	}

	public void setButtons(Buttons buttons) {
		this.buttons = buttons;
	}

}
