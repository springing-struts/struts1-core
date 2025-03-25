package org.apache.struts.validator;

import static org.apache.struts.validator.FieldChecks.validateEmail;
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
public class FieldChecksEmailTest {

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
      validateEmail(
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
  void itRejectsInvalidEmailFieldValue() {
    prepareFixture("test@example.com");
    shouldPass();

    prepareFixture("user.name123@example.co.uk");
    shouldPass();

    prepareFixture("first.last@subdomain.domain.com");
    shouldPass();

    prepareFixture("username@domain.org");
    shouldPass();

    prepareFixture("valid.email@domain.name");
    shouldPass();

    prepareFixture("user_123@example.io");
    shouldPass();

    prepareFixture("test@domain.us");
    shouldPass();

    prepareFixture("email1234@sub.domain.edu");
    shouldPass();

    prepareFixture("name@domain.travel");
    shouldPass();

    prepareFixture("john.doe@company.biz");
    shouldPass();

    prepareFixture("plainaddress");
    shouldReject();

    prepareFixture("@missingusername.com");
    shouldReject();

    prepareFixture("missingatsign.com");
    shouldReject();

    prepareFixture("user@domain..com"); // double dots
    shouldReject();

    prepareFixture("user@domain,com"); // comma instead of dot
    shouldReject();

    prepareFixture("user@domain@com.com"); // multiple @ symbols
    shouldReject();

    prepareFixture("user@.domain.com"); // dot before domain
    shouldReject();

    prepareFixture("user@domain"); // missing top-level domain
    shouldReject();

    prepareFixture("@domain.com"); // missing username
    shouldReject();

    prepareFixture("user@domain.c"); // incomplete top-level domain
    shouldReject();
  }

  @Test
  void itPassesNullField() {
    prepareFixture(null);
    shouldPass();

    prepareFixture("");
    shouldPass();
  }
}
