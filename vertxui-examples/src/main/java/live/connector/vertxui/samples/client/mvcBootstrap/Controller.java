package live.connector.vertxui.samples.client.mvcBootstrap;

import java.util.Collections;
import java.util.Date;

import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.InputElement;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Bill;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Bills.Name;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Grocery;
import live.connector.vertxui.samples.client.mvcBootstrap.dto.Totals;

public class Controller {

	// Models - here the controller 'owns' them, but you can also let the store
	// own them. Actually, the model-to-view objects also have the models,
	// because these are placeholders that do not change in the application
	// (only the .all content is changed).
	private Totals totals = new Totals();
	private Bills bills = new Bills();
	private Grocery grocery = new Grocery();
	// model 'menu' is a primitive not saved here, but directly pushed to the
	// client.

	private View view;

	private Store store;

	public Controller(Store store, View view) {
		this.store = store;
		this.view = view;
	}

	public Bills getBills() {
		return bills;
	}

	public Totals getTotals() {
		return totals;
	}

	public Grocery getGrocery() {
		return grocery;
	}

	public void onBillOnlyNumeric(KeyboardEvent event) {
		int code = event.getCharCode();
		if ((code >= 48 && code <= 57) || code == 0) {
			return; // numeric or a not-a-character is OK
		}
		event.preventDefault();
	}

	public void onGroceryAdd(KeyboardEvent event) {
		if (event.getKeyCode() != KeyboardEvent.KeyCode.ENTER) {
			return;
		}

		// PREVENT SENDING THE FORM BECAUSE IT HAS ONLY ONE ELEMENT (IE11)
		event.preventDefault();

		InputElement element = (InputElement) event.getTarget();
		String text = element.getValue();
		if (!text.isEmpty()) {
			element.setValue("");

			// Apply change in model
			grocery.all.add(text);

			// Push model to view
			view.syncGrocery();

			// Push change to server
			store.addGrocery(text);
		}

	}

	public void onGroceryDelete(MouseEvent evt) {
		Element element = ((Element) evt.getTarget());
		((InputElement) element).setChecked(false);
		String text = element.getAttribute("value");

		// Apply change in model
		grocery.all.remove(text);

		// Push model to view
		view.syncGrocery();

		// Push change to server
		store.deleteGrocery(text);
	}

	public void onAddBill(String name, String amount, Date date) {
		// Apply change in model
		Bill bill = new Bills.Bill(Name.valueOf(name), Integer.parseInt(amount), date);
		bills.all.add(bill);
		Collections.sort(bills.all);

		// Push model to view
		view.syncBills();

		// Push change to server
		store.addBill(bill);
	}

	public void onMenuHome(Event evt) {
		// Push model to view
		view.stateAndSyncMenu("home"); // the view handles this model

		// Note that old available data is already shown before any answer
		store.getTotals(this::setTotals);
	}

	public void onMenuBills(Event evt) {
		// Push model to view
		view.stateAndSyncMenu("bills");// the view handles this model

		// Note that old available data is already shown before any answer
		store.getBills(this::setBills);
	}

	public void onMenuGrocery(Event evt) {
		// Push model to view
		view.stateAndSyncMenu("grocery");// the view handles this model

		// Note that old available data is already shown before any answer
		store.getGrocery(this::setGrocery);
	}

	/**
	 * Callback from the store - in a seperate method so we can junit test it
	 * 
	 * @param responseCode
	 * @param totals
	 */
	public void setTotals(int responseCode, Totals totals) {
		this.totals.all = totals.all;
		view.syncTotals();
	}

	/**
	 * Callback from the store - in a seperate method so we can junit test it
	 * 
	 * @param responseCode
	 * @param totals
	 */
	public void setBills(int responseCode, Bills bills) {
		this.bills.all = bills.all;
		view.syncBills();
	}

	/**
	 * Callback from the store - in a seperate method so we can junit test it
	 * 
	 * @param responseCode
	 * @param totals
	 */
	public void setGrocery(int responseCode, Grocery grocery) {
		this.grocery.all = grocery.all;
		view.syncGrocery();
	}

}
