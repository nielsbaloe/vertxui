package live.connector.vertxui.samples.client.energyCalculator;

import java.util.HashMap;
import java.util.function.Function;

import elemental.events.UIEvent;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;
import live.connector.vertxui.samples.client.energyCalculator.components.MonthTable;
import live.connector.vertxui.samples.client.energyCalculator.components.Utils;

public class Heating {

	class Dimensions {
		public double width;
		public double length;
		public double height;

		public double getM3() {
			return width * length * height;
		}

		public double getM2Floor() {
			return width * length;
		}

	}

	class Surface {

		private double Rsi, Rse;

		public Surface(double Rsi, double Rse) {
			this.Rsi = Rsi;
			this.Rse = Rse;
		}

		public double sizeX;
		public double sizeY;
		public double lambda;
		public double thickness; // in meter

		public double getR() {
			return Rsi + (thickness / lambda) + Rse;
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

	// Source: Model and Rsi and Rse:
	// http://fvb.constructiv.be/~/media/Files/Shared/FVB/Centrale%20verwarming/NL/CV-warmteverliesberekening4_1A_theorie_for_web.pdf
	private Surface wall1 = new Surface(0.13, 0.04);
	private Surface wall2 = new Surface(0.13, 0.04);
	private Surface wall3 = new Surface(0.13, 0.04);
	private Surface wall4 = new Surface(0.13, 0.04);
	private Surface roof = new Surface(0.10, 0.04);
	private Surface floor = new Surface(0.17, 0.04);
	private double windowPercentage = 20;
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
	private double transmission = 0;
	private double[] heatingAndShower = new double[12];
	private double[] heatgap = new double[12];

	// Source: vollast uren naar 2016-2018 waarde:
	// https://warmtepomp-weetjes.nl/warmtepomp/indicatietabel/

	public Heating(Fluent body, ChartJs chart, Client clientt) {
		clientt.setHeating(this);

		this.client = clientt;

		Fluent heating = body.p();
		heating.h2(null, "Heating");
		heating.span().txt("The square (tiny) house has a width ");
		Fluent widthSelector = heating.select(null, "2.3", Utils.getSelectNumbers(2.2, 0.1, 4.0))
				.changed(this::onWidth);
		heating.span().txt(" m, length ");
		Fluent lengthSelector = heating.select(null, "8", Utils.getSelectNumbers(4.0, 0.5, 15.0))
				.changed(this::onLength);
		heating.span().txt(" m, height ");
		Fluent heightSelector = heating.select(null, "3", Utils.getSelectNumbers(2.0, 0.2, 4.0))
				.changed(this::onLength);
		heating.span().txt(" m, so ");
		cubic = heating.add(new Dimensions(), dimensions -> {
			Fluent result = Fluent.Span();
			if (dimensions.height == 0.0 || dimensions.length == 0.0 || dimensions.width == 0.0) {
				result.span(null, "..");
			} else {
				result.span(null, Utils.format(dimensions.getM3())).css(Css.fontStyle, "italic");
			}
			result.span().txt(" m3.");
			return result;
		});
		heating.span(null, " Suppose the insulation is only defined by the insulation material. ");
		Fluent loss = body.span();
		// loss.a(null, "u-wert.net","http://u-wert.net",
		// null).att(Att.target,"_blank");
		Fluent ul = loss.ul();
		Fluent liRoof = ul.li();
		liRoof.span(null, "The roof insulation: ");

		Fluent roofLambdaSelector = liRoof.select(null, "0.038",
				new String[] { "ecological insolation (lambda = 0.040)", "0.040",
						"ecological insolation (lambda = 0.039)", "0.039", "ecological insolation (lambda = 0.038)",
						"0.038", "chemical stuff (lambda= 0.036)", "0.036", "chemical stuff (lambda = 0.035)", "0.035",
						"chemical stuff (lambda = 0.030)", "0.030", "chemical stuff (lambda = 0.022)", "0.022" })
				.changed(this::onRoofLambda);
		liRoof.span(null, ",  tickness: ");
		Fluent roofThicknessSelector = liRoof.select(null, "25", Utils.getSelectNumbers(5, 5, 100))
				.changed(this::onRoofThickness);
		liRoof.span(null, " cm. ");
		roofR = liRoof.add(roof, showRandU);
		roofDetails = liRoof.ul().add(roof, showHandQ);

		Fluent liFloor = ul.li();
		liFloor.span(null, "The floor insulation: ");
		Fluent floorLambdaSelector = liFloor.select(null, "0.038",
				new String[] { "ecological insolation (lambda = 0.040)", "0.040",
						"ecological insolation (lambda = 0.039)", "0.039", "ecological insolation (lambda = 0.038)",
						"0.038", "chemical stuff (lambda= 0.036)", "0.036", "chemical stuff (lambda = 0.035)", "0.035",
						"chemical stuff (lambda = 0.030)", "0.030", "chemical stuff (lambda = 0.022)", "0.022" })
				.changed(this::onFloorLambda);
		liFloor.span(null, ",  tickness: ");
		Fluent floorThicknessSelector = liFloor.select(null, "15", Utils.getSelectNumbers(5, 5, 100))
				.changed(this::onFloorThickness);
		liFloor.span(null, " cm. ");
		floorR = liFloor.add(floor, showRandU);
		floorDetails = liFloor.ul().add(floor, showHandQ);

		Fluent liWalls = ul.li();
		liWalls.span(null, "All four walls insulation: ");
		Fluent wallsLambdaSelector = liWalls.select(null, "0.038",
				new String[] { "ecological insolation (lambda = 0.040)", "0.040",
						"ecological insolation (lambda = 0.039)", "0.039", "ecological insolation (lambda = 0.038)",
						"0.038", "chemical stuff (lambda= 0.036)", "0.036", "chemical stuff (lambda = 0.035)", "0.035",
						"chemical stuff (lambda = 0.030)", "0.030", "chemical stuff (lambda = 0.022)", "0.022" })
				.changed(this::onWallLambda);
		liWalls.span(null, ",  tickness: ");
		Fluent wallsThicknessSelector = liWalls.select(null, "20", Utils.getSelectNumbers(5, 5, 100))
				.changed(this::onWallThickness);
		liWalls.span(null, " cm.");
		wallsR = liWalls.add(wall1, showRandU);
		liWalls.br();

		// Windows
		liWalls.span(null, "All windows are ");
		liWalls.select(null, windowPercentage + "", Utils.getSelectNumbers(0, 5, 70)).changed((fluent, ___) -> {
			windowPercentage = Double.parseDouble(fluent.domSelectedOptions()[0]);
			totals.sync();
		});
		liWalls.span(null, "% of all walls made of ");
		liWalls.select(null,
				(1.0 / windowU) + "", new String[] { "single (R=0.175)", "0.175", "double (R=0.333)", "0.333",
						"HR (R=0.563)", "0.563", "HR+ (R=0.729)", "0.729", "HR++ (R=0.833)", "0.833" })
				.changed((fluent, ___) -> {
					windowU = 1.0 / Double.parseDouble(fluent.domSelectedOptions()[0]);
					totals.sync();
				});
		liWalls.span(null, " glass.");

		Fluent wallDetails = liWalls.ul();
		wallDetails1 = wallDetails.add(wall1, showHandQ);
		wallDetails2 = wallDetails.add(wall2, showHandQ);
		wallDetails3 = wallDetails.add(wall3, showHandQ);
		wallDetails4 = wallDetails.add(wall4, showHandQ);

		totals = body.add(this, ____ -> {
			StringBuilder result = new StringBuilder(
					"The total transmission energy for 1 degree is Q1 = (Hwalls+Hroof+Hfloor)*1 = ");
			Double total = wall1.getH() + wall2.getH() + wall3.getH() + wall4.getH() + roof.getH() + floor.getH();
			if (!total.isNaN() && !total.isInfinite()) {
				result.append(Utils.format(total));
			} else {
				result.append("..");
			}
			result.append(" watt per degree, so the peak heating is (-10 outside, 20 inside): Q=(H+H+...)*30=");
			if (!total.isNaN() && !total.isInfinite()) {
				transmission = total * 30.0;
				result.append(Utils.format(Math.round(transmission)));
			} else {
				result.append("..");
			}
			result.append(" watt, and with small correction for the (unknown) orientation: ");

			// Orientation loss
			double orientationLoss = 1.02;
			if (!total.isNaN() && !total.isInfinite()) {
				transmission = total * orientationLoss * 30.0;
				result.append(Utils.format(Math.round(transmission)));
			} else {
				result.append("..");
			}
			result.append(" watt, and with windows: ");

			// Windows
			double win = windowPercentage * 0.01;
			double wallPercentage = 1.0 - win;
			double full1 = (wall1.getH() * wallPercentage) + (wall1.getA() * windowU * win);
			double full2 = (wall2.getH() * wallPercentage) + (wall2.getA() * windowU * win);
			double full3 = (wall3.getH() * wallPercentage) + (wall3.getA() * windowU * win);
			double full4 = (wall4.getH() * wallPercentage) + (wall4.getA() * windowU * win);
			total = full1 + full2 + full3 + full4 + roof.getH() + floor.getH();
			if (!total.isNaN() && !total.isInfinite()) {
				transmission = total * orientationLoss * 30.0;
				result.append(Utils.format(Math.round(transmission)));
			} else {
				result.append("..");
			}

			// Ventilation
			result.append(" watt, and with ventilation (0.34*m3*d): ");
			if (!total.isNaN() && !total.isInfinite()) {
				transmission += (0.34 * cubic.state().getM3() * 30);
				updateHeatingPlusShower();
				result.append(Utils.format(Math.round(transmission)));
			} else {
				result.append("..");
			}
			result.append(" watt. The amount of energy that you aproximately need per month is (in 'vollast uren'):");
			return Fluent.Div(null, result.toString());
		});

		monthTable = new MonthTable(new String[] { "288 hours", "282 hours", "198 hours", "111 hours", "0 hours",
				"0 hours", "0 hours", "0 hours", "0 hours", "67 hours", "183 hours", "271 hours" });
		body.add(monthTable);

		// Setting the default values into all fields
		onWidth(widthSelector, null);
		onLength(lengthSelector, null);
		onHeight(heightSelector, null);
		onRoofLambda(roofLambdaSelector, null);
		onRoofThickness(roofThicknessSelector, null);
		onFloorLambda(floorLambdaSelector, null);
		onFloorThickness(floorThicknessSelector, null);
		onWallLambda(wallsLambdaSelector, null);
		onWallThickness(wallsThicknessSelector, null);
	}

	public double[] getHeatingAndShower() {
		return heatingAndShower;
	}

	public void updateHeatingPlusShower() {
		// update table when heat OR shower has changed

		double data[] = new double[] { (288 * transmission), (282 * transmission), (198 * transmission),
				(111 * transmission), (0 * transmission), (0 * transmission), (0 * transmission), (0 * transmission),
				(0 * transmission), (67 * transmission), (183 * transmission), (271 * transmission) };
		monthTable.state2(data);

		// update chart
		if (client.getShower() != null) {
			double[] showerResult = client.getShower().getResult();
			for (int x = 0; x < 12; x++) {
				data[x] += showerResult[x];
			}
			heatingAndShower = data;
			client.getWaterChart().showData("Heating+Shower", "blue", data);
			updateHeatgap();
		}
	}

	public void updateHeatgap() {
		// update when heating OR tubes OR cooking changed
		if (client.getSolarTubes() != null) {
			double[] solarTubes = client.getSolarTubes().getResult();
			for (int x = 0; x < 12; x++) {
				if (heatingAndShower[x] > solarTubes[x]) {
					heatgap[x] = solarTubes[x] - heatingAndShower[x];
				} else {
					heatgap[x] = 0;
				}
			}
			client.getWaterChart().showData("Heatgap", "red", heatgap);

			// reversed in electric chart
			if (client.getCooking() != null) {

				double[] cooking = client.getCooking().getResult();
				double[] forElectric = new double[12];
				for (int x = 0; x < 12; x++) {
					forElectric[x] = (heatgap[x] * -1.0) + cooking[x];
				}
				client.getElectricChart().showData("Cooking+other+heatgap", "lightblue", forElectric);
			}
			if (client.getStove() != null) {
				client.getStove().updateTable();
			}
		}
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

	private void onLength(Fluent fluent, UIEvent ____) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

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

		warnLength();
	}

	private void onHeight(Fluent fluent, UIEvent ____) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

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

	private void onWallLambda(Fluent fluent, UIEvent ____) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

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

	private void onWallThickness(Fluent fluent, UIEvent event) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]) * 0.01;

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

