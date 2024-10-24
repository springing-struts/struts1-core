package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.apache.struts.action.ActionMessage;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.MessagesAware;

import java.io.IOException;

import static org.apache.struts.util.MessageResources.getMessageResources;

/**
 * Conditionally display a set of accumulated error messages.
 * Displays a set of error messages prepared by a business logic component and
 * stored as an `ActionMessages` object, an `ActionErrors` object, a String, or
 * a String array in any scope. If such a bean is not found, nothing will be
 * rendered.
 * In order to use this tag successfully, you must have defined an application
 * scope `MessageResources` bean under the default attribute name, with
 * optional definitions of message keys specified in the following attributes:
 * - header:
 *   Text that will be rendered before the error messages list. Typically, this
 *   message text will end with `<ul>` to start the error messages list (default
 *   "errors.header").
 * - footer:
 *   Text that will be rendered after the error messages list. Typically, this
 *   message text will begin with `</ul>` to end the error messages list
 *   (default "errors.footer").
 * - prefix:
 *   Text that will be rendered before each individual error in the list
 *   (default "errors.prefix").
 * - suffix:
 *   Text that will be rendered after each individual error in the list
 *   (default "errors.suffix").
 * See the `messages` tag for an alternative to this tag that does not rely on
 * HTML in your `MessageResources`.
 */
public class ErrorsTag extends TagSupport implements MessagesAware
{

  public ErrorsTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
    ref = MessagesReference.create();
    headerKey = "errors.header";
    footerKey = "errors.footer";
    prefixKey = "errors.prefix";
    suffixKey = "errors.suffix";
    bundle = null;
    locale = null;
  }

  private MessagesReference ref;
  private String headerKey;
  private String footerKey;
  private String prefixKey;
  private String suffixKey;
  private @Nullable String bundle;
  private @Nullable String locale;

  @Override
  public MessagesReference getReference() {
    return ref;
  }

  @Override
  public int doStartTag() throws JspException {
    var messages = getMessages(pageContext);
    if (messages == null || messages.isEmpty()) {
      return SKIP_BODY;
    }
    var messageResources = getMessageResources(bundle);
    String header = messageResources.getMessageInLocale(locale, headerKey);
    String prefix = messageResources.getMessageInLocale(locale, prefixKey);
    String suffix = messageResources.getMessageInLocale(locale, suffixKey);
    String footer = messageResources.getMessageInLocale(locale, footerKey);

    if (header != null) write(header);
    for (var message : messages) {
      if (message == null) continue;
      if (prefix != null) write(prefix);
      if (message instanceof ActionMessage actionMessage) {
        write(actionMessage.getText(bundle));
      }
      else {
        write(message.toString());
      }
      if (suffix != null) write(suffix);
    }
    if (footer != null) write(footer);
    return SKIP_BODY;
  }

  public void write(String content) {
    try {
      pageContext.getOut().write(content);
    } catch (IOException e) {
      throw new RuntimeException(
        "An error occurred while writing <errors> tag content to the body.",
        e
      );
    }
  }

  /**
   * This value is an optional message resource key that will be printed before
   * the iteration of error messages begins. Defaults to "errors.header" if not
   * specified.
   */
  public void setHeader(String header) {
    headerKey = header;
  }

  /**
   * This value is an optional message resource key that will be printed after
   * the iteration of error messages has finished. Defaults to "errors.footer"
   * if not specified.
   */
  public void setFooter(String footerKey) {
    this.footerKey = footerKey;
  }

  /**
   * This value is an optional message resource key that will be printed before
   * an error message. Defaults to "errors.prefix" if not specified.
   */
  public void setPrefix(String prefixKey) {
    this.prefixKey = prefixKey;
  }

  /**
   * This value is an optional message resource key that will be printed after
   * an error message. Defaults to "errors.suffix" if not specified.
   */
  public void setSuffix(String suffixKey) {
    this.suffixKey = suffixKey;
  }

  /**
   * The servlet context attribute key for the MessageResources instance to
   * use. If not specified, defaults to the application resources configured
   * for our action servlet.
   */
  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  /**
   * The session attribute key for the Locale used to select messages to be
   * displayed. If not specified, defaults to the Struts standard value.
   */
  public  void setLocale(String locale) {
     this.locale = locale;
  }
}
