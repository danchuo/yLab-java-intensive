package org.wallet.in.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.wallet.aop.annotations.Authorized;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.domain.dto.request.PlayerRequestDto;
import org.wallet.domain.dto.response.BalanceResponseDto;
import org.wallet.exception.InvalidRequestException;
import org.wallet.utils.JwtTokenUtility;

/**
 * The {@code PlayerController} class handles player registration, login, and balance-related
 * operations.
 */
@Timed
@RestController
@RequiredArgsConstructor
public class PlayerController {

  private final WalletApplication walletApplication;

  /**
   * Registers a new player with the provided request data.
   *
   * @param request The player registration request data.
   * @return A response entity indicating the registration status.
   */
  @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> register(@RequestBody PlayerRequestDto request) {
    if (!request.isValid()) {
      throw new InvalidRequestException();
    }

    walletApplication.registerPlayer(request.getLogin(), request.getPassword());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * Logs in a player using the provided request data and returns a JWT token.
   *
   * @param request The player login request data.
   * @return A JWT token response.
   */
  @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  public JwtTokenResponseDto login(@RequestBody PlayerRequestDto request) {
    if (!request.isValid()) {
      throw new InvalidRequestException();
    }

    walletApplication.login(request.getLogin(), request.getPassword());

    var jwtToken = JwtTokenUtility.createJwtToken(request.getLogin());

    return new JwtTokenResponseDto(jwtToken);
  }

  /**
   * Retrieves the balance of an authenticated player.
   *
   * @param request The JWT token response data to authenticate the player.
   * @return A balance response containing the player's balance.
   */
  @Authorized
  @GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
  public BalanceResponseDto getBalance(@RequestBody JwtTokenResponseDto request) {
    var login = JwtTokenUtility.getLogin(request.getJwtToken());

    var balance = walletApplication.getBalanceOfPlayer(login);

    return new BalanceResponseDto(balance);
  }
}
