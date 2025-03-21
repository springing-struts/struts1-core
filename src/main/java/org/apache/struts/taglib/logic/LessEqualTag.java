package org.apache.struts.taglib.logic;

import springing.struts1.taglib.StrutsComparisonTagBase;

/**
 * Evaluate the nested body content of this tag if the requested variable is
 * less than or equal to the specified value.
 * Compares the variable specified by one of the selector attributes against
 * the specified constant value. The nested body content of this tag is
 * evaluated if the variable is **less than or equal** to the value.
 */
public class LessEqualTag extends StrutsComparisonTagBase {

  @Override
  protected boolean meetsCondition() {
    var comparison = compare();
    return comparison != null && comparison <= 0;
  }
}
