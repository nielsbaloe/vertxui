package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;

public class SolarPanels {

	private double quantity = 4, strength = 275, width = 1.650, length = 0.992;

	private ViewOn<?> conclusion;
	private MonthTable monthTable;

	public SolarPanels(Fluent body) {

		body.h2(null, "Solar Panels");
		body.span(null, "I have ");
		body.add(new InputNumber().att(Att.value, quantity + "").keyup((fluent, ___) -> {
			quantity = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " solar panels wich each a peak of ");
		body.add(new InputNumber().att(Att.value, strength + "").keyup((fluent, ___) -> {
			strength = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " watt and with a width ");
		body.add(new InputNumber().att(Att.value, width + "").keyup((fluent, ___) -> {
			width = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " meter and length ");
		body.add(new InputNumber().att(Att.value, length + "").keyup((fluent, ___) -> {
			length = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " meter. ");
		body.br();
		body.br();

		// already create monthTable, so that we do not need to check for its
		// existance in the conclusion
		monthTable = new MonthTable(new String[] { "1,3% ", "3,8% ", "7,3% ", "11,5% ", "14,7% ", "13,2% ", "14,6% ",
				"13,2% ", "10,1% ", "5,4% ", "2,5% ", "1,5% " });

		conclusion = body.add(null, ___ -> {

			// not:
			// http://www.e2energie.nl/zonne-energie-productie-per-maand.html
			// not:
			// https://www.essent.nl/content/particulier/kennisbank/zonnepanelen/opbrengst-zonnepanelen-per-maand.html

			// http://www.zonurencalculator.nl/sun_hours_calculation

			StringBuilder text1 = new StringBuilder("The peak production is peak*quantity = ");
			double peak = quantity * strength;
			text1.append(InputNumber.show(peak));
			text1.append(" watt. In the Netherlands there are 1040 effective sun hours (2016) so that is ");
			text1.append(InputNumber.show(peak * 1040));
			text1.append(" watt per year.");

			StringBuilder text2 = new StringBuilder("The area for these solar collectors is ");
			double area = width * length * quantity;
			text2.append(InputNumber.show(area));
			text2.append(" m2 of roof.");

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is peak/area =");
			text3.append(InputNumber.show(peak / area));
			text3.append(" watt per m2.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.br();
			result.span(null, text3.toString());

			// update table
			double yearly = peak * 1040;
			monthTable.state2(new double[] { 0.013 * yearly, 0.038 * yearly, 0.073 * yearly, 0.115 * yearly,
					0.147 * yearly, 0.132 * yearly, 0.146 * yearly, 0.132 * yearly, 0.101 * yearly, 0.054 * yearly,
					0.025 * yearly, 0.015 * yearly });

			return result;
		});

		body.add(monthTable);
	}

}
