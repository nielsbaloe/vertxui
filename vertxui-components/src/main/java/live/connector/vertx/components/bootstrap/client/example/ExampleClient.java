package live.connector.vertx.components.bootstrap.client.example;

import static live.connector.vertxui.client.fluent.FluentBase.body;
import static live.connector.vertxui.client.fluent.FluentBase.head;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertx.components.bootstrap.client.Container;
import live.connector.vertxui.client.FigWheelyClient;

public class ExampleClient implements EntryPoint {

	public static ArrayList<String> getCss() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css");
		return result;
	}

	@Override
	public void onModuleLoad() {
		head.script(FigWheelyClient.urlJavascript);

		Container container = new Container();
		body.add(container);

		container.h1(null, "Hi there");
		container.p(null, "bladiebla");
	}

}
