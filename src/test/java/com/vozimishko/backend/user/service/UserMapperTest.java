package com.vozimishko.backend.user.service;

import com.vozimishko.backend.user.model.Role;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.UserApi;
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

    UserApi testUserApi = UserApi.builder()
      .email("email")
      .password(testPassword)
      .firstName("Vozi")
      .lastName("Mishko")
      .phoneNumber("123-456")
      .build();

    when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

    User result = userMapper.transformToDbModel(testUserApi);

    assertThat(result.getEmail()).isEqualTo(testUserApi.getEmail());
    assertThat(result.getFirstName()).isEqualTo(testUserApi.getFirstName());
    assertThat(result.getLastName()).isEqualTo(testUserApi.getLastName());
    assertThat(result.getPhoneNumber()).isEqualTo(testUserApi.getPhoneNumber());
    assertThat(result.getPassword()).isEqualTo(encodedPassword);
    assertThat(result.getRole()).isEqualTo(Role.NORMAL_USER.getName());

  }
}
