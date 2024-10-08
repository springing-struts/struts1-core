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
  public static final String MESSAGES_KEY = ACTION_PACKAGE + ".ACTION_MESSAGE";

  /**
   * The session attributes key under which the user's selected
   * `java.util.Locale` is stored, if any.  If no such attribute is found, the
   * system default locale will be used when retrieving internationalized
   * messages. If used, this attribute is typically set during user login
   * processing.
   */
  public static final String LOCALE_KEY = ACTION_PACKAGE + ".LOCALE";


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

}
