package live.connector.vertxui.samples.sockjs;

/**
 * @author Niels Gorisse
 *
 */

// TODO this is just a scetch, this is WORK IN PROGRESS (nearly there)
public class Client {

	public static final String eventBusAddress = "someAddressBothWays";

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

	public Client() {

	}

}
