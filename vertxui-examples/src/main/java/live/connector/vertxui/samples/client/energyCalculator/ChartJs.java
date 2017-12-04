package live.connector.vertxui.samples.client.energyCalculator;

import java.util.ArrayList;
import java.util.Arrays;

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

	public ChartJs(Fluent root) {
		super("canvas", root);
		att(Att.width, "500", Att.height, "200", Att.id, id).css(Css.Float, "right");
		String eval = "var ctx = document.getElementById('" + id + "');						"
				+ "var data=  { labels: ['Jan.', 'Febr.','Maart','April','Mei','Juni','July','Aug.','Sept.','Okt.','Nov.','Dec.'], datasets: [] };"
				+ "var chart = new Chart(ctx, {type:'line', data:data, options:{responsive:false,maintainAspectRatio:false}  });";
		eval(eval);
		console.log(Css.Float.nameValid());
		showData(0, "whatever", new int[] { 1, 2, 3, 4, 5, 6, 7, 5, 9, 0, 6, 2 });
	}

	public void showData(int position, String title, int[] data) {
		String eval = "if (chart.data.datasets['" + position + "'] === undefined) {chart.data.datasets.push({});}"
				+ "chart.data.datasets['" + position + "'].label='" + title + "';  						"
				+ "chart.data.datasets['" + position + "'].data=" + Arrays.toString(data) + "; 			"
				+ "chart.data.datasets['" + position + "'].fill=false;									"
				+ "chart.data.datasets['" + position + "'].backgroundColor= 'red';						"
				+ "chart.data.datasets['" + position + "'].borderColor= 'red';							"
				+ "chart.update();";
		eval(eval);
	}

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js");
		return result;
	}

}
