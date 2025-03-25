package org.apache.struts.tiles.taglib;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.taglibs.standard.tag.common.core.OutSupport;
import org.springframework.lang.Nullable;

/**
 *  Render the value of the specified `tile/component/template` attribute to
 *  the current JspWriter.
 *  Retrieve the value of the specified tile/component/template attribute
 *  property, and render it to the current JspWriter as a String. The usual
 *  `toString()` conversions is applied on found value.
 *  Throw a JSPException if named value is not found.
 */
public class GetAttributeTag extends OutSupport {

  public GetAttributeTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  @Override
  public int doStartTag() throws JspException {
    var tilesDefinition = ServletActionContext.current().getTilesDefinition();
    var attr = tilesDefinition.getAttributes().get(getName());
    if (attr == null && !ignore) throw new IllegalArgumentException(
      format(
        "The value of attribute [%s] of the current tiles definition [%s] was null.",
        name,
        tilesDefinition.getName()
      )
    );
    value = attr != null ? attr.getValue() : "";
    return super.doStartTag();
  }

  private void init() {
    name = null;
    ignore = false;
  }

  private @Nullable String name;
  private boolean ignore = false;

  /**
   * [Required] Attribute name.
   */
  public void setName(String name) {
    this.name = name;
  }

  private String getName() {
    return requireNonNull(
      name,
      "The name attribute of <tiles:getAsString> tag is required."
    );
  }

  /**
   * If this attribute is set to true, and the attribute specified by the name
   * does not exist, simply return without writing anything. The default value
   * is false, which will cause a runtime exception to be thrown.
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * If the user is in the specified role, the tag is taken into account;
   * otherwise, the tag is ignored (skipped).
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }
}
