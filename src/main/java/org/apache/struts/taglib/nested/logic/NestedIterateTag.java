package org.apache.struts.taglib.nested.logic;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.taglib.logic.IterateTag;
import org.springframework.web.servlet.tags.NestedPathTag;

/**
 * Nested Extension - Repeat the nested body content of this tag over a
 * specified collection.
 * This tag is an extension of the `logic:iterate` tag. Please consult its
 * documentation for information on tag attributes and usage details.
 */
public class NestedIterateTag extends IterateTag {

  public NestedIterateTag() {
    super(true);
  }
}
