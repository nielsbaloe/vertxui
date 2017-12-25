package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	public static ArrayList<String> getScripts() {
		return ChartJs.getScripts();
	}

	public Client() {
		// try to put everthing in <div id=here />
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

		ChartJs chart = new ChartJs(root);
		chart.css(Css.position, "sticky");
		chart.css(Css.top, 0);
		new Heating(root);
		new Shower(root, chart);
		new Cooking(root);
		new SolarTubes(root, chart);
		new SolarPanels(root);
		new Stove(root);

	}

	@Override
	public void onModuleLoad() {
	}

}
