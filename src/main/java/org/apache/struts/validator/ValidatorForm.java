package org.apache.struts.validator;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import springing.struts1.validator.ValidationUtils;

import javax.servlet.http.HttpServletRequest;

/**
 *  This class extends `ActionForm` and provides basic field validation based
 *  on an XML file. The key passed into the validator is the action element's
 *  'name' attribute from the struts-config.xml which should match the form
 *  element's name attribute in the validation.xml.
 *  See `ValidatorPlugin` definition in struts-config.xml for validation rules.
 */
public class ValidatorForm extends ActionForm {

  /**
   * Validate the properties that have been set from this HTTP request, and
   * return an `ActionErrors` object that encapsulates any validation errors
   * that have been found.  If no errors are found, return `null` or an
   * `ActionErrors` object with no recorded error messages.
   */
  public ActionErrors validate(
    ActionMapping mapping,
    HttpServletRequest request
  ) {
    return ValidationUtils.validate(mapping, request, this);
  }

  /**
   * Returns the request-scope or session-scope attribute name under which our
   * form bean is accessed, if it is different from the form bean's specified
   * `name`.
   */
  public int getPage() {
    return page;
  }
  public void setPage(int page) {
    this.page = page;
  }
  private int page = 0;
}
