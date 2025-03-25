package org.apache.struts.taglib.logic;

/**
 * Generate the nested body content of this tag if the specified message is
 * not present in any scope.
 * Evaluates the nested body content of this tag if an `ActionMessages` object,
 * `ActionErrors` object, a String, or a String array is not present in any
 * scope. If such a bean is found, nothing will be rendered.
 */
public class MessagesNotPresentTag extends MessagesPresentTag {

  public MessagesNotPresentTag() {
    super(true);
  }
}
