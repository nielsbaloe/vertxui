package live.connector.vertxui.samples.client.mvcBootstrap;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * An example to create a component, for the datepicker from jquery-ui. Make
 * sure you have included the jquery-ui .js and .css before using this
 * component.
 * 
 * @author ng
 *
 */
public class ComponentDatePicker extends Fluent {

	public ComponentDatePicker() {
		super("INPUT", null);
		classs("form-control").att(Att.type, "text").id("datepicker");

		// The non-component version was:
		// Input("form-control", "text").id("datepicker");

	}

	@Override
	public void isRendered(boolean shown) {
		if (shown) {
			eval("$('#datepicker').datepicker({ dateFormat:'dd/mm/yy'});");
		}
	}
}
