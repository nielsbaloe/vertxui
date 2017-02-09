package live.connector.vertxui.client.fluent;

/**
 * A list of standarised CSS elements
 * 
 * @author ng
 *
 */
public enum Css {

	alignContent, alignItems, alignSelf, animation, animationDelay, animationDirection, //
	animationDuration, animationFillMode, animationIterationCount, animationName, //
	animationTimingFunction, animationPlayState, background, backgroundAttachment, //
	backgroundColor, backgroundImage, backgroundPosition, backgroundRepeat, backgroundClip, //
	backgroundOrigin, backgroundSize, backfaceVisibility, border, borderBottom, borderBottomColor, //
	borderBottomLeftRadius, borderBottomRightRadius, borderBottomStyle, borderBottomWidth, borderCollapse, //
	borderColor, borderImage, borderImageOutset, borderImageRepeat, borderImageSlice, borderImageSource, //
	borderImageWidth, borderLeft, borderLeftColor, borderLeftStyle, borderLeftWidth, borderRadius, //
	borderRight, borderRightColor, borderRightStyle, borderRightWidth, borderSpacing, borderStyle, //
	borderTop, borderTopColor, borderTopLeftRadius, borderTopRightRadius, borderTopStyle, //
	borderTopWidth, borderWidth, bottom, boxDecorationBreak, boxShadow, boxSizing, captionSide, //
	clear, clip, color, columnCount, columnFill, columnGap, columnRule, columnRuleColor, //
	columnRuleStyle, columnRuleWidth, columns, columnSpan, columnWidth, content, counterIncrement, //
	counterReset, cursor, direction, display, emptyCells, filter, flex, flexBasis, flexDirection, //
	flexFlow, flexGrow, flexShrink, flexWrap, cssFloat, font, fontFamily, fontSize, fontStyle, //
	fontVariant, fontWeight, fontSizeAdjust, fontStretch, hangingPunctuation, height, hyphens, //
	icon, imageOrientation, justifyContent, left, letterSpacing, lineHeight, listStyle, //
	listStyleImage, listStylePosition, listStyleType, margin, marginBottom, marginLeft, //
	marginRight, marginTop, maxHeight, maxWidth, minHeight, minWidth, navDown, navIndex, //
	navLeft, navRight, navUp, opacity, order, orphans, outline, outlineColor, //
	outlineOffset, outlineStyle, outlineWidth, overflow, overflowX, overflowY, //
	padding, paddingBottom, paddingLeft, paddingRight, paddingTop, pageBreakAfter, //
	pageBreakBefore, pageBreakInside, perspective, perspectiveOrigin, position, //
	quotes, resize, right, tableLayout, tabSize, textAlign, textAlignLast, textDecoration, //
	textDecorationColor, textDecorationLine, textDecorationStyle, textIndent, textJustify, //
	textOverflow, textShadow, textTransform, top, transform, transformOrigin, transformStyle, //
	transition, transitionProperty, transitionDuration, transitionTimingFunction, //
	transitionDelay, unicodeBidi, verticalAlign, visibility, whiteSpace, width, wordBreak, //
	wordSpacing, wordWrap, widows, zIndex;

	public String nameValid() {
		StringBuffer result = new StringBuffer();
		for (char a : name().toCharArray()) {
			if (Character.isUpperCase(a)) {
				result.append("-");
			}
			result.append(a);
		}
		return result.toString().toLowerCase();
	}

	public static Css valueOfValid(String linestyle) {
		String x = null;
		for (String y : linestyle.split("-")) {
			if (x == null) {
				x = y;
			} else {
				x += y.substring(0, 1).toUpperCase() + y.substring(1);
			}
		}
		return valueOf(x);
	}

}
