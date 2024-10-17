package org.apache.struts.validator;

import org.apache.commons.beanutils.DefaultDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MapBackedDynaBean;
import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.lang.Nullable;
import springing.struts1.validator.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class ValidationUtilsTest {
  @Autowired
  private TestApp app;

  @Test
  void itBindsHttpRequestParametersToAJavaBean() throws Exception {
    var request = app.createRequest(GET, "/validator/validateI18nExample", mock -> {
      mock.setParameters(Map.of(
        "language", "ja",
        "alternativeLanguages", new String[]{"en", "fr"}
      ));
    });
    var bean = new LocaleData();
    ValidationUtils.bindRequest(request, bean);
    assertEquals("ja", bean.getLanguage());
    assertEquals(2, bean.getAlternativeLanguages().size());
    assertEquals("fr", bean.getAlternativeLanguages().get(1));
  }

  static class LocaleData {
    public @Nullable String getLanguage() {
      return language;
    }
    public void setLanguage(String language) {
      this.language = language;
    }
    private @Nullable String language;

    public List<String> getAlternativeLanguages() {
      return alternativeLanguages;
    }
    public void setAlternativeLanguages(List<String> alternativeLanguages) {
      this.alternativeLanguages = alternativeLanguages;
    }
    private List<String> alternativeLanguages;
  }

  @Test
  void itBindsHttpRequestParametersToADynaBean() throws Exception {
    var request = app.createRequest(GET, "/validator/validateI18nExample", mock -> {
      mock.setParameters(Map.of(
          "language", "ja",
          "alternativeLanguages", new String[]{"en", "fr"}
      ));
    });
    var bean = new LocaleDynaBean();
    ValidationUtils.bindRequest(request, bean);
    if (bean.get("language") instanceof String language) {
      assertEquals("ja", language);
    }
    else {
      fail();
    }
    if (bean.get("alternativeLanguages") instanceof List<?> list) {
      assertEquals(2, list.size());
      assertEquals("fr", list.get(1));
    }
    else {
      fail();
    }
  }

  public static class LocaleDynaBean implements MapBackedDynaBean {
    private final DynaClass dynaClass = new DefaultDynaClass(
        "localeBean",
        List.of(
          new DynaProperty<>("language", String.class),
          new DynaProperty<>("alternativeLanguages", List.class)
        )
    );
    @Override
    public DynaClass getDynaClass() {
      return dynaClass;
    }
    @Override
    public Map<String, Object> getValues() {
      return values;
    }
    private final Map<String, Object> values = new HashMap<>();
  };
}
