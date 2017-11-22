package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Cooking {

	private double minutes = 30.0, plates = 2.0, energy = 1300.0;

	private ViewOn<?> conclusion;

	public Cooking() {
		body.h1(null, "Cooking");
		body.span(null, "I am usually cooking about ");
		body.add(Utils.getNumberInput().att(Att.value, minutes + "").keyup((fluent, ___) -> {
			minutes = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " minutes with ");
		body.add(Utils.getNumberInput().att(Att.value, plates + "").keyup((fluent, ___) -> {
			plates = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " plates on an electric cooking plate which consumes ");
		body.add(Utils.getNumberInput().att(Att.value, energy + "").keyup((fluent, ___) -> {
			plates = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " watt (electric 1500 watt, ceramic 1400 watt, induction 1300 watt).").br();
		conclusion = body.add(null, ___ -> {
			StringBuilder result = new StringBuilder("So this means that for every time cooking I consume about ");
			result.append(" hours*plates*energy = ");
			result.append(Utils.show((minutes / 60.0) * plates * energy));
			result.append(" watt per dinner.");
			return Fluent.Span(null, result.toString());
		});
	}

}
