# Мониторинг приложения: JConsole и VisualVM

## Реализованные MBean-классы

### 1. `PointStats` (`web03:type=Monitoring,name=PointStats`)

Интерфейс: `mbean.PointStatsMBean`  
Реализация: `mbean.PointStats` (расширяет `NotificationBroadcasterSupport`)

| Атрибут | Тип | Описание |
|---|---|---|
| `TotalPoints` | `long` | Общее число точек, введённых пользователем |
| `MissedPoints` | `long` | Число точек, **не попавших** в область |
| `ConsecutiveMisses` | `int` | Текущая серия промахов подряд |

**Оповещение:** при достижении 4 промахов подряд MBean отправляет JMX-уведомление типа `point.consecutive.misses` с сообщением `"4 промаха подряд! Всего промахов: N"`. Счётчик серии при этом **не сбрасывается** — уведомления продолжают рассылаться на каждый последующий промах, пока не будет засчитано попадание.

**Операции:**
- `reset()` — сбросить все счётчики в ноль.

### 2. `AreaCalculator` (`web03:type=Monitoring,name=AreaCalculator`)

Интерфейс: `mbean.AreaMBean`  
Реализация: `mbean.AreaCalculator`

| Атрибут | Тип | Описание |
|---|---|---|
| `R` | `double` | Текущий радиус (синхронизируется с формой) |
| `Area` | `double` | Суммарная площадь фигуры |
| `CircleArea` | `double` | Площадь четверти круга (Q1) |
| `RectangleArea` | `double` | Площадь прямоугольника (Q3) |
| `TriangleArea` | `double` | Площадь треугольника (Q4) |

**Формула площади** (при текущем R):

```
Area  = π·R²/16      ← четверть круга радиуса R/2 (Q1, x≥0, y≥0)
      + R²/2         ← прямоугольник [−R/2;0]×[−R;0] (Q3, x≤0, y≤0)
      + R²/8         ← прямоугольный треугольник с катетами R/2 (Q4, x≥0, y≤0)
```

Для R=2: `Area ≈ 0.785 + 2.0 + 0.5 = 3.285`

---

## Регистрация MBeans в приложении

MBeans регистрируются при инициализации `bean.AreaCheckBean` (метод `registerMBeans()`), вызываемой из `@PostConstruct`. Используется стандартный `ManagementFactory.getPlatformMBeanServer()`.

При каждом вводе точки (`checkPoint()`):
- `PointStats.recordPoint(hit)` обновляет счётчики и при необходимости отправляет уведомление;
- `AreaCalculator.setR(r)` синхронизирует текущий радиус.

---

## Ant-задачи

### Основная сборка и деплой

```bash
ant resolve      # скачать зависимости через Gradle в lib/
ant compile      # скомпилировать исходники
ant build        # собрать WAR-архив (вызывает compile)
ant test         # запустить JUnit 5 тесты (вызывает build)
ant scp          # задеплоить WAR на Helios по SCP (вызывает build)
ant clean        # удалить build/ и временные файлы
```

### Документация и дополнительные задачи

```bash
ant doc          # Javadoc + MD5/SHA-1 в MANIFEST.MF
ant xml          # валидация всех XML-файлов проекта
ant native2ascii # конвертация локализационных файлов UTF-8 → ASCII
ant music        # собрать WAR и воспроизвести звук
ant diff         # автокоммит, если watch.classes не изменены
ant report       # запустить тесты, сохранить JUnit-отчёт в git
ant env          # сборка в двух альтернативных JDK (Java 17 + Java 21)
ant alt          # сборка альтернативной версии с переименованными классами
ant team         # сборка 2 предыдущих git-ревизий и упаковка в ZIP
ant history      # найти последнюю компилируемую ревизию, показать diff
```

### Деплой и мониторинг

