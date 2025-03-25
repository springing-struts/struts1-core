package springing.struts1.validator;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springing.struts1.upload.FormFileWrapper;

public class ValidationUtils {

  private ValidationUtils() {}

  public static void bindRequest(HttpServletRequest request, Object bean)
    throws BindException {
    var binder = new StrutsDataBinder(bean);
    var bindingValues = new HashMap<String, Object>(request.getParameterMap());
    if (
      request.unwrap() instanceof MultipartHttpServletRequest multipartRequest
    ) {
      multipartRequest
        .getFileMap()
        .forEach((key, file) -> {
          bindingValues.put(key, new FormFileWrapper(file));
        });
    }
    binder.bind(new MutablePropertyValues(bindingValues));
    var result = binder.getBindingResult();
    if (result.hasErrors()) {
      throw new BindException(result);
    }
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
    request.setAttribute(
      "org.springframework.validation.BindingResult." + formName,
      result
    );
    return (validator == null)
      ? new ActionErrors()
      : validator.getActionErrors();
  }
}
