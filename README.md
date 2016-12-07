vertx-ui
===

A VertX optimised UI by runtime java to javascript translation. Ideal as microservice when serving just a piece of your browser screen. 

The runtime translation means that you don't need any Maven/IDE tools during development. You don't even need file access at runtime. Using java instead of javascript means strong-typing, direct binding with entity classes, and having access to the java ánd the javascript ecosystems.

To debug,  the VertxUI debug parameter to true, save your java file in your IDE (which triggers the auto-compilation) and VertxUI will translate the classfiles to javascript every time you refresh the browser.

Right now the hello world TeaVM works, without Vert.X eventbus or service-proxy (http://vertx.io/docs/vertx-service-proxy/java ) in the client yet.

A future example would be:

    public class GUI extends VertxUI {
    
	// Model (inline as demonstration)
	public class Model {
		public String user;
		public String password;
	}

	private Model model = new Model();

	private Html title;

	public GUI() {

		// View
		title = docRoot().div("<h1>Bla</h1>");
		Html form = docRoot().form();
		form.input("user", model.user, i -> {
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

