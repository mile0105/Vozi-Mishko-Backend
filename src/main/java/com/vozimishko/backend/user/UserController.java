package com.vozimishko.backend.user;

import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.user.model.UserApi;
import com.vozimishko.backend.user.service.UserService;
import com.vozimishko.backend.util.models.EmptyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<EmptyResponse> register(@Valid @RequestBody UserApi userApi) {

    userService.register(userApi);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @PostMapping("/login")
  public ResponseEntity<CustomJwtToken> login(@Valid @RequestBody UserApi userApi) {
    CustomJwtToken jwtToken = userService.login(userApi.getEmail(), userApi.getPassword());
    return ResponseEntity.ok(jwtToken);
  }

  @GetMapping("/me")
  public ResponseEntity<UserApi> getMyData() {
    UserApi loggedInUser = userService.getLoggedInUserData();
    return ResponseEntity.ok(loggedInUser);
  }
}
