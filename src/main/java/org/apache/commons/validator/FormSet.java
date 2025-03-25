package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

/**
 * Holds a set of Forms stored associated with a Locale based on the country,
 * language, and variant specified. Instances of this class are configured with
 * a `formset` xml element.
 */
public class FormSet {

  public FormSet() {}

  public ValidatorResources getValidatorResources() {
    return parent;
  }

  ValidatorResources parent;

  /**
   * Gets the equivalent of the language component of Locale.
   */
  public String getLanguage() {
    return language;
  }

  @JacksonXmlProperty(isAttribute = true, localName = "language")
  private String language = "";

  /**
   * Gets the equivalent of the country component of Locale.
   */
  public String getCountry() {
    return country;
  }

  @JacksonXmlProperty(isAttribute = true, localName = "country")
  private String country = "";

  /**
   * Gets the equivalent of the variant component of Locale.
   */
  public String getVariant() {
    return variant;
  }

  @JacksonXmlProperty(isAttribute = true, localName = "variant")
  private String variant = "";

  public boolean isDefaultLocale() {
    return language.isEmpty() && country.isEmpty() && variant.isEmpty();
  }

  @JacksonXmlProperty(localName = "validator")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<ValidatorAction> validatorActions = new ArrayList<>();

  public @Nullable String getConstantValue(String constantName) {
    if (constantMap.containsKey(constantName)) {
      return constantMap.get(constantName);
    }
    return parent.getConstants().get(constantName);
  }

  @JacksonXmlProperty(localName = "constant")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setConstants(List<Constant> constants) {
    constantMap.clear();
    for (var constant : constants) {
      constantMap.put(constant.getName(), constant.getValue());
    }
  }

  private final Map<String, String> constantMap = new HashMap<>();

  @JacksonXmlProperty(localName = "form")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setForms(List<Form> forms) {
    for (var form : forms) {
      formsByName.put(form.getName(), form);
    }
  }

  private final Map<String, Form> formsByName = new HashMap<>();

  /**
   * Retrieves a Form based on the form name.
   */
  public @Nullable Form getForm(String formName) {
    return formsByName.get(formName);
  }
}
