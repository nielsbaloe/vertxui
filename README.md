vertx-ui
===

A VertX optimised UI by runtime java to javascript. Ideal as microservice, serving a part of the browser screen. Compilation to javascript at runtime (not at compile-time using any maven/IDE frameworks), strong-binding with entity classes.

Right now the runtime javascript translation by TeaVM works, without Vert.X eventbus or service-proxy (http://vertx.io/docs/vertx-service-proxy/java ) in the client yet.

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

