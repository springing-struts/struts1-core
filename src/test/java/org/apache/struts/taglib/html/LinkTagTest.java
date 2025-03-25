package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

import java.util.Map;
import org.apache.struts.TestApp;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;

@WebMvcTest
public class LinkTagTest {

  @Autowired
  private TestApp app;

  /**
   *  <html:link action="/html-link" module="/exercise">
   */
  @Test
  void testItCanDetermineLinkUrlFromModuleNameAndActionName() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (linkTag, context) -> {
        linkTag.setModule("/exercise");
        linkTag.setAction("/html-link");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(content, containsString("href=\"/exercise/html-link\""));
      }
    );
  }

  /**
   * <html:link page="/welcome.do" module=""></html:link>
   * <html:link page="/welcome.do" module="/"></html:link>
   * <html:link page="/welcome.do" module="/validator"></html:link>
   */
  @Test
  void testItCanDetermineLinkUrlFromModuleNameAndPage() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (linkTag, context) -> {
        linkTag.setPage("/welcome.do");
        linkTag.setModule("");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(content, containsString("href=\"/welcome.do\""));
      }
    );
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (linkTag, context) -> {
        linkTag.setPage("/welcome.do");
        linkTag.setModule("/");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(content, containsString("href=\"/welcome.do\""));
      }
    );
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (linkTag, context) -> {
        linkTag.setPage("/welcome.do");
        linkTag.setModule("/validator");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(content, containsString("href=\"/validator/welcome.do\""));
      }
    );
  }

  /**
   * <html:link action="/html-link"
   *   paramId="booleanProperty"
   *   paramName="testbean"
   *   paramProperty="nested.booleanProperty"
   *   module="/exercise">
   * </html:link>
   */
  @Test
  void testItCanDetermineLinkUrlBasedOnARequestParameter() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        var testBean = Map.of("nested", Map.of("booleanProperty", true));
        context.getRequest().setAttribute("testBean", testBean);
        tag.setAction("/html-link");
        tag.setParamId("booleanProperty");
        tag.setParamName("testBean");
        tag.setParamProperty("nested.booleanProperty");
        tag.setModule("/exercise");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(
          content,
          containsString("href=\"/exercise/html-link?booleanProperty=true\"")
        );
      }
    );
  }

  /**
   *  <html:link
   *    action="/html-link-submit"
   *    name="newValues">
   *  </html:link>
   */
  @Test
  void testItCanAppendMapAsQueryParameters() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        var newValues = Map.of(
          "floatProperty",
          444.0f,
          "intProperty",
          555,
          "stringArray",
          new String[] { "Value 1", "Value&2", "Value/3" }
        );
        context.getRequest().setAttribute("newValues", newValues);
        tag.setAction("/html-link-submit");
        tag.setName("newValues");
      },
      (content, processedBody) -> {
        assertThat(
          content,
          containsString("href=\"/exercise/html-link-submit?")
        );
        assertThat(content, containsString("floatProperty=444.0"));
        assertThat(content, containsString("intProperty=555"));
        assertThat(content, containsString("stringArray=Value%201"));
        assertThat(content, containsString("stringArray=Value%262"));
        assertThat(content, containsString("stringArray=Value/3"));
      }
    );
  }

  /**
   * <global-forwards>
   *   <forward name="relative" path="/welcome.do"/>
   * </global-forwards>
   * <html:link forward="relative"></html:link>
   */
  @Test
  void testItCanRenderLinkCorrespondingToGlobalForwardName() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        var request = (MockHttpServletRequest) context.getRequest();
        request.setServletPath("/exercise/action.do");
        tag.setForward("relative");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(content, containsString("href=\"/exercise/welcome.do\""));
      }
    );
  }

  /**
   *  <html:link
   *    action="/html-link-submit?doubleProperty=321.321&amp;longProperty=321321">
   *    Double and long via hard coded changes
   *    </html:link>
   */
  @Test
  void testItHandlesQueryParametersAddedAfterActionPath() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        tag.setAction(
          "/html-link-submit?doubleProperty=321.321&amp;longProperty=321321"
        );
      },
      (content, processedBody) -> {
        assertThat(
          content,
          containsString("href=\"/exercise/html-link-submit?")
        );
        assertThat(content, containsString("doubleProperty=321.321"));
        assertThat(content, containsString("longProperty=321321\""));
      }
    );
  }

  /**
   * <html:link
   *   action="/html-link"
   *   paramId="booleanProperty"
   *   paramName="testbean"
   *   paramProperty="nested.booleanProperty"
   *   module="/exercise">
   *   Boolean via paramId, paramName, and paramValue (module)
   * </html:link>
   */
  @Test
  void testItHandlesNestedPropertyOfBean() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.setupActionForm("testbean", TestBean::new);
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        tag.setAction("/html-link");
        tag.setParamId("booleanProperty");
        tag.setParamName("testbean");
        tag.setParamProperty("nested.booleanProperty");
        tag.setModule("/exercise");
      },
      (content, processedBody) -> {
        assertThat(
          content,
          containsString("href=\"/exercise/html-link?booleanProperty=true")
        );
      }
    );
  }

  /**
   * <pre>
   *   <global-forwards>
   *     <forward name="absolute" path="http://jakarta.apache.org/struts"/>
   *   </global-forwards>
   *
   *   <html:link forward="absolute">Struts website</html:link>
   * </pre>
   */
  @Test
  void testItCanRenderLinkToAbsolutePath() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        tag.setForward("absolute");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(
          content,
          containsString("href=\"http://jakarta.apache.org/struts\"")
        );
      }
    );
  }

  @Test
  void testItRendersHtmlCommonProperties() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      LinkTag.class,
      (tag, context) -> {
        tag.setForward("absolute");
        tag.setDir("DIR VALUE");
        tag.setOnclick("function(e) { console.log(e); }");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(
          content,
          containsString("href=\"http://jakarta.apache.org/struts\"")
        );
        assertThat(content, containsString("dir=\"DIR VALUE\""));
        assertThat(
          content,
          containsString("onclick=\"function(e) { console.log(e); }\"")
        );
      }
    );
  }

  /**
   * <pre>
   * <!-- Locale Action -->
   * <action
   *   path="/locale"
   *   type="org.apache.struts.webapp.validator.LocaleAction"
   *   name="localeForm"
   *   scope="request">
   *     <forward name="success" path="/welcome.do" />
   * </action>
   *
   * <!-- Locale form bean -->
   * <form-bean name="localeForm" type="org.apache.struts.action.DynaActionForm">
   *   <form-property name="language" type="java.lang.String" />
   *   <form-property name="country"  type="java.lang.String" />
   * </form-bean>
   *
   * <html:link
   *   action="/locale?language=ja&city=東京"
   *   useLocalEncoding="true">
   *   Japanese | Japonais
   * </html:link>
   * </pre>
   */
  @Test
  void testItCanEncodeQueryParametersWithLocalCharset() throws Exception {
    app.createRequest(GET, "/validator/welcome");
    app.assertTagContent(
      "/welcome",
      LinkTag.class,
      (tag, context) -> {
        var response = context.getResponse();
        response.setCharacterEncoding("MS932");
        tag.setAction("/locale?language=ja&city=東京");
        tag.setUseLocalEncoding(true);
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(
          content,
          containsString(
            "href=\"/validator/locale?language=ja&city=%93%8C%8B%9E\""
          )
        );
      }
    );
    app.createRequest(GET, "/validator/welcome");
    app.assertTagContent(
      "/welcome",
      LinkTag.class,
      (tag, context) -> {
        var response = context.getResponse();
        response.setCharacterEncoding("UTF-8");
        tag.setAction("/locale?language=ja&city=東京");
        tag.setUseLocalEncoding(true);
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<a"));
        assertThat(
          content,
          containsString(
            "href=\"/validator/locale?language=ja&city=%E6%9D%B1%E4%BA%AC\""
          )
        );
      }
    );
  }
}
