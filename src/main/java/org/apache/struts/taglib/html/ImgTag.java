package org.apache.struts.taglib.html;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.HtmlElementTagBase;
import springing.struts1.taglib.UrlBuilderBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders an HTML `<img>` element with the image at the specified URL. Like
 * the link tag, URL rewriting will be applied automatically to the value
 * specified in `src`, `page`, or `action` to maintain session state in the
 * absence of cookies. This will allow dynamic generation of an image where the
 * content displayed for this image will be taken from the attributes of this
 * tag.
 * The base URL for this image is calculated directly based on the value
 * specified in `src`, `page`, or `action` or `page`, or indirectly by looking
 * up a message resource string based on the `srcKey` or `pageKey` attributes.
 * You **must** specify exactly one of these attributes.
 * Normally, the `src`, `page`, or `action` that you specify will be left
 * unchanged (other than URL rewriting if necessary). However, there are two
 * ways you can append one or more dynamically defined query parameters to the
 * `src` URL
 * - specify a single parameter with the `paramId` attribute (at its associated
 *   attributes to select the value), or specify the `name` (and optional
 *   `property`) attributes to select a `java.util.Map` bean that contains one
 *   or more parameter ids and corresponding values.
 *   To specify a single parameter, use the `paramId` attribute to define the
 *   name of the request parameter to be submitted. To specify the
 *   corresponding value, use one of the following approaches:
 *   **Specify only the `paramName` attribute**
 *     The named JSP bean (optionally scoped by the value of the `paramScope`
 *     attribute) must identify a value that can be converted to a String.
 *   **Specify both the `paramName` and `paramProperty` attributes**
 *     The specified property getter will be called on the JSP bean identified
 *     by the `paramName` (and optional `paramScope`) attributes, in order to
 *     select a value that can be converted to a String.
 * If you prefer to specify a `java.util.Map` that contains all the request
 * parameters to be added to the hyperlink, use one of the following techniques:
 * **Specify only the `name` attribute**
 *   The named JSP bean (optionally scoped by the value of the `scope`
 *   attribute) must identify a `java.util.Map` containing the parameters.
 * **Specify both `name` and `property` attributes**
 *   The specified property getter method will be called on the bean identified
 *   by the `name` (and optional `scope`) attributes, in order to return the
 *   `java.util.Map` containing the parameters.
 * As the `Map` is processed, the keys are assumed to be the names of query
 * parameters to be appended to the `src` URL.  The value associated with each
 * key must be either a String or a String array representing the parameter
 * value(s), or an object whose toString() method will be called. If a String
 * array is specified, more than one value for the same query parameter name
 * will be created.
 * You can specify the alternate text for this image (which most browsers
 * display as pop-up text block when the user hovers the mouse over this image)
 * either directly, through the `alt` attribute, or indirectly from a message
 * resources bundle, using the `bundle` and `altKey` attributes.
 */
public class ImgTag extends HtmlElementTagBase implements UrlBuilderBean {
  @Override
  protected String getTagName() {
    return "img";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var srcUrl = (src != null) ? src : buildUrl(pageContext);
    attrs.put("src", srcUrl);
    return attrs;
  }
  private final Map<String, String> attrs = new HashMap<>();

  /**
   * Where the image is aligned to.  Can be one of the following attributes:
   * - **left**
   *   left justify, wrapping text on right.
   * - **right**
   *   right justify, wrapping test on left.
   * - **top**
   *   aligns the image with the top of the text on the same row.
   * - **middle**
   *   aligns the image's vertical center with the text baseline.
   * - **bottom**
   *   aligns the image with the bottom of the text's baseline.
   * - **texttop**
   *   aligns the image's top with that of the text font on the same line.
   * - **absmiddle**
   *   aligns the image's vertical center with the absolute center of the text.
   * - **absbottom**
   *   aligns the image with the absolute bottom of the text font on the same row.
   */
  public void setAlign(String align) {
    attrs.put("align", align);
  }
  private @Nullable String align;

  /**
   * And alternative text to be displayed in browsers that don't support
   * graphics. Also used often as type of context help over images.
   */
  public void setAlt(String alt) {
    attrs.put("alt", alt);
  }

  /**
   * The message resources key of the alternate text for this element.
   */
  public void setAltKey(String altKey) {
    var basename = ModuleUtils.getCurrent().getMessageResourcesBasename();
    var messageResources = MessageResources.getMessageResources(basename);
    var message = messageResources.getMessage(altKey);
    attrs.put("alt", message);
  }

  /**
   * The width of the border surrounding the image.
   */
  public void setBorder(String border) {
    attrs.put("border", border);
  }

  /**
   * The height of the image being displayed. This parameter is very nice to
   * specify (along with `width`) to help the browser render the page faster.
   */
  public void setHeight(String height) {
    attrs.put("height", height);
  }

  /**
   * The amount of horizontal spacing between the icon and the text. The text
   * may be in the same paragraph, or be wrapped around the image.
   */
  public void setHspace(String hspace) {
    attrs.put("hspace", hspace);
  }

  /**
   * The scriptable name to be defined within this page, so that you can
   * reference it with intra-page scripts. In other words, the value specified
   * here will render a "name" element in the generated image tag.
   */
  public void setImageName(String imageName) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the server-side map that this image belongs to.
   */
  public void setIsmap(String ismap) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the request or session Locale attribute used to look up
   * internationalized messages.
   */
  public void setLocale(String locale) {
    throw new UnsupportedOperationException();
  }

  /**
   * The message key, in the message resources bundle named by the `bundle`
   * attribute, of the String to be used as the module-relative path for this
   * image.
   */
  public void setPageKey(String pageKey) {
    throw new UnsupportedOperationException();
  }

  @Override
  public UrlData getUrlData() {
    return urlDate;
  }
  private final UrlData urlDate = new UrlData();

  /**
   * The URL to which this image will be transferred from. This image may be
   * dynamically modified by the inclusion of query parameters, as described in
   * the tag description. This value will be used unmodified (other than
   * potential URL rewriting) as the value of the `src` attribute in the
   * rendered tag. You **must** specify either the `page` attribute or the
   * `src` attribute.
   */
  public void setSrc(String src) {
    this.src = src;
  }
  private @Nullable String src;

  /**
   * The message key, in the message resources bundle named by the `bundle`
   * attribute, of the String to be used as the URL of this image.
   */
  public void setSrcKey(String srcKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * If set to `true`, LocalCharacterEncoding will be used, that is, the
   * `characterEncoding` set to the `HttpServletResponse`, as preferred
   * character encoding rather than UTF-8, when `URLEncoding` is done on
   * parameters of the URL.
   */
  public void setUseLocalEncoding(boolean useLocalEncoding) {
    throw new UnsupportedOperationException();
  }
}
