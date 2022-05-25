package com.vozimishko.backend.user.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.user.model.*;
import com.vozimishko.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private PrincipalService principalService;
  @Mock
  private JwtUtils jwtUtils;
  @Mock
  private Authentication authentication;
  private UserService userService;

  private RegisterRequestBody testRegisterRequestBody;

  private UserData testUserData;
  private User testUser;
  private String testAccessToken;
  private String testRefreshToken;

  @BeforeEach
  void setUp() {

    userService = new UserService(userRepository, authenticationManager, principalService, jwtUtils, passwordEncoder);

    testRegisterRequestBody = RegisterRequestBody.builder().email("email").password("1234").phoneNumber("123-456").build();
    testUserData = UserData.builder().id(1L).email("email").phoneNumber("123-456").build();
    testUser = User.builder().id(1L).email("email").role(Role.NORMAL_USER.getName()).phoneNumber("123-456").password("3nc0d3d").build();
    testAccessToken = "access-token";
    testRefreshToken = "refresh-token";
  }

  @Test
  void shouldRegisterAUser() {

    when(userRepository.findByEmail(testRegisterRequestBody.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByPhoneNumber(testRegisterRequestBody.getPhoneNumber())).thenReturn(Optional.empty());
    when(passwordEncoder.encode("1234")).thenReturn("3nc0d3d");

    userService.register(testRegisterRequestBody);

    verify(userRepository).save(testUser.toBuilder().id(null).build());
  }


  @Test
  void shouldThrowExceptionIfUserWithEmailExists() {
    when(userRepository.findByEmail(testRegisterRequestBody.getEmail())).thenReturn(Optional.of(testUser));

    assertThrows(BadRequestException.class, () -> userService.register(testRegisterRequestBody),"User already exists");
  }

  @Test
  void shouldThrowExceptionIfUserWithPhoneNumberExists() {
    when(userRepository.findByPhoneNumber(testRegisterRequestBody.getPhoneNumber())).thenReturn(Optional.of(testUser));

    assertThrows(BadRequestException.class, () -> userService.register(testRegisterRequestBody),"User already exists");
  }

  @Test
  void shouldLogin() {
    String email = "email";
    String password = "email";
    Long userId = testUser.getId();

    when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
      .thenReturn(authentication);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

    when(jwtUtils.generateAccessToken(authentication, userId)).thenReturn(testAccessToken);
    when(jwtUtils.generateRefreshToken(authentication, userId)).thenReturn(testRefreshToken);

    CustomJwtToken customJwtToken = userService.login(email, password);

    assertThat(customJwtToken.getAccessToken()).isEqualTo(testAccessToken);
    assertThat(customJwtToken.getRefreshToken()).isEqualTo(testRefreshToken);
  }

  @Test
  void shouldGetLoggedInUserData() {
    Long userId = testUser.getId();
    when(principalService.getLoggedInUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    UserData result = userService.getLoggedInUserData();

    assertThat(result).isEqualTo(testUserData);
  }

  @Test
  void shouldUpdateFirstNameAndLastNameFromUser() {
    Long userId = testUser.getId();
    when(principalService.getLoggedInUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    String firstName = "first";
    String lastName = "last";
    UpdateUserRequestBody requestBody = UpdateUserRequestBody.builder().firstName(firstName).lastName(lastName).build();

    userService.updateUser(requestBody);

    verify(userRepository).save(testUser.toBuilder().firstName(firstName).lastName(lastName).build());
    verify(userRepository, never()).findByEmail(anyString());
    verify(userRepository, never()).findByPhoneNumber(anyString());
    verifyNoInteractions(passwordEncoder);
  }

  @Test
  void shouldUpdateAllUserValues() {
    Long userId = testUser.getId();
    when(principalService.getLoggedInUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    String email = "newEmail";
    String phoneNumber = "123123123123";
    String password = "password";
    String firstName = "first";
    String lastName = "last";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
    when(passwordEncoder.encode(password)).thenReturn("pa$$w0rd");

    UpdateUserRequestBody requestBody = UpdateUserRequestBody.builder()
      .email(email)
      .phoneNumber(phoneNumber)
      .firstName(firstName)
      .lastName(lastName)
      .password(password)
      .build();

    userService.updateUser(requestBody);

    verify(userRepository).save(testUser.toBuilder()
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .phoneNumber(phoneNumber)
        .password("pa$$w0rd")
      .build());
  }

  @Test
  void shouldThrowExceptionIfUserWithEmailExistsWhenUpdatingUser() {
    Long userId = testUser.getId();
    when(principalService.getLoggedInUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    String email = testUser.getEmail();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser.toBuilder().id(userId + 1).build()));

    UpdateUserRequestBody requestBody = UpdateUserRequestBody.builder().email(email).build();

    BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updateUser(requestBody));

    assertEquals(ErrorMessage.USER_EXISTS_EMAIL, exception.getErrorMessage());
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);
  }


  @Test
  void shouldThrowExceptionIfUserWithPhoneNumberExistsWhenUpdatingUser() {
    Long userId = testUser.getId();
    when(principalService.getLoggedInUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    String phoneNumber = testUser.getPhoneNumber();
    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser.toBuilder().id(userId + 1).build()));

    UpdateUserRequestBody requestBody = UpdateUserRequestBody.builder().phoneNumber(phoneNumber).build();

    BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updateUser(requestBody));

    assertEquals(ErrorMessage.USER_EXISTS_PHONE, exception.getErrorMessage());
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);
  }
}
