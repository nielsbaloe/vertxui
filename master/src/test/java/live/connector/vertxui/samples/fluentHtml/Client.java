package live.connector.vertxui.samples.fluentHtml;

import fj.data.List;
import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;
import live.connector.vertxui.fluentHtml.Li;

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
		Body body = FluentHtml.getBody();
		Div div = body.div();

		// Java 8
		// java.util.stream.Stream.of("aa", "a").filter(a -> a.length() >
		// 2).map(t -> new Li(t)).forEach(div::append);

		// ReactRX
		// Observable.fromArray("bb", "b").filter(a -> a.length() > 2).map(t ->
		// new Li(t)).forEach(div::append);

		// functionaljava.org
		List.list("cc", "c").filter(a -> a.length() > 2).map(t -> new Li(t)).foreachDoEffect(div::append);

		// body.swap(partOld,part);

	}

}
