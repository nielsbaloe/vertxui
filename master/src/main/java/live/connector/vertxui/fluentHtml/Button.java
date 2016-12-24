package live.connector.vertxui.fluentHtml;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLButtonElement;

public class Button extends FluentHtml {

	protected Button(FluentHtml element) {
		super("button", element);
	}

	@Override
	public Button inner(String innerHtml) {
		return (Button) super.inner(innerHtml);
	}

	public Button click(EventListener<MouseEvent> listener) {
		return (Button) super.listen(NameListen.click, listener);
	}

	@Override
	public Button id(String string) {
		return (Button) super.id(string);
	}

	public Button disable() {
		((HTMLButtonElement) element).setDisabled(true);
		return this;
	}

	public Button enable() {
		((HTMLButtonElement) element).setDisabled(false);
		return this;
	}

}
