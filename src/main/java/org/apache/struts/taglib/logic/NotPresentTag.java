package org.apache.struts.taglib.logic;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import springing.struts1.taglib.JspVariableAware;

/**
 * Evaluate the nested body content of this tag if the specified value is not
 * present for this request.
 */
public class NotPresentTag extends TagSupport implements JspVariableAware {

  @Override
  public JspVariableReference getReference() {
    return ref;
  }
  private final JspVariableReference ref = JspVariableReference.create();

  /**
   * The name of the security role to be checked for.
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }

  /**
   * The user principal name to be checked for.
   */
  public void setUser(String user) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int doStartTag() throws JspException {
    var value = getValue(pageContext);
    return value == null ? EVAL_BODY_INCLUDE : SKIP_BODY;
  }
}
