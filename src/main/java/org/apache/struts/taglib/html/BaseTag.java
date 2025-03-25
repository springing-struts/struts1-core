package org.apache.struts.taglib.html;

import java.util.Map;
import springing.struts1.taglib.StrutsHtmlElementTagBase;

/**
 *  Renders an HTML `base` element with an href attribute pointing to the
 *  absolute location of the enclosing JSP page. This tag is only valid when
 *  nested inside a head tag body. The presence of this tag allows the browser
 *  to resolve relative URL's to images, CSS stylesheets  and other resources
 *  in a manner independent of the URL used to call the ActionServlet.
 */
public class BaseTag extends StrutsHtmlElementTagBase {

  @Override
  protected String getTagName() {
    return "base";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    return Map.of();
  }
}
