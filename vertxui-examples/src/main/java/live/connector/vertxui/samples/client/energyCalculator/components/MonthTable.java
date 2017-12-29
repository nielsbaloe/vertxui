package live.connector.vertxui.samples.client.energyCalculator.components;

import static live.connector.vertxui.client.fluent.Fluent.Table;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOnBoth;

public class MonthTable extends ViewOnBoth<String[], double[]> {

	public static final String[] months = new String[] { "Jan.", "Febr.", "March", "April", "May", "June", "July",
			"Aug.", "Sept.", "Oct.", "Nov.", "Dec." };

	public MonthTable(String[] infoInitial) {
		super(infoInitial, null, (infos, datas) -> {
			Fluent result = Table().css(Css.border, "1px solid black", Css.width, "100%");

			Fluent header = result.tr();
			for (String month : months) {
				header.th(null, month).css(Css.border, "1px solid black");
			}

			Fluent infoRow = result.tr();
			if (infos != null) {
				for (String info : infos) {
					infoRow.td(null, info).css(Css.border, "1px solid black");
				}
			}

			Fluent dataRow = result.tr();
			if (datas != null) {
				for (double data : datas) {
					dataRow.td(null, InputNumber.show(Math.round(data)) + " watt").css(Css.border, "1px solid black");
				}
			}

			return result;
		});

	}

}
