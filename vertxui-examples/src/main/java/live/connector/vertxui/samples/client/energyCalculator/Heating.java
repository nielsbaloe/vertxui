package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import java.util.function.Function;

import elemental.events.KeyboardEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

public class Heating {

	class Dimensions {
		public double width;
		public double length;
		public double height;
	}

	class Surface {
		public double sizeX;
		public double sizeY;
		public double lambda = defaultLambda;
		public double thickness; // in meter
		public static final double defaultLambda = 0.039;

		public double getR() {
			return thickness / lambda;
		}

		public double getU() {
			return 1.0 / getR();
		}

		public double getA() {
			return sizeX * sizeY;
		}

		public double getH() {
			return getA() * getU();
		}
	}

	// Model
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
	private ViewOn<Heating> totals;

	// lambda: energie die door een 1m3 blok materiaal stroomt om 1 graden
	// verschil voor elkaar te boxen.
	// d dikte van isoleren in m
	// warmteweerstand R = d / lambda  in m²K/W 
	// U = k = 1 / R in W/m2K
	// A oppervlakte in m2
	// H warmteoverdracht-coefficient = U * A
	// Q transmissieverlies = U * A * deltaTemp = (Hdak + Hgrond + HmUur1 +
	// Haangrenzend) * deltaTemp
	// https://huisje.knudde.be/transmissieverliezen
	// https://huisje.knudde.be/warmteverliesberekening

