package live.connector.vertxui.samples.client.mvcBootstrap;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;

import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;

/**
 * A three-way store, which handles all communication with the server.
 * 
 * @author Niels Gorisse
 *
 */
public class Store {

	// URL's
	public static String totalsUrl = "/rest/totals";
	public static String billsUrl = "/rest/bills";
	public static String groceryUrl = "/rest/grocery";

	public void getTotals(BiConsumer<Integer, Totals> callback) {
		Pojofy.ajax("GET", totalsUrl, null, null, totalsMap, callback);
	}

	public void getBills(BiConsumer<Integer, Bills> callback) {
		Pojofy.ajax("GET", billsUrl, null, null, billsMap, callback);
	}

	public void getGrocery(BiConsumer<Integer, Grocery> callback) {
		Pojofy.ajax("GET", groceryUrl, null, null, groceryMap, callback);
	}

	public void deleteGrocery(String value, Consumer<String> revertCallback) {
		Pojofy.ajax("DELETE", groceryUrl, value, null, null, (status, __) -> {
			if (status != 200) {
				revertCallback.accept(value);
			}
		});
	}

	public void addGrocery(String text, Consumer<String> revertCallback) {
		Pojofy.ajax("POST", groceryUrl, text, null, null, (status, __) -> {
			if (status != 200) {
				revertCallback.accept(text);
			}
		});
	}

	public void addBill(Bill bill, Consumer<Bill> revertCallback) {
		Pojofy.ajax("POST", billsUrl, bill, billMap, null, (status, __) -> {
			if (status != 200) {
				revertCallback.accept(bill);
			}
		});
	}

	// POJO MAPPERS
	public interface TotalsMap extends ObjectMapper<Totals> {
	}

	public interface GroceryMap extends ObjectMapper<Grocery> {
	}

	public interface BillsMap extends ObjectMapper<Bills> {
	}

	public interface BillMap extends ObjectMapper<Bills.Bill> {
	}

	public static TotalsMap totalsMap = GWT.isClient() ? GWT.create(TotalsMap.class) : null;
	public static GroceryMap groceryMap = GWT.isClient() ? GWT.create(GroceryMap.class) : null;
	public static BillsMap billsMap = GWT.isClient() ? GWT.create(BillsMap.class) : null;
	public static BillMap billMap = GWT.isClient() ? GWT.create(BillMap.class) : null;

}
