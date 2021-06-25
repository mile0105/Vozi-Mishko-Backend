package com.vozimishko.backend.user.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.InternalServerErrorException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.UserApi;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final AuthenticationManager authenticationManager;
  private final PrincipalService principalService;
  private final JwtUtils jwtUtils;

  public void register(UserApi userApi) {
    checkIfUserWithEmailExists(userApi.getEmail());
    checkIfUserWithPhoneNumberExists(userApi.getPhoneNumber());

    User user = userMapper.transformToDbModel(userApi);
    userRepository.save(user);
  }

  public CustomJwtToken login(String email, String password) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(email, password));

    Long userId = userRepository.findByEmail(email).map(User::getId).orElse(null);

    String accessToken = jwtUtils.generateAccessToken(authentication, userId);
    String refreshToken = jwtUtils.generateRefreshToken(authentication, userId);

    return CustomJwtToken.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

  public UserApi getLoggedInUserData() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return userRepository.findById(loggedInUserId)
      .map(userMapper::transformFromDbModel)
      .orElseThrow(() -> new InternalServerErrorException(ErrorMessage.SOMETHING_WENT_WRONG));
  }

  public List<UserDetails> getUserDetails(List<Long> userIds) {
    Iterable<User> users = userRepository.findAllById(userIds);
    return StreamSupport.stream(users.spliterator(), false)
      .map(userMapper::transformToUserDetails)
      .collect(Collectors.toList());
  }

  private void checkIfUserWithEmailExists(String email) {

    Optional<User> existingUserByEmail = userRepository.findByEmail(email);

    if (existingUserByEmail.isPresent()) {
      throw new BadRequestException(ErrorMessage.USER_EXISTS_EMAIL);
    }
  }

  private void checkIfUserWithPhoneNumberExists(String phoneNumber) {
    Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
    if (existingUserByPhoneNumber.isPresent()) {
      throw new BadRequestException(ErrorMessage.USER_EXISTS_PHONE);
    }
  }
}
