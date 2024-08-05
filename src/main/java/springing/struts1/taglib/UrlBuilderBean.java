package springing.struts1.taglib;

import jakarta.servlet.jsp.PageContext;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static springing.util.StringUtils.normalizeForwardPath;

public interface UrlBuilderBean {

  default String buildUrl(PageContext context) {
    return getUrlData().buildUrl(context);
  }

  /**
   * Prefix name of a `Module` that contains the action mapping for the `Action`
   * that is specified by the `action` attribute. You `must` specify an `action`
   * attribute for this to have an effect.
   * **Note:**
   * Use "" to map to the default module.
   */
  default void setModule(String module) {
    getUrlData().module = module;
  }

  /**
   * Logical name of a `Action` that contains the actual content-relative URI
   * of the destination of this transfer. This hyperlink may be dynamically
   * modified by the inclusion of query parameters, as described in the tag
   * description. You *must* specify exactly one of the `action` attribute,
   * the `forward` attribute, the `href` attribute, the `linkName` attribute,
   * or the `page` attribute.
   * Additionally, you can specify a `module` prefix for linking to other
   * modules.
   */
  default void setAction(String action) {
    getUrlData().action = action;
  }

  /**
   * Logical name of a global `ActionForward` that contains the actual
   * content-relative URI of the destination of this transfer. This hyperlink
   * may be dynamically modified by the inclusion of query parameters, as
   * described in the tag description. You **must** specify exactly one of the
   * `action` attribute, the `forward` attribute, the `href` attribute, the
   * `linkName` attribute, or the `page` attribute.
   */
  default void setForward(String forward) {
    getUrlData().forward = forward;
  }

  /**
   * The name of a JSP bean that contains a `Map` representing the query
   * parameters (if `property` is not specified), or a JSP bean whose property
   * getter is called to return a `Map` (if `property` is specified).
   */
  default void setName(String name) {
    getUrlData().name = name;
  }

  /**
   * The name of a property of the bean specified by the `name` attribute,
   * whose return value must be `java.util.Map` containing the query parameters
   * to be added to the hyperlink. You **must** specify the `name` attribute if
   * you specify this attribute.
   */
  default void setProperty(String property) {
    getUrlData().property = property;
  }

  /**
   * The name of the request parameter that will be dynamically added to the
   * generated hyperlink. The corresponding value is defined by the `paramName`
   * and (optional) `paramProperty` attributes, optionally scoped by the
   * `paramScope` attribute.
   */
  default void setParamId(String paramId) {
    getUrlData().paramId = paramId;
  }

  /**
   * The name of a JSP bean that is a String containing the value for the
   * request parameter named by `paramId` (if `paramProperty` is not
   * specified), or a JSP bean whose property getter is called to return a
   * String (if `paramProperty` is specified). The JSP bean is constrained to
   * the bean scope specified by the `paramScope` property, if it is specified.
   */
  default void setParamName(String paramName) {
    getUrlData().paramName = paramName;
  }

  /**
   * The name of a property of the bean specified by the `paramName` attribute,
   * whose return value must be a String containing the value of the request
   * parameter (named by the `paramId` attribute) that will be dynamically
   * added to this hyperlink.
   */
  default void setParamProperty(String paramProperty) {
    getUrlData().paramProperty = paramProperty;
  }

  /**
   * The module-relative path (beginning with a "/" character) to which this
   * hyperlink will transfer control if activated. This hyperlink may be
   * dynamically modified by the inclusion of query parameters, as described
   * in the tag description. You *must* specify exactly one of the `action`
   * attribute, `forward` attribute, the `href` attribute, the `linkName`
   * attribute, or the `page` attribute.
   */
  default void setPage(String page) {
    getUrlData().page = page;
  }

  /**
   * The scope within which to search for the bean specified by the `paramName`
   * attribute. If not specified, all scopes are searched.
   */
  default void setParamScope(String paramScope) {
    throw new UnsupportedOperationException();
  }

  /**
   * The scope within which to search for the bean specified by the `name`
   * attribute. If not specified, all scopes are searched.
   */
  default void setScope(String scope) {
    throw new UnsupportedOperationException();
  }

  /**
   * If set to `true`, LocalCharacterEncoding will be used, that is, the
   * `characterEncoding` set to the `HttpServletResponse`, as preferred
   * character encoding rather than UTF-8, when `URLEncoding` is done on
   * parameters of the URL.
   */
  default void setUseLocalEncoding(boolean useLocalEncoding) {
    getUrlData().useLocalEncoding = useLocalEncoding;
  }

  default void setAwareNestedTag(boolean awareNestedTag) {
    getUrlData().awareNestedTag = awareNestedTag;
  }

  UrlData getUrlData();

  class UrlData {
    @Nullable String module;
    @Nullable String action;
    @Nullable String page;
    @Nullable String forward;
    @Nullable String name;
    @Nullable String property;
    @Nullable String paramId;
    @Nullable String paramName;
    @Nullable String paramProperty;
    boolean useLocalEncoding = false;
    boolean awareNestedTag = false;

    String buildUrl(PageContext pageContext) {
      if (forward != null) {
        var forwardConfig = ModuleUtils.getCurrent().findForwardConfig(forward);
        if (forwardConfig == null) throw new IllegalStateException(
            "Unknown forward name: " + forward + "."
        );
        return forwardConfig.getUrl();
      }
      var relPath = buildRelPath();
      var modulePath = buildModulePath(pageContext);
      var path = normalizeForwardPath(modulePath + "/" + relPath);
      var uri = ServletUriComponentsBuilder.fromUriString(path);
      if (name != null) {
        var bindStatus = StrutsDataBinding.onScope(
          pageContext, name, property, awareNestedTag
        );
        var propsMap = bindStatus.getValueAsMap();
        propsMap.forEach((key, value) -> {
          if (value instanceof Object[] arrayValue) {
            uri.replaceQueryParam(key, arrayValue);
          }
          else {
            uri.replaceQueryParam(key, value);
          }
        });
      }
      if (paramId != null) {
        var valuePath = (paramName == null) ? paramId
            : (paramProperty == null) ? paramName
            : (paramName + "." + paramProperty);

        var bindStatus = StrutsDataBinding.onScope(
          pageContext,
          paramName == null ? paramId : paramName,
          paramProperty,
          awareNestedTag
        );
        var value = bindStatus.getValue();
        if (value instanceof Object[] arrayValue) {
          uri.replaceQueryParam(paramId, arrayValue);
        }
        else {
          uri.replaceQueryParam(paramId, value);
        }
      }
      var charset = getCharset(pageContext);
      return uri.build().encode(charset).toUriString();
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
      if (action != null) {
        return action;// + ".do";
      }
      throw new IllegalArgumentException(
          "Failed to determine the relative url of this link tag."
      );
    }

    private String buildModulePath(PageContext pageContext) {
      if (module == null) {
        return ModuleUtils.getCurrent().getPrefix();
      }
      var config =  ModuleUtils.getInstance().getModuleConfig(module, pageContext.getServletContext());
      if (config == null) throw new IllegalArgumentException(
          "Unknown module prefix: " + module
      );
      return config.getPrefix();
    }
  }
}