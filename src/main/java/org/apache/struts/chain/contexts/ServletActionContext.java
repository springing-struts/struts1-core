package org.apache.struts.chain.contexts;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static springing.util.ObjectUtils.*;
import static springing.util.ObjectUtils.retrieveValue;

import jakarta.servlet.jsp.PageContext;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.tiles.config.TilesDefinition;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.NestedPathTag;

/**
 * Web-specific context information for actions.
 */
public class ServletActionContext implements ActionContext {

  public ServletActionContext(
    ActionServlet actionServlet,
    ModuleConfigBean moduleConfig,
    ConversionService conversionService
  ) {
    this.servlet = actionServlet;
    this.moduleConfig = moduleConfig;
    this.conversionService = conversionService;
  }

  private final ActionServlet servlet;
  private final ConversionService conversionService;

  public static class Holder {

    public Holder(ServletActionContext instance) {
      INSTANCE = instance;
    }

    private static @Nullable ServletActionContext INSTANCE;
  }

  public static ServletActionContext current() {
    return requireNonNull(Holder.INSTANCE);
  }

  public void init(
    jakarta.servlet.http.HttpServletRequest request,
    jakarta.servlet.http.HttpServletResponse response
  ) {
    currentRequest = HttpServletRequest.toJavaxNamespace(request);
    currentResponse = HttpServletResponse.toJavaxNamespace(response);
  }

  /**
   * Returns the current request.
   */
  public HttpServletRequest getRequest() {
    return requireNonNull(
      currentRequest,
      "This action context has not been initialized yet."
    );
  }

  private @Nullable HttpServletRequest currentRequest;

  /**
   * Returns the current response.
   */
  public HttpServletResponse getResponse() {
    return requireNonNull(
      currentResponse,
      "This action context has not been initialized yet."
    );
  }

  private @Nullable HttpServletResponse currentResponse;

  /**
   * Returns the ActionServlet.
   */
  public ActionServlet getActionServlet() {
    return servlet;
  }

  @Override
  public Action getAction() {
    return requireNonNull(
      action,
      "The action for the current action is not initialized."
    );
  }

  @Override
  public void setAction(Action action) {
    this.action = action;
  }

  private @Nullable Action action;

  @Override
  public @Nullable ActionForm getActionForm() {
    return actionForm;
  }

  @Override
  public void setActionForm(ActionForm actionForm) {
    this.actionForm = actionForm;
  }

  private @Nullable ActionForm actionForm;

  @Override
  public ActionConfig getActionConfig() {
    return getActionMapping();
  }

  @Override
  public void setActionConfig(ActionConfig actionConfig) {
    if (
      !(actionConfig instanceof ActionMapping mapping)
    ) throw new IllegalArgumentException(
      format(
        "The given action config is an instance of [%s], which should be instance of [%s].",
        actionConfig.getClass().getName(),
        ActionMapping.class.getName()
      )
    );
    this.actionMapping = mapping;
  }

  @Override
  public ModuleConfigBean getModuleConfig() {
    return requireNonNull(
      moduleConfig,
      "The module config for the current request is not initialized."
    );
  }

  @Override
  public void setModuleConfig(ModuleConfigBean moduleConfig) {
    this.moduleConfig = moduleConfig;
  }

  private @Nullable ModuleConfigBean moduleConfig;

  public TilesDefinition getTilesDefinition() {
    return requireNonNull(
      tilesDefinition,
      "Failed to retrieve Tiles definition for the current request." +
      " Perhaps, TilesRequestProcessor is not configured."
    );
  }

  public void setTilesDefinition(TilesDefinition tilesDefinition) {
    this.tilesDefinition = tilesDefinition;
  }

  public void setTilesDefinition(String tilesDefinitionName) {
    var tilesDefinition = getModuleConfig()
      .getTilesDefinitions()
      .getTilesDefinitionByName(tilesDefinitionName);
    if (tilesDefinition == null) throw new IllegalArgumentException(
      format("Unknown tiles definition name [%s].", tilesDefinitionName)
    );
    this.tilesDefinition = tilesDefinition.copy();
  }

  private @Nullable TilesDefinition tilesDefinition;

  @Override
  public @Nullable ForwardConfig getForwardConfig() {
    return forwardConfig;
  }

