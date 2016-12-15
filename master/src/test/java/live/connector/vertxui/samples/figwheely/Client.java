package live.connector.vertxui.samples.figwheely;

import live.connector.vertxui.fluidhtml.Button;
import live.connector.vertxui.fluidhtml.FluidHtml;

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

	/**
	 * Please, run the server and change some text in the constructor below, and
	 * look at your browser, press the button or see what happens. Don't forget
	 * to edit the /sources/sample.css file and look at your browser at the same
	 * time.
	 */
	public Client() {
		FluidHtml body = FluidHtml.getBody();
		Button button = body.button("First text");
		button.onClick(e -> {
			System.out.println("Hello *insert your name* !!!");
			button.inner("Something else!");
			body.button("sdfsdff!");
		});
	}

}
