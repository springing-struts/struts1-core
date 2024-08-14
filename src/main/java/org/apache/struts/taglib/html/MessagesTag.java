package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.taglibs.standard.tag.common.core.ForEachSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.MessagesAware;

import java.util.List;

import static org.apache.struts.util.MessageResources.getMessageResources;

/**
 * Conditionally display a set of accumulated messages.
 * Displays a set of messages prepared by a business logic component and stored
 * as an `ActionMessages` object, `ActionErrors` object, a String, or a String
 * array in any scope. If such a bean is not found, nothing will be rendered.
 * The messages are placed into the page scope in the body of this tag where
 * they can be displayed by standard JSP methods.
 * (For example: `bean:write`, `c:out`)
 * In order to use this tag successfully, you must have defined an application
 * scope `MessageResources` bean under the default attribute name.
 */
public class MessagesTag extends ForEachSupport implements MessagesAware {

  public MessagesTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
    ref = MessagesReference.create();
    id = null;
    bundle = null;
    filterArgs = false;
    locale = null;
    header = null;
    footer = null;
  }

  private MessagesReference ref;
  private @Nullable String id;
  private @Nullable String bundle;
  private boolean filterArgs;
  private @Nullable String locale;
  private @Nullable String header;
  private @Nullable String footer;

  @Override
  public int doStartTag() throws JspException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException(
      "<html:messages> tag requires the id attribute."
    );
    return super.doStartTag();
  }

  @Override
  protected void prepare() throws JspTagException {
    var messages = getMessages(pageContext);
    var iterator = (messages == null ? List.of() : messages).iterator();
    items = new ForEachIterator() {
      @Override
      public boolean hasNext() throws JspTagException {
        return iterator.hasNext();
      }
      @Override
      public Object next() throws JspTagException {
        var message = iterator.next();
        var text = getMessageText(message);
        pageContext.setAttribute(id, text);
        return text;
      }
    };
  }

  private String getMessageText(@Nullable Object message) {
    switch (message) {
      case null -> {
        return "";
      }
      case String text -> {
        return text;
      }
      case ActionMessage actionMessage -> {
        if (actionMessage.isResource()) {
          return actionMessage.getKey();
        }
        return getMessageResources(bundle).getMessage(
          actionMessage.getKey(),
          actionMessage.getValues()
        );
      }
      default -> {
        return message.toString();
      }
    }
  }

  @Override
  public MessagesReference getReference() {
    return ref;
  }

  /**
   * The name of a page scope JSP bean that will contain the current element of
   * the collection of messages on each iteration, if it is not `null`.
   */
  public void setId(String id) {
    this.id = id;
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
   * By default, no filtering to escape XML characters like '<' occurs on the
   * replacement values going into the message pattern. If this attribute is
   * set to 'true', the replacement values will be filtered, while the text of
   * the message pattern itself will be left intact. This can be useful if you
   * have markup in your message patterns which you want to keep, but would
   * like to filter the replacement values going into them, e.g. if they
   * reflect user input. For instance:
   * <pre>
   * errors.divideZero=The mathematical expression <strong>{0}</strong> caused a divide by zero.
   * </pre>
   */
  public void setFilterArgs(boolean filterArgs) {
    this.filterArgs = filterArgs;
  }

  /**
   * The session attribute key for the Locale used to select messages to be
   * displayed. If not specified, defaults to the Struts standard value.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * This value is an optional message resource key that will be printed before
   * the iteration of messages begins.
   */
  public void setHeader(String header) {
    this.header = header;
  }

  /**
   * This value is an optional message resource key that will be printed after
   * the iteration of messages has finished.
   */
  public void setFooter(String footer) {
    this.footer = footer;
  }
}
