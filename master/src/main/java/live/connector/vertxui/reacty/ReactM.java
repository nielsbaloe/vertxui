package live.connector.vertxui.reacty;

import live.connector.vertxui.fluentHtml.FluentHtml;

public interface ReactM<A> {
	public FluentHtml generate(A state);
}
