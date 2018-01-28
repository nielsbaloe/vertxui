package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class Shower {

	private double duration = 9, lPerMinute = 7, degrees = 38, timesPerWeek = 7, waterDegrees = 11, people = 1;
	private ViewOn<?> conclusion;
	private double[] result = new double[12];
	private boolean enabled = true;

	public Shower(Fluent body, ChartJs chart, Client client) {
		client.setShower(this);

		body.h2(null, "Shower");
		body.input(null, "checkbox").att(Att.checked, "1").click((fluent, b) -> {
			this.enabled = fluent.domChecked();
			conclusion.sync();
		});
		body.span(null, " People: ");
		body.select(null, people + "", Utils.getSelectNumbers(1, 1, 15.0)).changed((fluent, ___) -> {
			people = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, ", duration: ");
		body.select(null, duration + "", Utils.getSelectNumbers(1.0, 1, 15.0)).changed((fluent, ___) -> {
			duration = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " minutes with a shower head that uses ");
		body.select(null, lPerMinute + "", Utils.getSelectNumbers(5, 1, 12)).changed((fluent, ___) -> {
			lPerMinute = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " liters per minute, at ");
		body.select(null, degrees + "", Utils.getSelectNumbers(30, 1, 45)).changed((fluent, ___) -> {
			degrees = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " degrees, about ");
		body.select(null, timesPerWeek + "", Utils.getSelectNumbers(1, 1, 7)).changed((fluent, ___) -> {
			timesPerWeek = Double.parseDouble(fluent.domSelectedOptions()[0]);
			conclusion.sync();
		});
		body.span(null, " times per week.");
		conclusion = body.add(null, ___ -> {
			double totalLiters = duration * lPerMinute;
			double delta = degrees - waterDegrees;

			StringBuilder text1 = new StringBuilder("So in total ");
			text1.append(Utils.format(totalLiters));
			text1.append(" liters, which is heated ");
			text1.append(Utils.format(degrees) + "-" + Utils.format(waterDegrees) + "=" + Utils.format(delta));
			text1.append(" degrees. This takes liters*degrees*1.16W = ");
			text1.append(Utils.format(totalLiters));
			text1.append("*");
			text1.append(Utils.format(delta));
			text1.append("*1.16=");
			double perShower = Math.round(totalLiters * delta * 1.16);
			text1.append(Utils.format(perShower));
			text1.append(" watt per showering.");

			StringBuilder text2 = new StringBuilder("So this is more or less (30.5/7)*");
			text2.append(Utils.format(timesPerWeek));
			text2.append(")*");
			text2.append(Utils.format(perShower));
			text2.append(" = ");
			double resultPerMonth = Math.round(totalLiters * delta * 1.16 * timesPerWeek * (30.5 / 7.0));
			text2.append(Utils.format(Math.round(resultPerMonth * 0.001)));
			text2.append(" kW per month per person. For all people in total: ");
			resultPerMonth *= people;
			text2.append(Utils.format(Math.round(resultPerMonth * 0.001)));
			text2.append(" kW per month.");

			if (!enabled) {
				perShower = 0.0;
			}
			double perDay = perShower * people * (timesPerWeek / 7.0);
			result = new double[] { perDay * 31, perDay * 28, perDay * 31, perDay * 30, perDay * 31, perDay * 30,
					perDay * 31, perDay * 31, perDay * 30, perDay * 31, perDay * 30, perDay * 31 };
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
