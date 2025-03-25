package org.apache.struts.validator;

import static org.apache.struts.validator.FieldChecks.validateEmail;
import static org.apache.struts.validator.FieldChecks.validateUrl;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksUrlTest {

  @Mock
  Field mockField;

  @Mock
  ActionMessages mockErrors;

  @Mock
  HttpServletRequest mockRequest;

  @Mock
  Validator mockValidator;

  @Mock
  ValidatorAction mockAction;

  @Mock
  Object mockBean;

  void prepareFixture(@Nullable Object fieldValue) {
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  @AfterEach
  void resetLocale() {
    LocaleContextHolder.resetLocaleContext();
  }

  void shouldPass() {
    assertThat(
      validateUrl(
        mockBean,
        mockAction,
        mockField,
        mockErrors,
        mockValidator,
        mockRequest
      )
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateEmail(
        mockBean,
        mockAction,
        mockField,
        mockErrors,
        mockValidator,
        mockRequest
      )
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsInvalidUrlFieldValue() {
    prepareFixture("https://www.example.com");
    shouldPass();

    prepareFixture("http://example.com");
    shouldPass();

    prepareFixture("ftp://ftp.example.com");
    shouldPass();

    prepareFixture("https://subdomain.example.com");
    shouldPass();

    prepareFixture("https://example.com/path/to/resource");
    shouldPass();

    prepareFixture("http://www.example.co.uk");
    shouldPass();

    prepareFixture("https://www.example.com:8080");
    shouldPass();

    prepareFixture("https://www.example.com?query=value");
    shouldPass();

    prepareFixture("https://example.com/path?query=1&other=2");
    shouldPass();

    prepareFixture("https://www.example.com/#fragment");
    shouldPass();

    prepareFixture("https://example.com:443"); // port number
    shouldPass();

    prepareFixture("http://example.com/index.html");
    shouldPass();

    prepareFixture("https://example.org");
    shouldPass();

    prepareFixture("http://localhost:3000"); // local server
    shouldPass();

    prepareFixture("https://example.com/file.jpg");
    shouldPass();

    prepareFixture("htp://www.example.com"); // invalid protocol
    shouldReject();

    prepareFixture("http:/example.com"); // missing second slash
    shouldReject();

    prepareFixture("://example.com"); // missing protocol
    shouldReject();

    prepareFixture("www.example.com"); // missing protocol
    shouldReject();

    prepareFixture("http://example"); // no domain extension
    shouldReject();

    prepareFixture("https://..example.com"); // double dots in domain
    shouldReject();

    prepareFixture("http://example..com"); // double dots in domain name
    shouldReject();

    prepareFixture("https://.example.com"); // leading dot in domain
    shouldReject();

    prepareFixture("http://-example.com"); // leading hyphen in domain
    shouldReject();

    prepareFixture("https://example.com:999999"); // invalid port number
    shouldReject();

    prepareFixture("https://example.com/path space"); // space in path
    shouldReject();

    prepareFixture("ftp://ftp.example"); // incomplete FTP URL
    shouldReject();

    prepareFixture("http://example .com"); // space before the domain
    shouldReject();

    prepareFixture("http://.com"); // empty domain name
    shouldReject();

    prepareFixture("example.com"); // missing protocol
    shouldReject();
  }

  @Test
  void itPassesNullField() {
    prepareFixture(null);
    shouldPass();
  }
}
