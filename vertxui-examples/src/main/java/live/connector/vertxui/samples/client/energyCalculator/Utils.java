package live.connector.vertxui.samples.client.energyCalculator;

import com.google.gwt.i18n.client.NumberFormat;

import elemental.events.KeyboardEvent;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

public class Utils {

	/**
	 * An GUI input piece which only allows numbers.
	 * 
	 * @return a input field which only allows numeric entry
	 */
	public static Fluent getNumberInput() {
		return Fluent.Input(null, "text").css(Css.width, "39px").keypress((Fluent ___, KeyboardEvent event) -> {
			int code = event.getCharCode();
			if ((code >= 48 && code <= 57) || code == 0 || code == 46) {
				return;
			}
			event.preventDefault();
		});
	}

	/**
	 * Get the double value out of an input field.
	 * 
	 * @param fluent
	 *            the input field
	 * @return a floating point number that has been entered.
	 */
	public static double getDomNumber(Fluent fluent) {
		String value = fluent.domValue();
		if (value.length() == 0 || value.endsWith(".")) {
			value += "0";
		}
		return Double.parseDouble(value);
	}

	/**
	 * A method that converts a Surface object (with lambda and thickness) into
	 * a piece of GUI that shows the R and U values.
	 */

	private static NumberFormat numberFormat = NumberFormat.getFormat("##,###.##");

	public static String show(double value) {
		return numberFormat.format(value);
	}

}
