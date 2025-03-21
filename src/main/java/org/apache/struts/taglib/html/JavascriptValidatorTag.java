package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.StringUtils.lowerCamelize;

/**
 * Render JavaScript validation based on the validation rules loaded by the
 * `ValidatorPlugIn`.
 * The set of validation rules that should be generated is based on the
 * `formName` attribute passed in, which should match the name attribute of the
 * form element in the xml file.
 * The dynamicJavascript and staticJavascript attributes default to true, but
 * if dynamicJavascript is set to `true` and staticJavascript is set to `false`
 * then only the dynamic JavaScript will be rendered. If dynamicJavascript is
 * set to `false` and staticJavascript is set to `true` then only the static
 * JavaScript will be rendered which can then be put in separate JSP page so
 * the browser can cache the static JavaScript.
 */
public class JavascriptValidatorTag extends StrutsHtmlElementTagBase {

  public JavascriptValidatorTag() {
    init();
  }

  @Override
  protected String getTagName() {
    return "script";
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    processesBodyContent();
    writesRowBodyContent();
    cdata = true;
    dynamicJavascript = true;
    formName = null;
    methodName = null;
    page = 0;
    scriptLanguage = true;
    src = null;
    staticJavascript = true;
    htmlComment = true;
  }

  private boolean cdata;
  private boolean dynamicJavascript;
  private @Nullable String formName;
  private @Nullable String methodName;
  private int page;
  private boolean scriptLanguage;
  private @Nullable String src;
  private boolean staticJavascript;
  private boolean htmlComment;

  @Override
  protected boolean emitsEnclosingTag() {
    return hasText(formName);
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs =  super.getAdditionalAttributes();
    attrs.put("type", "text/javascript");
    return attrs;
  }

  @Override
  protected String getBodyTextForOutput() {
    var buff = new StringBuilder();
    var form = getForm();
    var validatorActions =
      (form == null) ? getValidatorResources().getValidatorActions().values()
                     : getValidatorActionsFor(form);
    if (dynamicJavascript) {
      if (form == null) throw new IllegalArgumentException(
        "formName property is required if dynamicJavascript is true in <html:javascript> tag."
      );
      writeFormValidatorScript(form, validatorActions, buff);
    }
    if (staticJavascript) {
      writeFieldValueValidatorScripts(validatorActions, buff);
      writeValidationUtilityScripts(buff);
    }
    return buff.toString();
  }

  private @Nullable Form getForm() {
    if (!hasText(formName)) {
      return null;
    }
    return getValidatorResources().getForm(formName);
  }

  private ValidatorResources getValidatorResources() {
    return ModuleUtils.getCurrent().getValidatorResources();
  }

  private Collection<ValidatorAction> getValidatorActionsFor(Form form) {
    var validatorActionByName = new HashMap<String, ValidatorAction>();
    var validatorResources = getValidatorResources();
    for (var field : form.getFields()) {
      for (var actionName : field.getDependencyList()) {
        validatorActionByName.computeIfAbsent(
          actionName,
          validatorResources::getValidatorAction
        );
      }
    }
    return validatorActionByName.values();
  }

  private void writeFieldValueValidatorScripts(
    Collection<ValidatorAction> validatorActions,
    StringBuilder buff
  ) {
    for (var validatorAction : validatorActions) {
      var script = validatorAction.getJavascript();
      if (script == null) {
        continue;
      }
      buff.append("\n");
      buff.append(script);
    }
  }

  private void writeValidationUtilityScripts(
    StringBuilder buff
  ) {
    if (!staticJavascript) {
      return;
    }
    for (var util : getValidatorResources().getJsUtilities()) {
      buff.append("\n");
      buff.append(util.getJavascript());
    }
  }

