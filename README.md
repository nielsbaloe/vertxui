vertxui
===

Suppose we had native Java access at the browser, backed by a no-nonsense Java asynchronous server. Then both browser and server would be using the same Java data entities/DTO's, and we would like to use these strong-typed POJO's for ajax/websocket/sockjs traffic. And an eventbus where server and browsers can share information.

For the view, we would not want to write HTML, but write in a fluent descriptive notation with Java 8 lambda's for event handling. And a clean way to describe a view on an entity, similar to ReactJS but then nicely in Java with lambda's streams enums and other object oriented features.

Regarding tooling, we would want to use trusted Java tooling like jUnit, and realtime debugging with automatic browser refreshing after a code change, but we would not want to install any IDE-specific tooling.  

Welcome to vertxui. This is how Java web programming should have looked like 15 years ago.

VertxUI offers:
* 100% java, 100% asynchronous
* communicate in 100% the same POJO's at client- and serverside through ajax/websockets/sockjs/eventbus.
* forget about HTML or learning a HTML-ish language like ReactJS, but declarate view-on-models by using Java lambdas and streams.
* no IDE tooling required, the java to javascript translation happens run-time.
* during development: automatic browser reloading of generated javascript, resources (.css/.jpg/etc) and state
* Fluent HTML has a virtual DOM behind the scenes (a la ReactJS), only visually updating what changed in your model.
* painless nodejs-less websockets/sockjs/eventbus at server and browsers in the same language.
* speeeeedy junit testing of Fluent HTML objects by testing against virtual DOM, and headless browser testing.

Serverside [Vert.X](http://vertx.io/) adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.
* the serverside EventBus, and a wonderful professional speedy async ecosystem.

Pure-Java clientside (not locked-in currently using down-to-the-DOM wrapped-away GWT/elemental) means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML like React)
* use the same DTO/entity classes and constants server-side and client-side.
* access to both the Java (threads etc) Ã¡nd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling

Examples are included for: hello world (vanilla js and Fluent HTML), automatic browser reloading (Figwheely), 3 webchats with: websockets SockJS and EventBus, POJO (de)serialization for ajax websockets sockJS and eventbus, TodoMVC, Bootstrap, jQuery Mobile and more.

### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. It compiles to javascript too (if there is a source folder), without installing an IDE plugin. Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

	router.route("/client").handler(new VertxUI(Client.class, true));

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, just turn on FigWheely. If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

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
		Fluent a = VirtualDomSearch.getElementById("titlerForJunitTest",Fluent.body);
		assertTrue(a.tag().equals("H1"));
	}

If you really need the DOM (for whatever reason), that's possible too (but not advisable because it's slower). Vertxui then first compiles to javascript and then runs one javascript function tests() right inside a real representative headless 100%-java browser. All inside jUnit, freely combinable with other pure-java tests. Thanks to GWT, the stacktrace is accurate too. Do not add a constructor, because that will be run in junit and in the browser ;) .

	public class FluentRenderTests extends TestDOM {
	
		@Override
		public void tests() {
			fluentAttributeRenderTests();
			fluentStylesTests();
		}

	...
	
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

### More

Currently GWT is used, because it is by far the most efficiënt and full-featured Java 8 implementation out there. In the first month, TeaVM is used, which is 1000% faster in compiling but does not correctly support all lambda notations. The same goes for jsweet, Vertxui was ported into jsweet in about half an hour, but jsweet does not support all Java constructions (not even enums properly).

Polyglot is possible as long as the sourcecode is included in the jars.


Niels Gorisse
