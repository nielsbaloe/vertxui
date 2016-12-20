package j2html;

public class EmptyTag extends Tag {

	public EmptyTag(String tagName) {
		super(tagName);
	}

	@Override
	public String render() {
		return renderOpenTag();
	}

}
