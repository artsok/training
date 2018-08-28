#### Исходные данные

Имеется Rest API:

∙             Возвращает справочные данные из таблицы в БД Oracle. В данном случае – типы клиентов.

∙             Может выполнять фильтрацию данных

∙             Тип запроса – Get

∙             Формат возвращаемого ответа – json

 

Например, http URL путь до API - /getClientTypes

Входной параметр – typeOfOwnership, может принимать значения 1 (физическое лицо) или 2 (юридическое лицо).

В зависимости от значения параметра, API либо производит фильтрацию возвращаемых записей, либо не производит, если параметр не передан.

 

Необходимо:

1. Написать на java тесты на данное API. API можно замокировать, либо даже тесты могут не компилироваться, представим что API имеется.

2. Технологии тестирования можно выбрать самостоятельно. 

#### Допущения
1. Фильтрация данных осуществляется по полю 'Name, либо Company'


#### Таблица проверок
| Rest (getClientTypes) | Param1 (typeOfOwnership)  |  Param2 (typeOfOwnership)     |   Result   |  Response Status (http)   |
| --- | --- | --- | --- | --- | 
| TestCase #1 (getClientTypes?typeOfOwnership=1)           |       none                |                    1          |  список физ.лиц  | 200 |
| TestCase #2 (getClientTypes?typeOfOwnership=1&orderBy=asc)           |       asc                 |                    1          |  список физ.лиц по возр. | 200 | 
| TestCase #3 (getClientTypes?typeOfOwnership=2&orderBy=desc)          |       desc                |                    2          |  список юр.лиц по убыв. | 200 |
| TestCase #4 (getClientTypes?typeOfOwnership=2)          |       empty                |                    2          |  список юр.лиц     | 200 |
| TestCase #5 (getClientTypes?typeOfOwnership=&orderBy=asc)           |       asc                 |                   none        |    error           | 404 |        



#### Для запуска тестов выполнить команду
1. mvn clean test
2. mvn allure:serve


#### Отчет 
![alt text](https://github.com/artsok/training/blob/master/images/Allure%20Report%202018-08-28%2017-47-20.png)
![alt text](https://github.com/artsok/training/blob/master/images/Allure%20Report%202018-08-28%2017-48-36.png)