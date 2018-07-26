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

Для IOS параметр deviceName должен четко соотвествовать симулятору, который вы используете. Для Android - имя может быть любое. 

![alt text](https://github.com/artsok/training/blob/master/images/Appium%202018-07-15%2014-26-39.png)

Это означает, что все capabilities были указаны верно.

### Простые сценарии в Appium
1. Чтобы найти элемент необходимо выполнить его поиск с помощью UI Automator Viewer, либо Appium Viewer.
![alt text](https://github.com/artsok/training/blob/master/images/UI%20Automator%20Viewer%202018-07-18%2018-45-51.png)

2. Выполнить действия с элементом:
```kotlin
        val findBt = driver.findElement(By.xpath("//*[contains(@text, 'Search Wikipedia')]"))
        findBt.click() //Кликаем 
        val searchInput = driver.findElementByXPath("//*[contains(@text, 'Search…')]")
        searchInput.sendKeys("Allure Server") //Отправляем текст в поле
```

3. Использовать ожидания при работе с WebElement'ами (using WebDriverWait)
 ```kotlin
     private fun waitElement(locator:By, errorMassage:String = "Can't find element", timeOut:Long = 5): WebElement {
         val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
         return driverWait.until(ExpectedConditions.presenceOfElementLocated(locator))
     }
 ```
### Общее
1. Отключить клавиатуру (отрабатывает, когда клавиутара отображается на экране)
```kotlin
driver.hideKeyboard()
```
, либо при инициализации свойств
```kotlin
val capabilities = DesiredCapabilities().apply {
    setCapability("unicodeKeyboard", true)
    setCapability("resetKeyboard", true)
        }
```

2. Каждый раз когда мы передаем ссылку на apk файл, то Appium запускает это приложение с чистого листа.  
Вопрос: У меня есть приложение которое при первом запуске показывает приветственный экран с которым надо отдельно взаимодействовать, 
и выставлять личные настройки. А можно ли использовать уже установленное приложение без установки нового апк?

В Appium есть несколько ключей для запуска приложения:
1) no_reset
2) full_reset
3) дефолтное состояние

в дефолтном - после сессии приложение закрывается, данные удаляются, приложение не удаляется
no_reset - приложение не закрывается, данные не удаляются, приложение не удаляется
full_reset - приложение закрывается, данные удяляются, приложение удаляется

Ключи, задаем в capabilities
```kotlin
val capabilities = DesiredCapabilities().apply {
    setCapability("noReset", true)
        }
```