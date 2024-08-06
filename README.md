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
- `GET` /currencies
Получение списка валют.
- `GET` /currencies/USD
Получение конкретной валюты.
- `POST` /currencies
Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - name, code, sign
- `GET` /exchangeRates
- `GET` /exchangeRate/USDRUB
- `POST` /exchangeRates
- `PATCH` /exchangeRates/USDRUB
- `GET` /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT
