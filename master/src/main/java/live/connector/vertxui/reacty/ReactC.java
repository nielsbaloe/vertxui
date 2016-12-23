package live.connector.vertxui.reacty;

import live.connector.vertxui.fluentHtml.FluentHtml;

public abstract class ReactC extends FluentHtml {

	public ReactC() {
		super(null);
	}

	abstract public FluentHtml generate();

	public void shown() {
	}

	public void removed() {
	}

}
