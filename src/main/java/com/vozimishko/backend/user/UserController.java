package com.vozimishko.backend.user;

import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.user.model.*;
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
  public ResponseEntity<EmptyResponse> register(@Valid @RequestBody RegisterRequestBody registerRequestBody) {

    userService.register(registerRequestBody);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @PostMapping("/login")
  public ResponseEntity<CustomJwtToken> login(@Valid @RequestBody LoginRequestBody loginApi) {
    CustomJwtToken jwtToken = userService.login(loginApi.getEmail(), loginApi.getPassword());
    return ResponseEntity.ok(jwtToken);
  }

  @GetMapping("/me")
  public ResponseEntity<UserData> getMyData() {
    UserData loggedInUser = userService.getLoggedInUserData();
    return ResponseEntity.ok(loggedInUser);
  }

  @PutMapping("/me")
  public ResponseEntity<User> updateMyData(@Valid @RequestBody UpdateUserRequestBody updateUserRequestBody) {
    User updatedUser = userService.updateUser(updateUserRequestBody);
    return ResponseEntity.ok(updatedUser);
  }
}
