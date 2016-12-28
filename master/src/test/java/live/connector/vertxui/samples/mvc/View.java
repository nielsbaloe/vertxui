package live.connector.vertxui.samples.mvc;

import java.util.Arrays;

import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;
import live.connector.vertxui.fluentHtml.Input;
import live.connector.vertxui.fluentHtml.Li;
import live.connector.vertxui.reacty.ReactC;

class ModelSendDto {
	public String name;
}

class ModelReceiveDto {
	public String betterTitle;
}

public class View {

	private ModelSendDto model = new ModelSendDto();
	private Div response;

	// TODO this is just a scetch, this does NOT WORK intentionally
	public View() {

		// View
		Body fluentBody = FluentHtml.getBody();
		Input input = fluentBody.div().input("text", "aName");

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
		response = fluentBody.div();
		// Append something
		response.add(new Li("bla"));
		// Append a stream of things
		response.add(Arrays.asList("aaa", "a").stream().filter(e -> e.length() > 1).map(t -> new Li(t)));
		// Create a custom class with a custom constructor that gets the model
		// (not here) and call sync() on your class when the model changes.
		response.add(new ReactC() {
			@Override
			public FluentHtml generate() {
				return new Li("no Model");
			}
		});
		// Or, give a model and a transfer function, and call sync on the parent
		// which is here 'response'!!
		response.add(model, m -> {
			if (m.name != null) {
				return new Li("with model: " + m.name);
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

	// Please don't run this class but run the Server instead.
	public static void main(String[] args) {
		try {
			new View();
		} catch (Error ule) {
			// This looks weird but teaVM does not know UnsatisfiedLinkError....
			if (ule.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
				System.out.println("Please don't run this class but run the Server instead.");
			} else {
				ule.printStackTrace();
			}
		}
	}

}
