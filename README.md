vertx-ui
===

A [Vert.X](http://vertx.io/) pure-Java UI toolkit with fast server-time Java to Javascript translation (by [TeaVM](http://teavm.org/)), a mini fluent HTML toolkit, and automatic browser reloading. Write serverside and clientside in Java with a lot of advantages.

Pure-Java clientside means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML code like React and other js libraries)
* use the same DTO/entity classes server-side and client-side.
* access to both the Java (threads etc) ánd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling
 
Vert.X adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.

Vertx-ui serves on top of that:
* an EventBus at server and clients in the same language.
* forget about URL's, just register and publish objects from and to the EventBus.
* no IDE tooling and IDE near-locking background processing, the Java to Javascript translation happens server-time.
* for development: automatic browser reloading of generated javascript and other files (.css/.jpg) without browser refresh.
* forget about HTML, just write a bit of code in fluent HTML.
* ideal as microservice: no file access necessary at the server.


### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. So, not only forget about javascript, but forget about editing html files too.

	router.route("/client").handler(new VertxUI(Client.class, true));

Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

The hello-world example translates from java to javascript within a second (and after that less) server startup time - that is probably less than you putting that file somewhere in the right folder. The result is one raw 68kb dependency-less javascript+html file, or a 16kb HTTP-zipped file. The resulting javascript is so small because TeaVM only translates from APIs that what was actually used.

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, set the VertxUI parameter to true and the javascript will not be minified and debug info is added too (thanks to TeaVM).

If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

	FigWheely.with(router);
  
### Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure 
[TeaVM](http://teavm.org/).

		HTMLButtonElement button = document.createElement("button").cast();
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.listenClick(evt -> clicked());
		body.appendChild(button);
		...
		
	private void clicked() {
		button.setDisabled(true);
		thinkingPanel.getStyle().setProperty("display", "");
		...
	}

### Clientside fluent HTML

You can also use fluent HTML, which is a lot shorter and more readable.

		Button button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		...
		
	private void clicked() {
		button.disable();
		thinkingPanel.css("display", "");
		...
	}

Of course you can mix with existing html and javascript by an encapsulated document.getElement(id):

	Div responses = Div.dom(id); // from Dom
	
	HTMLElement element = responses.dom(); // to Dom

Use Java 8 streams too for fluent filtering and creating. Streams are not (yet) Java 8 or ReactRX but functionaljava.org :

    List.list("ccc", "c").filter(a -> a.length() > 2).map(t -> new Li(t)).foreachDoEffect(ul::append);


### EventBus at server and client in pure java gives beautiful MVC 

The eventbus is available in Java at both sides. This is just like in GWT, but then without all the boilerplate nonsense. Just register the same DTO at clientside and serverside to be received or send. This is easier then also facilitating which service the DTO should go to, the server can work it out.

This project is just a few weeks old - so hang on - this will work 100% very soon, but not yet. Please check again later.

The model+view (browser):

    public class View {

	public static class ModelSendDto { // Models are placed inline as example
		public String name;
	}
	
	public static class ModelReceiveDto {
		public String betterTitle;
	}
	
	private ModelSendDto model = new ModelSendDto();
	private Div response;
	
	public View() {
		
		// View
		Body body = FluentHtml.getBody();
		response = body.div();
		Input input = body.div().input("text", "aName");
		
		// Controller
		EventBus eventBus = new EventBus("localhost:8100");
		input.keyUp(changed -> {
			model.name = input.getValue();
			eventBus.publish(model);
		});
		eventBus.consume(ModelReceiveDto.class, a -> {
			response.inner("Server says: " + a);
		});
	}

The controller (server) fragments look like this. In the start() of your vert.x you can bind specific DTO-classes to specific service-functions

	// receive a message
	vertx.eventBus().consumer(ModelSendDto.class.getName(), message-> {
		ModelSendDto modelSend = Json.decodeValue((String)message.body(), ModelSendDto.class);
			
		// reply to one message
		message.reply(Json.encode(new View.ModelReceiveDto());
			
		};
	
	// publish to everyone	
	vertx.eventBus().publish(ModelReceiveDto.class.getName(), new View.ModelReceiveDto() );
		
	// example registration of a DTO in the start() of your verticle
	vertx.eventBus().consumer(Order.class,mongoHandler::saveOrder);


