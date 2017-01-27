package live.connector.vertxui.samples.client.mvcBootstrap.dto;

import java.util.Date;
import java.util.List;

public class Bills {

	public List<Bill> bills;

	public static class Bill {

		public Bill() { // empty constructor for serialization
		}

		public Bill(Name who, double amount, Date date) {
			this.who = who;
			this.amount = amount;
			this.date = date;
		}

		public Name who;
		public double amount;
		public Date date;
	}

	public enum Name {
		Linda, Niels
	}

}