```bash
ant deploy-local  # собрать WAR и задеплоить в локальный WildFly (требует sudo)
ant jconsole      # запустить JConsole от root (sudo -E jconsole)
ant visualvm      # запустить VisualVM от root (sudo -E visualvm)
ant jmx-report    # найти PID WildFly и вывести адреса MBeans
```

---

## Задание 2: Мониторинг через JConsole

### Почему стандартные способы не работают

| Способ | Почему не работает |
|---|---|
| `service:jmx:remote+http://localhost:9990` | Требует `jboss-client.jar` на classpath JConsole. В JDK ≥ 9 нет отдельного `jconsole.jar`, добавить jar стандартным способом невозможно. |
| `-Dcom.sun.management.jmxremote.port=9999` | JVM management agent запускается **до** javaagent'ов и `-jar jboss-modules.jar`. `java.util.logging` инициализируется раньше, чем WildFly успевает установить свой LogManager — сервер падает при старте. |
| Local Process в JConsole (без sudo) | WildFly работает как root, служебные файлы хранятся в `/tmp/hsperfdata_root/`, недоступном для других пользователей. |

### Правильное решение: запускать JConsole от того же пользователя, что и WildFly

WildFly запущен через `sudo` (от root) → JConsole тоже нужно запускать от root. Тогда локальное attach-соединение по PID работает без URL и пароля.

```bash
# Предпочтительный вариант:
sudo -E jconsole

# Если sudo -E не работает (env_reset в sudoers):
sudo DISPLAY=$DISPLAY XAUTHORITY=$XAUTHORITY jconsole

# Через Ant:
ant jconsole
```

### Подключение к WildFly

1. JConsole откроется с диалогом **New Connection**.
2. Перейдите на вкладку **Local Process**.
3. Найдите процесс `jboss-modules.jar` / `[Standalone]` — это WildFly.
4. Выберите его и нажмите **Connect**.

> **На Helios (se.ifmo.ru):** JConsole и WildFly работают на одном хосте — подключайтесь через SSH с X11-forwarding:
> ```bash
> ssh -X -p 2222 s381731@se.ifmo.ru
> sudo -E jconsole
> ```

### Снятие показаний MBeans

1. В JConsole откройте вкладку **MBeans**.
2. Раскройте домен **web03** → **Monitoring**.
3. Выберите **PointStats** → раздел **Attributes**:
   - `TotalPoints` — общее число точек.
   - `MissedPoints` — число промахов.
   - `ConsecutiveMisses` — текущая серия промахов.
4. Выберите **AreaCalculator** → раздел **Attributes**:
   - `R` — текущий радиус.
   - `Area` — площадь фигуры.
   - `CircleArea`, `RectangleArea`, `TriangleArea` — части фигуры.

**Подписка на уведомления PointStats:**
1. Выберите `web03:type=Monitoring,name=PointStats`.
2. Вкладка **Notifications** → кнопка **Subscribe**.
3. Вводите 4 промаха подряд — в таблице появится уведомление типа `point.consecutive.misses`.

### Определение ОС и версии JVM

В JConsole → вкладка **MBeans** → домен `java.lang` → `OperatingSystem` → **Attributes**:

| Атрибут | Значение (пример) |
|---|---|
| `Name` | `Linux` |
| `Version` | `6.8.0-124-generic` |
| `Arch` | `amd64` |
| `AvailableProcessors` | `4` |
| `TotalPhysicalMemorySize` | `8 589 934 592` |

Аналогично `java.lang` → `Runtime` → `VmName`, `VmVersion` для версии JVM.

---

## Задание 3: Мониторинг и профилирование через VisualVM

### Установка и запуск

`jvisualvm` удалён из JDK 9+. apt-пакет `visualvm` (2.1.8) поставляется без MBeans-плагина, поэтому функция "Add to Chart" в нём недоступна. Нужна полная standalone-версия:

```bash
cd ~
wget https://github.com/oracle/visualvm/releases/download/2.2.1/visualvm_221.zip
unzip visualvm_221.zip
```

