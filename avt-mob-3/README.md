# Mobile Automation Training
### Настройка окружения и первые шаги для запуска тестов
1. Установить Android SDK
2. Через AVD Manager создать виртуальное устройство
3. Запустить устройство
4. В консоли выполнить команду adb devices и проверить, что запущенный эмулятор отображается
5. Установить APK на устройсво
6. Получить список всех пакетов установленных на нашем устройстве adb shell pm list packages 
7. Найти пакет тестируемого приложения adb shell pm list packages | grep "wiki"
8. Выполним команду при запущенном приложении adb shell dumpsys window windows >> activity.txt, 
чтобы найти главную активность. Далее в созданном файле ищем по пакету MainActivity
9. Запустим Appium Server, нажмем кнопку Start Inspector Session и пропишем  capabilities, 
чтобы Appium подключился к нашему приложенияю.
![alt text](https://github.com/artsok/training/blob/master/images/Appium%202018-07-15%2013-19-13.png)
  