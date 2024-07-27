package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.model.Currency;
import java.util.List;

public interface CurrencyDao {

    List<Currency> findAll();

    Currency findCode(String code);

    void saveCurrency(Currency currency);

    void updateCurrency(Currency currency);

    void deleteCurrency(Currency currency);
}
