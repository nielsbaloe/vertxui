package live.connector.vertxui.samples.client.energyCalculator;

import static live.connector.vertxui.client.fluent.FluentBase.body;

public class Stove {

	public Stove() {

		body.h2(null, "Stove");

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

}
