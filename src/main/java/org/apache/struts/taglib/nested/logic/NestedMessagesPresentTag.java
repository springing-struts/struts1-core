package org.apache.struts.taglib.nested.logic;

import org.apache.struts.taglib.logic.MessagesPresentTag;

/**
 * Nested Extension: Generate the nested body content of this tag if the
 * specified message is present in this request.
 * This tag is an extension of the `logic:messagesPresent` tag. Please consult
 * its documentation for information on tag attributes and usage details.
 */
public class NestedMessagesPresentTag extends MessagesPresentTag {
  @Override
  protected void init() {
    super.init();
    setAwareNestedTag(true);
  }
}
