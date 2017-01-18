vertxui
===

A 100% Java 100% asynchronous toolkit: Fluent HTML with a virtual DOM for speed and beautiful view-on-model ReactJS-ish notation, POJO serializers for ajax/websockets/sockJS/eventbus, an Eventbus server- and clientside, automatic browser reloading, fast pure-java jUnit GUI testing, and more. This is how Java web programming should have looked like 15 years ago.

VertxUI offers:
* forget about Javascript, you're familiar with Java.
* communicate in 100% the same POJO's at client- and serverside through ajax/websockets/sockjs/eventbus.
* forget about HTML or learning a HTML-ish language like ReactJS, but declarate view-on-model by using Java lambdas and streams.
* no IDE tooling required, the java to javascript translation happens run-time.
* during development: automatic browser reloading of generated javascript, resources (.css/.jpg/etc) and state
* Fluent HTML has a virtual DOM behind the scenes (a la ReactJS), only visually updating what changed in your model.
* painless nodejs-less websockets/sockjs/eventbus at server and browsers in the same language.
* speeedy junit testing of Fluent HTML objects by testing against the virtual DOM, or headless browser testing.

Serverside [Vert.X](http://vertx.io/) adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.
* the serverside EventBus, and a wonderful professional speedy async ecosystem.

Pure-Java clientside (using down to the DOM-metal GWT elemental) means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML like React and others)
* use the same DTO/entity classes and constants server-side and client-side.
* access to both the Java (threads etc) ánd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling

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

## View-On-Model

You can create state-aware Fluent HTML objects with ViewOn. The ViewOn<> constructor receives your model (or state) and a function how to translate this to a (Fluent HTML) view. On a sync() call, Fluent Html only updates changed DOM-items.

		ViewOn<Model> view = response.add(model, m -> {
				 return Li("myClass").a(m.name, "/details?name=" + m.name);
			}
		});

		input.keyup(event -> {
			 model.name = input.value();
			 view.sync(); // re-render.
		});

The ViewOn object has a reference to your model, so you don't have to keep a reference to it in your class. You can abuse this to set the state when your Model is just a primitive for example a string. The method .state(model) also calls sync:

		input.keyup(event -> {
			view.state("newValue");
		});

If necessary, use Java 8 streams to write your user interface:

	div(Stream.of("apple","a").filter(a->a.length()>2).map(t -> new Li(t)));


### jUnit

Because Fluent HTML has a Virtual DOM, you can also 'abuse' it to run jUnit testcases without firing up a browser. Now that is really fast.

	@Test
	public void test() {
		View view = new View();

		// Check the title (using 'id')
		List<Fluent> a = VirtualDomSearch.getElementsById("titlerForJunitTest",Fluent.body);
		assertEquals(a.size(), 1);
		assertTrue(a.get(0).tag().equals("H1"));
	}

If you really need the DOM (for whatever reason), that's possible too. Vertxui then compiles to javascript and runs a method which runs all your javascript tests right inside a headless browser. All inside jUnit. Add this method below to run junit with a dom. Also write in onModuleStart which methods should be run. Do not add a constructor, because that will be run in junit ánd in the browser ;) .

	@GwtIncompatible
	@Test
	public void runWithjUnit() throws Exception {
		TestWithDom.runwithJunit(this.getClass());
	}

	@Override
	public void onModuleLoad() {
		Asserty.asserty(() -> {
			// these tests will be done:
			testThis();
			testThat();
			// ... other tests here too
		});
	}
 

### Pojo

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
