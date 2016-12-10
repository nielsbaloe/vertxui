package live.connector.vertxui.fluidhtml;

import org.teavm.jso.dom.html.HTMLElement;

public class Div extends Html {

	protected Div(HTMLElement parent) {
		super("div", parent);
	}

	protected Div(String inner, HTMLElement parent) {
		super("div", parent);
		inner(inner);
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

}
