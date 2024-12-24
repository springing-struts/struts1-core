package org.apache.struts.dispatcher;

import org.apache.struts.chain.contexts.ActionContext;
import org.springframework.lang.Nullable;

/**
 * This interface defines an intermediate handler that determines what method
 * to execute in an `Action`. Unlike the classical `execute` signature, it is
 * up to the dispatcher implementation to determine the particular arguments
 * and return type.
 */
public interface Dispatcher {
  /**
   * Dispatches to the action referenced by the specified context.
   * @param context the current action context
   * @return the result type, `null` if the response was handled directly, or
   *   `Void` if the executed method has no return signature
   * @throws Exception if an exception occurs
   */
   @Nullable Object dispatch(ActionContext context) throws Exception;
}
