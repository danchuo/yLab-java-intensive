package org.wallet.in.controller;

import com.danchuo.starterannotations.aop.annotations.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wallet.aop.annotations.Authorized;
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
   */
  @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody PlayerRequestDto request) {
    if (!request.isValid()) {
      throw new InvalidRequestException();
    }

    walletApplication.registerPlayer(request.getLogin(), request.getPassword());
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
