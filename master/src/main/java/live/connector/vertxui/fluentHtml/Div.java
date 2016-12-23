package live.connector.vertxui.fluentHtml;

import java.util.List;

public class Div extends FluentHtml {

	protected Div(FluentHtml parent) {
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
	public Button button(String text) {
		return (Button) super.button(text);
	}

	@Override
	public Div div(List<? extends FluentHtml> list) {
		return (Div) super.div(list);
	}

	@Override
	public Div div(FluentHtml[] list) {
		return (Div) super.div(list);
	}

	@Override
	public Li li(String text) {
		return (Li) super.li(text);
	}

	@Override
	public Input input(String type, String name) {
		return (Input) super.input(type, name);
	}

	public void hidden(boolean b) {
		element.setHidden(b);
	}

}
