package org.apache.struts.taglib.logic;

import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * Forward control to the page specified by the specified ActionForward.
 * Performs a `PageContext.forward()` or `HttpServletResponse.sendRedirect()`
 * call for the global `ActionForward` entry for the specified name. URL
 * rewriting will occur automatically if a redirect is performed.
 */
public class ForwardTag extends TagSupport {

  public ForwardTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    name = null;
  }

  private @Nullable String name;

  @Override
  public int doEndTag() throws JspException {
    if (!hasText(name)) throw new IllegalArgumentException(
      "The logic:forward tag requires the `name` property."
    );
    var forwardConfig = ModuleUtils.getCurrent().findForwardConfig(name);
    if (forwardConfig == null) throw new IllegalArgumentException(String.format(
      "Unknown forward name: [%s].", name
    ));
    var forwardPath = forwardConfig.getUrl();
    try {
      ServletActionContext.current().forwardRequest(forwardPath);
    } catch (ServletException | IOException e) {
      throw new RuntimeException(e);
    }
    return SKIP_PAGE;
  }

  /**
   * [Required] The logical name of the global `ActionForward` entry	that
   * identifies the destination, and forwarding approach, to be used.
   * **Note**:
   * Forwarding to Tiles definitions is not supported from this tag. You should
   * forward to them from an Action subclass.
   */
  public void setName(String name) {
    this.name = name;
  }
}
