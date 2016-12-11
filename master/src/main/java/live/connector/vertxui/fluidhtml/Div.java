package live.connector.vertxui.fluidhtml;

public class Div extends Html {

	protected Div(Html parent) {
		super("div", parent);
	}

	@Override
	public Div inner(String innerHtml) {
		return (Div) super.inner(innerHtml);
	}

	@Override
	public Div id(String string) {
		return (Div) super.id(string);
	}

	@Override
	public Div css(String property, String value) {
		return (Div) super.css(property, value);
	}

	@Override
	public Div div() {
		return (Div) super.div();
	}
}
