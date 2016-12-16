package live.connector.vertxui.fluentHtml;

public class Li extends FluentHtml {

	protected Li(String text, FluentHtml parent) {
		super("li", parent);
		inner(text);
	}

	protected Li(FluentHtml parent) {
		super("li", parent);
	}

	/**
	 * Non-fluent creation, warning: append yourself to an object.
	 * 
	 * @param text
	 *            the default text.
	 */
	public Li(String text) {
		super("li", null);
		inner(text);
	}
}
