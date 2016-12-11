package live.connector.vertxui.fluidhtml;

import org.teavm.jso.dom.html.HTMLElement;

public class Body extends Html {

	protected Body(HTMLElement parent) {
		super(parent);
	}

	@Override
	public Div div() {
		return (Div) super.div();
	}
	
}
