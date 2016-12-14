package live.connector.vertxui.fluidhtml;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

public class FluidHtml {

	protected final static HTMLDocument document = Window.current().getDocument();

	/**
	 * Javascript input for optimalisation only.
	 */
	@JSBody(params = { "javascript" }, script = "javascript")
	protected static native void javascript(String pureJs);

	protected HTMLElement element;

	protected FluidHtml(String tagName, FluidHtml parent) {
		element = document.createElement(tagName);
		parent.element.appendChild(element);
	}

	protected FluidHtml(HTMLElement parent) {
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

	protected FluidHtml inner(String innerHtml) {
		element.setInnerHTML(innerHtml);
		return this;
	}

	protected FluidHtml onClick(EventListener<MouseEvent> listener) {
		element.listenClick(listener);
		return this;
	}

	protected FluidHtml id(String string) {
		return attribute("id", string);
	}

	protected FluidHtml css(String property, String value) {
		element.getStyle().setProperty(property, value);
		return this;
	}

	protected Div div() {
		return new Div(this);
	}

	public Button button(String text) {
		return new Button(text, this);
	}

	protected FluidHtml attribute(String name, String value) {
		element.setAttribute(name, value);
		return this;
	}

}
