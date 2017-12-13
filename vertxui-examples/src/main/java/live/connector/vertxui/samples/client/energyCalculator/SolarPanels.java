package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class SolarPanels {

	private double quantity = 4, strength = 275, width = 1.650, length = 0.992;

	private ViewOn<?> conclusion;

	public SolarPanels(Fluent body) {

		body.h2(null, "Solar Panels");
		body.span(null, "I have ");
		body.add(Utils.getNumberInput().att(Att.value, quantity + "").keyup((fluent, ___) -> {
			quantity = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " solar panels wich each a peak of ");
		body.add(Utils.getNumberInput().att(Att.value, strength + "").keyup((fluent, ___) -> {
			strength = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " watt and with a width ");
		body.add(Utils.getNumberInput().att(Att.value, width + "").keyup((fluent, ___) -> {
			width = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " meter and length ");
		body.add(Utils.getNumberInput().att(Att.value, length + "").keyup((fluent, ___) -> {
			length = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " meter. ");
		body.br();
		body.br();

		conclusion = body.add(null, ___ -> {

			// not:
			// http://www.e2energie.nl/zonne-energie-productie-per-maand.html
			// not:
			// https://www.essent.nl/content/particulier/kennisbank/zonnepanelen/opbrengst-zonnepanelen-per-maand.html

			// http://www.zonurencalculator.nl/sun_hours_calculation

			StringBuilder text1 = new StringBuilder("The peak production is peak*quantity = ");
			double peak = quantity * strength;
			text1.append(Utils.show(peak));
			text1.append(" watt. In the Netherlands there are 1040 effective sun hours (2016) so that is ");
			text1.append(Utils.show(peak * 1040));
			text1.append(" watt per year.");

			StringBuilder text2 = new StringBuilder("The area for these solar collectors is ");
			double area = width * length * quantity;
			text2.append(Utils.show(area));
			text2.append(" m2 of roof.");

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is peak/area =");
			text3.append(Utils.show(peak / area));
			text3.append(" watt per m2.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.br();
			result.span(null, text3.toString());

			double yearly = peak * 1040;
			result.br();
			result.span(null, "Jan. (1,3%) =" + Utils.show(0.013 * yearly) + " watt");
			result.br();
			result.span(null, "Febr. (3,8%) =" + Utils.show(0.038 * yearly) + " watt");
			result.br();
			result.span(null, "Maart. (7,3%) =" + Utils.show(0.073 * yearly) + " watt");
			result.br();
			result.span(null, "April. (11,5%) =" + Utils.show(0.115 * yearly) + " watt");
			result.br();
			result.span(null, "Mei. (14,7%) =" + Utils.show(0.147 * yearly) + " watt");
			result.br();
			result.span(null, "Juni. (13,2%) =" + Utils.show(0.132 * yearly) + " watt");
			result.br();
			result.span(null, "Juli. (14,6%) =" + Utils.show(0.146 * yearly) + " watt");
			result.br();
			result.span(null, "Aug. (13,2%) =" + Utils.show(0.132 * yearly) + " watt");
			result.br();
			result.span(null, "Sept. (10,1%) =" + Utils.show(0.101 * yearly) + " watt");
			result.br();
			result.span(null, "Okt. (5,4%) =" + Utils.show(0.054 * yearly) + " watt");
			result.br();
			result.span(null, "Nov. (2,5%) =" + Utils.show(0.025 * yearly) + " watt");
			result.br();
			result.span(null, "Dec. (1,5%) =" + Utils.show(0.015 * yearly) + " watt");
			result.br();
			return result;
		});

	}

}
