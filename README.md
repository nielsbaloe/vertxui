vertxui
===

A 100% Java 100% asynchronous toolkit (Vert.X and GWT elemental), with POJO serializers, Fluent HTML (with virtualDOM behind the scenes), an Eventbus server and clientside, automatic browser reloading and more.

VertxUI offers:
* forget about URL's, just register and publish POJO's with ajax websockets sockjs or the eventbus.
* forget about HTML or learning a HTML-ish language like ReactJS, just write fluent HTML with lambda's and streams.
* forget about Javascript, you're familiar with Java.
* forget about installing IDE tooling, the java to javascript translation happens run-time.
* during development: automatic browser reloading of generated javascript, resources (.css/.jpg/etc) and state
* Fluent html has a virtual DOM behind the scenes (a la ReactJS), only visually updating what changed in your model.
* websockets, sockjs and the VertX EventBus are available at server and browsers in the same language.

Pure-Java clientside (using GWT-elemental) means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML like React and others)
* use the same DTO/entity classes and constants server-side and client-side.
* access to both the Java (threads etc) ánd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling
 
[Vert.X](http://vertx.io/) adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.
* the serverside EventBus, and a wonderful speedy async ecosystem.

Examples are included for: hello world (vanilla js and Fluent HTML), automatic browser reloading (Figwheely), 3 webchats with: websockets SockJS and EventBus, lots of POJO (de)serialization, TodoMVC, Bootstrap, jQuery Mobile and more.

### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. It compiles to javascript too (if there is a source folder), that doesn't happen by installing any IDE plugin. Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

	router.route("/client").handler(new VertxUI(Client.class, true));

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, just turn on FigWheely. 

If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

		router.get("/figWheely.js").handler(FigWheely.create());
  
### Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure GWT elemental (previously TeaVM):

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

You can create state-aware Fluent HTML objects too. Fluent Html only updates the components that were changed: Work in progress!

		ViewOf<Model> view = response.add(model, m -> {
				 return Li("myClass").a(m.name, "/details?name=" + m.name);
			}
		});

		input.keyup(event -> {
			 model.name = input.value();
			 view.sync(); // re-render.
		});

The ViewOf<> object also keeps the state. In case you don't keep a reference your models, you can also use it to set a new state. By setting the state it also calls .sync(). This is specificly usefull when your Model is just a String. For example, if Model has a constructor which takes the name:

		input.keyup(event -> {
			view.state(new Model(input.value());
		});

If necessary, use Java 8 streams to write your user interface:

	div(Stream.of("apple","a").filter(a->a.length()>2).map(t -> new Li(t)));

### Pojo example

Having your entity and DTO classes (models) in the same language has its advantages. All three chat examples (websockets, sockjs, eventBus) also have POJO examples in them. Here is an example:

The model+view (browser):

	class Model {
		public String name;
	}

	class View {

	private Model model = new Model();
	private Div response;
	
	public View() {
		response = body.div();
		Input input = body.div().input("text", "aName");
		
		// Controller		
		input.keyUp(changed -> {
			model.name = input.value();
			Pojofy.ajax("POST", "/ajax", model, modelMap, null, (String s) -> console.log(s));
		});

	}

The controller (serverside) can be for example (ajax example):

		router.post("/ajax").handler(Pojofy.ajax(Model.class, (m, c) -> {
			log.info("Received a pojo from the client: color=" + m.color);
			return "a string";
		}));

Niels Gorisse
