package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Shower {

	private double minutes = 7, literPerMinute = 7, degrees = 40;

	private ViewOn<?> conclusion;

	public Shower() {

		body.h1(null, "Shower");
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
		body.span(null, " degrees.").br();
		conclusion = body.add(null, ___ -> {
			double totalLiters = minutes * literPerMinute;
			double delta = degrees - 15.0;
			StringBuilder result = new StringBuilder("So this means that in total ");
			result.append(Utils.show(totalLiters));
			result.append(" liters is used, which needs to be heated up about deltaT=");
			result.append(Utils.show(degrees) + "-15=" + Utils.show(delta));
			result.append(" degrees. This takes liters*deltaT*1,16W = ");
			result.append(Utils.show(totalLiters));
			result.append("*");
			result.append(Utils.show(delta));
			result.append("*1,16=");
			result.append(Utils.show(totalLiters * delta * 1.16));
			result.append(" watt per showering.");
			return Fluent.Span(null, result.toString());
		});

	}

}
