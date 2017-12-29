package live.connector.vertxui.samples.client.energyCalculator.components;

import com.google.gwt.i18n.client.NumberFormat;

import elemental.events.KeyboardEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * An GUI input html piece which only allows numbers.
 * 
 */
public class InputNumber extends Fluent {

	public InputNumber() {
		super("input", null);
		att(Att.type, "text").css(Css.width, "39px").keypress((Fluent ___, KeyboardEvent event) -> {
			int code = event.getCharCode();
			if ((code >= 48 && code <= 57) || code == 0 || code == 46) {
				return;
			}
			event.preventDefault();
		});
	}

	/**
	 * Get the double value out of this input field.
	 * 
	 * @param fluent
	 *            the input field
	 * @return a floating point number that has been entered.
	 */
	public double domValueDouble() {
		String value = super.domValue();
		// correct if there is no input at some point, or a dot is the last
		// input now
		if (value.length() == 0 || value.endsWith(".")) {
			value += "0";
		}
		return Double.parseDouble(value);
	}

	private static NumberFormat numberFormat = NumberFormat.getFormat("##,###.##");

	/**
	 * Helper method to show a number with maximum of two digits and a dot, with
	 * a comma for each thousand.
	 */
	public static String show(double value) {
		return numberFormat.format(value);
	}

}
