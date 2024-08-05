package org.apache.struts;


/**
 * Global manifest constants for the entire Struts Framework.
 */
public class Globals {

  /**
   * The request attributes key under which our
   * `org.apache.struts.ActionMapping` instance is passed.
   */
  public static final String MAPPING_KEY = "org.apache.struts.action.mapping.instance";

  /**
   * The request attributes key under which a boolean `true` value should be
   * stored if this request was cancelled.
   */
  public static final String CANCEL_KEY = "org.apache.struts.action.CANCEL";

  /**
   * The request attributes key under which your action should store an
   * `org.apache.struts.action.ActionErrors` object, if you are using the
   * corresponding custom tag library elements.
   */
  public static final String ERROR_KEY = "org.apache.struts.action.ERROR";

  /**
   * The request attributes key under which your action should store an
   * `org.apache.struts.action.ActionMessages` object, if you are using the
   * corresponding custom tag library elements.
   */
  public static final String MESSAGES_KEY = "org.apache.struts.action.ACTION_MESSAGE";

  /**
   * The session attributes key under which the user's selected
   * `java.util.Locale` is stored, if any.  If no such attribute is found, the
   * system default locale will be used when retrieving internationalized
   * messages. If used, this attribute is typically set during user login
   * processing.
   */
  public static final String LOCALE_KEY = "org.apache.struts.action.LOCALE";
}
