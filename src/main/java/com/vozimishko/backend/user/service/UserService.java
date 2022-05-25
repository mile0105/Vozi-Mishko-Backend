package com.vozimishko.backend.user.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.InternalServerErrorException;
import com.vozimishko.backend.error.exceptions.UnauthorizedException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.security.jwt.CustomJwtToken;
import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.user.model.*;
import com.vozimishko.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {

  private final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final PrincipalService principalService;
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;

  public void register(RegisterRequestBody registerRequestBody) {
    checkIfDifferentUserWithEmailExists(registerRequestBody.getEmail(), null);
    checkIfDifferentUserWithPhoneNumberExists(registerRequestBody.getPhoneNumber(), null);

    User user = registerRequestBody.transformToDbModel(passwordEncoder);
    logger.info("Registering user with email: {}", registerRequestBody.getEmail());
    userRepository.save(user);
  }

  public CustomJwtToken login(String email, String password) {
    Authentication authentication;
    try {

      authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password));
    } catch (BadCredentialsException ex) {
      logger.info("Authentication failed for email: {}" , email);
      //todo next iteration: retry logic
      throw new UnauthorizedException(ErrorMessage.INVALID_CREDENTIALS);
    }

    Long userId = userRepository.findByEmail(email).map(User::getId).orElse(null);

    String accessToken = jwtUtils.generateAccessToken(authentication, userId);
    String refreshToken = jwtUtils.generateRefreshToken(authentication, userId);

    return CustomJwtToken.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

  public UserData getLoggedInUserData() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return userRepository.findById(loggedInUserId)
      .map(User::toUserData)
      .orElseThrow(() -> new InternalServerErrorException(ErrorMessage.SOMETHING_WENT_WRONG));
  }

  public List<UserDetails> getUserDetails(List<Long> userIds) {
    Iterable<User> users = userRepository.findAllById(userIds);
    return StreamSupport.stream(users.spliterator(), false)
      .map(User::transformToUserDetails)
      .collect(Collectors.toList());
  }

  public User updateUser(UpdateUserRequestBody updateUserRequestBody) {

    Long loggedInUserId = principalService.getLoggedInUserId();
    User user = userRepository.findById(loggedInUserId).orElseThrow(() -> new InternalServerErrorException(ErrorMessage.SOMETHING_WENT_WRONG));

    User.UserBuilder updatedUserBuilder = user.toBuilder();


    if (StringUtils.isNotEmpty(updateUserRequestBody.getEmail())) {
      checkIfDifferentUserWithEmailExists(updateUserRequestBody.getEmail(), loggedInUserId);
      updatedUserBuilder.email(updateUserRequestBody.getEmail());
    }

    if (StringUtils.isNotEmpty(updateUserRequestBody.getPhoneNumber())) {
      checkIfDifferentUserWithPhoneNumberExists(updateUserRequestBody.getPhoneNumber(), loggedInUserId);
      updatedUserBuilder.phoneNumber(updateUserRequestBody.getPhoneNumber());
    }

    if (StringUtils.isNotEmpty(updateUserRequestBody.getFirstName())) {
      updatedUserBuilder.firstName(updateUserRequestBody.getFirstName());
    }

    if (StringUtils.isNotEmpty(updateUserRequestBody.getLastName())) {
      updatedUserBuilder.lastName(updateUserRequestBody.getLastName());
    }

    if (StringUtils.isNotEmpty(updateUserRequestBody.getPassword())) {
      updatedUserBuilder.password(passwordEncoder.encode(updateUserRequestBody.getPassword()));
    }

    return userRepository.save(updatedUserBuilder.build());
  }

  private void checkIfDifferentUserWithEmailExists(String email, Long loggedInUserId) {

    Optional<User> existingUserByEmail = userRepository.findByEmail(email);

    if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(loggedInUserId)) {
      throw new BadRequestException(ErrorMessage.USER_EXISTS_EMAIL);
    }
  }

  private void checkIfDifferentUserWithPhoneNumberExists(String phoneNumber, Long loggedInUserId) {
    Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
    if (existingUserByPhoneNumber.isPresent() && !existingUserByPhoneNumber.get().getId().equals(loggedInUserId)) {
      throw new BadRequestException(ErrorMessage.USER_EXISTS_PHONE);
    }
  }
}
