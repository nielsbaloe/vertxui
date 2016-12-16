package live.connector.vertxui.fluentHtml;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

public class FluentHtml {

	protected final static HTMLDocument document = Window.current().getDocument();

	/**
	 * Javascript input for optimalisation only.
	 */
	@JSBody(params = { "javascript" }, script = "javascript")
	protected static native void javascript(String pureJs);

	protected HTMLElement element;

	/**
	 * If we create an object with 'new' instead of the fluent notation, we
	 * should only add to the parent once.
	 */
	protected boolean appendedToParent;

	/**
	 * Add the object. The parent is only null when we create objects that will
	 * be appended later on.
	 * 
	 */
	protected FluentHtml(String tagName, FluentHtml parent) {
		element = document.createElement(tagName);
		if (parent != null) {
			parent.element.appendChild(element);
			appendedToParent = true;
		} else {
			appendedToParent = false;
		}
	}

	/**
	 * For when this represents an existing object: getBody() getHead() and
	 * dom().
	 * 
	 * @param parent
	 *            the existing object
	 */
	protected FluentHtml(HTMLElement parent) {
		element = parent;
		appendedToParent = true;
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

	protected FluentHtml inner(String innerHtml) {
		element.setInnerHTML(innerHtml);
		return this;
	}

	protected FluentHtml onClick(EventListener<MouseEvent> listener) {
		element.listenClick(listener);
		return this;
	}

	protected FluentHtml id(String string) {
		return attribute("id", string);
	}

	protected FluentHtml css(String property, String value) {
		element.getStyle().setProperty(property, value);
		return this;
	}

	protected Div div() {
		return new Div(this);
	}

	public Button button(String text) {
		return new Button(text, this);
	}

	protected FluentHtml attribute(String name, String value) {
		element.setAttribute(name, value);
		return this;
	}

	public Li li(String text) {
		return new Li(text, this);
	}

}
