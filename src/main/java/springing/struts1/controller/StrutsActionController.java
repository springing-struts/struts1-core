package springing.struts1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.taglib.html.Constants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import springing.util.ServletRequestUtils;
import java.lang.reflect.Method;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;
import static springing.struts1.validator.ValidationUtils.bindRequest;
import static springing.util.ObjectUtils.classFor;

public class StrutsActionController implements Controller {

  public StrutsActionController(
    ActionConfig actionConfig,
    GenericApplicationContext context
  ) {
    this.actionMapping = actionConfig.createActionMapping();
    this.formBeanConfig = actionConfig.getFormBeanConfig();
    this.context = context;
    registerAction();
  }

  private final ActionMapping actionMapping;
  private final @Nullable FormBeanConfig formBeanConfig;
  private final GenericApplicationContext context;

  @Override
  public ModelAndView handleRequest(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    var req = ServletRequestUtils.wrap(request);
    var res = ServletRequestUtils.wrap(response);
    req.setAttribute(Globals.MAPPING_KEY, actionMapping);
    var forwardUrl = actionMapping.getForwardUrl();
    if (forwardUrl != null) {
      return toModelAndView(forwardUrl);
    }
    var form = prepareFormBean(req);
    if (form != null && actionMapping.getValidate()) {
      var errors = form.validate(actionMapping, req);
      if (errors != null && !errors.isEmpty()) {
        var input = actionMapping.getInputForward();
        if (input == null) throw new IllegalStateException(
          "The input property is required when form validation is enabled: " +
          actionMapping.getName()
        );
        req.setAttribute(Globals.ERROR_KEY, errors);
        return toModelAndView(input.getUrl(), input.getRedirect());
      }
    }
    var forward = getAction().execute(
      actionMapping, form, req, res
    );
    return toModelAndView(forward.getUrl(), forward.getRedirect());
  }

  private Action getAction() {
    return context.getBean(getActionClass());
  }

  private Class<? extends Action> getActionClass() {
    String type = actionMapping.getType();
    if (!hasText(type)) throw new IllegalStateException(String.format(
      "Failed to determine action class for action mapping [%s].",
      actionMapping.getPath()
    ));
    return classFor(type);
  }

  private void registerAction() {
    if (!hasText(actionMapping.getType())) {
      return;
    }
    var actionClass = getActionClass();
    var actions = context.getBeansOfType(actionClass);
    if (!actions.isEmpty()) {
      return;
    }
    context.registerBean(actionClass);
  }

  public static final Method DEFAULT_METHOD;
  static {
    try {
      DEFAULT_METHOD = Controller.class.getDeclaredMethod(
          "handleRequest", HttpServletRequest.class, HttpServletResponse.class
      );
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nullable ActionForm prepareFormBean(javax.servlet.http.HttpServletRequest request) {
    if (request.getParameter(Constants.CANCEL_PROPERTY) != null) {
      request.setAttribute(Globals.CANCEL_KEY, Boolean.TRUE);
    }
    if (formBeanConfig == null) {
      var formBeanKey = (String) request.getAttribute(Constants.BEAN_KEY);
      if (formBeanKey == null || formBeanKey.isEmpty()) {
        return null;
      }
      return (ActionForm) request.getAttribute(formBeanKey);
    }
    var formBean = formBeanConfig.createActionForm();
    try {
      bindRequest(request, formBean);
    } catch (BindException e) {
      throw new IllegalArgumentException(String.format(
        "Failed to bind the parameters of the request [%s] to the form bean [%s].",
        actionMapping.getActionId(), actionMapping.getName()
      ), e);
    }
    return formBean;
  }

  private ModelAndView toModelAndView(String viewName) {
    return toModelAndView(viewName, false);
  }

  private ModelAndView toModelAndView(String viewName, boolean isRedirect) {
    var prefix = isRedirect ? "redirect:"
        : viewName.endsWith(".jsp") ? ""
        : "forward:";
    return new ModelAndView(prefix + viewName);
  }
}
