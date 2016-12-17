package live.connector.vertxui.samples.eventbusTwice;

import fj.data.List;
import live.connector.vertxui.core.EventBus;
import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;
import live.connector.vertxui.fluentHtml.Input;
import live.connector.vertxui.fluentHtml.Li;

public class View {

	public static class ModelSendDto { // Models are placed inline as example
		public String name;
	}

	public static class ModelReceiveDto {
		public String betterTitle;
	}

	private ModelSendDto model = new ModelSendDto();
	private Div response;

	// TODO this is just a scetch, this does NOT WORK intentionally
	public View() {

		// View
		Body body = FluentHtml.getBody();
		response = body.div();
		Input input = body.div().input("text", "aName");

		// Controller
		EventBus eventBus = new EventBus("localhost:8100");
		input.keyUp(changed -> {
			model.name = input.getValue();
			eventBus.publish(model);
		});
		eventBus.consume(ModelReceiveDto.class, a -> {
			response.inner("Server says: " + a);
		});

		// TODO move elsewhere in an example with li and option
		// STREAMS EXAMLES, only the latter works
		// STREAMS EXAMLES, only the latter works
		// STREAMS EXAMLES, only the latter works

		// Java 8
		// java.util.stream.Stream.of("aaa", "a").filter(a -> a.length() >
		// 2).map(t -> new Li(t)).forEach(div::append);

		// ReactRX 2
		// Observable.fromArray("bbb", "b").filter(a -> a.length() > 2).map(t
		// -> new Li(t)).forEach(div::append);

		// functionaljava.org
		List.list("ccc", "c").filter(a -> a.length() > 2).map(t -> new Li(t)).foreachDoEffect(response::append);

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
