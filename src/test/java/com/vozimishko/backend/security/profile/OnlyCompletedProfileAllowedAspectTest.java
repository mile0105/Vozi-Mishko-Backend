package com.vozimishko.backend.security.profile;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.user.model.UserData;
import com.vozimishko.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnlyCompletedProfileAllowedAspectTest {

  @Mock
  private UserService userService;

  private OnlyCompletedProfileAllowedAspect aspect;

  @BeforeEach
  void setUp() {
    aspect = new OnlyCompletedProfileAllowedAspect(userService);
  }

  @Test
  void shouldDoNothingIfProfileCompleted() {
    when(userService.getLoggedInUserData()).thenReturn(UserData.builder().firstName("first").lastName("last").build());

    aspect.verifyProfileCompleted();
  }

  @Test
  void shouldThrowExceptionIfProfileNotCompleted() {
    when(userService.getLoggedInUserData()).thenReturn(UserData.builder().firstName("").lastName("").build());

    BadRequestException exception = assertThrows(BadRequestException.class, () -> aspect.verifyProfileCompleted());

    assertEquals(ErrorMessage.PROFILE_NOT_COMPLETE, exception.getErrorMessage());
  }
}
