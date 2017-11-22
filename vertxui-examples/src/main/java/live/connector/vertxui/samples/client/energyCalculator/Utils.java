package live.connector.vertxui.samples.client.energyCalculator;

import java.util.function.Function;

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
		return Fluent.Input(null, "text").css(Css.width, "33px").keypress((Fluent ___, KeyboardEvent event) -> {
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
		if (value.endsWith(".") || value.length() == 0) {
			value += "0";
		}
		return Double.parseDouble(value);
	}

	public static Function<Surface, Fluent> showHandQ = surface -> {
		StringBuilder result = new StringBuilder();
		result.append("Surface A=");
		if (surface.sizeX == 0.0) {
			result.append("..");
		} else {
			result.append(surface.sizeX);
		}
		result.append("m * ");
		if (surface.sizeY == 0.0) {
			result.append("..");
		} else {
			result.append(surface.sizeY);
		}
		result.append("m = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0) {
			result.append("..");
		} else {
			result.append(showDouble(surface.getM2()));
		}
		result.append("m2. Heat transfer coefficient H = U * A = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0 || surface.thickness == 0.0) {
			result.append("..");
		} else {
			result.append(showDouble(surface.getM2() * surface.getU()));
		}
		return Fluent.Li().span(null, result.toString());
	};

	/**
	 * A method that converts a Surface object (with lambda and thickness) into
	 * a piece of GUI that shows the R and U values.
	 */

	public static String showDouble(double value) {
		String result = "" + value;
		if (result.length() > 4) {
			result = result.substring(0, 4);
		}
		return result;
	}

	public static Function<Surface, Fluent> showRandU = surface -> {
		StringBuilder text = new StringBuilder("So, the Rd = meter/lambda =");
		if (surface.thickness != 0.0) {
			text.append(showDouble(surface.getR()));
		} else {
			text.append("..");
		}
		text.append(" and the U=1/R=");
		if (surface.thickness != 0.0) {
			text.append(showDouble(surface.getU()));
		} else {
			text.append("..");
		}
		return Fluent.Span(null, text.toString());
	};

}
