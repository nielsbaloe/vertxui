package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class SolarTubes {

	private double collectors = 2, tubes = 20;

	public ViewOn<?> conclusion;

	public SolarTubes() {

		body.h1(null, "Solar tubes");
		body.span(null, "I want to have ");
		body.add(Utils.getNumberInput().att(Att.value, collectors + "").keyup((fluent, ___) -> {
			collectors = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " with each ");
		body.add(Utils.getNumberInput().att(Att.value, tubes + "").keyup((fluent, ___) -> {
			tubes = Utils.getDomNumber(fluent);
			conclusion.sync();
		}));
		body.span(null, " heat tubes. ").br();
		conclusion = body.add(null, ___ -> {
			StringBuilder result = new StringBuilder("That would bring me a total of ");
			result.append(Utils.show(collectors * tubes));
			result.append(" heat pipes, which each bring about 120.000 watt a year, so that is in total 120.000*");
			result.append(Utils.show(collectors * tubes));
			result.append("=");
			result.append(Utils.show(collectors * tubes * 120000));
			result.append(" watt a year. This is roughly per month:");
			Fluent text = Fluent.Span(null, result.toString());
			
			
			return Fluent.Span().add(text);
		});

		// http://www.solarkeymark.dk/
		// per buis CPC collector: 120kW/jaar bron: econo.nl
		//
		// energie per maand:
		// http://www.e2energie.nl/zonne-energie-productie-per-maand.html
		// Ik wil:
		// - ..2.. zonnecollector(en) met ieder ..30.. heatpipes die per stuk VV
		// W opleveren maximaal
		// dus ik heb in januari ..... W per dag opbrengst, en in juli .... W
		// per dag opbrengst.
		//
		// dus dat kost mij [...] m3 aan dak-oppervlak
		//
		// [[ - Berekening aanvoer zonneboiler warmte
		// http://www.solar2all.com/berekening.php
		// 20 heatpipes 1.61m2: jan. 1600W per dag , juli 6400W, gem. 4100W
		// ]]
		// [[ - Berekening https://econo.nl/berekening-zonneboiler-subsidie-2017
		// 18 CPC heatpipes 1.61m2: jan. 830W per dag , juli 9000W
		// ]]
		//

	}
}
