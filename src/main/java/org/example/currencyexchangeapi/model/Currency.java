package org.example.currencyexchangeapi.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Currency {

    private Long id;
    private String code;
    private String fullname;
    private String sign;

    public Currency(String code, String fullname, String sign) {
        this.code = code;
        this.fullname = fullname;
        this.sign = sign;
    }
}