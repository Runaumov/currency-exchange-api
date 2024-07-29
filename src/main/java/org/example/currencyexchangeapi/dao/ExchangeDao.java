package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.model.ExchangeRate;
import java.util.List;
import java.util.Optional;

public interface ExchangeDao {

    List<ExchangeRate> findAll();

    ExchangeRate findByCode(String baseCode, String targetCode);

}
