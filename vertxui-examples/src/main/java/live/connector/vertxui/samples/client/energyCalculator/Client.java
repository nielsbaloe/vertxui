package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.google.gwt.core.client.EntryPoint;

public class Client implements EntryPoint {

	public static String[] scripts = new String[] { "https://cdn.jsdelivr.net/npm/chart.js@2.7.1/dist/Chart.min.js" };

	public Client() {

		body.p().txt(
				"Door het gebruik van deze energie calculatorBy using this calculator, I understand that this probably contains serious errors, "
						+ "and if I see an error or can be improved according to my knowledge, I will let it know.");
		new Heating();
		new Shower();
		new Cooking();
		new SolarTubes();

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

	@Override
	public void onModuleLoad() {
	}

}
