package javax.servlet.jsp.tagext;

import javax.servlet.jsp.PageContext;

public interface Tag extends jakarta.servlet.jsp.tagext.Tag {
  @Override
  default void setParent(jakarta.servlet.jsp.tagext.Tag t) {
    setParent((Tag)t);
  }

  void setParent(Tag t);

  @Override
  default void setPageContext(jakarta.servlet.jsp.PageContext pageContext) {
    setPageContext(PageContext.toJavaxNamespace(pageContext));
  }
  void setPageContext(PageContext pageContext);
}
