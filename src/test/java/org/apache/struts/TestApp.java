package org.apache.struts;

import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springing.struts1.configuration.MessageResourcesConfiguration;
import springing.struts1.configuration.StrutsConfiguration;
import springing.struts1.configuration.WebMvcConfiguration;
import springing.struts1.controller.StrutsRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.springframework.web.servlet.support.RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE;

@SpringBootApplication
@Import({StrutsConfiguration.class, WebMvcConfiguration.class, MessageResourcesConfiguration.class})
public class TestApp {

  @Autowired
  private MockServletContext servletContext;

  @Autowired
  private ServletActionContext actionContext;
  public ServletActionContext getActionContext() {
    return actionContext;
  }

  private MockPageContext pageContext;

  private HttpServletRequest request;

  private HttpServletResponse response;

  @Autowired
  private RequestMappingHandlerMapping requestHandlerMappings;

  @Autowired
  private @Nullable RequestProcessor requestProcessor;
  public RequestProcessor getRequestProcessor() {
    return requireNonNull(requestProcessor);
  }

  @Autowired
  private MessageSource messageSource;

  @Autowired
  public GenericApplicationContext appContext;

  public HttpServletResponse getResponse() {
    return response;
  }

  private ActionForm formBean;

  public HttpServletRequest createRequest(HttpMethod method, String requestPath) {
    return createRequest(method, requestPath, null);
  }

  public WebApplicationContext webApplicationContext;

  public HttpServletRequest createRequest(
    HttpMethod method, String requestPath, @Nullable Consumer<MockHttpServletRequest> setup
  ) {

    pageContext = new MockPageContext(servletContext);
    var mockRequest = (MockHttpServletRequest) pageContext.getRequest();
    mockRequest.setMethod(method.name());
    mockRequest.setServletPath(requestPath);
    mockRequest.setRequestURI(requestPath);
    if (webApplicationContext == null) {
      webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }
    mockRequest.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
    if (setup != null) setup.accept(mockRequest);
    request = HttpServletRequest.toJavaxNamespace(mockRequest);
    var mockResponse = (MockHttpServletResponse) pageContext.getResponse();
    mockResponse.setCharacterEncoding("UTF-8");
    response = HttpServletResponse.toJavaxNamespace(mockResponse);
    var requestAttributes = new ServletRequestAttributes(mockRequest, mockResponse);
    RequestContextHolder.setRequestAttributes(requestAttributes);
    var requestHandler = getStrutsRequestHandlerFor(mockRequest);
    var actionMapping = requestHandler.getActionMapping();
    actionContext.init(mockRequest, mockResponse);
    actionContext.setActionConfig(requestHandler.getActionMapping());
    request.setAttribute(Globals.MAPPING_KEY, actionMapping);
    return request;
  }

  private StrutsRequestHandler getStrutsRequestHandlerFor(MockHttpServletRequest request) {
    try {
      var handlerExecutionChain = requestHandlerMappings.getHandler(request);
      if (handlerExecutionChain == null) throw new IllegalStateException(
        "Failed to retrieve action mapping for the path: " + request.getRequestURI()
      );
      var handler = (HandlerMethod) handlerExecutionChain.getHandler();
      return (StrutsRequestHandler) handler.getBean();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void setupActionForm(String name, Supplier<ActionForm> formSupplier) {
    request.setAttribute(name, formSupplier.get());
    request.setAttribute(Constants.BEAN_KEY, name);
  }

  public <T extends Tag> void assertTagContent(
    @Nullable String actionPath,
    Class<T> clazz,
    BiConsumer<T, MockPageContext> setup,
    BiConsumer<String, Boolean> assertion
  ) throws Exception {
    var tag = clazz.getConstructor().newInstance();
    tag.setPageContext(pageContext);
    setup.accept(tag, pageContext);
    var bodyProcessed = tag.doStartTag();
    tag.doEndTag();
    if (tag instanceof TagSupport tagSupport) {
      tagSupport.doAfterBody();
    }
    String content = ((MockHttpServletResponse) pageContext.getResponse()).getContentAsString();
    assertion.accept(content, bodyProcessed == Tag.EVAL_BODY_INCLUDE);
  }
}
