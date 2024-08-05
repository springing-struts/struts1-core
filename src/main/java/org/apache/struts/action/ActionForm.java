package org.apache.struts.action;

import jakarta.validation.Validation;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.servlet.http.HttpServletRequest;

public class ActionForm {
  /**
   * Can be used to reset all bean properties to their default state.
   * This method is called before the properties are repopulated by the
   * controller.
   * The default implementation attempts to forward to the HTTP version of
   * this method.
   */
  public void reset(
    ActionMapping mapping,
    HttpServletRequest request
  ) {
    // NOP
  }

  /**
   * Can be used to validate the properties that have been set for this HTTP
   * request, and return an `ActionErrors` object that encapsulates any
   * validation errors that have been found. If no errors are found, return
   * `null` or an `ActionErrors` object with no recorded error messages.
   * The default implementation performs no validation and returns `null`.
   * Subclasses must override this method to provide any validation they wish
   * to perform.
   */
  public @Nullable ActionErrors validate(
    ActionMapping mapping,
    HttpServletRequest request
  ) {
    return null;
  }

  /**
   * Return the servlet instance to which we are attached.
   * **NOTE:**
   * The instance returned by this method is a dummy implementation of the
   * ActionServlet class, as the Spring framework's DispatchServlet is used
   * instead.
   */
  public ActionServlet getServlet() {
    return servlet;
  }
  protected ActionServlet servlet = new ActionServlet();
}

