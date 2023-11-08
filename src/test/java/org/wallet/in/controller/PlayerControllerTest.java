package org.wallet.in.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.domain.dto.request.PlayerRequestDto;
import org.wallet.in.config.RestResponseEntityExceptionHandler;
import org.wallet.utils.JwtTokenUtility;

public class PlayerControllerTest {

  @InjectMocks private PlayerController playerController;

  @Mock private WalletApplication walletApplication;
  private MockMvc mockMvc;
  private PlayerRequestDto playerRequestDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(playerController)
            .setControllerAdvice(new RestResponseEntityExceptionHandler())
            .build();
    playerRequestDto = new PlayerRequestDto();
    playerRequestDto.setLogin("testLogin");
    playerRequestDto.setPassword("testPassword");
  }

  @Test
  @DisplayName("Register a new player")
  public void testRegisterPlayer() throws Exception {
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(playerRequestDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Log in a player and return JWT token")
  public void testLogin() throws Exception {
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(playerRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jwtToken").exists());
  }

  @Test
  @DisplayName("Retrieve the balance of an authenticated player")
  public void testGetBalance() throws Exception {
    JwtTokenResponseDto jwtTokenResponseDto =
        new JwtTokenResponseDto(JwtTokenUtility.createJwtToken("testLogin"));

    when(walletApplication.getBalanceOfPlayer("testLogin")).thenReturn(BigDecimal.valueOf(100.00));

    mockMvc
        .perform(
            get("/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(jwtTokenResponseDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(100.00));
  }
}
