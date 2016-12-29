package live.connector.vertxui.client.samples.mvc;

import java.util.Arrays;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ReactC;

class ModelSendDto {
	public String name;
}

class ModelReceiveDto {
	public String betterTitle;
}

public class View implements EntryPoint {

	private ModelSendDto model = new ModelSendDto();
	private Fluent response;

	// TODO this is just a scetch, this does NOT WORK intentionally
	public View() {

		// View
		Fluent body = Fluent.getBody();
		Fluent input = body.div().input("text", "aName");

		// Controller-SERVER
		// EventBus eventBus = new EventBus("localhost:8100", null);
		// input.keyUp(event -> {
		// model.name = input.getValue();
		// eventBus.publish(model);
		// });
		// eventBus.consume(ModelReceiveDto.class, a -> {
		// response.inner("Server says: " + a);
		// });

		// Controller-GUI
		response = body.div();
		// Append something
		response.add(body.li("bla"));
		// Append a stream of things
		response.add(Arrays.asList("aaa", "a").stream().filter(e -> e.length() > 1).map(t -> Fluent.Li(t)));
		// Create a custom class with a custom constructor that gets the model
		// (not here) and call sync() on your class when the model changes.
		response.add(new ReactC() {
			@Override
			public Fluent generate() {
				return Fluent.Li("no Model");
			}
		});
		// Or, give a model and a transfer function, and call sync on the parent
		// which is here 'response'!!
		response.add(model, m -> {
			if (m.name != null) {
				return Fluent.Li("with model: " + m.name);
			} else {
				return null;
			}
		});

		input.keyUp(event -> {
			model.name = input.getValue();
			if (model.name != null) {
				System.out.println(model.name);
			}
			response.sync(); // RE-RENDER!!
		});

	}

	@Override
	public void onModuleLoad() {
	}

}
