package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Client implements EntryPoint {

	private Dimensions dimensions = new Dimensions();

	private ViewOn<Dimensions> cubic;

	public Client() {

		body.div().txt("By using this calculator, I understand that this probably contains serious errors, "
				+ "and if I see an error according to my knowledge, I will let it know.");

		Fluent heating = body.div();
		heating.h1(null, "Heating");
		heating.span().txt("The room I want to heat has a width of ");
		heating.input(null, "text").keypress(this::onlyNumeric).keyup(this::onWidth).css(Css.width, "30px");
		heating.span().txt(" meter and a length of ");
		heating.input(null, "text").keypress(this::onlyNumeric).keyup(this::onLength).css(Css.width, "30px");
		heating.span().txt(" meter and a height of ");
		heating.input(null, "text").keypress(this::onlyNumeric).keyup(this::onHeight).css(Css.width, "30px");
		heating.span().txt(" meter.");
		heating.br();
		heating.span().txt("So my room has a volume of ");
		cubic = heating.add(dimensions, d -> {
			return Fluent.Span().txt("" + (d.width * d.height * d.length)).css(Css.fontStyle, "italic");
		});
		heating.span().txt(" cubic meter.");

	}

	private void onlyNumeric(Fluent __, KeyboardEvent event) {
		int code = event.getCharCode();
		if ((code >= 48 && code <= 57) || code == 0 || code == 46) {
			return; // numeric or a not-a-character is OK
		}
		event.preventDefault();
	}

	private void onWidth(Fluent fluent, KeyboardEvent event) {
		String value = fluent.domValue();
		if (value.endsWith(".") || value.length() == 0) {
			value += "0";
		}
		dimensions.width = Float.parseFloat(value);
		cubic.sync();
	}

	private void onLength(Fluent fluent, KeyboardEvent event) {
		String value = fluent.domValue();
		if (value.endsWith(".") || value.length() == 0) {
			value += "0";
		}
		dimensions.length = Float.parseFloat(value);
		cubic.sync();
	}

	private void onHeight(Fluent fluent, KeyboardEvent event) {
		String value = fluent.domValue();
		if (value.endsWith(".") || value.length() == 0) {
			value += "0";
		}
		dimensions.height = Float.parseFloat(value);
		cubic.sync();
	}

	@Override
	public void onModuleLoad() {
	}

}
