package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class SolarPanels {

	private double quantity = 3, strength = 280, width = 1.650, length = 0.992;
	private ViewOn<?> conclusion;
	private MonthTable monthTable;
	private double totalLength;
	private boolean enabled = true;

	public SolarPanels(Fluent body, Client client) {
		client.setSolarPanels(this);

		body.h2(null, "Solar Panels");
		body.input(null, "checkbox").att(Att.checked, "1").click((fluent, b) -> {
			this.enabled = fluent.domChecked();
			conclusion.sync();
		});
		body.span(null, " I want ");

		body.select(null, quantity + "", Utils.getSelectNumbers(0, 1, 100)).changed((fluent, ___) -> {
			quantity = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " solar panels each ");
		body.select(null, "280W 1.65x0.992",
				new String[] { "280W 1.65x0.992", "280W 1.65x0.992", "290W 1.65x0.992", "290W 1.65x0.992",
						"300W 1.65x0.992", "300W 1.65x0.992", "330W 1.559x1.046", "330W 1.559x1.046",
						"370W 1.710x1.016", "370W 1.710x1.016" })
				.changed((fluent, ___) -> {
					String selected = fluent.domSelectedOptions()[0];
					if (selected.equals("280W 1.65x0.992")) {
						strength = 280;
						width = 1.650;
						length = 0.992;
					} else if (selected.equals("290W 1.65x0.992")) {
						strength = 290;
						width = 1.650;
						length = 0.992;
					} else if (selected.equals("300W 1.65x0.992")) {
						strength = 300;
						width = 1.650;
						length = 0.992;
					} else if (selected.equals("330W 1.559x1.046")) {
						strength = 330;
						width = 1.559;
						length = 1.046;
					} else if (selected.equals("370W 1.710x1.016")) {
						strength = 370;
						width = 1.710;
						length = 1.016;
					}
					conclusion.sync();
				});
		body.br();

		// already create monthTable, so that we do not need to check for its
		// existance in the conclusion
		// Source solar hours:
		// http://www.zonurencalculator.nl/sun_hours_calculation
		monthTable = new MonthTable(new String[] { "1,3% ", "3,8% ", "7,3% ", "11,5% ", "14,7% ", "13,2% ", "14,6% ",
				"13,2% ", "10,1% ", "5,4% ", "2,5% ", "1,5% " });

		conclusion = body.add(null, ___ -> {

			StringBuilder text1 = new StringBuilder("The peak production is peak*quantity = ");
			double peak = quantity * strength;
			text1.append(Utils.format(peak));
			text1.append(" watt. In the Netherlands there are 1040 effective sun hours (2016) so that is ");
			text1.append(Utils.format(Math.round(peak * 1040 * 0.001)));
			text1.append(" kW per year.");

			StringBuilder text2 = new StringBuilder("Roof size: ");
			double area = width * length * quantity;
			text2.append(Utils.format(area));
			text2.append(" m2 of roof.");

			totalLength = length * quantity;
			client.getHeating().warnPanelsLength();

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is peak/area =");
			text3.append(Utils.format(peak / area));
			text3.append(" watt per m2.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.br();
			result.span(null, text3.toString());

			// update table
			double yearly = peak * 1040;
			double data[] = new double[] { 0.013 * yearly, 0.038 * yearly, 0.073 * yearly, 0.115 * yearly,
					0.147 * yearly, 0.132 * yearly, 0.146 * yearly, 0.132 * yearly, 0.101 * yearly, 0.054 * yearly,
					0.025 * yearly, 0.015 * yearly };
			monthTable.state2(data);

			if (!enabled) {
				for (int x = 0; x != data.length; x++) {
					data[x] = 0.0;
				}
			}
			client.getElectricChart().showData("Solar panels", "green", data);

			return result;
		});

		body.add(monthTable);
	}

	public double getTotalLength() {
		return totalLength;
	}

}
