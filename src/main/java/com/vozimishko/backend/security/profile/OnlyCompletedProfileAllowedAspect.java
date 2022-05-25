package com.vozimishko.backend.security.profile;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.user.model.UserData;
import com.vozimishko.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OnlyCompletedProfileAllowedAspect {

  private final UserService userService;

  @Before("@annotation(com.vozimishko.backend.security.profile.OnlyCompletedProfileAllowed)")
  public void verifyProfileCompleted() {
    UserData user = userService.getLoggedInUserData();
    if (!user.isProfileComplete()) {
      throw new BadRequestException(ErrorMessage.PROFILE_NOT_COMPLETE);
    }
  }
}