	public Heating() {
		Fluent heating = body.p();
		heating.h2(null, "Heating");
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
			Double volume = dimensions.width * dimensions.height * dimensions.length;
			if (dimensions.height == 0.0 || dimensions.length == 0.0 || dimensions.width == 0.0) {
				result.span(null, "..");
			} else {
				result.span(null, Utils.show(volume)).css(Css.fontStyle, "italic");
			}
			result.span().txt(
					" m3. That is per default roughly 45 watt/m3 for an inside temperature of 20 degrees, so that is about ");
			if (dimensions.height == 0.0 || dimensions.length == 0.0 || dimensions.width == 0.0) {
				result.span(null, "..");
			} else {
				result.span(null, Utils.show(volume * 45.0));
			}
			result.span(null, " watt per hour maximum.").br();
			return result;
		});

		Fluent loss = body.p();
		loss.span(null, "Een preciezere berekening is een warmteverlies- of transmissie berekening. "
				+ "Dit begint met de U-bepaling voor ieder apart oppervlak (muren, deuren, ramen, enz). Op ");
		loss.a(null, "u-wert.net", "http://u-wert.net", null).att(Att.target, "_blank");
		loss.span(null,
				" kun je precies oppervlaktes bepalen. Versimpeld gezien: " + "stel je hebt 4 wanden, een vloer en een "
						+ "plat dak, en stel dat alleen het isolatiemateriaal de totale isolatie bepaalt "
						+ "(wat grofweg ook zo is, alleen ramen en deuren zijn zeer grote energievreters, en er is geen correctie uitgevoerd voor noord/oost/zuid/west orientatie). "
						+ "Dan geldt:");

		Fluent ul = loss.ul();

		Fluent liRoof = ul.li();
		liRoof.span(null, "The roof is made of insulation material with lambda=");
		liRoof.add(Utils.getNumberInput().keyup(this::onRoofLambda).att(Att.value, "" + Surface.defaultLambda));
		liRoof.span(null, " with a thickness of ");
		liRoof.add(Utils.getNumberInput().keyup(this::onRoofThickness));
		liRoof.span(null, " cm. ");
		roofR = liRoof.add(roof, showRandU);
		roofDetails = liRoof.ul().add(roof, showHandQ);

		Fluent liFloor = ul.li();
		liFloor.span(null, "The floor is made of insulation material with lambda=");
		liFloor.add(Utils.getNumberInput().keyup(this::onFloorLambda).att(Att.value, "" + Surface.defaultLambda));
		liFloor.span(null, " with a thickness of ");
		liFloor.add(Utils.getNumberInput().keyup(this::onFloorThickness));
		liFloor.span(null, " cm. ");
		floorR = liFloor.add(floor, showRandU);
		floorDetails = liFloor.ul().add(floor, showHandQ);

		Fluent liWalls = ul.li();
		liWalls.span(null, "The four walls are made of insulation material with lambda=");
		liWalls.add(Utils.getNumberInput().keyup(this::onWallLambda).att(Att.value, "" + Surface.defaultLambda));
		liWalls.span(null, " with a thickness of ");
		liWalls.add(Utils.getNumberInput().keyup(this::onWallThickness));
		liWalls.span(null, " cm. ");
		wallsR = liWalls.add(wall1, showRandU);

		Fluent wallDetails = liWalls.ul();
		wallDetails1 = wallDetails.add(wall1, showHandQ);
		wallDetails2 = wallDetails.add(wall2, showHandQ);
		wallDetails3 = wallDetails.add(wall3, showHandQ);
		wallDetails4 = wallDetails.add(wall4, showHandQ);

		totals = loss.add(this, client -> {
			StringBuilder result = new StringBuilder(
					"So, the total transmission energy for 1 degree is Q1 = (Hwalls+Hroof+Hfloor)*1 = ");
			Double total = wall1.getH() + wall2.getH() + wall3.getH() + wall4.getH() + roof.getH() + floor.getH();
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(Utils.show(total));
			} else {
				result.append("..");
			}
			result.append(
					" watt per degree, so your peak heating should be (maximum air difference between -9 outside and 20 degrees inside): Q = (H+H+H) *29 = ");
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(Utils.show(Math.floor(total * 29)));
			} else {
				result.append("..");
			}
			result.append(" watt.");

			result.append(" According to this very unexact number, you need about Q/A = ");
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(Utils.show(Math.floor(total * 29 / (cubic.state().length * cubic.state().width))));
			} else {
				result.append("..");
			}
			result.append(" watt per m2 on (preferably non-electric) floorheating.");
			return Fluent.Span(null, result.toString());
		});

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

		totals.sync();
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

		totals.sync();
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

		totals.sync();
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

		totals.sync();
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

		totals.sync();
	}

	private void onRoofLambda(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent);

		roof.lambda = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onRoofThickness(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent) * 0.01; // from cm to meter

		roof.thickness = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onFloorLambda(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent);

		floor.lambda = value;
		floorR.sync();
		floorDetails.sync();

		totals.sync();
	}

	private void onFloorThickness(Fluent fluent, KeyboardEvent event) {
		double value = Utils.getDomNumber(fluent) * 0.01; // from cm to meter

		floor.thickness = value;
		floorR.sync();
		floorDetails.sync();

		totals.sync();
	}

	public static Function<Surface, Fluent> showHandQ = surface -> {
		StringBuilder result = new StringBuilder();
		result.append("Surface A=");
		if (surface.sizeX == 0.0) {
			result.append("..");
		} else {
			result.append(surface.sizeX);
		}
		result.append("m * ");
		if (surface.sizeY == 0.0) {
			result.append("..");
		} else {
			result.append(surface.sizeY);
		}
		result.append("m = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0) {
			result.append("..");
		} else {
			result.append(Utils.show(surface.getA()));
		}
		result.append("m2. Heat transfer coefficient H = U * A = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0 || surface.thickness == 0.0) {
			result.append("..");
		} else {
			result.append(Utils.show(surface.getH()));
		}
		return Fluent.Li().span(null, result.toString());
	};

	public static Function<Surface, Fluent> showRandU = surface -> {
		StringBuilder text = new StringBuilder("So, the Rd = meter/lambda =");
		if (surface.thickness != 0.0) {
			text.append(Utils.show(surface.getR()));
		} else {
			text.append("..");
		}
		text.append(" and the U=1/R=");
		if (surface.thickness != 0.0) {
			text.append(Utils.show(surface.getU()));
		} else {
			text.append("..");
		}
		return Fluent.Span(null, text.toString());
	};

}
