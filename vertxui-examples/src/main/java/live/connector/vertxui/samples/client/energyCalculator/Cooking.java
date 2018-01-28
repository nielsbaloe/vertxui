package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class Cooking {

	private double minutes = 45.0, plates = 2.0, energy = 1300.0, timesPerWeek = 6.0, other = 3_000;
	private ViewOn<?> conclusion;
	private double[] result = new double[12];
	private boolean enabled = true;

	public Cooking(Fluent body, Client client) {
		client.setCooking(this);

		body.h2(null, "Cooking");
		body.input(null, "checkbox").att(Att.checked, "1").click((fluent, b) -> {
			this.enabled = fluent.domChecked();
			conclusion.sync();
		});
		body.span(null,
				"WARNING: electronic cooking off-grid is absolutely non-sense, the power peaks that you need are huge - this is just for calculating how much external energy you would need.");
		body.br();
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
		body.span(null, " times per week. For all other things I use ");
		body.add(new InputNumber().att(Att.value, other + "").keyup((fluent, ___) -> {
			other = ((InputNumber) fluent).domValueDouble();
			conclusion.sync();
		}));
		body.span(null, " kW per year.");
		body.br();
		body.br();

		conclusion = body.add(null, ___ -> {

			StringBuilder text1 = new StringBuilder("Assuming that my cook plate only heats half of the time, ");
			text1.append(" this means that for every time cooking I consume about ");
			text1.append(" hours*plates*energy*0.5 = ");
			double perDinner = (minutes / 60.0) * plates * energy * 0.5;
			text1.append(Utils.format(Math.round(perDinner)));
			text1.append(" watt per dinner.");

			StringBuilder text2 = new StringBuilder("This is more or less (30.5/7)*");
			text2.append(Utils.format(timesPerWeek));
			text2.append("*");
			text2.append(Utils.format(perDinner));
			text2.append("=");
			double resultPerMonth = Math.round(perDinner * timesPerWeek * 30.5 / 7.0);
			text2.append(Utils.format(Math.round(resultPerMonth * 0.001)));
			text2.append(" kW per month.");

			double perDay = perDinner * (timesPerWeek / 7.0);
			if (enabled == false) {
				perDay = 0.0;
				resultPerMonth = 0.0;
			}

			double[] cooking = new double[] { perDay * 31, perDay * 28, perDay * 31, perDay * 30, perDay * 31,
					perDay * 30, perDay * 31, perDay * 31, perDay * 30, perDay * 31, perDay * 30, perDay * 31 };
			client.getElectricChart().showData("Cooking", "darkblue", cooking);

			// Cooking+other
			double withOtherPerMonth = resultPerMonth + other * 1000 / 12.0;
			result = new double[] { withOtherPerMonth, withOtherPerMonth, withOtherPerMonth, withOtherPerMonth,
					withOtherPerMonth, withOtherPerMonth, withOtherPerMonth, withOtherPerMonth, withOtherPerMonth,
					withOtherPerMonth, withOtherPerMonth, withOtherPerMonth };
			client.getElectricChart().showData("Cooking+other", "blue", result);
			client.getHeating().updateHeatgap();

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
