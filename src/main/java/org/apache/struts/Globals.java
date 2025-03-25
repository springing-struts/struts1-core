package org.apache.struts;

/**
 * Global manifest constants for the entire Struts Framework.
 */
public class Globals {

  private Globals() {}

  public static final String ACTION_PACKAGE = "org.apache.struts.action";

  /**
   * The request attributes key under which our
   * `org.apache.struts.ActionMapping` instance is passed.
   */
  public static final String MAPPING_KEY = ACTION_PACKAGE + ".mapping.instance";

  /**
   * The request attributes key under which a boolean `true` value should be
   * stored if this request was cancelled.
   */
  public static final String CANCEL_KEY = ACTION_PACKAGE + ".CANCEL";

  /**
   * The request attributes key under which your action should store an
   * `org.apache.struts.action.ActionErrors` object, if you are using the
   * corresponding custom tag library elements.
   */
  public static final String ERROR_KEY = ACTION_PACKAGE + ".ERROR";

  /**
   * The request attributes key under which your action should store an
   * `org.apache.struts.action.ActionMessages` object, if you are using the
   * corresponding custom tag library elements.
   */
  public static final String MESSAGE_KEY = ACTION_PACKAGE + ".ACTION_MESSAGE";

  /**
   * The base of the context attributes key under which our module
   * `MessageResources` will be stored. This will be suffixed with the actual
   * module prefix (including the leading "/" character) to form the actual
   * resources key.
   * For each request processed by the controller servlet, the
   * `MessageResources` object for the module selected by the request URI
   * currently being processed will also be exposed under this key as a request
   * attribute.
   */
  public static final String MESSAGES_KEY = ACTION_PACKAGE + ".MESSAGE";

  /**
   * The request attributes key under which Struts custom tags might store a
   * `Throwable` that caused them to report a JspException at runtime. This
   * value can be used on an error page to provide more detailed information
   * about what really went wrong.
   */
  public static final String EXCEPTION_KEY = ACTION_PACKAGE + ".EXCEPTION";

  /**
   * The session attributes key under which the user's selected
   * `java.util.Locale` is stored, if any.  If no such attribute is found, the
   * system default locale will be used when retrieving internationalized
   * messages. If used, this attribute is typically set during user login
   * processing.
   */
  public static final String LOCALE_KEY = ACTION_PACKAGE + ".LOCALE";

  /**
   * The context attributes key under which we store the mapping defined for
   * our controller servlet, which will be either a path-mapped pattern
   * `/action/*` or an extension mapped pattern `*.do`.
   */
  public static final String SERVLET_KEY = ACTION_PACKAGE + ".SERVLET_MAPPING";

  /**
   * The base of the context attributes key under which our `ModuleConfig` data
   * structure will be stored. This will be suffixed with the actual module
   * prefix (including the leading "/" character) to form the actual attributes
   * key.
   * For each request processed by the controller servlet, the `ModuleConfig`
   * object for the module selected by the request URI currently being
   * processed will also be exposed under this key as a request attribute.
   */
  public static final String MODULE_KEY = ACTION_PACKAGE + ".MODULE";

  /**
   * The context attributes key under which our `ActionServlet` instance will
   * be stored.
   */
  public static final String ACTION_SERVLET_KEY =
    ACTION_PACKAGE + ".ACTION_SERVLET";

  /**
   * The session attributes key under which our transaction token is stored,
   * if it is used.
   */
  public static final String TRANSACTION_TOKEN_KEY = ACTION_PACKAGE + ".TOKEN";

  public static final String TAGLIB_PACKAGE = "org.apache.struts.taglib.html";

  /**
   * The property under which a transaction token is reported.
   */
  public static final String TOKEN_KEY = TAGLIB_PACKAGE + ".TOKEN";

  public static final String GLOBALS_PACKAGE = "org.apache.struts.globals";

  /**
   * The page attributes key under which xhtml status is stored. This may be
   * "true" or "false". When set to true, the html tags output xhtml.
   */
  public static final String XHTML_KEY = GLOBALS_PACKAGE + ".XHTML";
}
