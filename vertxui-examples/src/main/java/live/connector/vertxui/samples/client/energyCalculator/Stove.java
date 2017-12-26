package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Stove {

	private double power = 3000, efficiency = 60, kgPerCubic = 350, wood = 4220;

	private ViewOn<?> conclusion;

	public Stove(Fluent body) {

		body.h2(null, "Stove");
		body.span(null, "I have a wood stove that can give ");
		body.add(Utils.getNumberInput().att(Att.value, power + "").keyup((fluent, ___) -> {
			power = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " watt per hour, and has an efficiency of ");
		body.select(null,
				new String[] { "60", "60", "70", "70", "75", "75", "80", "80", "85", "85", "90", "90", "95", "95" })
				.att(Att.value, efficiency + "").changed((fluent, ___) -> {
					efficiency = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " percent. I use wood that is ");
		body.select(null, new String[] { "350 (zachthout)", "350", "380 (spar)", "380", "544 (hardhout)", "544",
				"600 (berk)", "600", "750 (eik, beuk)", "750" }).att(Att.value, kgPerCubic + "")
				.changed((fluent, ___) -> {
					kgPerCubic = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " kg/m3 and that is ");
		body.select(null, new String[] { "normal (4222 w/kg)", "4222", "1 summer (3400 w/kg)", "3400",
				"not (2000 w/kg)", "2000" }).att(Att.value, wood + "").changed((fluent, ___) -> {
					wood = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " dried wood.");
		conclusion = body.add(null, ___ -> {

			// http://www.warmteprijzen.nl/brandhout_prijzen.html
			// http://www.warmteprijzen.nl/rekenmachine_kwh.html

			StringBuilder text1 = new StringBuilder("So for one hour the stove can give at maximum ");
			text1.append(Utils.show(power));
			text1.append("*(");
			text1.append(Utils.show(efficiency));
			text1.append("/100) = ");
			double maxStove = power * 0.01 * efficiency;
			text1.append(Utils.show(maxStove));
			text1.append(" watt per hour.");

			StringBuilder text2 = new StringBuilder("For this I need ");
			text2.append(Utils.show(wood));
			text2.append(" / ");
			text2.append(Utils.show(power));
			text2.append(" = ");
			double kgNeeded = wood / power;
			text2.append(Utils.show(kgNeeded));
			text2.append(" kg of wood. So with 1 m3 of wood, I can use the stove for about ");
			text2.append(Utils.show(kgPerCubic));
			text2.append(" / ");
			text2.append(Utils.show(kgNeeded));
			text2.append(" = ");
			text2.append(Utils.show(kgPerCubic / kgNeeded));
			text2.append(" hours.");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			return result;
		});

	}

}
