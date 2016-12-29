package live.connector.vertxui.client.fluent;

public abstract class ReactC extends Fluent {

	public ReactC() {
		super(null);
	}

	abstract public Fluent generate();

	public void shown() {
	}

	public void removed() {
	}

}
