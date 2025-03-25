package org.apache.struts.taglib.logic;

public class NotEmptyTag extends EmptyTag {

  @Override
  protected boolean meetsCondition() {
    return !super.meetsCondition();
  }
}
