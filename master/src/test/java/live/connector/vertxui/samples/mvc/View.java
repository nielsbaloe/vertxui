package live.connector.vertxui.samples.mvc;

import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.ul;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import j2html.Tag;
import live.connector.vertxui.core.EventBus;
import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.Div;
import live.connector.vertxui.fluentHtml.FluentHtml;
import live.connector.vertxui.fluentHtml.Input;
import live.connector.vertxui.fluentHtml.Li;

//Models are placed inline for demonstration purposes
class ModelSendDto {
	public String name;
}

class ModelReceiveDto {
	public String betterTitle;
}

abstract class ComponentClass {

	abstract public Tag generate();

	public void shown() {
	}

	public void removed() {
	}

}

interface ComponentMethod<A> {
	public Tag generate(A state);

}

class ComponentMethodWrapper<A> extends ComponentClass {
	private A state;
	private ComponentMethod<A> m;

	ComponentMethodWrapper(A state, ComponentMethod<A> m) {
		this.state = state;
		this.m = m;
	}

	@Override
	public Tag generate() {
		return m.generate(state);
	}

}

class SendComponent extends ComponentClass {
	Thread t;
	ModelSendDto state;

	SendComponent(ModelSendDto state) {
		this.state = state;
	}

	@Override
	public void shown() {
		Runnable r = () -> {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				state.name += "r";
				View.reRender(this);
			}
		};
		t = new Thread(r);
		t.start();
	}

	@Override
	public void removed() {
		t.stop();
	}

	@Override
	public Tag generate() {
		// TODO Auto-generated method stub
		return null;
	}

}

class JustATag extends ComponentClass {
	String tag;

	JustATag(String tag) {
		this.tag = tag;
	}

	@Override
	public Tag generate() {

		// TODO Auto-generated method stub
		return null;
	}

}

class Component {
	ComponentClass _tagOnly(String tag) {
		return new ComponentClass() {

			@Override
			public Tag generate() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}

public class View {

	private ModelSendDto model = new ModelSendDto();
	private Div response;

	// TODO this is just a scetch, this does NOT WORK intentionally
	public View() {

		// View
		Body fluentBody = FluentHtml.getBody();
		response = fluentBody.div();
		Input input = fluentBody.div().input("text", "aName");

		// Controller
		EventBus eventBus = new EventBus("localhost:8100", null);
		input.keyUp(event -> {
			model.name = input.getValue();
			eventBus.publish(model);
		});
		eventBus.consume(ModelReceiveDto.class, a -> {
			response.inner("Server says: " + a);
		});

		// STREAMS EXAMLES, only the latter works
		// STREAMS EXAMLES, only the latter works
		// STREAMS EXAMLES, only the latter works
		Div div = fluentBody.div();

		// Java 8
		Stream.of("aaa", "a").filter(a -> a.length() > 2).map(t -> new Li(t)).forEach(div::append);
		String[] some = new String[] { "aaa", "a" };
		Arrays.stream(some).filter(a -> a.length() > 2).map(t -> new Li(t)).forEach(div::append);

		// ReactRX 2
		// Observable.fromArray("bbb", "b").filter(a -> a.length() > 2).map(t
		// -> new Li(t)).forEach(div::append);

		// functionaljava.org
		// Streamy.fromDot("ccc", "c").filter(a -> a.length() > 2).map(t -> new
		// Li(t)).foreachDoEffect(response::append);

		// Markup
		// Markup
		// Markup
		// Markup

		// Groovy markup builder: tussen haakjes attr en innerText, daarna
		// accolades children
		// ul(classss("style"), tekstErtussen) { li("a"), li("a") }

		// http://j2html.com: lege constructor, attr en childs met With(..)
		html().with(head().with(link().withRel("stylesheet").withHref("/css/main.css")),
				body().with(h1("Heading!"), h2("kiekeboe")));
		// System.out.println(test.render());

		// own fluent htmlL idee: childs=constructor, setAttribute=method
		fluentBody.div(new Li("sdf"), new Li("sdfsdfdf"));

		// Componenten
		@SuppressWarnings("unused")
		ComponentMethod<String[]> gender1 = strings -> (Tag) Arrays.stream(strings).map(a -> h1(a))
				.collect(Collectors.toSet());
		@SuppressWarnings("unused")
		ComponentMethod<List<String>> gender4 = i -> ul()
				.with(i.stream().map(x -> li().withText(x)).collect(Collectors.toList()));
		ComponentMethod<ModelSendDto[]> gender2 = in -> div().with(Arrays.stream(in).filter(a -> a.name.length() > 2)
				.map(t -> h1(t.name)).reduce((x, y) -> x.with(y)).get());

		input.keyUp(event -> {
			model.name = input.getValue();
			startRender(new ComponentMethodWrapper<ModelSendDto[]>(Arrays.asList(model).toArray(new ModelSendDto[0]),
					gender2), fluentBody);
		});

		startRender(new SendComponent(model), fluentBody);

		// DENKWERK: Component en Tag moeten hetzelfde zijn, in en door elkaar
		// te gebruiken.

		// VERDER LEZENvc
		// https://facebook.github.io/react/docs/handling-events.html
	}

	public static <A> void reRender(ComponentClass sendComponent) {
		// CALLED WHEN AN ALREADY started rendering should be rerendered.
	}

	private static <A> void startRender(ComponentClass gen, FluentHtml target) {

		// VERY VERY SMART TRANSLATION FROM THIS VIRTUAL DOM INTO REAL DOM WITH
		// AS LESS REPLACEMENTS AS POSSIBLE
		// AND, IF GEN INSTANCEOF STATEFULL, CALL SHOWN AND UNSHOWN TOO.
		// https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060#.wc4ze6sj1

		// SAVE the connection betwen this target and the generator, in case a
		// rerender is requested...

	}

	// Please don't run this class but run the Server instead.
	public static void main(String[] args) {
		try {
			new View();
		} catch (Error ule) {
			// This looks weird but teaVM does not know UnsatisfiedLinkError....
			if (ule.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
				System.out.println("Please don't run this class but run the Server instead.");
			} else {
				ule.printStackTrace();
			}
		}
	}

}
