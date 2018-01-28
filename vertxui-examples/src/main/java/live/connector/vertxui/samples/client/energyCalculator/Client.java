package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;

/**
 * TODO from feedback:
 * 
 * - kozijnen
 * 
 */
public class Client implements EntryPoint {

	private Shower shower;
	private Heating heating;
	private SolarTubes solarTubes;
	private SolarPanels solarPanels;
	private ChartJs electricChart;
	private ChartJs waterChart;
	private Cooking cooking;
	private Stove stove;
	private ViewOn<HashMap<String, String>> infoAndwarnings;

	public static ArrayList<String> getScripts() {
		return ChartJs.getScripts();
	}

	public Client() {
		// try to put everthing in <div id=here />
		Fluent root = Fluent.getElementById("here");
		if (root == null) {
			root = body;
		}
		root.p().txt(
				"By using this energy calculator, you understand that it is extremely beta, and probably contains serious errors. "
						+ "Please help! For example, if you are an engineer and you see an improvement in the"
						+ " heat calculation, or if you know a lot about wood and you see an error: e-mail us!")
				.css(Css.color, "red");

		Fluent conclusions = root.div();
		conclusions.css(Css.position, "sticky", Css.top, "0px"); // sticky
		conclusions.css(Css.backgroundColor, "rgba(255, 255, 255, 0.8)"); // background
		electricChart = new ChartJs(conclusions, 500, 300, "Electricity (kW)");
		electricChart.css(Css.Float, "right"); // position
		waterChart = new ChartJs(conclusions, 500, 300, "Warm water (kW)");
		infoAndwarnings = conclusions.add(new HashMap<>(), infoAndWarnings -> {
			Fluent result = Fluent.Div();
			infoAndWarnings.forEach((key, message) -> {
				Fluent span = result.span(null, message);
				if (key.startsWith("info")) {
					span.css(Css.color, "purple");
					result.br();
				} else {
					span.css(Css.color, "red");
					result.br();
				}
			});
			return result;
		});

		new Heating(root, waterChart, this);
		new Shower(root, waterChart, this);
		new SolarTubes(root, waterChart, this);

		new Cooking(root, this);
		new SolarPanels(root, this);

		new Stove(root, this);
	}

	protected void setShower(Shower shower) {
		this.shower = shower;
	}

	protected Shower getShower() {
		return shower;
	}

	protected ViewOn<HashMap<String, String>> getInfoAndWarnings() {
		return infoAndwarnings;
	}

	protected void setHeating(Heating heating) {
		this.heating = heating;
	}

	protected Heating getHeating() {
		return heating;
	}

	protected SolarTubes getSolarTubes() {
		return solarTubes;
	}

	protected void setSolarTubes(SolarTubes solarTubes) {
		this.solarTubes = solarTubes;
	}

	protected ChartJs getElectricChart() {
		return electricChart;
	}

	protected ChartJs getWaterChart() {
		return waterChart;
	}

	protected Cooking getCooking() {
		return cooking;
	}

	protected void setCooking(Cooking cooking) {
		this.cooking = cooking;
	}

	public void setSolarPanels(SolarPanels solarPanels) {
		this.solarPanels = solarPanels;
	}

	public SolarPanels getSolarPanels() {
		return solarPanels;
	}

	public Stove getStove() {
		return stove;
	}

	public void setStove(Stove stove) {
		this.stove = stove;
	}

	@Override
	public void onModuleLoad() {
	}

}
