package live.connector.vertxui.samples.client.energyCalculator;

import java.util.function.Function;

import elemental.events.KeyboardEvent;
import elemental.events.UIEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.InputNumber;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;

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
	private double windowPercentage = 0.1;
	private double windowU = (1.0 / 0.333);

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
	private MonthTable monthTable;

	// For updating the chart
	private Client client;
	private ChartJs chart;
	private double transmission = 0;

	// lambda: energie die door een 1m3 blok materiaal stroomt om 1 graden
	// verschil voor elkaar te boxen.
	// d dikte van isoleren in m
	// warmteweerstand R = d / lambda in m²K/W
	// U = k = 1 / R in W/m2K
	// A oppervlakte in m2
	// H warmteoverdracht-coefficient = U * A
	// Q transmissionverlies = U * A * deltaTemp = (Hdak + Hgrond + HmUur1 +
	// Haangrenzend) * deltaTemp
	// https://huisje.knudde.be/transmissionverliezen
	// https://huisje.knudde.be/warmteverliesberekening

	// Vollast uren naar 2000-norm:
	// https://warmtepomp-weetjes.nl/warmtepomp/indicatietabel/

	public Heating(Fluent body, ChartJs chart, Client clientt) {
		clientt.setHeating(this);

		this.client = clientt;
		this.chart = chart;

		Fluent heating = body.p();
		heating.h2(null, "Heating");
		heating.span().txt("The room I want to heat has a width of ");
		Fluent widthSelector = heating.select(null, "2.3",
				new String[] { "2.2", "2.2", "2.3", "2.3", "2.4", "2.4", "2.5", "2.5", "2.6", "2.6", "2.7", "2.7",
						"2.8", "2.8", "2.9", "2.9", "3.0", "3.0", "3.1", "3.1", "3.2", "3.2", "3.3", "3.3", "3.4",
						"3.4", "3.5", "3.5", "3.6", "3.6", "3.7", "3.7", "3.8", "3.8", "3.9", "3.9", "4.0", "4.0" })
				.changed(this::onWidth);
		heating.span().txt(" meter and a length of ");
		heating.add(new InputNumber().keyup(this::onLength));
		heating.span().txt(" meter and a height of ");
		heating.add(new InputNumber().keyup(this::onHeight));
		heating.span().txt(" meter.");
		heating.br();
		heating.span().txt("So my room has a volume of ");
		cubic = heating.add(new Dimensions(), dimensions -> {
			Fluent result = Fluent.Span();
			Double volume = dimensions.width * dimensions.height * dimensions.length;
			if (dimensions.height == 0.0 || dimensions.length == 0.0 || dimensions.width == 0.0) {
				result.span(null, "..");
			} else {
				result.span(null, InputNumber.show(volume)).css(Css.fontStyle, "italic");
			}
			result.span().txt(
					" m3. That is per default roughly 45 watt/m3 for an inside temperature of 20 degrees, so worst case you need ");
			if (dimensions.height == 0.0 || dimensions.length == 0.0 || dimensions.width == 0.0) {
				result.span(null, "..");
			} else {
				result.span(null, InputNumber.show(volume * 45.0));
			}
			result.span(null, " watt per hour to heat up the space.").br();
			return result;
		});

		Fluent loss = body.p();
		loss.span(null,
				"A more detailed calculation is called a heat loss calculation or "
						+ "transmission calculation. This starts with defining the U value for every surface "
						+ "(walls, floor, roof, windows, etc.). For a near perfect determiniation of each U value "
						+ "take a look at ");
		loss.a(null, "u-wert.net", "http://u-wert.net", null).att(Att.target, "_blank");
		loss.span(null,
				". Here is a simplified version. Suppose you have 4 walls, a flat roof and a floor, and the "
						+ "insulation is only defined by the insulation material (this is often true). Suppose we leave "
						+ "windows and doors (the biggest energy consumers). Then: ");

		Fluent ul = loss.ul();

		Fluent liRoof = ul.li();
		liRoof.span(null, "The roof is made of insulation material with lambda=");
		liRoof.add(new InputNumber().keyup(this::onRoofLambda).att(Att.value, "" + Surface.defaultLambda));
		liRoof.span(null, " with a thickness of ");
		liRoof.add(new InputNumber().keyup(this::onRoofThickness));
		liRoof.span(null, " cm. ");
		roofR = liRoof.add(roof, showRandU);
		roofDetails = liRoof.ul().add(roof, showHandQ);

		Fluent liFloor = ul.li();
		liFloor.span(null, "The floor is made of insulation material with lambda=");
		liFloor.add(new InputNumber().keyup(this::onFloorLambda).att(Att.value, "" + Surface.defaultLambda));
		liFloor.span(null, " with a thickness of ");
		liFloor.add(new InputNumber().keyup(this::onFloorThickness));
		liFloor.span(null, " cm. ");
		floorR = liFloor.add(floor, showRandU);
		floorDetails = liFloor.ul().add(floor, showHandQ);

		Fluent liWalls = ul.li();
		liWalls.span(null, "The four walls are made of insulation material with lambda=");
		liWalls.add(new InputNumber().keyup(this::onWallLambda).att(Att.value, "" + Surface.defaultLambda));
		liWalls.span(null, " with a thickness of ");
		liWalls.add(new InputNumber().keyup(this::onWallThickness));
		liWalls.span(null, " cm, and total window amount is ");
		liWalls.select(null, windowPercentage + "",
				new String[] { "10%", "0.1", "20%", "0.2", "30%", "0.3", "40%", "0.4", "50%", "0.5", "60%", "0.6" })
				.changed((fluent, ___) -> {
					windowPercentage = Double.parseDouble(fluent.domSelectedOptions()[0]);
					totals.sync();
				});
		liWalls.span(null, " made of ");
		liWalls.select(null,
				(1.0 / windowU) + "", new String[] { "single (R=0.175)", "0.175", "double (R=0.333)", "0.333",
						"HR (R=0.563)", "0.563", "HR+ (R=0.729)", "0.729", "HR++ (R=0.833)", "0.833" })
				.changed((fluent, ___) -> {
					windowU = 1.0 / Double.parseDouble(fluent.domSelectedOptions()[0]);
					totals.sync();
				});
		liWalls.span(null, " glass.");

		Fluent wallDetails = liWalls.ul();
		wallsR = wallDetails.add(wall1, showRandU);
		wallDetails1 = wallDetails.add(wall1, showHandQ);
		wallDetails2 = wallDetails.add(wall2, showHandQ);
		wallDetails3 = wallDetails.add(wall3, showHandQ);
		wallDetails4 = wallDetails.add(wall4, showHandQ);

		totals = body.add(this, ____ -> {
			StringBuilder result = new StringBuilder(
					"So, the total transmission energy for 1 degree is Q1 = (Hwalls+Hroof+Hfloor)*1 = ");
			Double total = wall1.getH() + wall2.getH() + wall3.getH() + wall4.getH() + roof.getH() + floor.getH();
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(InputNumber.show(total));
			} else {
				result.append("..");
			}
			result.append(
					" watt per degree, so your peak heating should be (maximum air difference between -10 outside and 20 degrees inside): Q = (H+H+H) *30 = ");
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(InputNumber.show(Math.floor(total * 30)));
			} else {
				result.append("..");
			}
			result.append(" watt. With windows, this is: ");
			double wallPercentage = 1.0 - windowPercentage;
			double full1 = (wall1.getH() * wallPercentage) + (wall1.getA() * windowPercentage * windowU);
			double full2 = (wall2.getH() * wallPercentage) + (wall2.getA() * windowPercentage * windowU);
			double full3 = (wall3.getH() * wallPercentage) + (wall3.getA() * windowPercentage * windowU);
			double full4 = (wall4.getH() * wallPercentage) + (wall4.getA() * windowPercentage * windowU);
			total = full1 + full2 + full3 + full4 + roof.getH() + floor.getH();
			if (!total.isNaN() && !total.isInfinite()) {
				transmission = Math.floor(total * 30);
				chartChanged();
				result.append(InputNumber.show(transmission));
			} else {
				result.append("..");
			}
			result.append(" watt.");

			result.append(" ( If you would have floor heating, you would need ");
			if (!total.isNaN() && !total.isInfinite()) {
				double needed = Math.floor(transmission / (cubic.state().length * cubic.state().width));
				result.append(InputNumber.show(needed));
			} else {
				result.append("..");
			}
			result.append(" watt per m2 on (preferably non-electric) floorheating.");

			result.append(" The amount of energy that you aproximately need per month is (in 'vollast uren'):");

			return Fluent.Div(null, result.toString());

		});

		monthTable = new MonthTable(new String[] { "304 hours", "299 hours", "255 hours", "148 hours", "49 hours",
				"0 hours", "0 hours", "0 hours", "13 hours", "110 hours", "212 hours", "289 hours" });
		body.add(monthTable);

		// Setting the default values into all fields
		onWidth(widthSelector, null);

	}

	public void chartChanged() {
		// update table
		double data[] = new double[] { (304 * transmission), (299 * transmission), (225 * transmission),
				(148 * transmission), (49 * transmission), (0 * transmission), (0 * transmission), (0 * transmission),
				(13 * transmission), (110 * transmission), (212 * transmission), (289 * transmission) };
		monthTable.state2(data);

		// update chart
		double showerResult = client.getShower().getResultPerMonth();
		for (int x = 0; x < data.length; x++) {
			data[x] += showerResult;
		}
		chart.showData("Heating + Shower", "red", data);

	}

	private void onWidth(Fluent fluent, UIEvent ____) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

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
		double value = ((InputNumber) fluent).domValueDouble();

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
		double value = ((InputNumber) fluent).domValueDouble();

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
		double value = ((InputNumber) fluent).domValueDouble();

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
		double value = ((InputNumber) fluent).domValueDouble() * 0.01; // from
																		// cm to
																		// meter

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
		double value = ((InputNumber) fluent).domValueDouble();

		roof.lambda = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onRoofThickness(Fluent fluent, KeyboardEvent event) {
		double value = ((InputNumber) fluent).domValueDouble() * 0.01; // from
																		// cm to
		// meter

		roof.thickness = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onFloorLambda(Fluent fluent, KeyboardEvent event) {
		double value = ((InputNumber) fluent).domValueDouble();

		floor.lambda = value;
		floorR.sync();
		floorDetails.sync();

		totals.sync();
	}

	private void onFloorThickness(Fluent fluent, KeyboardEvent event) {
		double value = ((InputNumber) fluent).domValueDouble() * 0.01; // from
																		// cm to
		// meter

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
			result.append(InputNumber.show(surface.getA()));
		}
		result.append("m2. Heat transfer coefficient H = U * A = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0 || surface.thickness == 0.0) {
			result.append("..");
		} else {
			result.append(InputNumber.show(surface.getH()));
		}
		return Fluent.Li().span(null, result.toString());
	};

	public static Function<Surface, Fluent> showRandU = surface -> {
		StringBuilder text = new StringBuilder("So, the Rd = meter/lambda =");
		if (surface.thickness != 0.0) {
			text.append(InputNumber.show(surface.getR()));
		} else {
			text.append("..");
		}
		text.append(" and the U=1/R=");
		if (surface.thickness != 0.0) {
			text.append(InputNumber.show(surface.getU()));
		} else {
			text.append("..");
		}
		return Fluent.Span(null, text.toString());
	};

}
