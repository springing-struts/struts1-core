package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.requireNonNullElse;
import static java.util.regex.Matcher.quoteReplacement;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.ObjectUtils.getSize;
import static springing.util.ObjectUtils.retrieveValue;

/**
 * This contains the list of pluggable validators to run on a field and any
 * message information and variables to perform the validations and generate
 * error messages. Instances of this class are configured with a `field` xml
 * element.
 */
public class Field {
  public Field(
    @JacksonXmlProperty(isAttribute = true, localName = "property") String property,
    @JacksonXmlProperty(isAttribute = true, localName = "depends") @Nullable String depends
  ) {
    this.property = property;
    this.depends = List.of(
      (depends == null ? "" : depends).split("\\s*,\\s*")
    );
  }

  /**
   * Run the configured validations on this field. Run all validations in the
   * depends clause over each item in turn, returning when the first one fails.
   */
  public ActionErrors validate(
    Object target,
    Map<String, ValidatorAction> actionsByName
  ) {
    var validationErrors = new ActionErrors();
    var notIncludedInCurrentPage = (target instanceof ValidatorForm vf)
      && requireNonNullElse(vf.getPage(), 0) != getPage();
    if (notIncludedInCurrentPage) {
      return validationErrors;
    }
    for (var validatorActionName : getDependencyList()) {
      var action = actionsByName.get(validatorActionName);
      if (action == null) throw new IllegalArgumentException(format(
        "Unknown validation rule [%s] for field [%s].", validatorActionName, getKey()
      ));
      var valid = isIndexed()
        ? testIndexedValue(action, target, validationErrors)
        : testValue(action, target, validationErrors);
      if (!valid) {
        return validationErrors;
      }
    }
    return validationErrors;
  }

  private boolean testValue(ValidatorAction action, Object target, ActionMessages validationErrors) {
    return action.test(target, this, validationErrors);
  }

  private boolean testIndexedValue(ValidatorAction action, Object target, ActionMessages validationErrors) {
    var list = getIndexedValueOf(target);
    var size = getSize(list);
    if (size == null) {
      return true;
    }
    for (int index = 0; index < size; index++) {
      try {
        CURRENT_INDEX.set(index);
        var isValid = action.test(target, this, validationErrors);
        if (!isValid) {
          return false;
        }
      } finally {
        CURRENT_INDEX.remove();
      }
    }
    return true;
  }

  private static final ThreadLocal<Integer> CURRENT_INDEX = new ThreadLocal<>();


  public Form getForm() {
    return form;
  }
  @JsonBackReference
  private Form form;

  public @Nullable Object getValueOf(@Nullable Object bean) {
    return (bean == null) ? null : retrieveValue(bean, getProperty());
  }

  public @Nullable Object getIndexedValueOf(@Nullable Object bean) {
    return (bean == null) ? null : retrieveValue(bean, indexedListProperty);
  }


  /**
   * Returns a unique key based on the property and indexedProperty fields.
   */
  public String getKey() {
    return (isIndexed() ? indexedListProperty + "[]." : "") + getProperty();
  }

  /**
   * The Field's property name.
   */
  public String getProperty() {
    if (isIndexed() && CURRENT_INDEX.get() != null) {
      return indexedListProperty + "[" + CURRENT_INDEX.get() + "]." + property;
    }
    return property;
  }
  private final String property;

  /**
   * Checks if the validator is listed as a dependency.
   */
  public boolean isDependency(String validatorName) {
    return getDependencyList().contains(validatorName);
  }

  /**
   * The list of validator's this field depends on.
   */
  public List<String> getDependencyList() {
    return depends;
  }
  private final List<String> depends;

  /**
   * Retrieves the Args for the given validator name.
   */
  public List<Arg> getArgs(String validatorName) {
    var args = commonArgs();
    var argsForValidator = argsByValidatorName.get(validatorName);
    if (argsForValidator != null) {
      args.addAll(argsForValidator);
    }
    return args.stream().sorted(comparingInt(Arg::getPosition)).toList();
  }

  private List<Arg> commonArgs() {
    var args = argsByValidatorName.get("");
    return (args == null) ? new ArrayList<>() : new ArrayList<>(args);
  }

