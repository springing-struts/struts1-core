package org.apache.struts.taglib.html;

import java.util.Map;

/**
 * Render a Cancel Button
 * Renders an HTML `input` element of type submit. This tag is only valid
 * when nested inside a form tag body. Pressing of this submit button causes
 * the action servlet to bypass calling the associated form bean validate()
 * method. The action is called normally.
 */
public class CancelTag extends SubmitTag {

  public CancelTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  public void init() {}

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var props = super.getAdditionalAttributes();
    props.put("name", Constants.CANCEL_PROPERTY);
    return props;
  }

  @Override
  public String getDefaultValue() {
    return DEFAULT_CANCEL_BUTTON_LABEL;
  }

  private static final String DEFAULT_CANCEL_BUTTON_LABEL = "Cancel";
}
