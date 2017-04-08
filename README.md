vertxui
===

Suppose we had native asynchronous Java access at the browser, backed by a no-nonsense Java asynchronous server. Then both browser and server would be using the same Java strong-typed data models/entities/DTO's for ajax/websocket/sockjs traffic.

For the view, we would not want to write HTML or a new Java/C#-lookalike language, but write in a fluent descriptive notation with Java 8 lambda's for event handling. And a clean way to describe a view-on-a-model, similar to ReactJS but then nicely in Java with streams enums packages classes and other basic mature object oriented language features.

Regarding tooling, we would want to use trusted Java tooling like jUnit and Mockito, and realtime debugging with automatic browser refreshing after a code change, all in a professional familiar grown-up IDE. We would want plain stacktraces when something goes wrong, so we can trace errors. Ideally without having to install any IDE-specific tooling.

Welcome to vertxui. This is how Java web programming should have looked like 15 years ago.

Use it in your project by using one of these

	Maven:   
		<dependency>
				<groupId>live.connector</groupId>
				<artifactId>vertxui-core</artifactId>
				<version>1.0</version>
		</dependency>

	Gradle:	
	         compile 'live.connector:vertxui-core:1.0'

or another source from  the [MVN Repository](https://mvnrepository.com/artifact/live.connector/vertxui-core/1.0) .
For a laid back demo how to get it up and working from scratch and to see some running examples, look at [this video](https://www.youtube.com/watch?v=ZusasYDjMKo).

## General

VertxUI is ideal for writing single-paged-apps or microservices that serve a part of a page.

VertxUI offers:
* 100% java, 100% asynchronous.
* communication in 100% the same POJO's at client- and serverside through ajax/websockets/sockjs/eventbus.
* no JavaScript/TypeScript or tools like ReactJS, but declarate view-on-models by using Java lambdas.
* automatic runtime browser reloading of generated javascript, resources (.css/.jpg/etc) and state.
* a sophisticated virtual DOM behind the scenes (a la ReactJS), only updating visual changes.
* painless nodejs-less websockets/sockjs/eventbus at server and browsers in the one language.
* fast junit testing of Fluent views against virtual DOM, and mixed-languages headless browser tests with full stacktraces.

Serverside [Vert.X](http://vertx.io/) adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver.
* no need for anything else: no Apache and Tomcat.
* a clientside and eventside EventBus.
* in general a wonderful professional speedy async ecosystem.

Pure-Java clientside means:
* use Java 8's lambda's and streams and packages/classes for web view and behavior (instead of TypeScript and ES6).
* use a grown-up Java compiler and a grown-up IDE (instead of Lint and Gulp and thousands others to choose from).
* use the same strong-typed entity classes and constants server-side and client-side (instead of typeless)
* access to both the Java and the Javascript ecosystems
* extremely easy junit testing of client-side GUI (even without a DOM, but also with a DOM)
* zero browser-specific hacks, only ES5 Javascript which runs in [any browser that is used today](http://caniuse.com/#feat=es5).

VertxUI is
* _not_ a new GUI toolkit: there are no vertxui GUI elements, only external HTML/CSS libraries like bootstrap are used.
* _not_ a new HTML template engine: no HTML is parsed or generated (except one one-liner index.html for booting).
* _not_ a new Javascript toolkit: just use java, no need for external libraries like jquery.
* _not_ an IDE plugin, the java to javascript translation happens run-time in all IDE's.
* _not_ a locked-in solution: you can also use VertxUI to extend an existing page, or use Tomcat (or any other framework which serves plain files) instead of Verx. Down the drain, even the internal java to javascript bridge is easily swappable.

Examples are included for: hello world (vanilla js and Fluent HTML), automatic browser reloading (Figwheely), 3 webchats with: websockets SockJS and EventBus, POJO (de)serialization for ajax websockets sockJS and eventbus, TodoMVC, a Bootstrap application, and more. Vertxui mixes well with pure html+css frameworks like bootstrap, purecss, jquery mobile and similar.

### Serverside

The serverside is easy. This single line generates a single-lined wrapping HTML file (as /index.html) and serves it along with all generated Javascript code (in folder /a/... ). It compiles to javascript too (if there is a source folder), without installing an IDE plugin, just run a class with a main() method and point your browser to http://localhost/ ;) .

		router.get("/*").handler(VertxUI.with(Client.class, "/", isDebugMode, doGenerateHtml));

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, just turn on FigWheely. If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

		router.get("/figWheely.js").handler(FigWheely.create());
  
### Clientside pure DOM

Note that nowadays the trend is to make websites with rich CSS libraries that require no Javascript for the view;  most of the webpages you are looking at every day are written that way (Bootstrap, jQuery Mobile). These libraries are using plain low-level HTML with CSS, and only Javascript for interaction (and legacy or fall-back behind-the-scenes code). So let's write a bit of HTML easily connectable with CSS, and add some interaction - using Java.

The clientside with pure DOM looks like plain javascript but then with Java (8's lambda) callbacks. This is pure Elemental/GWT (previously TeaVM), so more or less, it 'is' plain javascript:

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setTextContent("Click me");
		button.setOnclick(event -> click());
		body.appendChild(button);
		...
		
	private void click() {
		button.setAttribute("disabled", "");
		thinking.getStyle().setProperty("display", "");
		...
	}

### Clientside Fluent HTML

You can also use Fluent HTML, which is a lot shorter and more readable. Don't worry about speed, Fluent uses a virtual DOM behind the scenes. An id is given to the button in the example below, but that is not the way to go in Fluent; typically you save the object as a class member (or only save the view-on-model), rather than to do all sorts of slow searches on the DOM with document.getElementBy....

		button = body.button(null, "Click me").id("hello-button").click(this::click);
		...
		
	private void click(Fluent __, MouseEvent __) {
		button.disabled(true);
		thinking.css(Style.display, "");
		...
	}

## View-On-Model

You can create state-aware visual objects with ViewOn (and ViewOnBoth for two models). The ViewOn<> constructor receives your model (or state) and a function how to translate this to a (Fluent HTML) view. On a sync() call, Fluent only updates DOM-items that were changed, so just declaratively write down how you would like to see things and don't worry about changing elements.

		ViewOn<Model> spanWithLink = response.add(model, m -> {
			return Span("myClass").a(null, m.name, "/details?name=" + m.name);
			}
		});

		someInput.keyup((fluent,event) -> {
			 model.name = fluent.domValue(); // change the model
			 spanWithLink.sync(); // re-render the view
		});

The ViewOn object has a reference to your model, so you don't have to keep a reference to it in your view class. You can abuse this to set the state when your Model is just a primitive like a string. The method state() also calls sync():

		someInput.keyup((fluent,event) -> {
			view.state(fluent.domValue());
		});

If necessary, use Java 8 streams to write your user interface:

	body.ul(Stream.of("apple","a").filter(a->a.length()>2).map(t -> new Li("aClass",t)));

For more information on Fluent, please take a look at the examples, especially [todoMVC](https://github.com/nielsbaloe/vertxui/tree/master/vertxui-todomvc/src/main/java/live/connector/vertxui/samples/client/todomvc) or [mvcBootstrap](https://github.com/nielsbaloe/vertxui/tree/master/vertxui-examples/src/main/java/live/connector/vertxui/samples/client/mvcBootstrap); these are quite big. Here was chosen for a typical and quite convenient store/model/view/controller setup, but of course you are free to choose your own way. Although checking out and running all examples might be the best starting point, you can also take a look at the [wiki](https://github.com/nielsbaloe/vertxui/wiki) for a bit more low-level explanation on Fluent.

### jUnit

Because Fluent HTML has a Virtual DOM, you can also 'abuse' it to run jUnit front-end tests without firing up a browser. Now that is really fast.

	@Test
	public void test() {
		View view = new View();

		// Check the title (using 'id')
		Fluent a = VirtualDomSearch.getElementById("titlerForJunitTest",Fluent.body);
		assertTrue(a.tag().equals("H1"));
	}

If you really need the DOM (I can't think of a good case that easily, but you might), that's possible too (but it's absolutely not advised because it's slower). Vertxui first compiles to javascript and then runs your java test inside a real representative headless 100%-java browser. Thanks to a register-and-run procedure, you decide when and which javascript tests are run, so you are absolutely free to mix java and javascript execution in your test. Do not add a constructor, because that will be run in jUnit and in the browser ;) . Use runJS and registerJS as follows:

	public class WithDom extends TestDOM {
	
		@GwtIncompatible
		@Test
		public void yourJUnitTestStartpoint() throws Exception {
			...
			System.out.println("This is java");
			runJS(3); // run '3'
			...
		}
	
		@Override
		public Map<Integer, Runnable> registerJS() {
			Map<Integer, Runnable> result = new HashMap<>();
			result.put(3, () -> attributeTests()); // register '3'
			return result;
		}

		public void attributeTests() {
			console.log("This is javascript");
			....
		}

	}
 

### Pojo

Having your entity and DTO classes (models) in the same language has its advantages. All three chat examples (websockets, sockjs, eventBus) also have POJO examples in the code, so open the console in your browser to see them (F12). Here is an example:

The model+view (browser):

	class Model {
		public String name;
	}

	class View {

	private Model model = new Model();
	private Fluent response;
	
	public View() {
		response = body.div();
		Input input = body.div().input("cssClass", "text");
		
		// Controller		
		input.keyUp((fluent,changed) -> {
			model.name = fluent.domValue();
			Pojofy.ajax("POST", "/ajax", model, modelMap, null, (String text) -> console.log(text));
		});

	}

The controller (serverside) can be for example (ajax example):

		router.post("/ajax").handler(Pojofy.ajax(Model.class, (model, c) -> {
			log.info("Received a pojo from the client: color=" + model.color);
			return "a string";
		}));

### More

Currently Elemental/GWT -extremely wrapped away- is used, because it is by far the most efficiënt and full-featured Java 2 Javascript implementation out there. In the first month, TeaVM was used, which is 1000% faster in compiling but does not correctly support lambda's and does not have a rich ES5 browser in its API. The same goes for jSweet, Vertxui was ported into jSweet in about half an hour, but jSweet does not support all Java constructions (like enums) and does not do a very good job in 100% Java support in general. GWT is actually very very reliable, and has a 100% browser abstraction in classes with Elemental. It has been chewed on since 2006 ;) .

Polyglot language support is possible as long as the sourcecode is included in the jars, there are vague plans to support Groovy as a proof of concept.

Niels Gorisse
