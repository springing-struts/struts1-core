package springing.struts1.taglib;

import jakarta.servlet.jsp.PageContext;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

import static org.apache.struts.chain.contexts.ServletActionContext.resolveValueOnScope;

public interface MessagesAware {

  MessagesReference getReference();

  class MessagesReference {
    public static MessagesReference create() {
      return new MessagesReference();
    }
    private MessagesReference() {
    }
    public @Nullable String name;
    public @Nullable String property;
    public boolean message = false;
    public @Nullable String count;
    public boolean awareNestedTag = false;

    @Nullable
    List<?> getMessages(PageContext pageContext) {
      var messages = getMessagesFromScope(pageContext);
      if (messages == null) {
        setCount(0, pageContext);
        return null;
      }
      if (messages.getClass().isArray()) {
        var messageList = Arrays.asList((Object[]) messages);
        setCount(messageList.size(), pageContext);
        return messageList;
      }
      if (messages instanceof ActionMessages actionMessages) {
        setCount(property == null ? actionMessages.size() : actionMessages.size(property), pageContext);
        return property == null ? actionMessages.get() : actionMessages.get(property);
      }
      if (messages instanceof List<?> messageList) {
        setCount(messageList.size(), pageContext);
        return messageList;
      }
      setCount(1, pageContext);
      return List.of(messages);
    }

    private @Nullable Object getMessagesFromScope(PageContext pageContext) {
      if  (name == null) {
        var beanName = message ? Globals.MESSAGE_KEY : Globals.ERROR_KEY;
        return resolveValueOnScope(beanName , property,  awareNestedTag, pageContext);
      }
      var path = name + "." + property;
      return resolveValueOnScope(null, path, awareNestedTag, pageContext);
    }

    private void setCount(int numberOfMessages, PageContext pageContext) {
      if (count == null || count.isEmpty()) {
        return;
      }
      pageContext.setAttribute(count, numberOfMessages);
    }
  }

  /**
   * Name of the bean in any scope under which our messages have been stored.
   * If not present, the name specified by the `Globals.ERROR_KEY` constant
   * string will be used.
   */
  default void setName(String name) {
    getReference().name = name;
  }

  /**
   * Name of the property for which messages should be retrieved. If not
   * specified, all messages (regardless of property) are retrieved.
   */
  default void setProperty(String property) {
    getReference().property = property;
  }

  /**
   * By default, the tag will retrieve the bean it will iterate over from the
   * `Globals.ERROR_KEY` constant string, but if this attribute is set to
   * 'true' the bean will be retrieved from the `Globals.MESSAGE_KEY` constant
   * string. Also, if this is set to 'true', any value assigned to the name
   * attribute will be ignored.
   */
  default void setMessage(boolean message) {
    getReference().message = message;
  }

  /**
   * Specifies the name of the page-scoped attribute to set with the message
   * count. This is useful, for instance, when needing to total the number of
   * messages or errors produced by a form submission. The attribute goes out
   * of scope after the tag executes.
   */
  default void setCount(String count) {
    getReference().count = count;
  }

  default @Nullable List<?> getMessages(PageContext pageContext) {
    return getReference().getMessages(pageContext);
  }

  default void setAwareNestedTag(boolean awareNestedTag) {
    getReference().awareNestedTag = awareNestedTag;
  }
}
