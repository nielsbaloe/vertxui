package live.connector.vertxui.samples.figwheely;

import live.connector.vertxui.fluentHtml.Button;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;

public class Client {

	/**
	 * Please, run the server and change some text in the constructor below,
	 * save, and then look at your browser, press the button or see what
	 * happens. Don't forget to edit the /sources/sample.css file, save, and
	 * look at your browser at the same time. Do NOT reload your browser!
	 */
	public Client() {
		Div div = FluentHtml.getBody().div();
		Button button = div.button("Look at the Client and css, and change something WITHOUT reloading the browser!");
		button.click(e -> {
			button.inner("Something else!!");
			div.button("sdfsdf!");
		});
	}

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

}
