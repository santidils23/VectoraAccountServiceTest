package com.bankdemo.account.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bankdemo.account.config.RedisTestConfig;
import com.bankdemo.account.dto.AccountRequestDTO;
import com.bankdemo.account.model.Account;
import com.bankdemo.account.repository.AccountRepository;
import com.bankdemo.account.util.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(RedisTestConfig.class)
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private String jwtToken;

    @Value("${jwt.secret}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtToken = "Bearer " + JwtTestUtil.generateTestToken("admin", secretKey);
        accountRepository.deleteAll(); // Limpiar la BD antes de cada prueba
    }

    @Test
    void testCreateAccountWithValidData() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO("John Doe", BigDecimal.valueOf(1000.0));

        mockMvc.perform(post("/accounts")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verificar en la base de datos
        Optional<Account> account = accountRepository.findById(1L);
        assertThat(account).isPresent();
        assertThat(account.get().getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(1000.0));
    }

    @Test
    void testCreateAccountWithInvalidData() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO("", BigDecimal.valueOf(-500.0));

        mockMvc.perform(post("/accounts")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccountById() throws Exception {
        Account account = new Account();
        account.setNombre("Jane Doe");
        account.setSaldo(BigDecimal.valueOf(2000.0));
        accountRepository.save(account);

        mockMvc.perform(get("/accounts/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Jane Doe"))
                .andExpect(jsonPath("$.saldo").value(2000.0));
    }

    @Test
    void testGetNonExistentAccount() throws Exception {
        mockMvc.perform(get("/accounts/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isNotFound());
    }

}