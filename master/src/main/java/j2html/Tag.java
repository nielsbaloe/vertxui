package j2html;

import java.util.ArrayList;

public abstract class Tag extends DomContent {

    protected String tagName;
    private ArrayList<Attribute> attributes;

    protected Tag(String tagName) {
        this.tagName = tagName;
        this.attributes = new ArrayList<>();
    }

    String renderOpenTag() {
        StringBuilder sb = new StringBuilder("<").append( tagName );
        for (Attribute attribute : attributes) {
            sb.append( attribute.render() );
        }
        sb.append( ">" );
        return sb.toString();
    }

    String renderCloseTag() {
        return "</" + tagName + ">";
    }

    /**
     * Sets an attribute on an element
     *
     * @param name  the attribute
     * @param value the attribute value
     */
    boolean setAttribute(String name, String value) {
        if (value == null) {
            return attributes.add(new Attribute(name));
        }
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
                attribute.setValue(value); //update with new value
                return true;
            }
        }
        return attributes.add(new Attribute(name, value));
    }

    /**
     * Sets a custom attribute
     *
     * @param attribute the attribute name
     * @param value     the attribute value
     * @return itself for easy chaining
     */
    public Tag attr(String attribute, String value) {
        setAttribute(attribute, value);
        return  this;
    }

    /**
     * Call attr-method based on condition
     * {@link #attr(String attribute, String value)}
     */
    public Tag condAttr(boolean condition, String attribute, String value) {
        return (condition ? attr(attribute, value) :  this);
    }

    /**
     * Convenience methods that call attr with predefined attributes
     *
     * @return itself for easy chaining
     */
    public Tag isAutoComplete()                                                { return attr(Attr.AUTOCOMPLETE, null); }
    public Tag isAutoFocus()                                                   { return attr(Attr.AUTOFOCUS, null); }
    public Tag isHidden()                                                      { return attr(Attr.HIDDEN, null); }
    public Tag isRequired()                                                    { return attr(Attr.REQUIRED, null); }
    public Tag withAlt(String alt)                                             { return attr(Attr.ALT, alt); }
    public Tag withAction(String action)                                       { return attr(Attr.ACTION, action); }
    public Tag withCharset(String charset)                                     { return attr(Attr.CHARSET, charset); }
    public Tag withClass(String className)                                     { return attr(Attr.CLASS, className); }
    public Tag withContent(String content)                                     { return attr(Attr.CONTENT, content); }
    public Tag withHref(String href)                                           { return attr(Attr.HREF, href); }
    public Tag withId(String id)                                               { return attr(Attr.ID, id); }
    public Tag withData(String dataAttr, String value)                         { return attr(Attr.DATA + "-" + dataAttr, value); }
    public Tag withMethod(String method)                                       { return attr(Attr.METHOD, method); }
    public Tag withName(String name)                                           { return attr(Attr.NAME, name); }
    public Tag withPlaceholder(String placeholder)                             { return attr(Attr.PLACEHOLDER, placeholder); }
    public Tag withTarget(String target)                                       { return attr(Attr.TARGET, target); }
    public Tag withType(String type)                                           { return attr(Attr.TYPE, type); }
    public Tag withRel(String rel)                                             { return attr(Attr.REL, rel); }
    public Tag withRole(String role)                                           { return attr(Attr.ROLE, role); }
    public Tag withSrc(String src)                                             { return attr(Attr.SRC, src); }
    public Tag withValue(String value)                                         { return attr(Attr.VALUE, value); }

    public Tag withCondAutoComplete(boolean condition)                         { return condAttr(condition, Attr.AUTOCOMPLETE, null); }
    public Tag withCondAutoFocus(boolean condition)                            { return condAttr(condition, Attr.AUTOFOCUS, null); }
    public Tag withCondHidden(boolean condition)                               { return condAttr(condition, Attr.HIDDEN, null); }
    public Tag withCondRequired(boolean condition)                             { return condAttr(condition, Attr.REQUIRED, null); }
    public Tag withCondAlt(boolean condition, String alt)                      { return condAttr(condition, Attr.ALT, alt); }
    public Tag withCondAction(boolean condition, String action)                { return condAttr(condition, Attr.ACTION, action); }
    public Tag withCharset(boolean condition, String charset)                  { return condAttr(condition, Attr.CHARSET, charset); }
    public Tag withCondClass(boolean condition, String className)              { return condAttr(condition, Attr.CLASS, className); }
    public Tag withCondContent(boolean condition, String content)              { return condAttr(condition, Attr.CONTENT, content); }
    public Tag withCondHref(boolean condition, String href)                    { return condAttr(condition, Attr.HREF, href); }
    public Tag withCondId(boolean condition, String id)                        { return condAttr(condition, Attr.ID, id); }
    public Tag withCondData(boolean condition, String dataAttr, String value)  { return condAttr(condition, Attr.DATA + "-" + dataAttr, value); }
    public Tag withCondMethod(boolean condition, String method)                { return condAttr(condition, Attr.METHOD, method); }
    public Tag withCondName(boolean condition, String name)                    { return condAttr(condition, Attr.NAME, name); }
    public Tag withCondPlaceholder(boolean condition, String placeholder)      { return condAttr(condition, Attr.PLACEHOLDER, placeholder); }
    public Tag withCondTarget(boolean condition, String target)                { return condAttr(condition, Attr.TARGET, target); }
    public Tag withCondType(boolean condition, String type)                    { return condAttr(condition, Attr.TYPE, type); }
    public Tag withCondRel(boolean condition, String rel)                      { return condAttr(condition, Attr.REL, rel); }
    public Tag withCondSrc(boolean condition, String src)                      { return condAttr(condition, Attr.SRC, src); }
    public Tag withCondValue(boolean condition, String value)                  { return condAttr(condition, Attr.VALUE, value); }

}
