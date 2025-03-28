package com.bankdemo.account.consumer;

import com.bankdemo.account.dto.TransactionDTO;
import com.bankdemo.account.exception.InsufficientBalanceException;
import com.bankdemo.account.exception.ResourceNotFoundException;
import com.bankdemo.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "${spring.kafka.topic.transaction-events}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void processTransaction(String message) {
        try {
            log.info("Recibido evento de transacción: {}", message);

            // Deserializar el mensaje a TransactionDTO
            TransactionDTO transaction = objectMapper.readValue(message, TransactionDTO.class);

            log.info("Procesando transacción [ID={}]: {} -> {} (monto={})",
                    transaction.getId(),
                    transaction.getFromAccount(),
                    transaction.getToAccount(),
                    transaction.getMonto());

            try {
                // EJECUTAR LA TRANSFERENCIA DIRECTAMENTE SIN VALIDACIÓN PREVIA
                // Ya que la validación se hizo en el Transaction Service

                // Actualizar saldo en cuenta de origen (débito)
                log.info("Debitando de la cuenta {}: {}", transaction.getFromAccount(), transaction.getMonto());
                accountService.updateBalance(
                        transaction.getFromAccount(),
                        transaction.getMonto().negate()
                );

                // Actualizar saldo en cuenta de destino (crédito)
                log.info("Acreditando a la cuenta {}: {}", transaction.getToAccount(), transaction.getMonto());
                accountService.updateBalance(
                        transaction.getToAccount(),
                        transaction.getMonto()
                );

                // Publicar evento de éxito
                log.info("Transacción completada con éxito: {}", transaction.getId());
                transaction.setStatus("COMPLETED");
                publishTransactionResult(transaction);

            } catch (InsufficientBalanceException e) {
                // Este error no debería ocurrir si la validación previa fue correcta,
                // pero se maneja por si acaso (posible condición de carrera)
                log.error("Error por fondos insuficientes (posible condición de carrera): {}", e.getMessage());
                transaction.setStatus("FAILED");
                transaction.setErrorMessage("Fondos insuficientes: " + e.getMessage());
                publishTransactionResult(transaction);
            } catch (ResourceNotFoundException e) {
                log.error("Error por recurso no encontrado: {}", e.getMessage());
                transaction.setStatus("FAILED");
                transaction.setErrorMessage("Cuenta no encontrada: " + e.getMessage());
                publishTransactionResult(transaction);
            } catch (Exception e) {
                // En caso de error, publicar evento de fallo
                log.error("Error procesando transacción: {}", e.getMessage(), e);
                transaction.setStatus("FAILED");
                transaction.setErrorMessage(e.getMessage());
                publishTransactionResult(transaction);
            }

        } catch (Exception e) {
            log.error("Error deserializando evento de transacción: {}", e.getMessage(), e);
        }
    }

    private void publishTransactionResult(TransactionDTO transaction) {
        try {
            String resultMessage = objectMapper.writeValueAsString(transaction);
            kafkaTemplate.send("transaction-results", transaction.getId().toString(), resultMessage);
            log.info("Resultado de transacción publicado: {} -> {}", transaction.getId(), transaction.getStatus());
        } catch (Exception e) {
            log.error("Error publicando resultado de transacción: {}", e.getMessage(), e);
        }
    }
}