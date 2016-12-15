package live.connector.vertxui.fluidhtml;

import java.lang.invoke.MethodHandles;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.teavm.jso.dom.html.HTMLElement;

public class Div extends FluidHtml {

	protected Div(FluidHtml parent) {
		super("div", parent);
	}

	protected Div(HTMLElement element) {
		super(element);
	}

	/**
	 * Get an existing object from the dom by 'id'.
	 * 
	 * // TODO refactor: move up to FluidHtml
	 * 
	 * @param id
	 *            the id
	 * @return a fluid-html object
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
	public Div div() {
		return (Div) super.div();
	}

	// TODO as test
	public Div div(Stream<FluidHtml> fluidHtmls) {
		Div result = (Div) super.div();
		for (FluidHtml fluidHtml : fluidHtmls.collect(Collectors.toList())) {
			result.element.appendChild(fluidHtml.element);
		}
		return result;
	}

	@Override
	public Li li(String text) {
		return (Li) super.li(text);
	}

}
