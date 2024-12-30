package org.apache.struts.validator;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

import static org.apache.struts.validator.FieldChecks.validateLong;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksLongTest {

  @Mock Field mockField;
  @Mock ActionMessages mockErrors;
  @Mock HttpServletRequest mockRequest;
  @Mock Validator mockValidator;
  @Mock ValidatorAction mockAction;
  @Mock Object mockBean;

  void prepareFixture(@Nullable Object fieldValue) {
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateLong(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateLong(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsFieldValuesThatCouldNotBeConvertedToLong() {
    prepareFixture("  ");
    shouldReject();

    prepareFixture("1");
    shouldPass();

    prepareFixture("１２３");
    shouldPass();

    prepareFixture("一");
    shouldReject();

    prepareFixture("aaa");
    shouldReject();

    prepareFixture("12a");
    shouldReject();

    prepareFixture("1.00");
    shouldReject();

    prepareFixture("0");
    shouldPass();

    prepareFixture("0.1");
    shouldReject();

    prepareFixture("128");
    shouldPass();

    prepareFixture("127");
    shouldPass();

    prepareFixture("-128");
    shouldPass();

    prepareFixture("-129");
    shouldPass();

    prepareFixture("+2147483647");
    shouldPass();

    prepareFixture("+2147483648");
    shouldPass();

    prepareFixture("-2147483648");
    shouldPass();

    prepareFixture("-2147483649");
    shouldPass();

    prepareFixture("+9223372036854775807");
    shouldPass();

    prepareFixture("+9223372036854775808");
    shouldReject();

    prepareFixture("-9223372036854775808");
    shouldPass();

    prepareFixture("-9223372036854775809");
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
