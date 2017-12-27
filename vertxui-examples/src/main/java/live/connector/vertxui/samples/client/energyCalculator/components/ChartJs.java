package live.connector.vertxui.samples.client.energyCalculator.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * http://www.chartjs.org/docs/latest/
 * 
 * @author ng
 *
 */
public class ChartJs extends Fluent {

	private String id = Math.random() + "";

	private List<String> names;

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js");
		return result;
	}

	public ChartJs(Fluent root) {
		super("canvas", root);
		att(Att.width, "500", Att.height, "200", Att.id, id).css(Css.Float, "right");

		names = new ArrayList<>();

		String eval = "var ctx = document.getElementById('" + id + "');						"
				+ "var data=  { labels: ['Jan.', 'Febr.','Maart','April','Mei','Juni','July','Aug.','Sept.','Okt.','Nov.','Dec.'], datasets: [] };"
				+ "var chart = new Chart(ctx, {type:'line', data:data, options:{responsive:false,maintainAspectRatio:false}  });";
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
		String eval = "if (chart.data.datasets['" + position + "'] === undefined) {chart.data.datasets.push({});}"
				+ "chart.data.datasets['" + position + "'].label='" + title + "';  						"
				+ "chart.data.datasets['" + position + "'].data=" + Arrays.toString(data) + "; 			"
				+ "chart.data.datasets['" + position + "'].fill=false;									"
				+ "chart.data.datasets['" + position + "'].backgroundColor= '" + color + "';			"
				+ "chart.data.datasets['" + position + "'].borderColor= '" + color + "';				"
				+ "chart.update();";
		eval(eval);
	}

}
