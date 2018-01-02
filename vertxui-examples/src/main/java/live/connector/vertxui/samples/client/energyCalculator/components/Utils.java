package live.connector.vertxui.samples.client.energyCalculator.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;

public class Utils {

	private static NumberFormat numberFormat = NumberFormat.getFormat("##,###.##");

	/**
	 * Helper method to show a number with maximum of two digits and a dot, with
	 * a comma for each thousand.
	 */
	public static String format(double value) {
		return numberFormat.format(value);
	}

	public static String[] getSelectNumbers(double start, double step, double stop) {
		List<String> result = new ArrayList<String>();
		for (; start <= stop; start += step) {
			result.add(format(start));
			result.add(format(start));
		}
		return result.toArray(new String[result.size()]);
	}

}
