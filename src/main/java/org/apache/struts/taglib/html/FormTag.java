package org.apache.struts.taglib.html;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts.util.ModuleUtils;
import springing.struts1.taglib.DelegatingHtmlElementTagBase;

/**
 * Define An Input Form.
 * Renders an HTML `<form>` element whose contents are described by the body
 * content of this tag. The form implicitly interacts with the specified
 * request scope or session scope bean to populate the input fields with the
 * current property values from the bean.
 * The form bean is located, and created if necessary, based on the form bean
 * specification for the associated `ActionMapping`.
 */
public class FormTag extends DelegatingHtmlElementTagBase<org.springframework.web.servlet.tags.form.FormTag> {
  public FormTag() {
    super(new org.springframework.web.servlet.tags.form.FormTag());
  }

  /**
   * The URL to which this form will be submitted. This value is also used to
   * select the `ActionMapping` we are assumed to be processing, from which we
   * can identify the appropriate form bean and scope. If a value is not
   * provided, the original URI (servletPath) for the request is used.
   *  If you are using extension mapping for selecting the controller servlet,
   * this value should be equal to the `path` attribute of the corresponding
   * `<action>` element, optionally followed by the correct extension suffix.
   *  If you are using path mapping to select the controller servlet, this
   * value should be exactly equal to the `path` attribute of the corresponding
   * `<action>` element.
   */
  public void setAction(String action) {
    var moduleConfig = ModuleUtils.getCurrent();
    var actionConfig = moduleConfig.findActionConfig(action);
    if (actionConfig == null) {
      getBaseTag().setAction(action);
      return;
    }
    getBaseTag().setAction(actionConfig.getActionUrl());
    var formBeanConfig = actionConfig.getFormBeanConfig();
    if (formBeanConfig == null) {
      return;
    }
    var request = (HttpServletRequest) getPageContext().getRequest();
    formBeanConfig.prepare(request);
  }

  /**
   * The HTTP method that will be used to submit this request (GET, POST).
   * Default: [POST]
   */
  public void setMethod(String method) {
    getBaseTag().setMethod(method);
  }

  /**
   * The content encoding to be used to submit this form, if the method is
   * POST. This must be set to "multipart/form-data" if you are using the file
   * tag to enable file upload. If not specified, the browser default (normally
   * "application/x-www-form-urlencoded") is used.
   */
  public void setEnctype(String enctype) {
    getBaseTag().setEnctype(enctype);
  }

  /**
   * The field name (among the fields on this form) to which initial focus will
   * be assigned with a JavaScript function. If not specified, no special
   * JavaScript for this purpose will be rendered.
   */
  public void setFocus(String focus) {
    throw new UnsupportedOperationException();
  }

  /**
   * If the focus field is a field array, such as a radio button group, you can
   * specify the index in the array to receive focus.
   */
  public void setFocusIndex(int focusIndex) {
    throw new UnsupportedOperationException();
  }

  /**
   * The list of character encodings for input data that the server should
   * accept.
   */
  public void setAcceptCharset(String acceptCharset) {
    getBaseTag().setAcceptCharset(acceptCharset);
  }

  /**
   * JavaScript event handler executed if the form is reset.
   */
  public void setOnreset(String onreset) {
    getBaseTag().setOnreset(onreset);
  }

  /**
   * JavaScript event handler executed if the form is submitted.
   */
  public void setOnsubmit(String onsubmit) {
    getBaseTag().setOnsubmit(onsubmit);
  }

  /**
   * Set to `true` if the Form's input fields should be disabled.
   */
  public void setDisabled(boolean disabled) {
    throw new UnsupportedOperationException();
  }

  /**
   * Set to <code>true</code> if the Form's input fields should be read only.
   */
  public void setReadonly(boolean readonly) {
    throw new UnsupportedOperationException();
  }

  /**
   * The form's focus &lt;script&gt; element will not contain a language
   * attribute when this is set to false.  The default is true but this
   * property is ignored in XHMTL mode.
   */
  public void setScriptLanguage(boolean scriptLanguage) {
    throw new UnsupportedOperationException();
  }

  /**
   * Window target to which this form is submitted, such as for use in framed
   * presentations.
   */
  public void setTarget(String target) {
    getBaseTag().setTarget(target);
  }
}
