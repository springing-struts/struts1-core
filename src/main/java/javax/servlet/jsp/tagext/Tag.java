package javax.servlet.jsp.tagext;

import jakarta.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public interface Tag extends jakarta.servlet.jsp.tagext.Tag {
  @Override
  Tag getParent();

  @Override
  default void setParent(jakarta.servlet.jsp.tagext.Tag t) {
    setParent(toJavaxNamespace(t));
  }

  void setParent(Tag t);

  @Override
  default void setPageContext(jakarta.servlet.jsp.PageContext pageContext) {
    setPageContext(PageContext.toJavaxNamespace(pageContext));
  }

  void setPageContext(PageContext pageContext);

  static Tag toJavaxNamespace(jakarta.servlet.jsp.tagext.Tag jakarta) {
    if (jakarta instanceof Tag tag) {
      return tag;
    }
    return new Tag() {
      @Override
      public void setParent(Tag t) {
        jakarta.setParent(t);
      }

      @Override
      public void setPageContext(PageContext pageContext) {
        jakarta.setPageContext(pageContext);
      }

      @Override
      public Tag getParent() {
        return toJavaxNamespace(jakarta.getParent());
      }

      @Override
      public int doStartTag() throws JspException {
        return jakarta.doStartTag();
      }

      @Override
      public int doEndTag() throws JspException {
        return jakarta.doEndTag();
      }

      @Override
      public void release() {
        jakarta.release();
      }
    };
  }
}
