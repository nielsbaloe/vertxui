package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class SolarTubes {

	private double quantity = 2, tubes = 20;
	private ViewOn<?> conclusion;
	private MonthTable monthTable;
	private double[] result = new double[12];
	private double totalLength;

	public SolarTubes(Fluent body, ChartJs chart, Client client) {
		client.setSolarTubes(this);

		body.h2(null, "Solar tubes");
		body.span(null, "I want ");
		body.select(null, quantity + "", Utils.getSelectNumbers(0, 1, 100)).changed((fluent, ___) -> {
			quantity = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " collectors with each ");
		body.select(null, tubes + "", "8", "8", "18", "18", "20", "20", "24", "24", "30", "30", "36", "36", "48", "48",
				"54", "54").changed((fluent, ___) -> {
					tubes = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " heat tubes. ").br();

		// already create monthTable, so that we do not need to check for its
		// existance in the conclusion
		monthTable = new MonthTable(new String[] { "1,3% ", "3,8% ", "7,3% ", "11,5% ", "14,7% ", "13,2% ", "14,6% ",
				"13,2% ", "10,1% ", "5,4% ", "2,5% ", "1,5% " });

		conclusion = body.add(null, ___ -> {

			// Source 110.000:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017 with 45
			// degrees.
			// percentages:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017

			StringBuilder text1 = new StringBuilder("Total: ");
			text1.append(Utils.format(quantity * tubes));
			text1.append(" heat pipes = 110,000 W * ");
			text1.append(Utils.format(quantity * tubes));
			text1.append(" = ");
			double yearly = quantity * tubes * 1100_00;
			text1.append(Utils.format(yearly));
			text1.append(" watt a year.");

			StringBuilder text2 = new StringBuilder(" Size: 1.98m x (0.115*");
			text2.append(Utils.format(tubes));
			text2.append("*");
			text2.append(Utils.format(quantity));
			text2.append(") = 1.98m x ");
			double tubeWidth = 0.115;
			double length = tubeWidth * tubes * quantity;
			text2.append(Utils.format(length));
			text2.append("m = ");
			double area = 1.98 * length;
			text2.append(Utils.format(area));
			text2.append(" m2 roof.");

			totalLength = tubeWidth * tubes * quantity;
			client.getHeating().warnLength();

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is (yearly/1040)/area =");
			text3.append(Utils.format((yearly / 1040) / area));
			text3.append(" watt per m2.");

			Fluent returner = Fluent.P();
			returner.span(null, text1.toString());
			returner.br();
			returner.span(null, text2.toString());
			returner.br();
			returner.span(null, text3.toString());
			returner.add(monthTable);

			// update chart and table
			result = new double[] { 0.013 * yearly, 0.038 * yearly, 0.087 * yearly, 0.138 * yearly, 0.13 * yearly,
					0.13 * yearly, 0.13 * yearly, 0.13 * yearly, 0.10 * yearly, 0.067 * yearly, 0.024 * yearly,
					0.013 * yearly };
			chart.showData("Solar tubes", "green", result);
			monthTable.state2(result);
			client.getHeating().updateHeatgap();

			return returner;
		});

	}

	public double[] getResult() {
		return result;
	}

	public double getTotalLength() {
		return totalLength;
	}

}
