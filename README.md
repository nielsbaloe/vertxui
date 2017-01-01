vertx-ui
===

A [Vert.X](http://vertx.io/) pure-Java toolkit containing a Fluent HTML (with virtualDOM behind the scenes)), an Eventbus at server and clientside, and automatic browser reloading.

Vertx-ui offers:
* an EventBus at server and clients in the same language.
* forget about URL's, just register and publish objects from and to the EventBus.
* forget about HTML, just write fluent HTML.
* forget about Javascript, you're familiar with Java.
* forget about installing IDE tooling, the java to javascript translation happens at server-time.
* during development: automatic browser reloading of generated javascript and other files (.css/.jpg) without browser refresh.
* Fluent html has a virtual DOM behind the scenes (a la ReactJS), only visually updating what changed in your model.

Pure-Java clientside (using GWT-elemental) means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML like React and others)
* use the same DTO/entity classes and constants server-side and client-side.
* access to both the Java (threads etc) ánd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling
 
Vert.X adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.
* the serverside EventBus, and a wonderful speedy async ecosystem.

Examples are included for: hello world (vanilla js and Fluent HTML), automatic browser reloading (Figwheely), webchats with: websockets SockJS plus EventBus, TodoMVC, Bootstrap, and more.

### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. So, not only forget about javascript, but forget about editing html files too. Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

	router.route("/client").handler(new VertxUI(Client.class, true));

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, just turn on FigWheely. 

If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

	FigWheely.with(router);
  
### Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure GWT elemental:

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.setOnclick(evt -> clicked());
		body.appendChild(button);
		...
		
	private void clicked() {
		button.setAttribute("disabled", "");
		thinking.getStyle().setProperty("display", "");
		...
	}

### Clientside Fluent HTML

You can also use fluent HTML, which is a lot shorter and more readable. Don't worry about speed, fluent HTML uses a virtual DOM behind the scenes.

		Button button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		...
		
	private void clicked() {
		button.disabled(true);
		thinking.css(Style.display, "");
		...
	}

Work in progress!!: You can create FluentHTML objects yourself, which are state-aware. Fluent Html only updates the components that were changed:

		response.add(model, m -> {
				 return Fluent.Li(m.name);
			}
		});

    ....
    
		input.keyup(event -> {
			 model.name = input.value();
			 response.sync(); // re-render
		});

GWT has Java 8 lambda's and streams, ideal to write your user interface:

	Stream.of("apple","a").filter(a->a.length()>2).map(t -> new Li(t)).forEach(ul::append);

### EventBus at server and client in pure java gives beautiful MVC 

The eventbus is available in Java at both sides. This is just like in GWT, but then stretched out to _all_ browsers (a la socket.io). Just register the same DTO at clientside and serverside to be received or send. This is easier then also facilitating which service the DTO should go to, the server can work it out.

Work in progress!! This project is just weeks old - so hang on - this will work 100% very soon, but not yet. Please check again later.

The model+view (browser):

	class Model {
		public String betterTitle;
	}

	class View {

	private Model model = new Model();
	private Div response;
	
	public View() {
		response = body.div();
		Input input = body.div().input("text", "aName");
		
		// Controller
		EventBus eventBus = new EventBus("localhost/eventBus");
		input.keyUp(changed -> {
			model.name = input.getValue();
			eventBus.publish(model, null);
		});
		eventBus.consumer(Model.class, a -> {
			response.inner("Server says: " + a);
		});
	}

The controller (server) fragments look like this. In the start() of your vert.x you can bind specific DTO-classes to specific service-functions

	// receive a message
	vertx.eventBus().consumer(Model.class.getName(), message-> {
		Model modelSend = Json.decodeValue((String)message.body(), Model.class);
			
		// reply to one message
		message.reply(Json.encode(model);
			
		};
	
	// publish to all users
	vertx.eventBus().publish(Model.class.getName(), new Model() );
		
	// example registration of a DTO in the start() of your verticle: if an Order.class is received,
	// call method MongoHandler::saveOrder(Order).
	vertx.eventBus().consumer(Order.class,mongoHandler::saveOrder);


Niels Gorisse
