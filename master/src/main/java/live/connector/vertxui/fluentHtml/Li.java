package live.connector.vertxui.fluentHtml;

public class Li extends FluentHtml {

	protected Li(FluentHtml parent) {
		super("li", parent);
	}

	/**
	 * Non-fluent creation.
	 * 
	 * @param text
	 *            the default text.
	 */
	public Li(String text) {
		super("li", null);
		inner(text);
	}

}
