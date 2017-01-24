package live.connector.vertxui.samples.client.mvcBootstrap.dto;

import java.util.Date;
import java.util.List;

public class Bills {

	public List<Bill> bills;

	public static class Bill {
		public Date date;
		public Name who;
		public int amount;
		public String notes;

	}

	public enum Name {
		Linda, Niels
	}

}
