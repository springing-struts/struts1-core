package javax.servlet.jsp.tagext;

import org.springframework.lang.Nullable;

import javax.servlet.jsp.PageContext;

public class BodyTagSupport extends jakarta.servlet.jsp.tagext.BodyTagSupport implements Tag {

  @Override
  public void setParent(Tag t) {
    super.setParent(t);
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

  protected @Nullable PageContext pageContext;
}