	private void onRoofLambda(Fluent fluent, UIEvent event) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

		roof.lambda = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onRoofThickness(Fluent fluent, UIEvent event) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]) * 0.01;

		roof.thickness = value;
		roofR.sync();
		roofDetails.sync();

		totals.sync();
	}

	private void onFloorLambda(Fluent fluent, UIEvent event) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]);

		floor.lambda = value;
		floorR.sync();
		floorDetails.sync();

		totals.sync();
	}

	private void onFloorThickness(Fluent fluent, UIEvent event) {
		double value = Double.parseDouble(fluent.domSelectedOptions()[0]) * 0.01;

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
			result.append(Utils.format(surface.getA()));
		}
		result.append("m2. Heat transfer coefficient H = U * A = ");
		if (surface.sizeY == 0.0 || surface.sizeX == 0.0 || surface.thickness == 0.0) {
			result.append("..");
		} else {
			result.append(Utils.format(surface.getH()));
		}
		return Fluent.Li().span(null, result.toString());
	};

	public static Function<Surface, Fluent> showRandU = surface -> {
		StringBuilder text = new StringBuilder("R=Rsi+(tickness/lambda)+Rse=");
		if (surface.thickness != 0.0) {
			text.append(Utils.format(surface.getR()));
		} else {
			text.append("..");
		}
		text.append(" and U=1/R=");
		if (surface.thickness != 0.0) {
			text.append(Utils.format(surface.getU()));
		} else {
			text.append("..");
		}
		return Fluent.Span(null, text.toString());
	};

	public void warnLength() {
		if (client.getSolarPanels() == null || client.getSolarTubes() == null) {
			return;
		}
		ViewOn<HashMap<String, String>> warnings = client.getWarnings();
		final String name = "totalWidth";
		double totalLength = cubic.state().length;

		double solarTubesLength = client.getSolarTubes().getTotalLength();
		double solarPanelsLength = client.getSolarPanels().getTotalLength();
		if (solarTubesLength + solarPanelsLength > totalLength) {
			warnings.state().put(name,
					"Warning: the panels do not fit your roof: roof length = " + Utils.format(totalLength)
							+ "m, solar tubes length = " + Utils.format(solarTubesLength) + "m, solar panels length = "
							+ Utils.format(solarPanelsLength) + "m.");
			warnings.sync();
		} else {
			warnings.state().remove(name);
			warnings.sync();
		}

	}

	public double[] getHeatgap() {
		return heatgap;
	}

}
