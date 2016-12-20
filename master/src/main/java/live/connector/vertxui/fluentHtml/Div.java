package live.connector.vertxui.fluentHtml;

import java.lang.invoke.MethodHandles;

import org.teavm.jso.dom.html.HTMLElement;

public class Div extends FluentHtml {

	protected Div(FluentHtml parent) {
		super("div", parent);
	}

	protected Div(HTMLElement element) {
		super(element);
	}

	/**
	 * Get an existing object from the dom by 'id'.
	 * 
	 * @param id
	 *            the id
	 * @return a fluent html object
	 */

	public static Div dom(String id) {
		HTMLElement found = document.getElementById("id");
		if (!found.getTagName().equals(MethodHandles.lookup().lookupClass().getName().toLowerCase())) {
			throw new IllegalArgumentException(
					"Requested non-existing dom with id=" + id + ": tagname=" + found.getTagName()
							+ " requesting tagname=" + MethodHandles.lookup().lookupClass().getName().toLowerCase());
		}
		return new Div(found);
	}

	@Override
	public Div inner(String innerHtml) {
		return (Div) super.inner(innerHtml);
	}

	@Override
	public Div id(String string) {
		return (Div) super.id(string);
	}

	@Override
	public Div css(String property, String value) {
		return (Div) super.css(property, value);
	}

	@Override
	public Button button(String text) {
		return (Button) super.button(text);
	}

	@Override
	public Div div(FluentHtml... fluentHtmls) {
		return (Div) super.div(fluentHtmls);
	}

	@Override
	public Li li(String text) {
		return (Li) super.li(text);
	}

	@Override
	public Input input(String type, String name) {
		return (Input) super.input(type, name);
	}

	/**
	 * Add a non-fluid (with 'new') created object. Note that writing fluent
	 * code is preferred.
	 * 
	 * @param app
	 */
	public void append(FluentHtml app) {
		if (app.appendedToParent) {
			throw new IllegalArgumentException("Already added to a dom (fluently or with append()): " + app);
		}
		element.appendChild(app.element);
		app.appendedToParent = true;
	}

}
