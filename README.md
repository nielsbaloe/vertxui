vertx-ui
===

A Vert.X optimised UI package containing runtime Java to Javascript translation (by TeaVM.org), and a small fluid HTML toolkit. Ideal as microservice when serving just a piece of your browser screen. Use the Java ánd the JavaScript ecosystems for your product.

The runtime translation means that you don't need any Maven/IDE tools during development. You don't even need file access at runtime. Using java instead of javascript means strong-typing, direct binding with entity classes, and convenient tooling.

To debug, set the VertxUI debug parameter to true, press CTRL+S  in your IDE (to triggerauto-compilation) and VertxUI will translate the classfiles to javascript when you refresh the browser.

An almost-there example:

    public class Client extends VertxUI {
    
	// Model (inline as demonstration)
	public class Model {
		public String user;
		public String password;
	}

	private Model model = new Model();

	private Div title;

	public Client() {
		Html body = Html.body();
		
		// View
		Div title = body.div("<h1>Bla</h1>");
		Form form = body.form()
			.input("user", model.user, i -> {
			model.user = i;
		}).input("password", i -> {
			model.password = i;
		}).input("SEND", null, () -> {
			form.send();
		});

		// View-Controller binding
		form.post(GUI::send);
    
		eventBus().register("response", GUI::receive);
	}

	// Controller
	private void send(JsonObject updated) {
		eventBus().wrap("login", updated);
		title.inner("<h1>Sent!</h1>");
	}

	private void receive(JsonObject received) {
		title.inner("Received from the server: "+received);

	  }
    }

