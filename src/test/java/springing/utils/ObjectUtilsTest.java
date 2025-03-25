package springing.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static springing.util.ObjectUtils.createInstanceOf;
import static springing.util.ObjectUtils.retrieveValue;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DefaultDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;

public class ObjectUtilsTest {

  @Test
  void createInstance_ItIgnoresEntriesInPropsWhichAreNotContainedTheBean()
    throws Exception {
    var props = Map.of("stringProperty", "STRING VALUE", "unknown", "UNKNOWN");
    var fqn = TestBean.class.getCanonicalName();
    var bean = (TestBean) createInstanceOf(fqn, props);
    assertEquals("STRING VALUE", bean.getStringProperty());
  }

  @Test
  void createInstance_ItAcceptsNullValueProperty() throws Exception {
    var props = new HashMap<String, Object>() {
      {
        put("stringProperty", null);
      }
    };
    var fqn = TestBean.class.getCanonicalName();
    var bean = (TestBean) createInstanceOf(fqn, props);
    assertThat(bean.getStringProperty()).isNull();
  }

  @Test
  void createInstance_ItCorrectlyHandlesCheckboxCancellation()
    throws Exception {
    var props = Map.of("booleanProperty", "on");
    var fqn = TestBean.class.getCanonicalName();
    var bean = (TestBean) createInstanceOf(fqn, props);
    assertTrue(bean.getBooleanProperty());
    props = Map.of("_booleanProperty", "on");
    bean = (TestBean) createInstanceOf(fqn, props);
    assertFalse(bean.getBooleanProperty());
  }

  @Test
  void retrieveValue_ItRetrievesValuesFromDynaBean() throws Exception {
    var bean = new DefaultDynaBean(
      "TestBean",
      new DynaProperty("stringProp", String.class),
      new DynaProperty("integerProp", Integer.class),
      new DynaProperty("arrayProp", String[].class)
    );
    bean.set("stringProp", "stringValue");
    bean.set("integerProp", 42);
    bean.set("arrayProp", new String[] { "item@0", "item@1" });
    assertEquals("stringValue", retrieveValue(bean, "stringProp"));
    assertEquals(42, retrieveValue(bean, "integerProp"));
    assertEquals("item@0", retrieveValue(bean, "arrayProp[0]"));
    assertEquals("item@1", retrieveValue(bean, "arrayProp[1]"));
  }
}
