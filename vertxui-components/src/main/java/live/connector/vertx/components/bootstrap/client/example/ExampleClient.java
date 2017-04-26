package live.connector.vertx.components.bootstrap.client.example;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.head;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertx.components.bootstrap.client.Container;
import live.connector.vertxui.client.FigWheelyClient;

public class ExampleClient implements EntryPoint {

	public static String[] css = new String[] { "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css",
			"https://cdnjs.cloudflare.com/ajax/libs/pikaday/1.5.1/css/pikaday.min.css" };

	public static String[] scripts = new String[] {
			"https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.17.1/moment.min.js",
			"https://cdnjs.cloudflare.com/ajax/libs/pikaday/1.5.1/pikaday.min.js" };

	@Override
	public void onModuleLoad() {
		head.script(FigWheelyClient.urlJavascript);

		Container container = new Container();
		body.add(container);

		container.h1(null, "Hi there");
		container.p(null, "bladiebla");
	}

}
