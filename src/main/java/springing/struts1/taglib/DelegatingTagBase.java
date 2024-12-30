package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.IterationTag;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TryCatchFinally;
import org.springframework.lang.Nullable;

public abstract class DelegatingTagBase<TAG extends IterationTag>
  implements IterationTag, TryCatchFinally {

  public DelegatingTagBase(TAG tag) {
    this.tag = tag;
  }

  @Override
  public void release() {
    tag.release();
    pageContext = null;
  }

  private final TAG tag;
  private @Nullable PageContext pageContext;

  protected TAG getBaseTag() {
    return tag;
  }

  @Override
  public int doAfterBody() throws JspException {
    return tag.doAfterBody();
  }

  @Override
  public void setPageContext(PageContext pageContext) {
    this.pageContext = pageContext;
    tag.setPageContext(pageContext);
  }

  public PageContext getPageContext() {
    if (pageContext == null) throw new IllegalStateException(
      "The pageContext have not been set to this tag."
    );
    return pageContext;
  }

  @Override
  public void setParent(Tag parent) {
    tag.setParent(parent);
  }

  @Override
  public Tag getParent() {
    return tag.getParent();
  }

  @Override
  public int doStartTag() throws JspException {
    return tag.doStartTag();
  }

  @Override
  public int doEndTag() throws JspException {
    return tag.doEndTag();
  }

  @Override
  public void doCatch(Throwable throwable) throws Throwable {
    if (tag instanceof TryCatchFinally t) {
      t.doCatch(throwable);
    }
  }

  @Override
  public void doFinally() {
    if (tag instanceof TryCatchFinally t) {
      t.doFinally();
    }
  }
}
