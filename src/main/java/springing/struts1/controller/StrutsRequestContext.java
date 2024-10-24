package springing.struts1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionMapping;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.requireNonNull;

public class StrutsRequestContext {

  private @Nullable ActionMapping actionMapping;

  public static ActionMapping getActionMapping() {
    return requireNonNull(
      get().actionMapping,
      "The action mapping for the current request has not been initialized yet."
    );
  }

  public static void setActionMapping(ActionMapping actionMapping) {
    get().actionMapping = actionMapping;
  }

  private static final ThreadLocal<StrutsRequestContext> HOLDER = ThreadLocal.withInitial(
    StrutsRequestContext::new
  );

  public static StrutsRequestContext get() {
    return HOLDER.get();
  }

  public static void clean() {
    HOLDER.remove();
  }

  public static class Interceptor implements HandlerInterceptor {
    @Override
    public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable ModelAndView modelAndView
    ) throws Exception {
      StrutsRequestContext.clean();
    }
  }
}
