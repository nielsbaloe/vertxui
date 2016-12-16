package live.connector.vertxui.fluentHtml;

import org.teavm.jso.dom.html.HTMLElement;

public class Body extends FluentHtml {

	protected Body(HTMLElement parent) {
		super(parent);
	}

	@Override
	public Div div() {
		return (Div) super.div();
	}
	
}
