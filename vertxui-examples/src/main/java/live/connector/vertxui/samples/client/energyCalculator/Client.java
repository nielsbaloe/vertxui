package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.KeyboardEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Client implements EntryPoint {

	// Data
	private Surface wall1 = new Surface();
	private Surface wall2 = new Surface();
	private Surface wall3 = new Surface();
	private Surface wall4 = new Surface();
	private Surface roof = new Surface();
	private Surface floor = new Surface();

	// Dynamic views
	private ViewOn<Dimensions> cubic;
	private ViewOn<Surface> wallsR; // looks at wall1
	private ViewOn<Surface> wallDetails1;
	private ViewOn<Surface> wallDetails2;
	private ViewOn<Surface> wallDetails3;
	private ViewOn<Surface> wallDetails4;
	private ViewOn<Surface> roofR;
	private ViewOn<Surface> roofDetails;
	private ViewOn<Surface> floorR;
	private ViewOn<Surface> floorDetails;

	public Client() {

		body.p().txt(
				"Door het gebruik van deze energie calculatorBy using this calculator, I understand that this probably contains serious errors, "
						+ "and if I see an error according to my knowledge, I will let it know.");

		Fluent heating = body.p();
		heating.h1(null, "Heating");
		heating.span().txt("The room I want to heat has a width of ");
		heating.add(Utils.getNumberInput().keyup(this::onWidth));
		heating.span().txt(" meter and a length of ");
		heating.add(Utils.getNumberInput().keyup(this::onLength));
		heating.span().txt(" meter and a height of ");
		heating.add(Utils.getNumberInput().keyup(this::onHeight));
		heating.span().txt(" meter.");
		heating.br();
		heating.span().txt("So my room has a volume of ");
		cubic = heating.add(new Dimensions(), dimensions -> {
			Fluent result = Fluent.Span();
			double volume = dimensions.width * dimensions.height * dimensions.length;
			result.span().txt("" + Utils.showDouble(volume)).css(Css.fontStyle, "italic");
			result.span()
					.txt("m3. That is roughly 45 watt/m3 for an inside temperature of 20 degrees, so that is about ");
			result.span().txt((volume * 45.0) + " watt per hour.").br();
			return result;
		});

		Fluent loss = body.p();
		loss.span(null, "Een preciezere berekening is een warmteverlies- of transmissie berekening. "
				+ "Dit begint met de U-bepaling voor ieder apart oppervlak (muren, deuren, ramen, enz). Op ");
		loss.a(null, "u-wert.net", "http://u-wert.net", null).att(Att.target, "_blank");
		loss.span(null,
				" kun je precies oppervlaktes bepalen. Versimpeld gezien: " + "stel je hebt 4 wanden, een vloer en een "
						+ "plat dak, en stel dat alleen het isolatiemateriaal de totale isolatie bepaalt "
						+ "(wat grofweg ook zo is, alleen ramen en deuren zijn zeer grote energievreters). "
						+ "Dan geldt:");

		Fluent ul = loss.ul();

		Fluent liRoof = ul.li();
		liRoof.span(null, "The roof is made of insulation material with lambda=");
		liRoof.add(Utils.getNumberInput().keyup(this::onRoofLambda).att(Att.value, "" + Surface.defaultLambda));
		liRoof.span(null, " with a thickness of ");
		liRoof.add(Utils.getNumberInput().keyup(this::onRoofThickness));
		liRoof.span(null, " cm. ");
		roofR = liRoof.add(roof, Utils.showRandU);
		roofDetails = liRoof.ul().add(roof, Utils.showHandQ);

		Fluent liFloor = ul.li();
		liFloor.span(null, "The floor is made of insulation material with lambda=");
		liFloor.add(Utils.getNumberInput().keyup(this::onFloorLambda).att(Att.value, "" + Surface.defaultLambda));
		liFloor.span(null, " with a thickness of ");
		liFloor.add(Utils.getNumberInput().keyup(this::onFloorThickness));
		liFloor.span(null, " cm. ");
		floorR = liFloor.add(floor, Utils.showRandU);
		floorDetails = liFloor.ul().add(floor, Utils.showHandQ);

		Fluent liWalls = ul.li();
		liWalls.span(null, "The four walls are made of insulation material with lambda=");
		liWalls.add(Utils.getNumberInput().keyup(this::onWallLambda).att(Att.value, "" + Surface.defaultLambda));
		liWalls.span(null, " with a thickness of ");
		liWalls.add(Utils.getNumberInput().keyup(this::onWallThickness));
		liWalls.span(null, " cm. ");
		wallsR = liWalls.add(wall1, Utils.showRandU);

		Fluent wallDetails = liWalls.ul();
		wallDetails1 = wallDetails.add(wall1, Utils.showHandQ);
		wallDetails2 = wallDetails.add(wall2, Utils.showHandQ);
		wallDetails3 = wallDetails.add(wall3, Utils.showHandQ);
		wallDetails4 = wallDetails.add(wall4, Utils.showHandQ);

		// lambda: energie die door een 1m3 blok materiaal stroomt om 1 graden
		// verschil voor elkaar te boxen.
		// d dikte van isoleren
		// R = d / lambda
		// U = k = 1 / R in W/m2K
		// A oppervlakte in m2
		// H warmteoverdracht-coefficient = U * A
		// Q transmissieverlies = U * A * deltaTemp = (Hdak + Hgrond + HmUur1 +
		// Haangrenzend) * deltaTemp
		//
		// lambda= ..0,040..
		// dikte: ..5cm..
		// R= 1,25
		// U= 0,8
		// l*b*h = 8*2,5*4 = 80m3
		// deltaTemperatuur = -10 graden buiten naar 20 graden = 30 graden
		// tranmissieverlies = 0,8 * (2*(2,5*8) + 2*(4*8) + 2*(2,5*4)) * 30 =
		// (2*4 + 2*6,4 + 2*2)*30 = 744
		// = 0,8 * (40 + 64 + 20) * 30
		// = 0,8 * 124 * 30
		// = 2976 watt
		//
		// Bron: https://huisje.knudde.be/transmissieverliezen
		// Nog meer: https://huisje.knudde.be/warmteverliesberekening
		// ]]
		//
		// Qua vloerverwarming zal ik dan dus [transmissieverlies/l*b =
		// 2976/2,5*8 = 150 W/m2 nodig
		// Meer info: "afgift tabel vloerverwarming":
		// https://www.cvtotaal.nl/blog/vloerverwarming-berekenen/
		// Meer info: https://www.warmtepomp-info.nl/boiler/vloerverwarming/
		//
		// >----
		//
		// Ik douche gemiddeld ..8.. minuten op ..37.. graden
		// met een douchekop die ...8(default).. l/min verbruikt
		// [[
		// dus in totaal kost dit 8*8=64liter water
		// dus ik moet (37-15)=22 graden opwarmen
		// - 1 liter 1 graden opwarmen: 4,19 kJ = 1,16W
		// wat ongeveer 64 liter * 22 graden * 1,16W = 1633 W per douchebeurt
		// kost.
		// ]]
		//
		// >----
		//
		//
		// http://www.solarkeymark.dk/
		// per buis CPC collector: 120kW/jaar bron: econo.nl
		//
		// energie per maand:
		// http://www.e2energie.nl/zonne-energie-productie-per-maand.html
		//
		//
		//
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
		// Ik wil:
		// - ..3.. zonnepanelen met ieder ..270W.. opbrengst
		// dus ik heb in januari .... W per dag opbrengst, en in juli .... W per
		// dag opbrengst.
		// - een houtkachel met een rendement van ..... % en met vermogen .....
		// W en die stook ik ... uur:
		// dus ik heb dan ongeveer .... W om mijn ruimte te verwarmen wat mij
		// ... kg hout kost.
		//
		// >---
		// Ik wil een kacheltje met als vermogen ..2000-(3000 default)-6000..
		// watt en met een rendement van ..50-95 (default 60)..
		// dus dat levert per uur stoken [3000*0,6 = 1800 watt] op
		// Ik stook ..normaal gedroogd hout 4220 W (default) / zomergedroogd
		// hout 3400W / vers hout 2000 W....
		// en daarvoor heb ik dan nodig per uur [3000 / 4220 energiewaarde hout
		// = 0,71 ] kilo hout.
		// Met 1 kg ..zachthout 350 kg/m3.. (default) / spar 380 kg\m3 /
		// hardhout 544 kg/m3 / eik\beuk 750kg / berk 600 kg/m3 ... kan ik dus
		// 350/0,71 = 492 uur stoken.
		//
		// Bronnen:
		// http://www.warmteprijzen.nl/rekenmachine_kwh.html
		// http://www.warmteprijzen.nl/brandhout_prijzen.html
		//

	}

	private void onWidth(Fluent fluent, KeyboardEvent ____) {
		double value = Utils.getDomNumber(fluent);

		Dimensions dimensions = cubic.state();
		dimensions.width = value;
		cubic.sync();

		wall2.sizeX = value;
		wallDetails2.sync();

		wall4.sizeX = value;
		wallDetails4.sync();

		roof.sizeX = value;
		roofDetails.sync();

		floor.sizeX = value;
		floorDetails.sync();
	}

	private void onLength(Fluent fluent, KeyboardEvent ____) {
		double value = Utils.getDomNumber(fluent);

		Dimensions dimensions = cubic.state();
		dimensions.length = value;
		cubic.sync();

		wall1.sizeX = value;
		wallDetails1.sync();

		wall3.sizeX = value;
		wallDetails3.sync();

		roof.sizeY = value;
		roofDetails.sync();

		floor.sizeY = value;
		floorDetails.sync();
	}

	private void onHeight(Fluent fluent, KeyboardEvent ____) {
		double value = Utils.getDomNumber(fluent);

		Dimensions dimensions = cubic.state();
		dimensions.height = value;
		cubic.sync();

		wall1.sizeY = value;
		wallDetails1.sync();

		wall2.sizeY = value;
		wallDetails2.sync();

		wall3.sizeY = value;
		wallDetails3.sync();

		wall4.sizeY = value;
		wallDetails4.sync();

	}

	private void onWallLambda(Fluent fluent, KeyboardEvent ____) {
		double value = Utils.getDomNumber(fluent);

		wall1.lambda = value;
		wallDetails1.sync();
		wallsR.sync();

		wall2.lambda = value;
		wallDetails2.sync();

		wall3.lambda = value;
		wallDetails3.sync();

		wall4.lambda = value;
		wallDetails4.sync();

	}

	private void onWallThickness(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent) * 0.01; // from cm to meter

		wall1.thickness = value;
		wallDetails1.sync();
		wallsR.sync();

		wall2.thickness = value;
		wallDetails2.sync();

		wall3.thickness = value;
		wallDetails3.sync();

		wall4.thickness = value;
		wallDetails4.sync();

	}

	private void onRoofLambda(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent);

		roof.lambda = value;
		roofR.sync();
		roofDetails.sync();
	}

	private void onRoofThickness(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent) * 0.01; // from cm to meter

		roof.thickness = value;
		roofR.sync();
		roofDetails.sync();
	}

	private void onFloorLambda(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent);

		floor.lambda = value;
		floorR.sync();
		floorDetails.sync();
	}

	private void onFloorThickness(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent) * 0.01; // from cm to meter

		floor.thickness = value;
		floorR.sync();
		floorDetails.sync();
	}

	@Override
	public void onModuleLoad() {
	}

}
