package com.vozimishko.backend.user.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.user.model.Role;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.RegisterRequestBody;
import com.vozimishko.backend.user.model.UserData;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;
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

    verify(userRepository).save(userArgumentCaptor.capture());

    assertEquals(testUser.toBuilder().id(null).build(), userArgumentCaptor.getValue());
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
}
