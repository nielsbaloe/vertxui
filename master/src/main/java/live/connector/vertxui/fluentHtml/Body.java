package live.connector.vertxui.fluentHtml;

import elemental.dom.Element;

public class Body extends FluentHtml {

	protected Body(Element parent) {
		super(parent);
	}

	@Override
	public Div div() {
		return (Div) super.div();
	}
	
}
