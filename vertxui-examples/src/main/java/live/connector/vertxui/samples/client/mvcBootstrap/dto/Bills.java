package live.connector.vertxui.samples.client.mvcBootstrap.dto;

import java.util.Date;
import java.util.List;

public class Bills {

	public List<Bill> all;

	public static class Bill implements Comparable<Bill> {

		public Name who;
		public double amount;
		public Date date;

		public Bill() { // empty constructor for serialization
		}

		public Bill(Name who, double amount, Date date) {
			this.who = who;
			this.amount = amount;
			this.date = date;
		}

		@Override
		public int compareTo(Bill o) {
			return o.date.compareTo(date);
		}

	}

	public enum Name {
		Linda, Niels
	}

}
