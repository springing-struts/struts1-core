package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This contains a set of validation rules for a form/JavaBean. The information
 * is contained in a list of Field objects. Instances of this class are
 * configured with a `form` xml element.
 */
public class Form {
  public Form(
    @JacksonXmlProperty(isAttribute = true, localName = "name") String name
  ) {
    this.name = name;
  }

  public FormSet getFormSet() {
    return formSet;
  }
  @JsonBackReference
  private FormSet formSet;

  /**
   * The name/key of the set of validation rules.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The name/key of the parent set of validation rules.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "extends")
  private @Nullable String baseFormName;

  /**
   * The list of Fields contained in this form.
   */
  public List<Field> getFields() {
    if (commonSettings.isEmpty()) {
      return fieldByName.values().stream().toList();
    }
    var fields = new HashMap<String, Field>();
    for (var commonSetting : commonSettings) {
      fields.putAll(commonSetting.fieldByName);
    }
    fields.putAll(fieldByName);
    return fields.values().stream().toList();
  }

  @JacksonXmlProperty(localName = "field")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private void setFields(List<Field> fields) {
    fieldByName.clear();
    for (var field : fields) {
      fieldByName.put(field.getProperty(), field);
    }
  }
  public Field getFieldByName(String name) {
    return fieldByName.get(name);
  }
  private final Map<String, Field> fieldByName = new HashMap<>();

  public void setCommonSettings(Form... commonSettings) {
    commonSettings.clone();
    for (var commonSetting: commonSettings) {
      if (commonSetting != null) {
        this.commonSettings.add(commonSetting);
      }
    }
  }
  private final List<Form> commonSettings = new ArrayList<>();
}
