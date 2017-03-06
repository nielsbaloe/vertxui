package live.connector.vertxui.samples.client.testjUnitWithDom;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import live.connector.vertxui.samples.client.mvcBootstrap.Store;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;

public class StoreNone extends Store {

	@Override
	public void getTotals(BiConsumer<Integer, Totals> callback) {
		callback.accept(200, new Totals());
	}

	@Override
	public void getBills(BiConsumer<Integer, Bills> callback) {
		callback.accept(200, new Bills());
	}

	@Override
	public void getGrocery(BiConsumer<Integer, Grocery> callback) {
		callback.accept(200, new Grocery());
	}

	@Override
	public void deleteGrocery(String value, Consumer<String> revertCallback) {
	}

	@Override
	public void addGrocery(String text, Consumer<String> revertCallback) {
	}

	@Override
	public void addBill(Bill bill, Consumer<Bill> revertCallback) {
	}
}
