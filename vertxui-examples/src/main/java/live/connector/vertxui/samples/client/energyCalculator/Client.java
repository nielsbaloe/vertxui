package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	// public static String[] scripts = new String[] {
	// "https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js" };

	public Client() {

		Fluent root = Fluent.getElementById("here");
		if (root == null) {
			root = body;
		}
		root.p().txt(
				"By using this energy calculator, I understand that it is extremely beta, contains serious errors, "
						+ "does not take windows and doors and construction junctures and sun orientation into account yet, "
						+ "will be improved and does not contain any visual feedback yet. If I see an error or if I have "
						+ "the knowledge how to improve this calculator, I will definitely give feedback on this website.")
				.css(Css.color, "red");

		new Heating(root);
		new Shower(root);
		new Cooking(root);
		new SolarTubes(root);
		new SolarPanels(root);
		new Stove(root);

		// TODO
		// http://www.chartjs.org/docs/latest/
		// https://stackoverflow.com/questions/24590737/gwt-native-js-chart

	}

	@Override
	public void onModuleLoad() {
	}

}