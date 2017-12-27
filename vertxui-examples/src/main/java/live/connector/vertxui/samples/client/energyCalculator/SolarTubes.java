package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;

public class SolarTubes {

	private double collectors = 2, tubes = 18;

	public ViewOn<?> conclusion;

	public SolarTubes(Fluent body, ChartJs chart) {

		body.h2(null, "Solar tubes");
		body.span(null, "I want to have ");
		body.add(new InputNumber().att(Att.value, collectors + "").keyup((fluent, ___) -> {
			collectors = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " collectors with each ");
		body.add(new InputNumber().att(Att.value, tubes + "").keyup((fluent, ___) -> {
			tubes = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " heat tubes. ").br();
		conclusion = body.add(null, ___ -> {

			// 110.000: source:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017 with 45
			// degrees.

			// percentages:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017

			StringBuilder text1 = new StringBuilder("That would bring me a total of ");
			text1.append(InputNumber.show(collectors * tubes));
			text1.append(" heat pipes, which each give about 110.000 watt a year, so that is in total 110.000*");
			text1.append(InputNumber.show(collectors * tubes));
			text1.append("=");
			double yearly = collectors * tubes * 110000;
			text1.append(InputNumber.show(yearly));
			text1.append(" watt a year.");

			StringBuilder text2 = new StringBuilder(" The roof size per collector is about 1.98m height and ");
			double tubeWidth = 0.115;
			text2.append(InputNumber.show(tubeWidth));
			text2.append("m per pipe, so the total roof area is 1.98m * (");
			text2.append(InputNumber.show(tubeWidth));
			text2.append("*");
			text2.append(InputNumber.show(tubes));
			text2.append("*");
			text2.append(InputNumber.show(collectors));
			text2.append(") = ");
			double area = 1.98 * tubeWidth * tubes * collectors;
			text2.append(InputNumber.show(area));
			text2.append(" m2 roof.");

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is (yearly/1040)/area =");
			text3.append(InputNumber.show((yearly / 1040) / area));
			text3.append(" watt per m2.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.br();
			result.span(null, text3.toString());

			result.br();
			result.span(null, "Jan. (1,3%) =" + InputNumber.show(0.013 * yearly) + " watt");
			result.br();
			result.span(null, "Febr. (3,8%) =" + InputNumber.show(0.038 * yearly) + " watt");
			result.br();
			result.span(null, "Maart. (8,7%) =" + InputNumber.show(0.087 * yearly) + " watt");
			result.br();
			result.span(null, "April. (13,8%) =" + InputNumber.show(0.138 * yearly) + " watt");
			result.br();
			result.span(null, "Mei. (13%) =" + InputNumber.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Juni. (13%) =" + InputNumber.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Juli. (13%) =" + InputNumber.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Aug. (13%) =" + InputNumber.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Sept. (10%) =" + InputNumber.show(0.10 * yearly) + " watt");
			result.br();
			result.span(null, "Okt. (6,7%) =" + InputNumber.show(0.067 * yearly) + " watt");
			result.br();
			result.span(null, "Nov. (2,4%) =" + InputNumber.show(0.024 * yearly) + " watt");
			result.br();
			result.span(null, "Dec. (1,3%) =" + InputNumber.show(0.013 * yearly) + " watt");
			result.br();

			chart.showData("Solar tubes", "brown",
					new double[] { 0.013 * yearly, 0.038 * yearly, 0.087 * yearly, 0.138 * yearly, 0.13 * yearly,
							0.13 * yearly, 0.13 * yearly, 0.13 * yearly, 0.10 * yearly, 0.067 * yearly, 0.024 * yearly,
							0.013 * yearly });

			return result;
		});

	}
}
