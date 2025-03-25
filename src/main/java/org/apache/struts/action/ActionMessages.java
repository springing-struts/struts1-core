package org.apache.struts.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *  A class that encapsulates messages.
 *  Messages can be either global or they are specific to a particular bean
 *  property. Each individual message is described by an `ActionMessage`
 *  object, which contains a message key (to be looked up in an appropriate
 *  message resources database), and up to four placeholder arguments used for
 *  parametric substitution in the resulting message.
 *  **IMPLEMENTATION NOTE**
 *    It is assumed that these objects are created and manipulated only within
 *    the context of a single thread. Therefore, no synchronization is
 *    required for access to internal collections.
 */
public class ActionMessages {

  /**
   * The "property name" marker to use for global messages, as opposed to those
   * related to a specific property.
   */
  public static final String GLOBAL_MESSAGE =
    "org.apache.struts.action.GLOBAL_MESSAGE";

  private final MultiValueMap<String, ActionMessage> messages;

  /**
   * Create an empty `ActionMessages` object.
   */
  public ActionMessages() {
    messages = new LinkedMultiValueMap<>();
  }

  /**
   * Create an `ActionMessages` object initialized with the given messages.
   */
  public ActionMessages(ActionMessages messages) {
    this();
    add(messages);
  }

  /**
   * Add a message to the set of messages for the specified property. An order
   * of the property/key is maintained based on the initial addition of the
   * property/key.
   */
  public void add(String property, ActionMessage message) {
    messages.add(property, message);
  }

  /**
   * Adds the messages from the given `ActionMessages` object to this set of
   * messages. The messages are added in the order they are returned from the
   * `properties` method. If a message's property is already in the current
   * `ActionMessages` object, it is added to the end of the list for that
   * property. If a message's property is not in the current list it is added
   * to the end of the properties.
   */
  public void add(ActionMessages actionMessages) {
    messages.addAll(actionMessages.messages);
  }

  public void addValidationError(Field field, ValidatorAction validator) {
    add(field.getProperty(), field.getActionMessageFor(validator));
  }

  /**
   * Clear all messages recorded by this object.
   */
  public void clear() {
    messages.clear();
  }

  /**
   * Return `true` if there are no messages recorded in this collection, or
   * `false` otherwise.
   */
  public boolean isEmpty() {
    return messages.isEmpty();
  }

  /**
   * Return the set of all recorded messages, without distinction by which
   * property the messages are associated with. If there are no messages
   * recorded, an empty enumeration is returned.
   */
  public List<ActionMessage> get() {
    return messages.values().stream().flatMap(Collection::stream).toList();
  }

  /**
   * Return the set of messages related to a specific property. If there are no
   * such messages, an empty enumeration is returned.
   */
  public List<ActionMessage> get(String property) {
    var messagesOfProperty = messages.get(property);
    if (messagesOfProperty == null) {
      return List.of();
    }
    return messagesOfProperty;
  }

  public void forEach(BiConsumer<String, ActionMessage> action) {
    messages.forEach((property, messagesForProperty) -> {
      for (ActionMessage message : messagesForProperty) {
        action.accept(property, message);
      }
    });
  }

  /**
   * Return the set of property names for which at least one message has been
   * recorded. If there are no messages, an empty `Iterator` is returned. If
   * you have recorded global messages, the `String` value of
   * `ActionMessages.GLOBAL_MESSAGE` will be one of the returned property
   * names.
   */
  public Iterator<String> properties() {
    return messages.keySet().iterator();
  }

  /**
   * Return the number of messages recorded for all properties (including
   * global messages).
   * **NOTE**
   *   it is more efficient to call `isEmpty` if all you care about is whether
   *   there are any messages at all.
   */
  public int size() {
    return messages.values().stream().map(List::size).reduce(0, Integer::sum);
  }

  /**
   * Return the number of messages associated with the specified property.
   */
  public int size(String property) {
    var messagesOfProperty = messages.get(property);
    if (messagesOfProperty == null) {
      return 0;
    }
    return messages.size();
  }
}
