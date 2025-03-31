package org.apache.struts.taglib.bean;

import jakarta.servlet.jsp.JspException;
import springing.struts1.taglib.DelegatingTagBase;
import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;

/**
 * Custom tag that retrieves an internationalized messages string
 * (with optional parametric replacement) from the `ActionResources`
 * object stored as a context attribute by our associated `ActionServlet`
 * implementation.
 */
public class MessageTag
  extends DelegatingTagBase<org.springframework.web.servlet.tags.MessageTag>
  implements JspVariableAware {

  public MessageTag() {
    super(new org.springframework.web.servlet.tags.MessageTag());
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    args = new String[] { "", "", "", "", "", "" };
    ref = JspVariableReference.create();
  }

  private String[] args;
  private JspVariableReference ref;

  /**
   * The first optional argument.
   */
  public void setArg0(String arg) {
    args[0] = arg;
  }

  /**
   * The second optional argument.
   */
  public void setArg1(String arg) {
    args[1] = arg;
  }

  /**
   * The third optional argument.
   */
  public void setArg2(String arg) {
    args[2] = arg;
  }

  /**
   * The fourth optional argument.
   */
  public void setArg3(String arg) {
    args[3] = arg;
  }

  /**
   * The fifth optional argument.
   */
  public void setArg4(String arg) {
    args[4] = arg;
  }

  /**
   * The servlet context attribute key for our resources.
   */
  public void setBundle(String bundle) {
    args[5] = bundle;
  }

  /**
   * The message key of the message to be retrieved.
   */
  public void setKey(String key) {
    getBaseTag().setCode(key);
  }

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  @Override
  public int doStartTag() throws JspException {
    getBaseTag().setArguments(args);
    return super.doStartTag();
  }
}
