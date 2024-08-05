package org.apache.struts.taglib.bean;

import jakarta.servlet.jsp.JspException;
import springing.struts1.taglib.DelegatingTagBase;

/**
 * Custom tag that retrieves an internationalized messages string
 * (with optional parametric replacement) from the `ActionResources`
 * object stored as a context attribute by our associated `ActionServlet`
 * implementation.
 */
public class MessageTag extends DelegatingTagBase<org.springframework.web.servlet.tags.MessageTag> {

  public MessageTag() {
    super(new org.springframework.web.servlet.tags.MessageTag());
  }

  private final String[] args = { "", "", "", "", "", "" };

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

  /**
   * Name of the bean that contains the message key.
   */
  public void setName(String name) {
    throw new UnsupportedOperationException();
  }

  /**
   * Name of the property to be accessed on the specified bean.
   */
  public void setProperty(String property) {
    throw new UnsupportedOperationException();
  }

  /**
   * The scope to be searched to retrieve the specified bean.
   */
  public void setScope(String scope) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int doStartTag() throws JspException {
    getBaseTag().setArguments(args);
    return super.doStartTag();
  }
}
