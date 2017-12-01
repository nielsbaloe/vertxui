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

	public ChartJs() {
		super("canvas", null);
		att(Att.width, "400", Att.height, "400").id(id);

		eval("var ctx = document.getElementById('" + id + "');									"
				+ "console.log(id);																"
				+ "var data=  [{x: 10,  y: 20 }, { x: 15, y: 10 }];								"
				+ "var options=[ {showLines: false }];											"
				+ "var myLineChart = new Chart(ctx, {type: 'line', data: data, options: options});");
	}

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js");
		return result;
	}

}
