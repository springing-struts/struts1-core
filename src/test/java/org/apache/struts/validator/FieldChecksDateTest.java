package org.apache.struts.validator;

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

import javax.servlet.http.HttpServletRequest;

import static org.apache.struts.validator.FieldChecks.validateDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksDateTest {

  @Mock Field mockField;
  @Mock ActionMessages mockErrors;
  @Mock HttpServletRequest mockRequest;
  @Mock Validator mockValidator;
  @Mock ValidatorAction mockAction;
  @Mock Object mockBean;

  void prepareFixture(
    @Nullable Object fieldValue,
    @Nullable String datePattern,
    @Nullable String datePatternStrict
  ) {
    when(mockField.getVarValue("datePattern")).thenReturn(datePattern);
    when(mockField.getVarValue("datePatternStrict")).thenReturn(datePatternStrict);
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  @AfterEach void resetLocale() {
    LocaleContextHolder.resetLocaleContext();
  }

  void shouldPass() {
    assertThat(
      validateDate(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateDate(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsInvalidDateFieldValues() {

    prepareFixture("2024/02/29", "yyyy/MM/dd", null);
    shouldPass();

    prepareFixture("2024/2/29", "yyyy/MM/dd", null);
    shouldPass();

    prepareFixture("20240229", "yyyy/MM/dd", null);
    shouldReject();

    prepareFixture("2024/02/30", "yyyy/MM/dd", null);
    shouldReject();

    prepareFixture("2024/2/29", null, "yyyy/MM/dd");
    shouldReject();

    prepareFixture("2024/02/29", null, "yyyy/MM/dd");
    shouldPass();

    prepareFixture("2024/02/29", "yyyy年MM月dd日", null);
    shouldReject();

    prepareFixture("2024年02月29日", "yyyy年MM月dd日", null);
    shouldPass();

    assertThatThrownBy(() -> {
      prepareFixture("2024/02/29", "yyyy年MM月dd日", null);
      shouldPass();
    });
  }

  @Test
  void itPassesNullField() {
    prepareFixture(null, "yyyy/MM/dd", "");
    shouldPass();

    prepareFixture("", "yyyy/MM/dd", null);
    shouldPass();
  }
}
