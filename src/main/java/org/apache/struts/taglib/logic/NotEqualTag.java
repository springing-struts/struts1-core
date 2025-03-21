package org.apache.struts.taglib.logic;

import springing.struts1.taglib.StrutsComparisonTagBase;

/**
 * Evaluate the nested body content of this tag if the requested variable is
 * not equal to the specified value.
 */
public class NotEqualTag extends StrutsComparisonTagBase {

  @Override
  protected boolean meetsCondition() {
    var comparison = compare();
    return comparison != null && comparison != 0;
  }
}