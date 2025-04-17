package springing.struts1.taglib;

import javax.servlet.jsp.PageContext;
import org.springframework.lang.Nullable;

public interface UrlBuilderSupport {
  default String buildUrl(PageContext context) {
    return getUrlBuilder().buildUrl(context);
  }

  /**
   * Prefix name of a `Module` that contains the action mapping for the `Action`
   * that is specified by the `action` attribute. You `must` specify an `action`
   * attribute for this to have an effect.
   * **Note:**
   * Use "" to map to the default module.
   */
  default void setModule(String module) {
    getUrlBuilder().module = module;
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
    getUrlBuilder().action = action;
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
    getUrlBuilder().forward = forward;
  }

  /**
   * The name of a JSP bean that contains a `Map` representing the query
   * parameters (if `property` is not specified), or a JSP bean whose property
   * getter is called to return a `Map` (if `property` is specified).
   */
  default void setName(String name) {
    getUrlBuilder().name = name;
  }

  /**
   * The name of a property of the bean specified by the `name` attribute,
   * whose return value must be `java.util.Map` containing the query parameters
   * to be added to the hyperlink. You **must** specify the `name` attribute if
   * you specify this attribute.
   */
  default void setProperty(String property) {
    getUrlBuilder().property = property;
  }

  /**
   * The name of the request parameter that will be dynamically added to the
   * generated hyperlink. The corresponding value is defined by the `paramName`
   * and (optional) `paramProperty` attributes, optionally scoped by the
   * `paramScope` attribute.
   */
  default void setParamId(String paramId) {
    getUrlBuilder().paramId = paramId;
  }

  /**
   * The name of a JSP bean that is a String containing the value for the
   * request parameter named by `paramId` (if `paramProperty` is not
   * specified), or a JSP bean whose property getter is called to return a
   * String (if `paramProperty` is specified). The JSP bean is constrained to
   * the bean scope specified by the `paramScope` property, if it is specified.
   */
  default void setParamName(String paramName) {
    getUrlBuilder().paramName = paramName;
  }

  /**
   * The name of a property of the bean specified by the `paramName` attribute,
   * whose return value must be a String containing the value of the request
   * parameter (named by the `paramId` attribute) that will be dynamically
   * added to this hyperlink.
   */
  default void setParamProperty(String paramProperty) {
    getUrlBuilder().paramProperty = paramProperty;
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
    getUrlBuilder().page = page;
  }

  /**
   * The message key, in the message resources bundle named by the `bundle`
   * attribute, of the String to be used as the module-relative path for this
   * image.
   */
  default void setPageKey(String pageKey) {
    getUrlBuilder().pageKey = pageKey;
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
    getUrlBuilder().useLocalEncoding = useLocalEncoding;
  }

  /**
   * Optional anchor tag ("#xxx") to be added to the generated hyperlink.
   * Specify this value *without* any "#" character. Exactly one of `forward`,
   * `href`, or `page` attribute must still be specified with the anchor.
   */
  default void setAnchor(String anchor) {
    getUrlBuilder().anchor = anchor;
  }

  /**
   * The URL to which this hyperlink will transfer control if activated. This
   * hyperlink may be dynamically modified by the inclusion of query
   * parameters, as described in the tag description. You *must* specify
   * exactly one of the `action` attribute, the `forward` attribute, the `href`
   * attribute, the `linkName` attribute, or the `page` attribute.
   */
  default void setHref(String href) {
    throw new UnsupportedOperationException();
  }

  /**
   * Valid only inside `logic:iterate` tag. If `true` then indexed parameter
   * with name from indexId attribute will be added to the query string.
   * Indexed parameter looks like `"index[32]"`. Number in brackets will be
   * generated for every iteration and taken from ancestor `logic:iterate` tag.
   */
  default void setIndexed(boolean indexed) {
    throw new UnsupportedOperationException();
  }

  /**
   * By this attribute different name for the indexed parameter can be
   * specified.Take a look to the "indexed" attribute for details.
   */
  default void setIndexId(String indexId) {
    throw new UnsupportedOperationException();
  }

  /**
   * The anchor name to be defined within this page, so that you can reference
   * it with intra-page hyperlinks. In other words, the value specified here
   * will render a "name" element in the generated anchor tag.
   */
  default void setLinkName(String linkName) {
    throw new UnsupportedOperationException();
  }

  /**
   * If set to `true`, any current transaction control token will be included
   * in the generated hyperlink, so that it will pass an `isTokenValid()` test
   * in the receiving Action.
   */
  default void setTransaction(boolean transaction) {
    throw new UnsupportedOperationException();
  }

  default void setBundle(String bundle) {
    getUrlBuilder().bundle = bundle;
  }

  default void setAwareNestedTag(boolean awareNestedTag) {
    getUrlBuilder().awareNestedTag = awareNestedTag;
  }

  default void addParam(String key, @Nullable Object value) {
    getUrlBuilder().additionalParams.put(key, value);
  }

  UrlBuilder getUrlBuilder();
}
