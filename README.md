# Требования приложения
Для своей работы приложение требует ввода адреса веб-страницы, вторым необязательным аргументом является путь, куда будет сохранена веб-страница. По умолчанию это корень приложения и имя файла page.html.
Приложение написано с использованием функционала JDK 11.

# Работа с приложением
Проект можно открыть в IntelliJ IDEA и сделать запуск оттуда.
Также приложение можно запускать из консоли, и передать аргументы при вызове программы.



# Инструкция для запуска из консоли в windows, в других ОС суть такая же, но могут отличаться команды

`cd "[путь к корню приложения]\out\production\testTask" `            (Теперь это текущая директория, включая директорию для файлов создаваемых приложением - log, page.html)

`java Program`      						(Запуск приложения без аргументов)

Если есть желание передать аргументы сразу, делаем это вот так:

`java Program "https://www.reddit.com/" "mypage.html"`   (Запуск приложения с двумя аргументами) 

Если нет желания то делаем так:

`java Program "https://www.reddit.com/"`   (Запуск приложения с одним,обязательным аргументом) 

# Настройка проекта для запуска в IDEA
Заходим в "run-debug configurations", добавляем новую конфигурацию типа "Application", выбираем версию джавы(не ниже 11), выбираем в окне выбора класса с методом main класс Program, расположенный в проекте. Далее apply и OK. Приложение готово к запуску. 

# Нюансы относительно ввода исходных данных
Если при запросе ввода  ввести пустые строки в качестве адреса html страницы и пути к файлу, запустится тестовый метод и покажет работу приложения на тестовых веб-страницах.

# Логирование
В корне приложения находится log файл, в него будут записаны ошибки, возникающие во время работы приложения например ошибка при подключении к веб-ресурсу, ошибки файловой системы и т.д. 
