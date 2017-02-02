package live.connector.vertxui.samples.client.todomvc;

public class Model {

	private String text;
	private boolean completed;

	public Model() {
	}

	public Model(String text, boolean completed) {
		this.text = text;
		this.completed = false;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
