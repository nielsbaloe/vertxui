package live.connector.vertxui.samples.client.helloWorldFluentHtml;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Style;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.AllExamples;
import live.connector.vertxui.samples.client.chatEventBus.Dto;

public class Client implements EntryPoint {

	public final static String url = "/ajax";

	private Fluent button;
	private Fluent response;
	private Fluent thinking;

	public Client() {
		button = body.div().button("Click me!").id("hello-button").click(this::clicked);
		response = body.div();
		thinking = body.div().inner("The server waits as demonstration!").id("thinking-panel").css(Style.display,
				"none");
	}

	// It is advisable to write callbacks into methods, so you can easily write
	// jUnit tests.
	private void clicked(Event e) {
		button.disabled(true);
		thinking.css(Style.display, "");
		Pojofy.ajax("POST", url, null, null, null, this::responsed);
	}

	// It is advisable to write callbacks into methods, so you can easily write
	// jUnit tests.
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
