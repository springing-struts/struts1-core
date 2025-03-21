package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import static java.util.Objects.requireNonNull;

public abstract class StrutsConditionalTagBase extends TagSupport {
  @Override
  public final int doStartTag() throws JspException {
    return meetsCondition() ? EVAL_BODY_INCLUDE : SKIP_BODY;
  }

  protected abstract boolean meetsCondition();

  protected PageContext getPageContext() {
    return requireNonNull(pageContext, "PageContext is not initialized.");
  }

}
