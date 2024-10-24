package org.apache.struts.taglib.bean;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class MessageTagTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   * <message-resources parameter="org.apache.struts.webapp.validator.MessageResources">
   *   <set-property key="mode" value="JSTL"/>
   * </message-resources>
   *
   * # Index Page
   * index.title=Struts Validator
   *
   * <bean:message key="index.title"/>
   * </pre>
   */
  @Test
  void testItShowsMessageFromMessageResourceBundleConfiguredInModule() throws Exception {
    app.createRequest(GET, "/validator/welcome");
    app.assertTagContent(
      "/welcome",
      MessageTag.class,
      (tag, context)  -> {
        tag.setKey("index.title");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("Struts Validator"));
      }
    );
  }

  /**
   * <pre>
   * [MessageResource.properties]
   * errors.required={0} is required.
   *
   * [MessageResource_ja.properties]
   * errors.required={0} \u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044\u3002
   * </pre>
   */
  @Test
  void testItCanShowLocalizedMessages() throws Exception {
    app.createRequest(GET, "/validator/welcome", request -> {
      request.addHeader( "Accept-Language", "ja");
    });
    app.assertTagContent(
      "/welcome",
      MessageTag.class,
      (tag, context) -> {
        tag.setKey("errors.required");
        tag.setArg0("パスワード");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("パスワード を入力してください。"));
      }
    );
  }

  /**
   * <pre>
   * <message-resources
   *   key="i18nExample"
   *   null="false"
   *   parameter="org.apache.struts.webapp.validator.I18nExample">
   *   <set-property key="mode" value="JSTL"/>
   * </message-resources>
   *
   * [I18nExample.properties]
   * standard.title=I18N Variables
   *
   * <bean:message key="standard.title" bundle="i18nExample"/>
   * </pre>
   */
  @Test
  void testItCanSwitchMessageBundle() throws Exception {
    app.createRequest(GET, "/validator/welcome");
    app.assertTagContent(
      "/welcome",
      MessageTag.class,
      (tag, context) -> {
        tag.setKey("standard.title");
        tag.setBundle("i18nExample");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("I18N Variables"));
      }
    );
  }
}
