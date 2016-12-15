package live.connector.vertxui.fluidhtml;

public class Li extends FluidHtml {

	protected Li(String text, FluidHtml parent) {
		super("li", parent);
		inner(text);
	}

	protected Li(FluidHtml parent) {
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
	}
}
