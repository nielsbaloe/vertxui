package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Shower {

	private double minutes = 7, literPerMinute = 7, degrees = 40, timesPerWeek = 7;

	private ViewOn<?> conclusion;

	public Shower() {

		body.h2(null, "Shower");
		body.span(null, "I usually shower about ");
		body.add(Utils.getNumberInput().att(Att.value, minutes + "").keyup((fluent, ___) -> {
			minutes = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " minutes with a shower head that uses ");
		body.add(Utils.getNumberInput().att(Att.value, literPerMinute + "").keyup((fluent, ___) -> {
			literPerMinute = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " liters per minute, and I like to shower at ");
		body.add(Utils.getNumberInput().att(Att.value, degrees + "").keyup((fluent, ___) -> {
			degrees = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " degrees. I shower about ");
		body.add(Utils.getNumberInput().att(Att.value, timesPerWeek + "").keyup((fluent, ___) -> {
			timesPerWeek = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " times per week.");
		conclusion = body.add(null, ___ -> {
			double totalLiters = minutes * literPerMinute;
			double delta = degrees - 15.0;

			StringBuilder text1 = new StringBuilder("So this means that in total ");
			text1.append(Utils.show(totalLiters));
			text1.append(" liters is used, which needs to be heated up about deltaT=");
			text1.append(Utils.show(degrees) + "-15=" + Utils.show(delta));
			text1.append(" degrees. This takes liters*deltaT*1,16W = ");
			text1.append(Utils.show(totalLiters));
			text1.append("*");
			text1.append(Utils.show(delta));
			text1.append("*1,16=");
			text1.append(Utils.show(totalLiters * delta * 1.16));
			text1.append(" watt per showering.");

			StringBuilder text2 = new StringBuilder("So this is more or less per month (30/7)*");
			text2.append(Utils.show(timesPerWeek));
			text2.append(")*");
			text2.append(Utils.show(totalLiters * delta * 1.16));
			text2.append(" = ");
			text2.append(Utils.show(totalLiters * delta * 1.16 * timesPerWeek * (30.0 / 7.0)));
			text2.append(" watt per month.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			return result;
		});

	}

}
