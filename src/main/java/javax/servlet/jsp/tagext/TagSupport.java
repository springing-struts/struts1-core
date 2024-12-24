package javax.servlet.jsp.tagext;

import org.springframework.lang.Nullable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class TagSupport extends jakarta.servlet.jsp.tagext.TagSupport implements Tag {

  public int doEndTag() throws JspException {
    try {
      return super.doEndTag();
    } catch (jakarta.servlet.jsp.JspException e) {
      throw JspException.toJavaxNamespace(e);
    }
  }

  @Override
  public void setPageContext(PageContext pageContext) {
    super.setPageContext(pageContext);
    this.pageContext = pageContext;
  }

  @Override
  public void setPageContext(jakarta.servlet.jsp.PageContext pageContext) {
    super.setPageContext(pageContext);
    setPageContext(PageContext.toJavaxNamespace(pageContext));
  }

  @Override
  public Tag getParent() {
    return Tag.toJavaxNamespace(super.getParent());
  }

  @Override
  public void setParent(jakarta.servlet.jsp.tagext.Tag t) {
    super.setParent(t);
  }

  public void setParent(Tag t) {
    super.setParent(t);
  }

  protected @Nullable PageContext pageContext;
}
