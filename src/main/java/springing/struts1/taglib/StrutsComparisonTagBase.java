package springing.struts1.taglib;

import java.math.BigDecimal;
import org.springframework.lang.Nullable;

public abstract class StrutsComparisonTagBase
  extends StrutsConditionalTagBase
  implements JspVariableAware {

  public StrutsComparisonTagBase() {
    init();
  }

  private void init() {
    testValue = null;
    ref = JspVariableReference.create();
  }

  private @Nullable String testValue;
  private JspVariableReference ref;

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  /**
   * The constant value to which the variable, specified by other attribute(s)
   * of this tag, will be compared. [Required]
   */
  public void setValue(String value) {
    this.testValue = value;
  }

  public @Nullable BigDecimal asNumber(String value) {
    try {
      return new BigDecimal(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public @Nullable Integer compare() {
    if (testValue == null) throw new IllegalArgumentException(
      "The value attribute for this tag is required."
    );
    var actualValue = resolveValue(getPageContext());
    if (actualValue == null) {
      return testValue.isBlank() ? 0 : null;
    }
    var testNumber = asNumber(testValue);
    if (testNumber != null) {
      var actualNumber = asNumber(actualValue.toString());
      if (actualNumber == null) {
        return null;
      }
      return actualNumber.compareTo(testNumber);
    }
    return actualValue.toString().compareTo(testValue);
  }
}
