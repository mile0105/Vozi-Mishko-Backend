package com.vozimishko.backend.user.service;

import com.vozimishko.backend.user.model.Role;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.RegisterRequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = new UserMapper(passwordEncoder);
  }


  @Test
  void shouldTransformToDbModel() {

    String testPassword = "password";
    String encodedPassword = "3nc0d3dP@$$w0rD";

    RegisterRequestBody testRegisterRequestBody = RegisterRequestBody.builder()
      .email("email")
      .password(testPassword)
      .firstName("Vozi")
      .lastName("Mishko")
      .phoneNumber("123-456")
      .build();

    when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

    User result = userMapper.transformToDbModel(testRegisterRequestBody);

    assertThat(result.getEmail()).isEqualTo(testRegisterRequestBody.getEmail());
    assertThat(result.getFirstName()).isEqualTo(testRegisterRequestBody.getFirstName());
    assertThat(result.getLastName()).isEqualTo(testRegisterRequestBody.getLastName());
    assertThat(result.getPhoneNumber()).isEqualTo(testRegisterRequestBody.getPhoneNumber());
    assertThat(result.getPassword()).isEqualTo(encodedPassword);
    assertThat(result.getRole()).isEqualTo(Role.NORMAL_USER.getName());
  }

  @Test
  void shouldTransformToApiModel() {

    User user = User.builder()
      .email("email")
      .id(1L)
      .phoneNumber("123-456")
      .firstName("Vozi")
      .lastName("Mishko")
      .password("{noop}secretPassword123")
      .role(Role.NORMAL_USER.getName())
      .build();

    RegisterRequestBody result = userMapper.transformFromDbModel(user);

    assertThat(result.getEmail()).isEqualTo(user.getEmail());
    assertThat(result.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    assertThat(result.getFirstName()).isEqualTo(user.getFirstName());
    assertThat(result.getLastName()).isEqualTo(user.getLastName());
    assertThat(result.getPassword()).isNull();
    assertThat(result.getId()).isEqualTo(user.getId());
  }
}
