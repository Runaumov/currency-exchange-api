package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.model.Currency;
import java.util.List;
import java.util.Optional;

public interface CurrencyDao {

    List<Currency> findAll();

    Optional<Currency> findByCode(String code);

    void saveCurrency(Currency currency);

    void updateCurrency(Currency currency);

    void deleteCurrency(Currency currency);
}
