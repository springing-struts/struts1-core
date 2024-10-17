package org.apache.struts.taglib.nested.html;

import org.apache.struts.taglib.html.MessagesTag;

/**
 * Nested Extension: Conditionally display a set of accumulated messages.
 * This tag is an extension of the `html:messages` tag. Please consult its
 * documentation for information on tag attributes and usage details.
 */
public class NestedMessagesTag extends MessagesTag {

  @Override
  protected void init() {
    super.init();
    setAwareNestedTag(true);
  }
}
