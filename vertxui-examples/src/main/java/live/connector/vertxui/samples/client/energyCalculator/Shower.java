package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class Shower {

	private double minutes = 9, literPerMinute = 7, degrees = 38, timesPerWeek = 7, waterTemperature = 11;
	private ViewOn<?> conclusion;
	private double[] result = new double[12];

	public Shower(Fluent body, ChartJs chart, Client client) {
		client.setShower(this);

		body.h2(null, "Shower");
		body.span(null, "I usually shower about ");
		body.select(null, minutes + "", Utils.getSelectNumbers(1.0, 1, 15.0)).changed((fluent, ___) -> {
			minutes = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " minutes with a shower head that uses ");
		body.select(null, literPerMinute + "", Utils.getSelectNumbers(5, 1, 12)).changed((fluent, ___) -> {
			literPerMinute = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " liters per minute, and I like to shower at ");
		body.select(null, degrees + "", Utils.getSelectNumbers(30, 1, 45)).changed((fluent, ___) -> {
			degrees = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " degrees. I shower about ");
		body.select(null, timesPerWeek + "", Utils.getSelectNumbers(1, 1, 7)).changed((fluent, ___) -> {
			timesPerWeek = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " times per week.");
		conclusion = body.add(null, ___ -> {
			double totalLiters = minutes * literPerMinute;
			double delta = degrees - waterTemperature;

			StringBuilder text1 = new StringBuilder("So this means that in total ");
			text1.append(Utils.format(totalLiters));
			text1.append(" liters is used, which needs to be heated up about ");
			text1.append(Utils.format(degrees) + "-" + Utils.format(waterTemperature) + "=" + Utils.format(delta));
			text1.append(" degrees. This takes liters*degrees*1.16W = ");
			text1.append(Utils.format(totalLiters));
			text1.append("*");
			text1.append(Utils.format(delta));
			text1.append("*1.16=");
			double perShower = Math.round(totalLiters * delta * 1.16);
			text1.append(Utils.format(perShower));
			text1.append(" watt per showering.");

			StringBuilder text2 = new StringBuilder("So this is more or less (30/7)*");
			text2.append(Utils.format(timesPerWeek));
			text2.append(")*");
			text2.append(Utils.format(perShower));
			text2.append(" = ");

			double resultPerMonth = Math.round(totalLiters * delta * 1.16 * timesPerWeek * (30.0 / 7.0));

			text2.append(Utils.format(resultPerMonth));
			text2.append(" watt per month.");

			result = new double[] { resultPerMonth, resultPerMonth, resultPerMonth, resultPerMonth, resultPerMonth,
					resultPerMonth, resultPerMonth, resultPerMonth, resultPerMonth, resultPerMonth, resultPerMonth,
					resultPerMonth };
			chart.showData("Shower", "darkblue", result);
			if (client.getHeating() != null) {
				client.getHeating().updateHeatingPlusShower();
			}

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			return result;
		});

	}

	public double[] getResult() {
		return result;
	}
}
