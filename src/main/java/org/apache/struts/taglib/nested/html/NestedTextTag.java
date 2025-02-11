package org.apache.struts.taglib.nested.html;

import org.apache.struts.taglib.html.TextTag;

/**
 * Nested Extension: Render An Input Field of Type text.
 * This tag is an extension of the `html:text` tag. Please consult its
 * documentation for information on tag attributes and usage details.
 */
public class NestedTextTag extends TextTag {
  @Override
  protected void init() {
    super.init();
    setAwareNestedTag(true);
  }
}
