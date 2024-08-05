package org.example.currencyexchangeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestExchangeRateDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private String rate;
}
