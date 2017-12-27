package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;

public class Cooking {

	private double minutes = 30.0, plates = 2.0, energy = 1300.0, timesPerWeek = 6.0;

	private ViewOn<?> conclusion;

	public Cooking(Fluent body) {
		body.h2(null, "Cooking");
		body.span(null, "I am usually cooking about ");
		body.add(new InputNumber().att(Att.value, minutes + "").keyup((fluent, ___) -> {
			minutes = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " minutes with ");
		body.add(new InputNumber().att(Att.value, plates + "").keyup((fluent, ___) -> {
			plates = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " plates on an electric cooking plate which consumes ");
		body.add(new InputNumber().att(Att.value, energy + "").keyup((fluent, ___) -> {
			energy = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " watt (electric 1500 watt, ceramic 1400 watt, induction 1300 watt).");
		body.span(null, " I cook about ");
		body.add(new InputNumber().att(Att.value, timesPerWeek + "").keyup((fluent, ___) -> {
			timesPerWeek = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " times per week.");
		body.br();
		body.br();

		conclusion = body.add(null, ___ -> {

			StringBuilder text1 = new StringBuilder("Assuming that my cook plate only heats half of the time, ");
			text1.append(" this means that for every time cooking I consume about ");
			text1.append(" hours*plates*energy*0.5 = ");
			double perDinner = (minutes / 60.0) * plates * energy * 0.5;
			text1.append(InputNumber.show(perDinner));
			text1.append(" watt per dinner.");

			StringBuilder text2 = new StringBuilder("This is more or less (30/7)*");
			text2.append(InputNumber.show(timesPerWeek));
			text2.append("*");
			text2.append(InputNumber.show(perDinner));
			text2.append("=");
			text2.append(InputNumber.show(Math.floor(perDinner * timesPerWeek * 30.0 / 7.0)));
			text2.append(" watt per month.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			return result;
		});
	}

}
