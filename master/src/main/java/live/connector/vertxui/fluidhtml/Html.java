package live.connector.vertxui.fluidhtml;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

public class Html {

	protected final static HTMLDocument document = Window.current().getDocument();

	/**
	 * Javascript input for optimalisation only.
	 */
	@JSBody(params = { "javascript" }, script = "javascript")
	protected static native void javascript(String pureJs);

	protected HTMLElement element;

	protected Html(String tagName, Html parent) {
		element = document.createElement(tagName);
		parent.element.appendChild(element);
	}

	protected Html(HTMLElement parent) {
		element = parent;
	}

	/**
	 * Do not create but GET the body.
	 * 
	 */
	public static Body getBody() {
		return new Body(document.getBody());
	}

	/**
	 * Do not create but GET the head.
	 * 
	 */
	public static Head getHead() {
		return new Head(document.getHead());
	}

	/**
	 * @return as org.w3 element object.
	 */
	public Element dom() {
		return element;
	}

	protected Html inner(String innerHtml) {
		element.setInnerHTML(innerHtml);
		return this;
	}

	protected Html onClick(EventListener<MouseEvent> listener) {
		element.listenClick(listener);
		return this;
	}

	protected Html id(String string) {
		return attribute("id", string);
	}

	protected Html css(String property, String value) {
		element.getStyle().setProperty(property, value);
		return this;
	}

	protected Div div() {
		return new Div(this);
	}

	public Button button(String text) {
		return new Button(text, this);
	}

	protected Html attribute(String name, String value) {
		element.setAttribute(name, value);
		return this;
	}

}
