package springing.struts1.taglib;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static springing.util.StringUtils.normalizeForwardPath;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

  boolean useLocalEncoding = false;
  boolean awareNestedTag = false;

  public String buildUrl(PageContext pageContext) {
    if (forward != null) {
      return buildUrlFromForward(forward);
    }
    var relPath = buildRelPath();
    var modulePath = buildModulePath(pageContext);
    var path = normalizeForwardPath(modulePath + "/" + relPath);
    var uri = ServletUriComponentsBuilder.fromUriString(path);
    if (name != null) {
      bindParamsFromBeanName(name, uri, pageContext);
    }
    if (paramId != null) {
      bindParamFromVariable(paramId, uri, pageContext);
    }
    var charset = getCharset(pageContext);
    return uri.build().encode(charset).toUriString();
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
    UriComponentsBuilder uri,
    PageContext pageContext
  ) {
    var bindStatus = StrutsDataBinding.onScope(
      pageContext,
      beanName,
      property,
      awareNestedTag
    );
    var propsMap = bindStatus.getValueAsMap();
    propsMap.forEach((key, value) -> {
      if (value instanceof Object[] arrayValue) {
        uri.replaceQueryParam(key, arrayValue);
      } else {
        uri.replaceQueryParam(key, value);
      }
    });
  }

  private void bindParamFromVariable(
    String varName,
    UriComponentsBuilder uri,
    PageContext pageContext
  ) {
    var bindStatus = StrutsDataBinding.onScope(
      pageContext,
      requireNonNullElse(paramName, varName),
      paramProperty,
      awareNestedTag
    );
    var value = bindStatus.getValue();
    if (value instanceof Object[] arrayValue) {
      uri.replaceQueryParam(varName, arrayValue);
    } else {
      uri.replaceQueryParam(varName, value);
    }
  }

  private Charset getCharset(PageContext context) {
    if (!useLocalEncoding) {
      return StandardCharsets.UTF_8;
    }
    return Charset.forName(
      context.getResponse().getCharacterEncoding(),
      StandardCharsets.UTF_8
    );
  }

  private String buildRelPath() {
    if (page != null) {
      return page;
    }
    if (pageKey != null) {
      return ModuleUtils.getCurrent()
        .getMessageResources(bundle)
        .requireMessage(pageKey);
    }
    if (action != null) {
      return action; // + ".do";
    }
    throw new IllegalArgumentException(
      "Failed to determine the relative url of this link tag."
    );
  }

  private String buildModulePath(PageContext pageContext) {
    if (module == null) {
      return ModuleUtils.getCurrent().getPrefix();
    }
    var config = ModuleUtils.getInstance()
      .getModuleConfig(
        module,
        ServletContext.toJavaxNamespace(pageContext.getServletContext())
      );
    if (config == null) throw new IllegalArgumentException(
      "Unknown module prefix: " + module
    );
    return config.getPrefix();
  }
}
