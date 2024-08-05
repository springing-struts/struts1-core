package org.apache.struts.validator;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;
import springing.struts1.validator.FormBeanValidator;

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
    var formName = mapping.getName();
    if (formName == null) {
      return new ActionErrors();
    }
    var validator = FormBeanValidator.forName(formName);
    if (validator == null) {
      return new ActionErrors();
    }
    var binder = new DataBinder(this);
    binder.setValidator(validator);
    binder.bind(new MutablePropertyValues(request.getParameterMap()));
    binder.validate();
    var result = binder.getBindingResult();
    request.setAttribute("org.springframework.validation.BindingResult." + formName, result);
    return validator.getActionErrors();
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
