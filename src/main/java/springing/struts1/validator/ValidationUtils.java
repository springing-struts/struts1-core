package springing.struts1.validator;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import javax.servlet.http.HttpServletRequest;

public class ValidationUtils {

  private ValidationUtils() {
  }

  public static ActionErrors validate(
      ActionMapping mapping,
      HttpServletRequest request,
      Object bean
  ) {
    var formName = mapping.getName();
    if (formName == null) {
      return new ActionErrors();
    }
    var validator = FormBeanValidator.forName(formName);
    if (validator == null) {
      return new ActionErrors();
    }
    var binder = new DataBinder(bean);
    binder.setValidator(validator);
    binder.bind(new MutablePropertyValues(request.getParameterMap()));
    binder.validate();
    var result = binder.getBindingResult();
    request.setAttribute("org.springframework.validation.BindingResult." + formName, result);
    return validator.getActionErrors();
  }
}
