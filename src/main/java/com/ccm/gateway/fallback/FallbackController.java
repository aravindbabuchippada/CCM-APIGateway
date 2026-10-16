package com.ccm.gateway.fallback;

import com.ccm.gateway.pojo.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @RequestMapping("/{service}")
    public ResponseEntity<ExceptionResponse> fallback(@PathVariable String service,
                                                      ServerWebExchange exchange) {
        Throwable cause = exchange.getAttribute(
                ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        log.warn("Fallback for '{}' | path={} | cause={}",
                service,
                exchange.getRequest().getPath(),
                cause == null ? "unknown (circuit open?)" : cause.toString());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ExceptionResponse.builder()
                        .errorCode(HttpStatus.SERVICE_UNAVAILABLE)
                        .errorMessage(service + " service is temporarily unavailable")
                        .build());
    }
}
