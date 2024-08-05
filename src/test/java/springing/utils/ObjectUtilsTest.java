package springing.utils;

import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static springing.util.ObjectUtils.createInstanceOf;

public class ObjectUtilsTest {

  @Test
  void createInstance_ItIgnoresEntriesInPropsWhichAreNotContainedTheBean() throws Exception {
    var props = Map.of("stringProperty", "STRING VALUE", "unknown", "UNKNOWN");
    var fqn = TestBean.class.getCanonicalName();
    var bean = (TestBean) createInstanceOf(fqn, props);
    assertEquals("STRING VALUE", bean.getStringProperty());
  }

  @Test
  void createInstance_ItCorrectlyHandlesCheckboxCancellation() throws Exception {
    var props = Map.of("booleanProperty", "on");
    var fqn = TestBean.class.getCanonicalName();
    var bean = (TestBean) createInstanceOf(fqn, props);
    assertTrue(bean.getBooleanProperty());
    props = Map.of("_booleanProperty", "on");
    bean = (TestBean) createInstanceOf(fqn, props);
    assertFalse(bean.getBooleanProperty());
  }
}
