package springing.struts1.taglib;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class UrlBuilder {

  @Nullable
  String module;

  @Nullable
  String action;

  @Nullable
  String page;

  @Nullable
  String pageKey;

  @Nullable
  String forward;

  @Nullable
  String name;

  @Nullable
  String property;

  @Nullable
  String paramId;

  @Nullable
  String paramName;

  @Nullable
  String paramProperty;

  @Nullable
  String bundle;

  @Nullable
  String anchor;

  boolean useLocalEncoding = false;
  boolean awareNestedTag = false;
  final Map<String, Object> additionalParams = new HashMap<>();

  public String buildUrl(PageContext pageContext) {
    if (forward != null) {
      return buildUrlFromForward(forward);
    }
    var moduleRelPath = buildModuleRelPath(pageContext);
    var module = getModule(pageContext);
    var path = module.prependModuleBasePath(moduleRelPath);
    var uri = ServletUriComponentsBuilder.fromUriString(path);
    var params = new HashMap<String, Object>();
    if (name != null) {
      bindParamsFromBeanName(name, params, pageContext);
    }
    if (paramId != null) {
      bindParamFromVariable(paramId, params, pageContext);
    }
    params.putAll(additionalParams);
    params.forEach((key, value) -> {
      if (value instanceof Object[] arrayValue) {
        uri.replaceQueryParam(key, arrayValue);
      } else {
        uri.replaceQueryParam(key, value);
      }
    });
    var charset = getCharset(pageContext);
    return uri.build().encode(charset).toUriString() + buildAnchor();
  }

  private String buildAnchor() {
    return anchor == null ? "" : ("#" + UriUtils.encodePath(anchor, UTF_8));
  }

  private String buildUrlFromForward(String forward) {
    var forwardConfig = ModuleUtils.getCurrent().findForwardConfig(forward);
    if (forwardConfig == null) throw new IllegalStateException(
      format("Unknown forward name: %s.", forward)
    );
    return forwardConfig.getUrl();
  }

  private void bindParamsFromBeanName(
    String beanName,
    Map<String, Object> params,
    PageContext pageContext
  ) {
    var bindStatus = StrutsDataBinding.onScope(
      pageContext,
      beanName,
      property,
      awareNestedTag
    );
    params.putAll(bindStatus.getValueAsMap());
  }

  private void bindParamFromVariable(
    String varName,
    Map<String, Object> params,
    PageContext pageContext
  ) {
    var bindStatus = StrutsDataBinding.onScope(
      pageContext,
      requireNonNullElse(paramName, varName),
      paramProperty,
      awareNestedTag
    );
    var value = bindStatus.getValue();
    params.put(varName, value);
  }

  private Charset getCharset(PageContext context) {
    if (!useLocalEncoding) {
      return UTF_8;
    }
    return Charset.forName(context.getResponse().getCharacterEncoding(), UTF_8);
  }

  private String buildModuleRelPath(PageContext pageContext) {
    if (page != null) {
      return page;
    }
    if (pageKey != null) {
      return getModule(pageContext)
        .getMessageResources(bundle)
        .requireMessage(pageKey);
    }
    if (action != null) {
      return getModule(pageContext).evalActionId(action);
    }
    throw new IllegalArgumentException(
      "Failed to determine the relative url of this link tag."
    );
  }

  private ModuleConfig getModule(PageContext pageContext) {
    if (module == null) {
      return ModuleUtils.getCurrent();
    }
    return requireNonNull(
      ModuleUtils.getInstance()
        .getModuleConfig(
          module,
          ServletContext.toJavaxNamespace(pageContext.getServletContext())
        ),
      "Unknown module prefix: " + module
    );
  }
}
