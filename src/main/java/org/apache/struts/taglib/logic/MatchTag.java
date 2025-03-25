package org.apache.struts.taglib.logic;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;
import springing.struts1.taglib.StrutsConditionalTagBase;

/**
 * Evaluate the nested body content of this tag if the specified value is an
 * appropriate substring of the requested variable.
 * Matches the variable specified by one of the selector attributes (as a
 * String) against the specified constant value. If the value is a substring
 * (appropriately limited by the `location` attribute), the nested body content
 * of this tag is evaluated.
 */
public class MatchTag
  extends StrutsConditionalTagBase
  implements JspVariableAware {

  public MatchTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
    testValue = null;
    matchesFromStart = false;
    matchesFromEnd = false;
  }

  private JspVariableReference ref;
  private @Nullable String testValue;
  private boolean matchesFromStart;
  private boolean matchesFromEnd;

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  @Override
  protected boolean meetsCondition() {
    if (!hasText(testValue)) throw new IllegalArgumentException(
      "The value attribute is required for this tag."
    );
    var actualValue = resolveValue(requireNonNull(pageContext));
    if (actualValue == null) {
      return false;
    }
    var actualText = actualValue.toString();
    if (matchesFromStart) {
      return actualText.startsWith(testValue);
    }
    if (matchesFromEnd) {
      return actualText.endsWith(testValue);
    }
    return actualText.contains(testValue);
  }

  /**
   * If not specified, a match between the variable and the value may occur at
   * any position within the variable string. If specified, the match must
   * occur at the specified location (either `start` or `end`) of the variable
   * string.
   */
  public void setLocation(String location) {
    if ("start".equals(location)) {
      matchesFromStart = true;
      matchesFromEnd = false;
    } else if ("end".equals(location)) {
      matchesFromStart = false;
      matchesFromEnd = true;
    } else {
      matchesFromStart = false;
      matchesFromEnd = false;
    }
  }

  /**
   * [Required] The constant value which is checked for existence as a
   * substring of the specified variable.
   */
  public void setValue(String value) {
    this.testValue = value;
  }
}
