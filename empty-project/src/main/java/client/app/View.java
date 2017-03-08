package client.app;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.console;
import static live.connector.vertxui.client.fluent.FluentBase.head;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Css;

public class View implements EntryPoint {

	@Override
	public void onModuleLoad() {
		View view = new View();
		Controller controller = new Controller();
		controller.setView(view);
		view.start(controller);
	}

	public void start(Controller controller) {
		head.script("/figwheely.js");

		console.log("Hi there console");
		body.div(null, "Hi there body").css(Css.fontSize, "330%");
		controller.doAjax();
	}

	public void setResponse(int responseCode, String text) {
		body.div(null, "Server said: " + text);
	}

}