  @Override
  public void setForwardConfig(ForwardConfig forwardConfig) {
    this.forwardConfig = forwardConfig;
  }

  private @Nullable ForwardConfig forwardConfig;

  public ActionMapping getActionMapping() {
    return requireNonNull(
      actionMapping,
      "The action mapping for the current request has not been initialized yet."
    );
  }

  public void setActionMapping(ActionMapping actionMapping) {
    this.actionMapping = actionMapping;
  }

  private @Nullable ActionMapping actionMapping;

  public @Nullable String interpolatePathParams(@Nullable String str) {
    return getActionMapping().interpolatePathParams(str, getRequest());
  }

  /**
   * Forwards or includes the request to the specified URI.
   * This method dispatches the current HTTP request to the given URI either by forwarding
   * the request (i.e., the current response will be replaced with the response from the
   * target resource) or by including the content of the target resource within the current
   * response. The method determines whether to forward or include based on the value of the
   * `includes` parameter.
   */
  public void forwardRequest(String uri, boolean includes)
    throws ServletException, IOException {
    var request = getRequest();
    var response = getResponse();
    var dispatcher = request.getRequestDispatcher(uri);
    if (dispatcher == null) throw new ServletException(
      format("Failed to retrieve a RequestDispatcher for url [%s].", uri)
    );
    try {
      if (includes) {
        dispatcher.include(request.unwrap(), response.unwrap());
      } else {
        dispatcher.forward(request.unwrap(), response.unwrap());
      }
    } catch (jakarta.servlet.ServletException e) {
      throw new ServletException(e);
    }
  }

  public void forwardRequest(String uri) throws ServletException, IOException {
    forwardRequest(uri, false);
  }

  public <T> @Nullable T convertValue(@Nullable Object value, Class<T> toType) {
    return conversionService.convert(value, toType);
  }

  public static @Nullable Object getAttributeFromScope(
    PageContext pageContext,
    @Nullable String key
  ) {
    return getAttributeFromScope(pageContext, key, null);
  }

  public static @Nullable Object getAttributeFromScope(
    PageContext pageContext,
    @Nullable String key,
    @Nullable Integer scope
  ) {
    if (key == null) {
      return null;
    }
    var m = INDEXED_PROPERTY.matcher(key);
    var isIndexed = m.matches();
    var index = isIndexed ? Integer.valueOf(m.group(2)) : null;
    var name = isIndexed ? m.group(1) : key;
    var model = (scope == null)
      ? pageContext.findAttribute(name)
      : pageContext.getAttribute(name, scope);
    if (model == null) {
      return null;
    }
    return isIndexed ? getAt(model, index) : model;
  }

  public static void setAttributeToScope(
    PageContext pageContext,
    @Nullable String key,
    @Nullable Object value,
    @Nullable Integer scope
  ) {
    if (key == null) {
      return;
    }
    if (scope == null) {
      pageContext.setAttribute(key, value);
    } else {
      pageContext.setAttribute(key, value, scope);
    }
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext
  ) {
    return resolveValueOnScope(
      attributeName,
      relPath,
      awareNestedTag,
      pageContext,
      null
    );
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext,
    @Nullable Integer scope
  ) {
    if (attributeName != null) {
      var bean = getAttributeFromScope(pageContext, attributeName, scope);
      if (relPath == null) {
        return bean;
      }
      return retrieveValue(
        bean,
        awareNestedTag ? resolveNestedPath(relPath, pageContext) : relPath
      );
    }
    if (relPath == null) {
      return null;
    }
    var fullPath = resolveNestedPath(relPath, pageContext);
    var pos = fullPath.indexOf(".");
    var attr = pos < 0 ? fullPath : fullPath.substring(0, pos);
    var propertyPath = pos < 0 ? null : relPath.substring(pos + 1);
    var bean = getAttributeFromScope(pageContext, attr, scope);
    return retrieveValue(bean, propertyPath);
  }

  public static String resolveNestedPath(
    String relPath,
    PageContext pageContext
  ) {
    var nestedPath = (String) pageContext.getAttribute(
      NestedPathTag.NESTED_PATH_VARIABLE_NAME,
      PageContext.REQUEST_SCOPE
    );
    var path = (nestedPath == null ? "" : (nestedPath + ".")) + relPath;
    return path.replaceAll("(\\.{2,})", ".").replaceAll("\\.$", "");
  }
}
