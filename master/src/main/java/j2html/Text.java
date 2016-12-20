package j2html;

public class Text extends DomContent {

	private String text;

	public Text(String text) {
		this.text = text;
	}

	@Override
	public String render() {
		return escapeHtml4(text);
	}

	public static String escapeHtml4(String txt) {
		// TODO include return
		// org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(text);
		return txt.replace("<", "&lt;").replace("&", "&amp;");
	}

}
