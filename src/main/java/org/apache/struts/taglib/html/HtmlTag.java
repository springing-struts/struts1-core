package org.apache.struts.taglib.html;

import springing.struts1.taglib.HtmlElementTagBase;

import java.util.Map;

/*
 * Renders an HTML <html> element with appropriate language attributes if
 * there is a current Locale available in the user's session.
 */
public class HtmlTag extends HtmlElementTagBase {

  @Override
  protected String getTagName() {
    return "html";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    return Map.of();
  }
}
