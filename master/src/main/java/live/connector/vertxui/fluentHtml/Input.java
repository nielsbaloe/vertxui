package live.connector.vertxui.fluentHtml;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.HTMLInputElement;

public class Input extends FluentHtml {

	protected Input(String type, String name, FluentHtml element) {
		super("input", element);
		attr("type", type);
		attr("name", name);
	}

	@Override
	public Input keyUp(EventListener<KeyboardEvent> listener) {
		return (Input) super.keyUp(listener);
	}

	@Override
	public Input id(String string) {
		return (Input) super.id(string);
	}

	// TODO put into fluentHtml
	public Input disable() {
		((HTMLInputElement) element).setDisabled(true);
		return this;
	}

	// TODO put into fluentHtml
	public Input enable() {
		((HTMLInputElement) element).setDisabled(false);
		return this;
	}

	// TODO put into fluentHtml
	public String getValue() {
		return element.getNodeValue();
	}

}
