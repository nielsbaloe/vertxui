package live.connector.vertxui.samples.client.energyCalculator.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * http://www.chartjs.org/docs/latest/
 * 
 * @author ng
 *
 */
public class ChartJs extends Fluent {

	private String id = "chartJs" + Math.round(Math.random() * 1000000000);

	private List<String> names = new ArrayList<>();

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js");
		return result;
	}

	public ChartJs(Fluent root, int width, int height) {
		super("canvas", root);
		att(Att.id, id, Att.width, width + "", Att.height, 200 + "");

		String months = Stream.of(MonthTable.months).map(month -> "'" + month + "'").collect(Collectors.joining(","));
		String eval = "var canvas = document.getElementById('" + id + "');										"
				+ "var data = { labels: [" + months + "], datasets: [] };										"
				+ "var options = { responsive: false, maintainAspectRatio: false };								"
				+ "var c" + id + " = new Chart( canvas, { type: 'line', data: data, options: options });		";
		// Fluent.console.log(eval);
		eval(eval);
	}

	public void showData(String title, String color, double[] data) {
		// round data
		for (int x = 0; x < data.length; x++) {
			data[x] = Math.round(data[x]);
		}

		// get position in dataset array
		if (!names.contains(title)) {
			names.add(title);
		}
		int position = names.indexOf(title);

		// show
		String eval = "var cdata= c" + id + ".data;																"
				+ "if (cdata.datasets['" + position + "'] === undefined) {cdata.datasets.push({});}"
				+ "cdata.datasets['" + position + "'].label='" + title + "';  						"
				+ "cdata.datasets['" + position + "'].data=" + Arrays.toString(data) + "; 			"
				+ "cdata.datasets['" + position + "'].fill=false;									"
				+ "cdata.datasets['" + position + "'].backgroundColor= '" + color + "';				"
				+ "cdata.datasets['" + position + "'].borderColor= '" + color + "';";
		eval += "c" + id + ".update();";
		eval(eval);
	}

}
