package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class SolarTubes {

	private double collectors = 2, tubes = 18;

	public ViewOn<?> conclusion;

	public SolarTubes(Fluent body) {

		body.h2(null, "Solar tubes");
		body.span(null, "I want to have ");
		body.add(Utils.getNumberInput().att(Att.value, collectors + "").keyup((fluent, ___) -> {
			collectors = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " collectors with each ");
		body.add(Utils.getNumberInput().att(Att.value, tubes + "").keyup((fluent, ___) -> {
			tubes = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " heat tubes. ").br();
		conclusion = body.add(null, ___ -> {
			StringBuilder text1 = new StringBuilder("That would bring me a total of ");
			text1.append(Utils.show(collectors * tubes));
			// 110.000: source:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017 with 45
			// degrees.
			text1.append(" heat pipes, which each give about 110.000 watt a year, so that is in total 110.000*");
			text1.append(Utils.show(collectors * tubes));
			text1.append("=");
			double yearly = collectors * tubes * 110000;
			text1.append(Utils.show(yearly));
			text1.append(" watt a year.");

			StringBuilder text2 = new StringBuilder(" The roof size per collector is about 1.98m height and ");
			double tubeWidth = 0.115;
			text2.append(Utils.show(tubeWidth));
			text2.append("m per pipe, so the total roof area is 1.98m * (");
			text2.append(Utils.show(tubeWidth));
			text2.append("*");
			text2.append(Utils.show(tubes));
			text2.append("*");
			text2.append(Utils.show(collectors));
			text2.append(") = ");
			double area = 1.98 * tubeWidth * tubes * collectors;
			text2.append(Utils.show(area));
			text2.append(" m2 roof.");

			StringBuilder text3 = new StringBuilder("The efficiency of the panels is (yearly/1040)/area =");
			text3.append(Utils.show((yearly / 1040) / area));
			text3.append(" watt per m2.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.br();
			result.span(null, text3.toString());

			// percentages:
			// https://econo.nl/berekening-zonneboiler-subsidie-2017
			result.br();
			result.span(null, "Jan. (1,3%) =" + Utils.show(0.013 * yearly) + " watt");
			result.br();
			result.span(null, "Febr. (3,8%) =" + Utils.show(0.038 * yearly) + " watt");
			result.br();
			result.span(null, "Maart. (8,7%) =" + Utils.show(0.087 * yearly) + " watt");
			result.br();
			result.span(null, "April. (13,8%) =" + Utils.show(0.138 * yearly) + " watt");
			result.br();
			result.span(null, "Mei. (13%) =" + Utils.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Juni. (13%) =" + Utils.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Juli. (13%) =" + Utils.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Aug. (13%) =" + Utils.show(0.13 * yearly) + " watt");
			result.br();
			result.span(null, "Sept. (10%) =" + Utils.show(0.10 * yearly) + " watt");
			result.br();
			result.span(null, "Okt. (6,7%) =" + Utils.show(0.067 * yearly) + " watt");
			result.br();
			result.span(null, "Nov. (2,4%) =" + Utils.show(0.024 * yearly) + " watt");
			result.br();
			result.span(null, "Dec. (1,3%) =" + Utils.show(0.013 * yearly) + " watt");
			result.br();
			return result;
		});

	}
}
