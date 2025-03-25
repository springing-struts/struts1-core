package org.apache.struts.taglib.logic;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import springing.struts1.taglib.MessagesAware;

/**
 * Generate the nested body content of this tag if the specified message is
 * present in any scope.
 * Evaluates the nested body content of this tag if an `ActionMessages` object,
 * `ActionErrors` object, a String, or a String array is present in any scope.
 * If such a bean is not found, nothing will be rendered.
 */
public class MessagesPresentTag extends TagSupport implements MessagesAware {

  public MessagesPresentTag() {
    this(false);
  }

  public MessagesPresentTag(boolean negate) {
    this.negate = negate;
    init();
  }

  private final boolean negate;

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
    ref = MessagesReference.create();
  }

  private MessagesReference ref;

  @Override
  public MessagesReference getReference() {
    return ref;
  }

  @Override
  public int doStartTag() throws JspException {
    var messages = getMessages(pageContext);
    var hasMessages = messages == null || messages.isEmpty();
    var evaluatesContent = negate != hasMessages;
    return evaluatesContent ? SKIP_BODY : EVAL_BODY_INCLUDE;
  }
}
