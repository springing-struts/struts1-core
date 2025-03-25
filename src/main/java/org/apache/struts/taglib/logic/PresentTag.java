package org.apache.struts.taglib.logic;

import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;
import springing.struts1.taglib.StrutsConditionalTagBase;

/**
 * Generate the nested body content of this tag if the specified value is
 * present in this request.
 * Depending on which attribute is specified, this tag checks the current
 * request, and evaluates the nested body content of this tag only if the
 * specified value **is** present. Only one of the attributes may be used in
 * one occurrence of this tag, unless you use the `property` attribute, in
 * which case the `name` attribute is also required.
 */
public class PresentTag
  extends StrutsConditionalTagBase
  implements JspVariableAware {

  public PresentTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
  }

  private JspVariableReference ref;

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  @Override
  protected boolean meetsCondition() {
    var value = resolveValue(getPageContext());
    return value != null;
  }

  /**
   * Checks whether the currently authenticated user (if any) has been
   * associated with any of the specified security roles. Use a comma-delimited
   * list to check for multiple roles.
   * **Example:**
   * <pre>{@code
   *   <logic:present role="role1,role2,role3">
   *   code.....
   *   </logic:present>
   * }</pre>
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }

  /**
   * Checks whether the currently authenticated user principal has the
   * specified name.
   */
  public void setUser(String user) {
    throw new UnsupportedOperationException();
  }
}
