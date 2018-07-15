# Mobile Automation Training
### Подготовка APK
1. Нашли приложение в Google Play
2. Скопировали web-ссылку c Google Play
3. Открыли apps.evozi.com/apk-downloader/, вставили ссылку на приложение и нажали скачать

![alt text](https://github.com/artsok/training/blob/master/images/APK%20Downloader%202018-07-15%2014-11-00.png)

### Установка APK на эмулятор
1. Выделили apk и перетащили (drag and drop) в запущенный эмулятор/Либо 'adb install org.wikipedia.apk'
2. Убедиться, что приложение установлено и запускается

![alt text](https://github.com/artsok/training/blob/master/images/Nexus_6P_API_27:5554%202018-07-15%2014-14-47.png)


### Настройка окружения и первые шаги для запуска тестов
1. Установить Android SDK
2. Через AVD Manager создать виртуальное устройство
3. Запустить устройство
4. В консоли выполнить команду 'adb devices' и проверить, что запущенный эмулятор отображается
5. Установить APK на устройсво
6. Получить список всех пакетов установленных на нашем устройстве 'adb shell pm list packages' 
7. Найти пакет тестируемого приложения 'adb shell pm list packages | grep "wiki"'
8. Выполним команду при запущенном приложении 'adb shell dumpsys window windows >> activity.txt', 
чтобы найти главную активность. Далее в созданном файле ищем по пакету MainActivity
9. Запустим Appium Server, нажмем кнопку Start Inspector Session и пропишем  capabilities, 
чтобы Appium подключился к нашему приложенияю.

![alt text](https://github.com/artsok/training/blob/master/images/Appium%202018-07-15%2013-19-13.png)
![alt text](https://github.com/artsok/training/blob/master/images/Appium%202018-07-15%2014-01-06.png)

Далее нажимаем кнопку Start Session, начинает работать Appium, открыл приложение и подключился к нему с помощью собственного инспектора.

![alt text](https://github.com/artsok/training/blob/master/images/Appium%202018-07-15%2014-26-39.png)

Это означает, что все capabilities были указаны верно. 

  
