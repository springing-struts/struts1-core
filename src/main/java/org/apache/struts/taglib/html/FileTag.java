package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

import java.util.Map;

/**
 * Render A File Select Input Field.
 * Renders an HTML `input` element of type file, defaulting to the specified
 * value or the specified property of the bean associated with our current
 * form. This tag is only valid when nested inside a form tag body.
 * As with the corresponding HTML `input` element, the enclosing form element
 * must specify "POST" for the `method` attribute, and "multipart/form-data"
 * for the `enctype` attribute. For example:
 * **WARNING:**
 * In order to correctly recognize uploaded files, the ActionForm bean
 * associated with this form must include a statement setting the corresponding
 * `org.apache.struts.upload.FormFile` property to null in the reset() method.
 * <pre>{@code
 * <html:form
 *   method="POST"
 *   enctype="multipart/form-data">
 *   <html:file property="theFile" />
 * </html:form>
 * }</pre>
 */
public class FileTag extends StrutsInputElementTagBase {

  public FileTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    accept = null;
    maxlength = null;
    size = null;
  }

  private @Nullable String accept;
  private @Nullable Long maxlength;
  private @Nullable Long size;

  @Override
  protected String getType() {
    return "file";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs =  super.getAdditionalAttributes();
    attrs.put("accept", accept);
    attrs.put("maxlength", (maxlength == null) ? null : maxlength.toString());
    attrs.put("size", (size == null) ? null : size.toString());
    return attrs;
  }

  /**
   * Comma-delimited set of content types that the server you submit to knows
   * how to process. This list can be used by the client browser to limit the
   * set of file options that is made available for selection. If not
   * specified, no content type list will be sent.
   */
  public void setAccept(String accept) {
    this.accept = accept;
  }

  /**
   * Maximum number of input characters to accept. This is ignored by most
   * browsers.
   */
  public void setMaxlength(String maxlength) {
    this.maxlength = Long.parseLong(maxlength);
  }

  /**
   * Size of the file selection box to be displayed.
   */
  public void setSize(String size) {
    this.size = Long.parseLong(size);
  }
}