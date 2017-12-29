package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;

public class Stove {

	private double power = 4000, efficiency = 60, kgPerCubic = 350, wood = 4220;

	private ViewOn<?> conclusion;

	public Stove(Fluent body) {

		body.h2(null, "Stove");
		body.span(null, "I have a wood stove that can give ");
		body.select(null, power + "", new String[] { "4000", "4000", "5000", "5000", "6000", "6000", "7000", "7000",
				"8000", "8000", "9000", "9000", "10000", "10000" }).changed((fluent, ___) -> {
					power = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " watt per hour, and has an efficiency of ");
		body.select(null, efficiency + "",
				new String[] { "60", "60", "70", "70", "75", "75", "80", "80", "85", "85", "90", "90", "95", "95" })
				.changed((fluent, ___) -> {
					efficiency = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " percent. I use wood that is ");
		body.select(null, kgPerCubic + "", new String[] { "350 (zachthout)", "350", "380 (spar)", "380",
				"544 (hardhout)", "544", "600 (berk)", "600", "750 (eik, beuk)", "750" }).changed((fluent, ___) -> {
					kgPerCubic = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " kg/m3 and that is ");
		body.select(null, wood + "", new String[] { "normal (4222 w/kg)", "4220", "1 summer (3400 w/kg)", "3400",
				"not (2000 w/kg)", "2000" }).changed((fluent, ___) -> {
					wood = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " dried wood.");
		conclusion = body.add(null, ___ -> {

			// http://www.warmteprijzen.nl/brandhout_prijzen.html
			// http://www.warmteprijzen.nl/rekenmachine_kwh.html

			StringBuilder text1 = new StringBuilder("So for one hour the stove can give at maximum ");
			text1.append(InputNumber.show(power));
			text1.append("*(");
			text1.append(InputNumber.show(efficiency));
			text1.append("/100) = ");
			double maxStove = power * 0.01 * efficiency;
			text1.append(InputNumber.show(maxStove));
			text1.append(" watt per hour.");

			StringBuilder text2 = new StringBuilder("For this I need ");
			text2.append(InputNumber.show(power));
			text2.append(" / ");
			text2.append(InputNumber.show(wood));
			text2.append(" = ");
			double kgNeeded = power / wood;
			text2.append(InputNumber.show(kgNeeded));
			text2.append(" kg of wood. So with 1 m3 of wood, I can use the stove for about ");
			text2.append(InputNumber.show(kgPerCubic));
			text2.append(" / ");
			text2.append(InputNumber.show(kgNeeded));
			text2.append(" = ");
			text2.append(InputNumber.show(kgPerCubic / kgNeeded));
			text2.append(" hours.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			return result;
		});

	}

}