  @JacksonXmlProperty(localName = "arg")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArgs(List<Arg> args) {
    for (var arg : args) {
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  @JacksonXmlProperty(localName = "arg0")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArg0(List<Arg> args) {
    for (var arg : args) {
      arg.setPosition(0);
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  @JacksonXmlProperty(localName = "arg1")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArg1(List<Arg> args) {
    for (var arg : args) {
      arg.setPosition(1);
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  @JacksonXmlProperty(localName = "arg2")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArg2(List<Arg> args) {
    for (var arg : args) {
      arg.setPosition(2);
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  @JacksonXmlProperty(localName = "arg3")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArg3(List<Arg> args) {
    for (var arg : args) {
      arg.setPosition(3);
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  @JacksonXmlProperty(localName = "arg4")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArg4(List<Arg> args) {
    for (var arg : args) {
      arg.setPosition(4);
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  private final MultiValueMap<String, Arg>
    argsByValidatorName = new LinkedMultiValueMap<>();

  /**
   * Retrieve a variable's value.
   */
  public @Nullable String getVarValue(String varName) {
    return getVarValue(varName, null);
  }

  public @Nullable String getVarValue(String varName, @Nullable String defaultValue) {
    var aVar = getVarMap().get(varName);
    if (aVar == null) {
      return defaultValue;
    }
    return aVar.getVarValue();
  }

  public String getRequiredVarValue(String varName) {
    return getRequiredVarValue(varName, null);
  }

  public String getRequiredVarValue(String varName, @Nullable String defaultValue) {
    var varValue = getVarValue(varName, defaultValue);
    if (varValue == null) throw new IllegalArgumentException(format(
      "The var [%s] is required but is not assigned to the validation config for field [%s] of form [%s].",
      varName, property, form.getName()
    ));
    return varValue;
  }

  public @Nullable Long getVarValueAsLong(String varName) {
    var varValue = getVarValue(varName);
    return varValue == null ? null : Long.parseLong(varValue);
  }

  public long getRequiredVarValueAsLong(String varName) {
    return getRequiredVarValueAsLong(varName, null);
  }

  public long getRequiredVarValueAsLong(String varName, @Nullable Long defaultValue) {
    var varValue = getRequiredVarValue(
      varName,
      defaultValue == null ? null : defaultValue.toString()
    );
    return Long.parseLong(varValue);
  }

  public @Nullable BigDecimal getVarValueAsNumber(String varName) {
    var varValue = getVarValue(varName);
    return varValue == null ? null : new BigDecimal(varValue);
  }

  public BigDecimal getRequiredVarValueAsNumber(String varName) {
    return getRequiredVarValueAsNumber(varName, null);
  }

  public BigDecimal getRequiredVarValueAsNumber(String varName, @Nullable Long defaultValue) {
    var varValue = getRequiredVarValue(
      varName,
      defaultValue == null ? null : defaultValue.toString()
    );
    return new BigDecimal(varValue);
  }

  public boolean getRequiredVarValueAsBool(String varName) {
    return getRequiredVarValueAsBool(varName, null);
  }

  public boolean getRequiredVarValueAsBool(String varName, @Nullable Boolean defaultValue) {
    var varValue = getRequiredVarValue(
      varName,
      defaultValue == null ? null : defaultValue.toString()
    );
    return Boolean.parseBoolean(varValue);
  }

  /**
   * The field's variables are returned as an unmodifiable Map.
   */
  public Map<String, Var> getVars() {
    return Collections.unmodifiableMap(getVarMap());
  }

  /**
   * Returns a Map of String Var names to Var objects.
   */
  public Map<String, Var> getVarMap() {
    return varsByName;
  }
  @JacksonXmlProperty(localName = "var")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  public void setVars(List<Var> vars) {
    varsByName.clear();
    for (var aVar : vars) {
      varsByName.put(aVar.getName(), aVar);
    }
  }
  private final Map<String, Var> varsByName = new HashMap<>();

  @JacksonXmlProperty(localName = "msg")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setMessages(List<Msg> messages) {
    for (var message : messages) {
      messagesByValidatorName.put(message.getName(), message);
    }
  }
  private final Map<String, Msg> messagesByValidatorName = new HashMap<>();

  /**
   * The Page Number. [Default: 0]
   */
  public int getPage() {
    return requireNonNullElse(page, 0);
  }
  @JacksonXmlProperty(isAttribute = true, localName = "page")
  private @Nullable Integer page;


  /**
   * If there is a value specified for the indexedProperty field then `true`
   * will be returned. Otherwise, it will be `false`.
   */
  public boolean isIndexed()  {
    return hasText(indexedListProperty);
  }

  /**
   * The Field's indexed list property name.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "indexedListProperty")
  private @Nullable String indexedListProperty;

  /**
   * Returns the validation error message for this field concerning the given
   * validator action.
   */
  public String getMessageFor(
    ValidatorAction validatorAction,
    @Nullable String bundle
  ) {
    var actionMessage = getActionMessageFor(validatorAction);
    return actionMessage.getText(bundle);
  }

  public ActionMessage getActionMessageFor(ValidatorAction validatorAction) {
    var validatorName = validatorAction.getName();
    var args = getArgs(validatorAction.getName()).stream().map(Arg::getText).toArray();
    var fieldMessage = messagesByValidatorName.get(validatorName);
    if (fieldMessage != null) {
      return fieldMessage.getActionMessage(args);
    }
    return new ActionMessage(validatorAction.getMsg(), args);
  }

  /**
   * Performs string interpolation by replacing placeholders in the input
   * string with corresponding values.
   */
  public String interpolate(@Nullable String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }
    return INTERPOLATION_PLACEHOLDER.matcher(value).replaceAll(m -> {
      var type = m.group(1);
      var name = m.group(2);
      if (type == null || type.isEmpty()) {
        var v = form.getFormSet().getConstantValue(name);
        return v == null ? "" : quoteReplacement(v);
      }
      if (type.equals("var:")) {
        var v = getVarValue(name);
        return v == null ? "" : quoteReplacement(v);
      }
      return "";
    });
  }
  private static final Pattern INTERPOLATION_PLACEHOLDER = Pattern.compile(
    "\\$\\{(var:)?([_0-9a-zA-Z]+)}"
  );
}
