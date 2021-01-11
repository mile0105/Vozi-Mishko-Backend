package com.vozimishko.backend.security;


import com.vozimishko.backend.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PrincipalServiceTest {

  private PrincipalService principalService;
  @Mock
  private JwtUtils jwtUtils;

  @BeforeEach
  void setUp() {
    principalService = new PrincipalService(jwtUtils);
  }

  @Test
  void shouldGetTheLoggedInUserId() {
    String token = "my hashed jwt token";
    Long id = 1L;

    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addHeader("Authorization", "Bearer " + token);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

    when(jwtUtils.getUserIdFromToken(token)).thenReturn(id);

    Long result = principalService.getLoggedInUserId();

    assertThat(result).isEqualTo(id);
  }
}
