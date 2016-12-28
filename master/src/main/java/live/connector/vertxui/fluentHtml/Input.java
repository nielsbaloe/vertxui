package live.connector.vertxui.fluentHtml;

import elemental.events.EventListener;

public class Input extends FluentHtml {

	protected Input(FluentHtml element) {
		super("input", element);
	}

	public Input keyUp(EventListener listener) {
		return (Input) super.listen(Event.keyup, listener);
	}

	@Override
	public Input id(String string) {
		return (Input) super.id(string);
	}

}
