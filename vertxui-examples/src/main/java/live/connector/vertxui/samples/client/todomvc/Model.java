package live.connector.vertxui.samples.client.todomvc;

public class Model {

	private String text;

	private boolean done;

	public Model(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}
