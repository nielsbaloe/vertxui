package live.connector.vertxui.samples.fluidHtml;

public class Client {

	// Please don't run this class but run the Server instead.
	public static void main(String[] args) {
		try {
			new Client();
		} catch (Error ule) {
			// This looks weird but teaVM does not know UnsatisfiedLinkError....
			if (ule.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
				System.out.println("Please don't run this class but run the Server instead.");
			} else {
				ule.printStackTrace();
			}
		}
	}


	// TODO this is just a scetch, this does NOT WORK intentionally
	public Client() {
		// Body body = FluidHtml.getBody();
		// Div div = body.div();

		// nice examples:
		// http://stackoverflow.com/questions/22382453/java-8-streams-flatmap-method-example
		// Stream<String> items = Stream.of("apples", "pears", "bears", "wind");
		//
		// div.div(items.map(x -> {
		// return new Li(x);
		// }));
		// div.div(items.map(x -> {
		// return new Li(x);
		// }));

		// List<String> others = new ArrayList("a", "b", "c");
		// others.forEach(x -> {
		// div.li(x);
		// });
		// body.swap(partOld,part);

	}

}
