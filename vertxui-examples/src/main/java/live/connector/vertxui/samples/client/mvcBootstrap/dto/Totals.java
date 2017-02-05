package live.connector.vertxui.samples.client.mvcBootstrap.dto;

import java.util.HashMap;
import java.util.Map;

public class Totals {

	public Map<Bills.Name, Double> all;

	public Totals() {
		all = new HashMap<>();
	}

}
