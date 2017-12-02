package live.connector.vertxui.samples.client.energyCalculator;

import java.util.ArrayList;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * http://www.chartjs.org/docs/latest/
 * 
 * @author ng
 *
 */
public class ChartJs extends Fluent {

	private String id = Math.random() + "";

	public ChartJs(Fluent root) {
		super("canvas", root);
		att(Att.width, "400", Att.height, "400", Att.id, id);

		Fluent.eval("var ctx = document.getElementById('" + getId() + "');									"
				+ "var data=  [{x: 10,  y: 20 }, { x: 15, y: 10 }];								"
				+ "var options=[ {showLines: false }];											"
				+ "var myLineChart = new Chart(ctx, {type: 'line', data: data, options: options});");

	}

	public String getId() {
		return id;
	}

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js");
		return result;
	}

}
