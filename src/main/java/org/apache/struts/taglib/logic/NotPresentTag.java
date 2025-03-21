package org.apache.struts.taglib.logic;

/**
 * Evaluate the nested body content of this tag if the specified value is not
 * present for this request.
 */
public class NotPresentTag extends PresentTag {

  @Override
  protected boolean meetsCondition() {
    return !super.meetsCondition();
  }
}
