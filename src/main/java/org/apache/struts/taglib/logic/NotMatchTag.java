package org.apache.struts.taglib.logic;

/**
 * Evaluate the nested body content of this tag if the specified value is not
 * an appropriate substring of the requested variable.
 * Matches the variable specified by one of the selector attributes (as a
 * String) against the specified constant value. If the value is not a
 * substring (appropriately limited by the `location` attribute), the nested
 * body content of this tag is evaluated.
 */
public class NotMatchTag extends MatchTag {
  @Override
  protected boolean meetsCondition() {
    return !super.meetsCondition();
  }
}
