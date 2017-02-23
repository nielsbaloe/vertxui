package client.app;

import live.connector.vertxui.client.transport.Pojofy;

public class Controller {

	public static String url = "/ajax";

	private View view;

	public Controller() {
	}

	public void setView(View view) {
		this.view = view;
	}

	public void doAjax() {
		Pojofy.ajax("PUT", url, "from Browser", null, null, view::setResponse);
	}

}