Запуск от root (WildFly работает как root):

```bash
sudo DISPLAY=$DISPLAY XAUTHORITY=$XAUTHORITY ~/visualvm_221/bin/visualvm
# или через Ant:
ant visualvm
```

```bash
jvisualvm
# или через Ant:
ant visualvm
```

### Подключение к WildFly

WildFly работает как root → VisualVM тоже запускайте от root:

```bash
sudo -E visualvm
# или:
sudo DISPLAY=$DISPLAY XAUTHORITY=$XAUTHORITY visualvm
# или через Ant:
ant visualvm
```

После запуска:
1. В панели **Applications** раскройте **Local**.
2. Дважды щёлкните на процессе `jboss-modules.jar` / `[Standalone]`.

### График изменения MBeans во времени — JConsole

VisualVM 2.x убрал "Add to Chart" из контекстного меню MBeans Browser. Для live-графиков MBean-атрибутов используйте **JConsole**:

1. В JConsole → вкладка **MBeans** → `web03 → Monitoring → PointStats → Attributes`.
2. **Двойной клик** по строке `TotalPoints` — внизу панели откроется live-график.
3. Аналогично для `MissedPoints`, `ConsecutiveMisses`.
4. Перейдите к **AreaCalculator** → двойной клик на `Area`, `R`.
5. Вводите точки в приложении — линии обновляются в реальном времени.

### Профилирование памяти — Sampler

**Profiler → Memory** требует инструментирования байткода и не работает с WildFly (ошибка "Provided Memory settings are invalid"). Используйте **Sampler** — он работает через стандартный JVM attach:

1. В VisualVM выберите процесс WildFly → вкладка **Sampler**.
2. Нажмите **Memory** → **Start**.
3. Поработайте с приложением (вводите точки несколько раз).
4. Нажмите **Snapshot**.
5. В таблице **Classes** отсортируйте по колонке **Bytes** (убывание).

**Ожидаемые результаты:**
- Наибольший объём занимают объекты стандартных классов JVM, например `byte[]`, `char[]`, `java.lang.String` — они накапливаются фреймворками (Weld, JSF, WildFly internals).
- Среди **пользовательских классов** наибольший объём занимает `entity.Result`: каждый введённый пользователем запрос создаёт новый объект `Result`, которые накапливаются в `ResultsBean.results` (поле типа `List<Result>`).

Для нахождения пользовательского класса-владельца:
1. В Heap Dump выберите `entity.Result` → **Show in Instances** (или **References**).
2. В дереве ссылок найдите обратные ссылки — они укажут на `bean.ResultsBean.results`.

---

## Результаты

### JConsole — показания MBeans (пример при R=2, после 10 точек, из которых 6 промахов)

| MBean | Атрибут | Значение |
|---|---|---|
| `PointStats` | `TotalPoints` | `10` |
| `PointStats` | `MissedPoints` | `6` |
| `PointStats` | `ConsecutiveMisses` | `2` |
| `AreaCalculator` | `R` | `2.0` |
| `AreaCalculator` | `Area` | `3.2853...` |
| `AreaCalculator` | `CircleArea` | `0.7853...` |
| `AreaCalculator` | `RectangleArea` | `2.0` |
| `AreaCalculator` | `TriangleArea` | `0.5` |

### JConsole — ОС и JVM

| Параметр | Значение |
|---|---|
| ОС | Linux 6.8.0-124-generic (amd64) |
| JVM | OpenJDK 64-Bit Server VM, версия 17.x или 21.x |

### VisualVM — класс с наибольшим объёмом памяти

| # | Класс | Тип |
|---|---|---|
| 1 | `byte[]` | JDK-внутренний |
| 2 | `java.lang.String` | JDK-внутренний |
| … | … | … |
| — | `entity.Result` | **пользовательский класс** |

Экземпляры `entity.Result` хранятся в поле `results: List<Result>` бина `bean.ResultsBean`.
