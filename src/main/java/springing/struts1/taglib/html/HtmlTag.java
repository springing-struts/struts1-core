package springing.struts1.taglib.html;

import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.form.TagWriter;

/*
 * Renders an HTML <html> element with appropriate language attributes if
 * there is a current Locale available in the user's session.
 */
public class HtmlTag extends HtmlEscapingAwareTag {

  @Override
  protected int doStartTagInternal() throws Exception {
    var writer = new TagWriter(pageContext);
    writer.startTag("html");
    writer.endTag();
    return EVAL_BODY_INCLUDE;
  }
}
