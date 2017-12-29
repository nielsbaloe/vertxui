package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.samples.client.energyCalculator.components.ChartJs;

// TODO
// !! att(selected) kapot voor alle selects !! 
// heating: overal standaard wat ingevuld
// water chart: negatieve waarden voor bijstook
// electrical chart: toevoegen
// overal selects ipv invulvelden
// referentie naar alle bronnen
// zonneboilers: waarschuwen bij R waarde te laag voor bouwbesluit
// zonneboilers: switch maken met/zonder CRC
// zonneboiler+zonnepanelen: breedte totale zonneboilers+zonnepanelen te groot
// shower: aantal personen toevoegen bij Shower
// shower: per maand precies aantal dagen
// core: orginele folders bewaren voor bekijken directory change ipv per file (zodat aanmaken werkt)
// core: nagaan waarom zoveel meuk in temp blijft hangen bij de standaard GWT opties

public class Client implements EntryPoint {

	private Shower shower;
	private Heating heating;

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
				"By using this energy calculator, I understand that it is extremely beta, contains serious errors, "
						+ "does not take windows and doors and construction junctures and sun orientation into account yet, "
						+ "will be improved and does not contain any visual feedback yet. If I see an error or if I have "
						+ "the knowledge how to improve this calculator, I will definitely give feedback on this website.")
				.css(Css.color, "red");

		ChartJs chart = new ChartJs(root, 500, 200);
		// sticky
		chart.css(Css.position, "sticky", Css.top, "0");
		// position
		chart.css(Css.Float, "right");
		// transparant white background
		chart.css(Css.backgroundColor, "rgba(255, 255, 255, 0.6)");

		new Heating(root, chart, this);
		new Shower(root, chart, this);
		new Cooking(root);
		new SolarTubes(root, chart);
		new SolarPanels(root);
		new Stove(root);
	}

	protected void setShower(Shower shower) {
		this.shower = shower;
	}

	protected Shower getShower() {
		return shower;
	}

	public void setHeating(Heating heating) {
		this.heating = heating;
	}

	public Heating getHeating() {
		return heating;
	}

	@Override
	public void onModuleLoad() {
	}

}
