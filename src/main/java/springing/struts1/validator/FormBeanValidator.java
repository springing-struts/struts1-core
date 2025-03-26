package springing.struts1.validator;

import org.apache.commons.validator.Form;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * A Spring `Validator` implementation that validates a Struts form bean
 * instance based on the Struts validator plugin configuration
 * (validation.xml).
 */
public class FormBeanValidator implements Validator {

  private FormBeanValidator(Form form, FormBeanConfig formBeanConfig) {
    this.form = form;
    this.formBeanConfig = formBeanConfig;
    this.actionErrors = new ActionErrors();
  }

  private final Form form;
  private final FormBeanConfig formBeanConfig;

  public ActionErrors getActionErrors() {
    return actionErrors;
  }

  private final ActionErrors actionErrors;

  public static @Nullable FormBeanValidator forName(String formBeanName) {
    var module = ModuleUtils.getCurrent();
    var validatorResources = module.getValidatorResources();
    var form = validatorResources.getForm(
      LocaleContextHolder.getLocale(),
      formBeanName
    );
    if (form == null) {
      return null;
    }
    var formBeanConfig = module.findFormBeanConfig(formBeanName);
    if (formBeanConfig == null) throw new IllegalStateException(
      String.format(
        "The formBeanName [%s] in the validation configuration" +
        " is not defined in struts-config for the module [%s].",
        formBeanName,
        module.getPrefix()
      )
    );
    return new FormBeanValidator(form, formBeanConfig);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.getName().equals(formBeanConfig.getType());
  }

  @Override
  public void validate(Object target, Errors errors) {
    var actions = form
      .getFormSet()
      .getValidatorResources()
      .getValidatorActions();
    for (var field : form.getFields()) {
      var fieldErrors = field.validate(target, actions);
      fieldErrors.forEach((property, fieldError) -> {
        errors.rejectValue(
          property,
          fieldError.getKey(),
          fieldError.getValues(),
          fieldError.getKey()
        );
      });
      actionErrors.add(fieldErrors);
    }
  }
}
