package springing.struts1.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.actions.ForwardAction;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionController implements Controller {

  public ActionController(
    ActionConfig actionConfig
  ) {
    this.actionConfig = actionConfig;
    this.formBeanConfig = actionConfig.getFormBeanConfig();
  }

  private final ActionConfig actionConfig;
  private final @Nullable FormBeanConfig formBeanConfig;

  @Override
  public ModelAndView handleRequest(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    var action = createAction();
    var form = createForm();
    var forward = action.execute(
      actionConfig.createActionMapping(),
      form,
      javax.servlet.http.HttpServletRequest.wrap(request),
      javax.servlet.http.HttpServletResponse.wrap(response)
    );
    return toModelAndView(forward);
  }

  private Action createAction() {
    var forward = actionConfig.getForwardPath();
    if (forward != null) {
      return new ForwardAction();
    }
    var type = actionConfig.getType();
    if (type != null) {
      return createInstanceOf(type);
    }
    throw new IllegalStateException(
        "Failed to create an action."
    );
  }

  private @Nullable ActionForm createForm() {
    if (formBeanConfig == null) return null;
    return createInstanceOf(formBeanConfig.getType());
  }

  private <T> T createInstanceOf(String type) {
    try {
      var clazz = Class.forName(type);
      return (T) clazz.getConstructor().newInstance();
    } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
             NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
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

  private ModelAndView toModelAndView(ActionForward forward) {
    return new ModelAndView(forward.getPath());
  }
}
