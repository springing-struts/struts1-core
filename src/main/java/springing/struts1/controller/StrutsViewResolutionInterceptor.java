package springing.struts1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import static springing.util.StringUtils.getExtensionOf;

public class StrutsViewResolutionInterceptor implements HandlerInterceptor {

  @Override
  public void postHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    @Nullable ModelAndView modelAndView
  ) throws Exception {
    forwardIfMatchesPattern(modelAndView);
  }

  private void forwardIfMatchesPattern(@Nullable ModelAndView modelAndView) {
    if (modelAndView == null) {
      return;
    }
    var path = modelAndView.getViewName();
    if (path == null) {
      return;
    }
    var extension = getExtensionOf(path);
    if (extension.equals("do")) {
      modelAndView.setViewName("forward:" + path);
    }
    if (extension.isEmpty()) {
      modelAndView.setViewName("forward:" + path + ".do");
    }
  }
}
