package org.apache.struts.taglib.logic;

import springing.struts1.taglib.StrutsComparisonTagBase;

/**
 * Evaluate the nested body content of this tag if the requested variable is
 * equal to the specified value.
 * Compares the variable specified by one of the selector attributes against
 * the specified constant value. The nested body content of this tag is
 * evaluated if the variable and value are **equal**.
 */
public class EqualTag extends StrutsComparisonTagBase {

  protected boolean meetsCondition() {
    var comparison = compare();
    return comparison != null && comparison == 0;
  }
}