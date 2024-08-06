# Проект "Обменник валют".

## Описание
- REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, и совершать расчёт конвертации произвольных сумм из одной валюты в другую.
- Имеется веб-интерфейс, взятый [отсюда.](https://github.com/zhukovsd/currency-exchange-frontend)
- Техническое задание проекта находится [тут.](https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/)

## Стек
- Jakarta EE
- JDBC
- SQLite
- Maven
- Postman
- Docker

## Колекция запросов
- `GET` /currencies <br>
Получение списка валют.
- `GET` /currencies/USD <br>
Получение конкретной валюты.
- `POST` /currencies <br>
Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - name, code, sign.
- `GET` /exchangeRates <br>
Получение списка всех обменных курсов.
- `GET` /exchangeRate/USDRUB <br>
Получение конкретного обменного курса
- `POST` /exchangeRates <br>
Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - baseCurrencyCode, targetCurrencyCode, rate.
- `PATCH` /exchangeRates/USDRUB <br>
Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Единственное поле формы - rate.
- `GET` /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT <br>
Расчёт перевода определённого количества средств из одной валюты в другую. Пример запроса - `GET` /exchange?from=USD&to=AUD&amount=10.

## Запуск приложения
- С помощью docker:
- - Соберите контейнер:<br>
    `docker build -t currency-exchange-api .`
  - Запустите контейнер:<br>
    `docker run -p 8080:8080 currency-exchange-api`
  - Адрес приложения: `http://localhost:8080/currency_exchange_api/`
