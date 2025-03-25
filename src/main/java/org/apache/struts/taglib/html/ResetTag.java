package org.apache.struts.taglib.html;

import java.util.Map;

/**
 * Renders an HTML `input` element of type reset.
 */
public class ResetTag extends SubmitTag {

  public ResetTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {}

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var attrs = super.getAdditionalAttributes();
    attrs.put("type", "reset");
    return attrs;
  }

  @Override
  public String getDefaultValue() {
    return RESET_BUTTON_DEFAULT_LABEL;
  }

  private static final String RESET_BUTTON_DEFAULT_LABEL = "Reset";
}
