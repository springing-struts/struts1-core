package springing.struts1.validator;

import org.apache.struts.action.ActionErrors;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import javax.servlet.http.HttpServletRequest;

public class ValidationUtils {

  private ValidationUtils() {
  }

  public static void bindRequest(
    HttpServletRequest request,
    Object bean
  ) throws BindException {
    var binder = new StrutsDataBinder(bean);
    binder.bind(new MutablePropertyValues(request.getParameterMap()));
    var result = binder.getBindingResult();
    if (result.hasErrors()) throw new BindException(result);
  }

  public static ActionErrors validateRequest(
    @Nullable String formName,
    HttpServletRequest request,
    Object bean
  ) {
    if (formName == null) {
      return new ActionErrors();
    }
    var validator = FormBeanValidator.forName(formName);
    var binder = new StrutsDataBinder(bean);
    binder.setValidator(validator);
    binder.validate();
    var result = binder.getBindingResult();
    request.setAttribute("org.springframework.validation.BindingResult." + formName, result);
    return validator == null ? new ActionErrors() : validator.getActionErrors();
  }
}
