package org.example.currencyexchangeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestExchangeDto {
    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal amount;
}
