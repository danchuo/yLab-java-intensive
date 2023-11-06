package org.wallet.in.controller;

import com.danchuo.starterannotations.aop.annotations.Timed;
import java.util.List;
import java.util.stream.Collectors;
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
import org.wallet.domain.dto.mapper.TransactionMapper;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.domain.dto.request.TransactionRequestDto;
import org.wallet.domain.dto.response.TransactionResponseDto;
import org.wallet.domain.model.Transaction;
import org.wallet.exception.InvalidRequestException;
import org.wallet.utils.JwtTokenUtility;

/**
 * The {@code TransactionController} class handles transaction creation and retrieval of
 * transactions.
 */
@Timed
@RestController
@RequiredArgsConstructor
public class TransactionController {

  private final WalletApplication walletApplication;

  /**
   * Creates a new transaction based on the provided request data.
   *
   * @param requestDto The transaction request data.
   */
  @Authorized
  @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void createTransaction(@RequestBody TransactionRequestDto requestDto) {
    var login = JwtTokenUtility.getLogin(requestDto.getJwtToken());

    if (!requestDto.isValid()) {
      throw new InvalidRequestException();
    }

    Transaction transaction =
        TransactionMapper.INSTANCE.transactionRequestDtoToTransaction(requestDto);
    walletApplication.registerTransaction(transaction);
  }

  /**
   * Retrieves all transactions of the authenticated player.
   *
   * @param request The JWT token response data for player authentication.
   * @return A list of transaction response DTOs.
   */
  @Authorized
  @GetMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<TransactionResponseDto> getAllTransactions(@RequestBody JwtTokenResponseDto request) {
    var login = JwtTokenUtility.getLogin(request.getJwtToken());

    List<Transaction> transactions = walletApplication.getTransactionsOfPlayer(login);

    return transactions.stream()
        .map(TransactionMapper.INSTANCE::transactionToTransactionResponseDto)
        .collect(Collectors.toList());
  }
}
