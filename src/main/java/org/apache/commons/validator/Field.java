package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;
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

  public Form getForm() {
    return form;
  }
  @JsonBackReference
  private Form form;

  public @Nullable Object getValueOf(@Nullable Object bean) {
    if (bean == null) return null;
    return retrieveValue(bean, property);
  }

  /**
   * The Field's property name.
   */
  public String getProperty() {
    return property;
  }
  private final String property;

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
    return args;
  }

  private List<Arg> commonArgs() {
    var args = argsByValidatorName.get("");
    return (args == null) ? new ArrayList<>() : new ArrayList<>(args);
  }

  @JacksonXmlProperty(localName = "arg")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setArgs(List<Arg> args) {
    argsByValidatorName.clear();
    for (var arg : args) {
      argsByValidatorName.add(arg.getName(), arg);
    }
  }
  private final MultiValueMap<String, Arg>
    argsByValidatorName = new LinkedMultiValueMap<>();

  /**
   * Retrieve a variable's value.
   */
  public @Nullable String getVarValue(String varName) {
    var aVar = varsByName.get(varName);
    if (aVar == null) {
      return null;
    }
    return aVar.getVarValue();
  }

  public String getRequiredVarValue(String varName) {
    var aVar = varsByName.get(varName);
    if (aVar == null) throw new IllegalArgumentException(String.format(
      "The var [%s] is required but is not assigned to the validation config for field [%s] of form [%s].",
      varName, property, form.getName()
    ));
    return aVar.getVarValue();
  }

  public int getRequiredVarNumber(String varName) {
    var varValue = getRequiredVarValue(varName);
    return Integer.parseInt(varValue);
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
  private List<Msg> messages = new ArrayList<>();

  /**
   * The Page Number.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "page")
  private @Nullable Integer page;

  /**
   * The Field's indexed list property name.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "indexedListProperty")
  private @Nullable String indexedListProperty;


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
