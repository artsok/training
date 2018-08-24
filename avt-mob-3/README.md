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

### Настройка окружения для IOS приложений"
1. Требуется установить XCode. После установки появится вот такое окно приветствия:
![alt text](https://github.com/artsok/training/blob/master/images/welcome_to_xcode.png)
2. Проверить установленный симуляторы. Перейти во вкладку Window -> Devices and Simulators
![alt text](https://github.com/artsok/training/blob/master/images/xcode-simulators.png)
Если потребуется еще установить дополнительные симуляторы, то нажимаем кнопочку 'плюс' в окне Devices and Simulators. 
![alt text](https://github.com/artsok/training/blob/master/images/xcode-add-new-sim.png)
3. Скачиваем архив с приложением wikipedia-ios-develop.zip, разархивируем.
4. Открываем Readme.md и следуем инструкции установки (!Внимание - не меняйте порядок действий команд).
```bash
cd Downloads/wikipedia-ios-develop
scripts/setup 
```
Установка зависимостей прошла успешно.
![alt text](https://github.com/artsok/training/blob/master/images/wikipedia-ios-develop%20%E2%80%94%20-bash%20%E2%80%94%2091%C3%9727%202018-08-21%2008-25-58.png)
5. Производим запуск приложения. Переходим в папку с приложением и запустим Wikipedia.xcodeproj (!Внимание: не запускайте его
до установки всех зависимостей. Это может привести к тому, что проект придется удалять и устанавливать все заново). 
Дождитесь индексации всех файлов! 
![alt text](https://github.com/artsok/training/blob/master/images/Empty%20Tab%202018-08-21%2008-34-22.png)
6. Скомпилировать проект и запустить на симуляторе. Производим выбор нужного симулятора и нажимаем кнопку запустить. 
7. Получить app файл
8. Запустить Appium с режимом Inspector
```json
{
  "platformName": IOSTest,
  "platformVersion": "11.3",
  "deviceName": "iPhone 8",
  "app": "/Users/asokovets/IdeaProjects/training/avt-mob-3/src/test/resources/apk/Wikipedia.app"
}
```
Симулятор запускается самостоятельно по сигналу из Appium. Это тот симулятор, который мы указали. 
!Внимание: В IOS, параметр deviceName должен соответствовать названию симулятора. Устанавливается само приложение и WebDriverAgent. 


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
#### 1. Отключить клавиатуру (отрабатывает, когда клавиутара отображается на экране)
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

#### 2. Каждый раз когда мы передаем ссылку на apk файл, то Appium запускает это приложение с чистого листа.  
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

#### 3. Прокрутка экрана
Необходимо вычислить координаты относительного нашего экрана, без конкретных значений. В этом нам поможет Dimension
```kotlin
val (width, height) = driver.manage().window().size
val startX = width / 2 //Ширина устройства дели на 2 части и следовательно попадаем в центр
val startY = (height * 0.80).toInt() //Так мы можем получить начальную точку, которая будет находится в 80% внизу (т.е немного над нижним краем экрана)
val endX = width / 2 //Одинаковая как startX, так как прокручиваем 'SWIPE DOWN'
val endY = (height * 0.20).toInt() //Точка до которой прокручиваем
```
Нажимаем внизу экрана и передвигаемся наверх с определенным timeout. Чем дольше timeout, тем медленее swipe.  
```kotlin
  val touchAction = AndroidTouchAction(driver)
        touchAction.press(PointOption<ElementOption>().withCoordinates(startX, startY))
                .waitAction(waitOptions(ofSeconds(waitSeconds)))
                .moveTo(PointOption<ElementOption>().withCoordinates(endX, endY))
                .release().perform()
```

#### 4. Возможность перезапустить приложение без сброса настроек в Appium
```java
driver.closeApp();
try{driver.runAppInBackground(1);}catch (Exception e) {}
```

#### 5. Appium Capabilities
http://appium.io/docs/en/writing-running-appium/caps/

#### 6. Debug тестов в IDE
Чтобы сессия не отваливалась при отладке, требуется установить данное свойство.
```kotlin
setCapability("newCommandTimeout", 6000 * 5)
```

#### 7. Rotation (Поворот)
У экрана есть две главной ориентации, портретная и альбомная (ландшафтная = экран повернут на 90 градусов)
```kotlin
driver.rotate(ScreenOrientation.LANDSCAPE)
driver.rotate(ScreenOrientation.PORTRAIT)
```
Если одна и ротаций не пройдет, то Appium самостоятельно  выдаст ошибку. Она будет выдана вкачестве Exception.

#### 8. Background
В тестировании мобильных приложений очень часто возникают проблемы с различными состояниями приложения после ухода в background
и возвращения оттуда. Их тоже надо тестировать, в том числе и автоматически.   
```kotlin
driver.runAppInBackground(ofSeconds(3))
```
Выставляем время, в течение которого приложение будет в background. После этого приложение автоматически развернется

#### 9. Ошибки, которые встретились при обучении
https://stackoverflow.com/questions/32533063/sendkey-method-in-appium-sometime-triggers-a-paste-as-well
https://github.com/appium/appium/issues/9684


# Запуск тестов
mvn test -DexcludeTags={tag names}