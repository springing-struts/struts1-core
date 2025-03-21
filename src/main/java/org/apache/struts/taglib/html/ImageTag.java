package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;
import java.util.Map;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.StringUtils.normalizeForwardPath;

/**
 * Render an input tag of type "image".
 * Renders an HTML `input` tag of type "image". The base URL for this image is
 * calculated directly based on the value specified in the `src` or `page`
 * attributes, or indirectly by looking up a message resource string based on
 * the `srcKey` or `pageKey` attributes. You **must** specify exactly one of
 * these attributes.
 * If you would like to obtain the coordinates of the mouse click that
 * submitted this request, see the information below on the `property`
 * attribute.
 * This tag is only valid when nested inside a form tag body.
 */
public class ImageTag extends StrutsInputElementTagBase {

  public ImageTag() {
    init();
  }

  @Override
  protected String getType() {
    return "image";
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    align = null;
    border = null;
    locale = null;
    module = null;
    page = null;
    pageKey = null;
    property = null;
    src = null;
    srcKey = null;
  }

  private @Nullable String align;
  private @Nullable String border;
  private @Nullable String locale;
  private @Nullable String module;
  private @Nullable String page;
  private @Nullable String pageKey;
  private @Nullable String property;
  private @Nullable String src;
  private @Nullable String srcKey;

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var imageUrl = getImageUrl();
    if (!hasText(imageUrl)) throw new IllegalArgumentException(String.format(
      "Failed to determine the URL for the image from the following attributes:" +
      " src=[%s], srcKey=[%s], page=[%s], pageKey=[%s]"
      , src, srcKey, page, pageKey
    ));
    var attrs = super.getAdditionalAttributes();
    attrs.put("align", align);
    attrs.put("border", border);
    attrs.put("src", imageUrl);
    return attrs;
  }

  private @Nullable String getImageUrl() {
    if (src != null && srcKey == null && page == null && pageKey == null) {
      return src;
    }
    var messageResources = ModuleUtils.getCurrent().getMessageResources();
    if (src == null && srcKey != null && page == null && pageKey == null) {
      return messageResources.getMessageInLocale(locale, srcKey);
    }
    var modulePrefix = ModuleUtils.getCurrent().getPrefix();
    if (src == null && srcKey == null && page != null && pageKey == null) {
      return normalizeForwardPath(modulePrefix + "/" + page);
    }
    if (src == null && srcKey == null && page == null && pageKey != null) {
      var page = messageResources.getMessageInLocale(locale, pageKey);
      return normalizeForwardPath(modulePrefix + "/" + page);
    }
    throw new IllegalArgumentException(String.format(
      "Only one of the following attributes should be specified for the image tag:" +
      " src=[%s], srcKey=[%s], page=[%s], pageKey=[%s]."
      , src, srcKey, page, pageKey
    ));
  }

  /**
   * The alignment option for this image.
   * **NOTE**
   *  This attribute is not supported in HTML5.
   */
  public void setAlign(String align) {
    this.align = align;
  }

  /**
   * The width (in pixels) of the border around this image.
   * **NOTE**
   *  This attribute is not supported in HTML5.
   */
  public void setBorder(String border) {
    this.border = border;
  }

  /**
   * The session attribute key for the Locale used to select internationalized
   * messages. If not specified, defaults to the Struts standard value.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * Prefix name of a `Module` that the `page` or `pageKey` attributes relate to.
   */
  public void setModule(String module) {
    this.module = module;
  }

  /**
   * The module-relative path of the image for this input tag.
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * The key of the message resources string specifying the module-relative
   * path of the image for this input tag.
   */
  public void setPageKey(String pageKey) {
    this.pageKey = pageKey;
  }

  /**
   * The source URL of the image for this input tag.
   */
  public void setSrc(String src) {
    this.src = src;
  }

  /**
   * The key of the message resources string specifying the source URL of the
   * image for this input tag.
   */
  public void setSrcKey(String srcKey) {
    this.srcKey = srcKey;
  }
}
