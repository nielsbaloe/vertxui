package live.connector.vertxui.client.samples.helloWorldFluentHtml;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Style;
import live.connector.vertxui.client.samples.AllExamples;
import live.connector.vertxui.client.samples.chatEventBus.Dto;
import live.connector.vertxui.client.transport.Pojofy;

public class Client implements EntryPoint {

	public final static String url = "/ajax";

	private Fluent button;
	private Fluent response;
	private Fluent thinking;

	public Client() {
		button = body.div().button("Click me!").id("hello-button").click(evt -> {
			button.disabled(true);
			thinking.css(Style.display, "");
			Pojofy.ajax("POST", url, null, null, null, this::responsed);
		});
		response = body.div();
		thinking = body.div().inner("The server waits as demonstration!").id("thinking-panel").css(Style.display,
				"none");
	}

	private void responsed(String text) {
		button.disabled(false);

		response.div().inner(text);
		thinking.css(Style.display, "none");

		// extra: POJO example
		Pojofy.ajax("POST", urlPojo, new Dto("white"), AllExamples.dto, AllExamples.dto, a -> console.log(a.color));
	}

	public final static String urlPojo = "/pojo";

	@Override
	public void onModuleLoad() {
	}

}
