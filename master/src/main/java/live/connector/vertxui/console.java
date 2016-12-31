package live.connector.vertxui;


public class console {

	public native static void log(String message) /*-{
													console.log(message);
													}-*/;
}
