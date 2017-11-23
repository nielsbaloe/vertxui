package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.google.gwt.core.client.EntryPoint;

public class Client implements EntryPoint {

	// public static String[] scripts = new String[] {
	// "https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js" };

	public Client() {

		body.p().txt(
				"Door het gebruik van deze energie calculatorBy using this calculator, I understand that this probably contains serious errors, "
						+ "and if I see an error or can be improved according to my knowledge, I will let it know.");
		new Heating();
		new Shower();
		new Cooking();
		new SolarTubes();
		new SolarPanels();
		new Stove();

		// Grafiek:
		// http://www.chartjs.org/docs/latest/
		// https://stackoverflow.com/questions/24590737/gwt-native-js-chart

	}

	@Override
	public void onModuleLoad() {
	}

}
