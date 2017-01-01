package live.connector.vertxui.client.samples.mvcBootstrap;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.head;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;

public class View implements EntryPoint {

	// private List<Bill> bills;

	// TODO this is work-in-progress, get back in a few weeks!
	public View() {
		// Head
		head.style("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css").scriptSync(
				"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");
		head.meta().attr(Att.charset, "utf-8");
		head.meta().attr(Att.name_, "viewport").attr(Att.content, "width=device-width, initial-scale=1");

		// Body
		body.divd(Fluent.h1("Bills")).classs("jumbotron text-center");

		// Controller-SERVER
		// EventBus eventBus = new EventBus("localhost:8100", null);
		// input.keyUp(event -> {
		// model.name = input.getValue();
		// eventBus.publish(model);
		// });
		// eventBus.consume(ModelReceiveDto.class, a -> {
		// response.inner("Server says: " + a);
		// });

		// // Controller-GUI
		// Div response = body.div();
		// // Append something
		// response.add(body.li("bla"));
		// // Append a stream of things
		// response.add(Arrays.asList("aaa", "a").stream().filter(e ->
		// e.length() > 1).map(t -> Fluent.Li(t)));
		// // Create a custom class with a custom constructor that gets the
		// model
		// // (not here) and call sync() on your class when the model changes.
		// response.add(new ReactC() {
		// @Override
		// public Fluent generate() {
		// return Fluent.Li("no Model");
		// }
		// });
		// // Or, give a model and a transfer function, and call sync on the
		// parent
		// // which is here 'response'!!
		// response.add(model, m -> {
		// if (m.name != null) {
		// return Fluent.Li(m.name);
		// } else { // TODO support null case
		// return Fluent.Li("no name yet.");
		// }
		// });
		//
		// input.keyup(event -> {
		// model.name = input.value();
		// console.log("model name: " + model.name);
		// response.sync(); // re-render
		// });
	}

	@Override
	public void onModuleLoad() {
	}

}
