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

import static org.apache.struts.validator.FieldChecks.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksShortTest {

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
      validateShort(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateShort(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsFieldValuesThatCouldNotBeConvertedToShort() {
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

    prepareFixture("32767");
    shouldPass();

    prepareFixture("32768");
    shouldReject();

    prepareFixture("-32768");
    shouldPass();

    prepareFixture("-32769");
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
