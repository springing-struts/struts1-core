package springing.struts1.controller;

import jakarta.servlet.ServletException;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;
import java.lang.reflect.Method;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class StrutsRequestHandler implements HttpRequestHandler {

  public StrutsRequestHandler(
    ActionConfig actionConfig,
    ModuleConfig moduleConfig,
    ServletActionContext actionContext,
    RequestProcessor requestProcessor,
    GenericApplicationContext appContext
  ) {
    if (!(actionConfig instanceof ActionMapping mapping)) throw new IllegalStateException(format(
      "The action config [%s] should be an instance of [%s].",
      actionConfig.getPath(), ActionMapping.class.getName()
    ));
    this.actionMapping = mapping;
    this.moduleConfig = moduleConfig;
    this.actionContext = actionContext;
    this.requestProcessor = requestProcessor;
    this.appContext = appContext;
  }

  public ActionMapping getActionMapping() {
    return requireNonNull(actionMapping);
  }
  private final ActionMapping actionMapping;

  public ModuleConfig getModuleConfig() {
    return requireNonNull(moduleConfig);
  }
  private final ModuleConfig moduleConfig;

  public ServletActionContext actionContext() {
    return requireNonNull(actionContext);
  }
  private final ServletActionContext actionContext;

  private final RequestProcessor requestProcessor;
  private final GenericApplicationContext appContext;

 public void handleRequest(
    jakarta.servlet.http.HttpServletRequest request,
    jakarta.servlet.http.HttpServletResponse response
  ) throws ServletException, IOException {
    request.setAttribute(Globals.MESSAGES_KEY, moduleConfig.getMessageResources());
    actionContext.init(request, response);
    actionContext.setActionMapping(actionMapping);
    if (request.getRequestURI().endsWith(".jsp")) {
      new JspForwardingHandler().handleRequest(request, response);
      return;
    }
    actionMapping.registerAction(appContext, actionContext);
    requestProcessor.process(
      actionContext.getRequest(),
      actionContext.getResponse()
    );
  };
  public static final Method HANDLE_REQUEST;
  static {
    try {
      HANDLE_REQUEST = HttpRequestHandler.class.getMethod(
        "handleRequest",
        jakarta.servlet.http.HttpServletRequest.class,
        jakarta.servlet.http.HttpServletResponse.class
      );
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }
}

