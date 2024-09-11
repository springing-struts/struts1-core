package org.apache.commons.validator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.lang.Nullable;
import springing.util.ServletRequestUtils;

import java.util.*;

/**
 * General purpose class for storing FormSet objects based on their associated
 * Locale. Instances of this class are usually configured through a
 * `validation.xml` file that is parsed in a constructor.
 */
@JacksonXmlRootElement(localName = "form-validation")
public class ValidatorResources {

  @JacksonXmlProperty(localName = "formset")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<FormSet> validationFormSetList = new ArrayList<>();

  @JacksonXmlProperty(localName = "global")
  private GlobalDefinitions globalDefinitions = new GlobalDefinitions();

  /**
   * Gets an Map of the ValidatorActions.
   */
  public Map<String ,ValidatorAction> getValidatorActions() {
    return globalDefinitions.validatorActionsByName;
  }

  /**
   * Returns a Map of String constant names to their String values.
   */
  public Map<String, String> getConstants() {
    return globalDefinitions.constantMap;
  }

  public static class GlobalDefinitions {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "constant")
    private void setConstants(List<Constant> constants) {
      for (var constant: constants) {
        constantMap.put(constant.getName(), constant.getValue());
      }
    }
    private final Map<String, String> constantMap = new HashMap<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "validator")
    private void setValidatorActions(List<ValidatorAction> validators) {
      for (var validator : validators) {
        validatorActionsByName.put(validator.getName(), validator);
      }
    }
    private final Map<String, ValidatorAction> validatorActionsByName = new HashMap<>();
  }

  /**
   * Merges the content of the given resources into this instance.
   */
  public void merge(ValidatorResources another) {
    validationFormSetList.addAll(another.validationFormSetList);
    another.validationFormSetList.forEach(it -> it.parent = this);
    globalDefinitions.constantMap.putAll(another.globalDefinitions.constantMap);
    globalDefinitions.validatorActionsByName.putAll(another.globalDefinitions.validatorActionsByName);
  }

  public @Nullable Form getForm(String formKey) {
    return getForm(ServletRequestUtils.getCurrent().getLocale(), formKey);
  }

  /**
   * Gets a Form based on the name of the form and the Locale that most closely
   * matches the Locale passed in. The order of Locale matching is:
   * - language + country + variant
   * - language + country
   * - language
   * - default locale
   */
  public @Nullable Form getForm(Locale locale, String formKey) {
    return getForm(
      locale.getLanguage(),
      locale.getCountry(),
      locale.getVariant(),
      formKey
    );
  }

  /**
   * Gets a Form based on the name of the form and the Locale that most closely
   * matches the Locale passed in. The order of Locale matching is:
   * - language + country + variant
   * - language + country
   * - language
   * - default locale
   */
  private @Nullable Form getForm(
    String language, String country, String variant, String formKey
  ) {
    Form forDefaultLocale = null;
    Form forLanguage = null;
    Form forCounty = null;
    Form forVariant = null;

    for (var formSet : validationFormSetList) {
      if (formSet.isDefaultLocale() && forDefaultLocale == null) {
        var form = formSet.getForm(formKey);
        if (form != null) {
          forDefaultLocale = form;
          continue;
        }
      }
      if (formSet.getLanguage().equals(language)) {
        if (forLanguage == null && formSet.getCountry().isEmpty()) {
          var form = formSet.getForm(formKey);
          if (form != null) {
            forLanguage = form;
            continue;
          }
        }
        if (formSet.getCountry().equals(country)) {
          if (forCounty == null && formSet.getVariant().isEmpty()) {
            var form = formSet.getForm(formKey);
            if (form != null) {
              forCounty = form;
              continue;
            }
          }
          if (forVariant == null && formSet.getVariant().equals(variant)) {
            var form = formSet.getForm(formKey);
            if (form != null) {
              forVariant = form;
            }
          }
        }
      }
    }
    if (forVariant != null) {
      forVariant.setCommonSettings(forCounty, forLanguage, forDefaultLocale);
      return forVariant;
    }
    if (forCounty != null) {
      forCounty.setCommonSettings(forLanguage, forDefaultLocale);
      return forCounty;
    }
    if (forLanguage != null) {
      forLanguage.setCommonSettings(forDefaultLocale);
      return forLanguage;
    }
    return forDefaultLocale;
  }
}
