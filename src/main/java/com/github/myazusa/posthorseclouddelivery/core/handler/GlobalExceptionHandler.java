package com.github.myazusa.posthorseclouddelivery.core.handler;

import com.github.myazusa.posthorseclouddelivery.core.exception.*;
import com.github.myazusa.posthorseclouddelivery.model.dto.InformationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 用于捕获未定义的异常
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<InformationResponseDTO> handleException(Exception e) {
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage("非预期的异常，请在日志查看详细"));
    }

    // 用于捕获用户名不存在的异常
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<InformationResponseDTO> handleUsernameNotFoundException(Exception e) {
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }

    // 捕获异常token
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<InformationResponseDTO> handleInvalidTokenException(Exception e){
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }

    // 捕获用户验证失败异常
    @ExceptionHandler(AuthUserException.class)
    public ResponseEntity<InformationResponseDTO> handleAuthUserException(Exception e){
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }

    // 捕获任意属于数据冲突的异常
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<InformationResponseDTO> handleDataConflictException(Exception e){
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }

    // 捕获远端服务连接的异常
    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<InformationResponseDTO> handleRemoteServiceException(Exception e){
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }
    @ExceptionHandler(NotProvidedServiceException.class)
    public ResponseEntity<InformationResponseDTO> handleNotProvidedServiceException(Exception e){
        log.error("异常: {}", e.getMessage(), e);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage(e.getMessage()));
    }
}
