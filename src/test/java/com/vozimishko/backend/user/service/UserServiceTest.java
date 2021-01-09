package com.vozimishko.backend.user.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.UserApi;
import com.vozimishko.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private Authentication authentication;

  private UserService userService;
  private UserApi testUserApi;
  private User testUser;
  private String testAccessToken;
  private String testRefreshToken;

  @BeforeEach
  void setUp() {

    userService = new UserService(userRepository, userMapper, authenticationManager, jwtUtils);

    testUserApi = UserApi.builder().email("email").phoneNumber("123-456").build();
    testUser = User.builder().id(1L).email("email").phoneNumber("123-456").build();
    testAccessToken = "access-token";
    testRefreshToken = "refresh-token";
  }

  @Test
  void shouldRegisterAUser() {

    when(userRepository.findByEmail(testUserApi.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByPhoneNumber(testUserApi.getPhoneNumber())).thenReturn(Optional.empty());
    when(userMapper.transformToDbModel(testUserApi)).thenReturn(testUser);

    userService.register(testUserApi);

    verify(userRepository).save(testUser);
  }


  @Test
  void shouldThrowExceptionIfUserWithEmailExists() {
    when(userRepository.findByEmail(testUserApi.getEmail())).thenReturn(Optional.of(testUser));
    when(userRepository.findByPhoneNumber(testUserApi.getPhoneNumber())).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class, () -> userService.register(testUserApi),"User already exists");
  }

  @Test
  void shouldThrowExceptionIfUserWithPhoneNumberExists() {
    when(userRepository.findByEmail(testUserApi.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByPhoneNumber(testUserApi.getPhoneNumber())).thenReturn(Optional.of(testUser));

    assertThrows(BadRequestException.class, () -> userService.register(testUserApi),"User already exists");
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
}
