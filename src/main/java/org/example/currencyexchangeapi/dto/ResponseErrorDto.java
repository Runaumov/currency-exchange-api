package org.example.currencyexchangeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResponseErrorDto {

    private String message;
    private int status;
    
}
