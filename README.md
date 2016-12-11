vertx-ui
===

A [Vert.X](http://vertx.io/) optimised pure-Java UI toolkit with unnoticable-fast server-time Java to Javascript translation (by [TeaVM](http://teavm.org/)), and a fluid HTML toolkit. The Vert.X eventbus does not only stretchs all the way inside your browser (by using websockets with sockjs), but now also in the same programming language.

The server-time translation works at startup time so you don't need any Maven/IDE tools during development. You don't even need file access at runtime, ideal as minimal microservice. Using Java instead of Javascript means strong-typing, direct binding with entity classes, convenient tooling, easy junit testing, and both the Java ánd the JavaScript ecosystems under your fingertips.

Server-time translation does not mean you can not debug your code. To debug, set the VertxUI debug parameter to true, run your code, press CTRL+S in your IDE (to trigger auto-compilation) and VertxUI will translate the classfiles to javascript when you refresh the browser.

## Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary wrapping HTML, ready to be shown in the browser.

    router.route("/client").handler(new VertxUI(Client.class, false, true));

Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).


## Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure 
[TeaVM](http://teavm.org/).

		HTMLButtonElement helloButton = document.createElement("button").cast();
		helloButton.setAttribute("id", "hello-button");
		helloButton.setInnerHTML("Click me");
		helloButton.listenClick(evt -> clicked());
		body.appendChild(helloButton);
		...
		
	private void clicked() {
		helloButton.setDisabled(true);
		thinkingPanel.getStyle().setProperty("display", "");
		...
	}

## Clientside fluid HTML

You can also use fluid HTML, which is a lot shorter and more readable.

		Button button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		...
		
	private void clicked() {
		button.disable();
		thinkingPanel.css("display", "");
		...
	}


## EventBus websocket at server and client in pure java 


This project is just a few weeks old - so hang on. I hope to have the eventbus access ready very very soon, it doesn't look that difficult to wrap an existing javascript API inside TeaVM.

A scetch I wrote before I started:

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
	private static void send(JsonObject updated) {
		eventBus().wrap("login", updated);
		title.inner("<h1>Sent!</h1>");
	}
	
	private void receive(JsonObject received) {
		title.inner("Received from the server: "+received);
	
	  }
    }

