package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

/**
 * Render A Password Input Field.
 * Renders an HTML `input` element of type password, populated from the
 * specified value or the specified property of the bean associated with our
 * current form. This tag is only valid when nested inside a form tag body.
 */
public class PasswordTag extends StrutsInputElementTagBase {

  public PasswordTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    size = null;
    maxlength = null;
    onselect = null;
    redisplay = true;
  }

  @Override
  protected @Nullable String getType() {
    return "password";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("size", size);
    attrs.put("maxlength", maxlength);
    attrs.put("onselect", onselect);
    return attrs;
  }

  private @Nullable String size;
  private @Nullable String maxlength;
  private @Nullable String onselect;
  private boolean redisplay;

  /**
   * Number of character positions to allocate. [Browser default]
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * Maximum number of input characters to accept. [No limit]
   */
  public void setMaxlength(String maxlength) {
    this.maxlength = maxlength;
  }

  /**
   * JavaScript event handler executed when a user selects some text in a text
   * field.
   */
  public void setOnselect(String onselect) {
    this.onselect = onselect;
  }

  /**
   * Boolean flag indicating whether the existing values will be redisplayed
   * if they exist. Even though the redisplayed value will be shown as
   * asterisks on the visible HTML page, the clear text of the actual password
   * value will be visible though the "Show Page Source" menu option of the
   * client browser. You may wish to set this value to `false` on login pages.
   * Defaults to `true` for consistency with all other form tags that redisplay
   * their contents.
   */
  public void setRedisplay(boolean redisplay) {
    this.redisplay = redisplay;
  }
}
