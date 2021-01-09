package com.vozimishko.backend.user;

import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.user.model.UserApi;
import com.vozimishko.backend.user.service.UserService;
import com.vozimishko.backend.util.models.EmptyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<EmptyResponse> register(@RequestBody UserApi userApi) {

    userService.register(userApi);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @PostMapping("/login")
  public ResponseEntity<CustomJwtToken> login(@RequestBody UserApi userApi) {
    CustomJwtToken jwtToken = userService.login(userApi.getEmail(), userApi.getPassword());
    return ResponseEntity.ok(jwtToken);
  }
}
