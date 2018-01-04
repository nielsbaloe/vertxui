package live.connector.vertxui.samples.client.energyCalculator;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class Stove {

	private double power = 5000, efficiency = 75, kgPerCubic = 350, wood = 4220;
	private ViewOn<?> conclusion;
	private ViewOn<Double> total;
	private MonthTable monthTable;
	private Client client;

	public Stove(Fluent body, Client client) {
		this.client = client;
		client.setStove(this);

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
		body.select(null, wood + "", new String[] { "normal (4220 w/kg)", "4220", "1 summer (3400 w/kg)", "3400",
				"not (2000 w/kg)", "2000" }).changed((fluent, ___) -> {
					wood = Double.parseDouble(fluent.domSelectedOptions()[0]);
					conclusion.sync();
				});
		body.span(null, " dried wood.");

		// initialise now to avoid null-pointer error later
		monthTable = new MonthTable(null);
		total = new ViewOn<>(null, total -> {
			return Fluent.Span(null, " in total " + Utils.format(total) + "m3 wood.");
		});

		conclusion = body.add(null, ___ -> {

			// Source for statistics:
			// http://www.warmteprijzen.nl/brandhout_prijzen.html
			// http://www.warmteprijzen.nl/rekenmachine_kwh.html

			StringBuilder text1 = new StringBuilder("So for one hour the stove can give at maximum ");
			text1.append(Utils.format(power));
			text1.append("*(");
			text1.append(Utils.format(efficiency));
			text1.append("/100) = ");
			double maxStove = power * 0.01 * efficiency;
			text1.append(Utils.format(maxStove));
			text1.append(" watt per hour.");

			StringBuilder text2 = new StringBuilder("For this I need ");
			text2.append(Utils.format(power));
			text2.append("/");
			text2.append(Utils.format(wood));
			text2.append(" = ");
			double kgNeeded = power / wood;
			text2.append(Utils.format(kgNeeded));
			text2.append(" kg of wood. This is ");
			text2.append(Utils.format(kgNeeded));
			text2.append("/");
			text2.append(Utils.format(kgPerCubic));
			text2.append(" m3 of wood. The table below show how much wood you may need for the heatgap, ");

			Fluent result = Fluent.P();
			result.span(null, text1.toString());
			result.br();
			result.span(null, text2.toString());
			result.add(total);
			updateTable();

			return result;
		});
		body.add(monthTable);
	}

	public void updateTable() {
		String[] heatgapString = new String[12];
		double[] heatgap = client.getHeating().getHeatgap();
		for (int x = 0; x < 12; x++) {
			heatgapString[x] = Math.round(heatgap[x] * -0.001) + "kW";
		}

		double[] m3 = new double[12];
		double maxStove = power * 0.01 * efficiency;
		double kgNeeded = power / wood;
		double all = 0;
		for (int x = 0; x < 12; x++) {
			if (heatgap[x] == 0) {
				m3[x] = 0;
			} else {
				double hours = (-1.0 * heatgap[x]) / maxStove;
				double kgNeededNow = hours * kgNeeded;
				m3[x] = kgNeededNow / kgPerCubic;
				all += m3[x];
			}
		}

		monthTable.state(heatgapString, m3);
		total.state(all);
	}

}
