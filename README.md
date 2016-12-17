vertx-ui
===

A [Vert.X](http://vertx.io/) pure-Java UI toolkit with fast server-time Java to Javascript translation (by [TeaVM](http://teavm.org/)), a small fluid HTML toolkit, and automatic browser reloading called Figwheely. The Vert.X eventbus does not only stretchs all the way inside your browser (with SockJS websockets), but now also in the same programming language.

Using Java instead of Javascript means strong-typing, direct binding with entity classes, convenient tooling, easy junit testing of UI code, Java 8 lambda's and streams to write javascript and generate html, and both the Java ánd the JavaScript ecosystems under your fingertips.

The server-time translation works at server startup time so you don't need to set up any Maven/IDE tools for developing, and you don't have your IDE being locked because it is doing 'something in the background' when you save a file (a nightmare you probably recognise with Maven or GWT).

You don't need file access at runtime, which makes vertx-ui ideal as minimal microservice. Heck, remember you don't need to setup Apache or Tomcat too because you're using Vert.X which can [handle thousands of connections per core](https://dzone.com/articles/inside-vertx-comparison-nodejs).


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

## Clientside fluent HTML

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


## EventBus at server and client in pure java gives beautiful MVC 

The eventbus is available in Java at both sides. This is just like in GWT, but then without all the boilerplate nonsense. Just register the samen DTO at clientside and serverside to be received or send.

This project is just a few weeks old - so hang on - this will work 100% very soon, but not yet.

The model+view client-side is:

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

The server code will this kind of code:

		vertx.eventBus().consumer(ModelSendDto.class.getName(), message-> {
			ModelSendDto modelSend = Json.decodeValue((String)message.body(), ModelSendDto.class);
			
			message.reply(Json.encode(new View.ModelReceiveDto());
			
		};
		
		vertx.eventBus().publish(ModelReceiveDto.class.getName(), new View.ModelReceiveDto() );
		
