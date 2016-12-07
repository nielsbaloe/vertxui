vertx-ui
===

A compile-time UI package optimised for Vert.X. Ideal as microservice, serving a part of the browser screen.
I am programming towards a GUI that is written in Java, compiled to javascript at runtime, strongly-binded with entity classes, and directly communication with the Vert.X eventbus or even a VertX. service proxy (http://vertx.io/docs/vertx-service-proxy/java ) for example:

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

