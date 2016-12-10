package live.connector.vertxui.fluidhtml;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

public class Html {

	private final static HTMLDocument document = Window.current().getDocument();

	protected HTMLElement element;

	protected Html(String tagName, HTMLElement parent) {
		if (parent == null) {
			element = (HTMLElement) document.getBody();
		} else {
			element = document.createElement(tagName);
			parent.appendChild(element);
		}
	}

	public static Html body() {
		return new Html(null, null);
	}

	public Element el() {
		return element;
	}

	public Html inner(String innerHtml) {
		element.setInnerHTML(innerHtml);
		return this;
	}

	public Html onClick(EventListener<MouseEvent> listener) {
		element.listenClick(listener);
		return this;
	}

	public Button button(String text) {
		return new Button(text, element);
	}

	public Div div() {
		return new Div(element);
	}

	public Div div(String inner) {
		return new Div(inner, element);
	}

	public Html id(String string) {
		element.setAttribute("id", string);
		return this;
	}

	public Html css(String property, String value) {
		element.getStyle().setProperty(property, value);
		return this;
	}

}
