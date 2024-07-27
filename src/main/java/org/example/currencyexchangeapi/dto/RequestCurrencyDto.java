package org.example.currencyexchangeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestCurrencyDto {
    private Long id;
    private String code;
    private String fullname;
    private String sign;

    public RequestCurrencyDto(String code, String fullname, String sign) {
        this.code = code;
        this.fullname = fullname;
        this.sign = sign;
    }
}