  private void writeFormValidatorScript(
    Form form,
    Collection<ValidatorAction> validatorActions,
    StringBuilder buff
  ) {
    buff.append(format(join("\n", "",
      "let bCancel = false;",
      "",
      "function %s(form) {",
      "  if (bCancel) {",
      "    return true;",
      "  }",
      "  return %s;",
      "}"
      ),
      getValidatorMethodName(),
      getFormValidationMethodBody(validatorActions)
    ));
    for (var validatorAction : validatorActions) {
      buff.append("\n");
      buff.append(format(
        "function %s_%s() {", formName, validatorAction.getJsFunctionName()
      ));
      var fields = form.getFields();
      for (int i = 0; i < fields.size(); i++) {
        var field = fields.get(i);
        if (field.isIndexed()) {
          continue;
        }
        if (field.getPage() != page || !field.isDependency(validatorAction.getName())) {
          continue;
        }
        var message = field.getMessageFor(validatorAction, getBundle());
        buff.append(format(join("\n", "",
          "  this.%s = [",
          "    '%s',",
          "    '%s',",
          "    (varName) => {"
        ), ("a" + i), field.getKey(), escapeJsString(message)));
        field.getVars().forEach((name, var) -> {
          buff.append(format(join("\n", "",
          "      if ('%s' === varName) {",
          "        return %s;",
          "      }"
          ), name, var.getVarValueAsJsLiteral()));
        });
        buff.append(join("\n", "",
          "      return null;",
          "    }",
          "  ];"
        ));
      }
      buff.append("\n");
      buff.append("}");
    }
  }

  private @Nullable String escapeJsString(@Nullable String message) {
    if (!hasText(message)) {
      return message;
    }
    return message
      .replace("\\", "\\\\")
      .replace("\"", "\\\"");
  }

  private String getValidatorMethodName() {
    return requireNonNullElse(
      methodName,
      lowerCamelize("validate", formName)
    );
  }

  private String getFormValidationMethodBody(
    Collection<ValidatorAction> validatorActions
  ) {
    if (validatorActions.isEmpty()) {
      return "true";
    }
    return validatorActions.stream().filter(it -> hasText(it.getJavascript())).map(
      it -> it.getMethod() + "(form)"
    ).collect(joining("\n    && "));
  }

  /**
   * If set to `true` and XHTML has been enabled, the JavaScript will be
   * wrapped in a CDATA section to prevent XML parsing. The default is `true`
   * to comply with the W3C's recommendation.
   */
  public void setCdata(boolean cdata) {
    this.cdata = cdata;
  }

  /**
   * Indicates whether to render the dynamic JavaScript.
   * Defaults to `true`.
   */
  public void setDynamicJavascript(boolean dynamicJavascript) {
    this.dynamicJavascript = dynamicJavascript;
  }

  /**
   * The key (form name) to retrieve a specific set of validation rules.
   * If `dynamicJavascript` is set to `true` and formName is missing or is not
   * recognized by the `ValidatorPlugIn`, a JspException will be thrown.
   * Specifying a form name places script tag around the javascript.
   */
  public void setFormName(String formName) {
    this.formName = formName;
  }

  /**
   * The alternate JavaScript method name to be used. The default is 'validate'
   * concatenated in front of the key (form name) passed in
   * (ex: validateRegistrationForm).
   */
  public void setMethod(String method) {
    this.methodName = method;
  }

  /**
   * The current page of a set of validation rules if the page attribute for
   * the field element in the xml file is in use.
   */
  public void setPage(int page) {
    this.page = page;
  }

  /**
   * The `script` element will not contain a language attribute when this is
   * set to false.
   * The default is true but this property is ignored in XHTML mode.
   */
  public void setScriptLanguage(boolean scriptLanguage) {
    this.scriptLanguage = scriptLanguage;
  }

  /**
   * The src attribute's value when defining the html script element.
   */
  public void setSrc(String src) {
    this.src = src;
  }

  /**
   * Indicates whether to render the static JavaScript.
   * Defaults to `true`.
   */
  public void setStaticJavascript(boolean staticJavascript) {
    this.staticJavascript = staticJavascript;
  }

  /**
   * Indicates whether to enclose the javascript with HTML comments. This
   * attribute is ignored in XHTML mode because the script would be deleted by
   * the XML parser. See the cdata attribute for details on hiding scripts from
   * XML parsers. Defaults to `true`.
   */
  public void setHtmlComment(boolean htmlComment) {
    this.htmlComment = htmlComment;
  }
}
