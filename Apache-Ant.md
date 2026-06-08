# Apache Ant — сборка web-03

## Установка

### 1. Ant

```bash
sudo apt install ant ant-optional
ant -version   # Apache Ant(TM) version 1.10.x
```

`ant-optional` добавляет задачи `<scp>`, `<native2ascii>`, `<sound>` и др.

### 2. jsch (нужен для `ant scp`)

```bash
# Скопировать jsch из кэша Gradle в ANT_HOME/lib
sudo cp ~/.gradle/wrapper/dists/gradle-*/*/gradle-*/lib/plugins/jsch-*.jar \
        /usr/share/ant/lib/
```

### 3. Зависимости проекта в `lib/`

```bash
./gradlew copyAntDeps
```

Задача создаёт:
| Директория | Содержимое |
|---|---|
| `lib/` | Compile-time JAR (Jakarta EE API, JSF, PostgreSQL, PrimeFaces, Jackson) |
| `lib/war/` | Только то, что попадёт в WAR: PrimeFaces, Jackson |
| `lib/test/` | JUnit 5 + junit-platform-launcher |

---

## Файл параметров `build.properties`

Все переменные вынесены в `build.properties`. Чувствительные данные (хост, ключ) переопределяются через `.env`:

```properties
# .env — НЕ коммитить
helios.user=s381731
helios.host=helios.cs.ifmo.ru
helios.deploy.path=/home/studs/s381731/web-03/
helios.key.path=/home/ваш_пользователь/.ssh/id_rsa
```

`.env` уже добавлен в `.gitignore`.

---

## Цели (targets)

### Основные

| Команда | Описание |
|---|---|
| `ant compile` | Компилирует `src/main/java` → `build/ant/classes/` |
| `ant build` | `compile` + упаковка в `build/ant/dist/web.war` |
| `ant clean` | Удаляет `build/ant/` и файлы `*.md5`/`*.sha1` рядом с исходниками |
| `ant test` | `build` + компиляция тестов + запуск JUnit 5; отчёты в `build/ant/reports/` |

### Деплой и сервер

| Команда | Описание |
|---|---|
| `ant scp` | `build` + копирует WAR на Helios по SCP (читает креды из `.env`) |

**Требования для `ant scp`:**
- `ant-jsch.jar` и `jsch-*.jar` в `/usr/share/ant/lib/`
- Пароль пользователя задан в `.env` (поле `helios.password`)

### Документация и валидация

| Команда | Описание |
|---|---|
| `ant doc` | `build` + Javadoc в `build/ant/javadoc/` + MD5/SHA-1 исходников в `MANIFEST.MF` |
| `ant xml` | Валидация всех `*.xml` в проекте (кроме `build/`, `.gradle/`, `wildfly/`) |
| `ant native2ascii` | Конвертирует `src/main/resources/l10n/*.properties` (UTF-8 → `\uXXXX`) в `build/ant/l10n/` |

### Git и отчёты

| Команда | Описание |
|---|---|
| `ant diff` | Проверяет `git status`; если `AreaCheckBean.java`/`DatabaseService.java` **не изменены** — делает `git add -A && git commit` |
| `ant report` | `test` + `git add build/ant/reports/ && git commit` с меткой времени |

### Сборка в разных окружениях

| Команда | Описание |
|---|---|
| `ant env` | Компилирует и упаковывает WAR двумя разными JDK (Java 17 и 21), результат: `env1-web.war`, `env2-web.war` |
| `ant alt` | `build` + создаёт альтернативную версию с переименованием `AreaCheckBean→AreaValidatorBean` и `checkArea→validateArea`, упаковывает в `build/ant/alt/alt-web.war` |
| `ant team` | Берёт 2 предыдущих коммита из git, компилирует каждый, упаковывает в `build/ant/team/team-builds.zip` |
| `ant history` | Пытается скомпилировать текущий HEAD; если не компилируется — ищет последнюю рабочую ревизию в git и записывает diff в `build/ant/history-diff.txt` |

### Музыка

```bash
ant music   # build + воспроизвести /usr/share/sounds/alsa/Front_Center.wav
```

Файл звука переопределяется в `build.properties`:
```properties
music.file=/путь/к/файлу.wav
```

---

## Типичный рабочий процесс

```bash
# Первый раз — установить зависимости
./gradlew copyAntDeps

# Обычная разработка
ant build               # собрать WAR
ant test                # запустить тесты

# Перед отправкой на сервер
ant scp                 # задеплоить на Helios
# или вручную
ant build
scp build/ant/dist/web.war s381731@helios.cs.ifmo.ru:/home/studs/s381731/web-03/

# Зафиксировать отчёт о тестах
ant report

# Автокоммит не-критичных изменений
ant diff
```

---

## Структура MANIFEST.MF

Создаётся при `ant build`, расширяется при `ant doc`:

```
Implementation-Title: web
Implementation-Version: 1.0.0
Main-Class: bean.AreaCheckBean
Built-By: <user.name>
Build-Time: 2026-06-08 20:00:00
# После ant doc:
Source-MD5: <combined MD5 всех .java>
Source-SHA1: <combined SHA-1 всех .java>
```

---

## Настройка альтернативных окружений (`ant env`)

Пути к JDK и JVM-аргументы задаются в `build.properties`:

```properties
env.java.home.1=/usr/lib/jvm/java-17-openjdk-amd64
env.java.home.2=/usr/lib/jvm/java-21-openjdk-amd64
env.jvm.args.1=-Xmx256m -Xms64m
env.jvm.args.2=-Xmx512m -Xms128m
```

---

## Настройка `ant alt` (замена через replaceregexp)

Параметры замены в `build.properties`:

```properties
alt.token=AreaCheckBean          # что заменяем (класс)
alt.replacement=AreaValidatorBean  # на что
alt.method.token=checkArea       # что заменяем (метод)
alt.method.replacement=validateArea
```

---

## Диагностика

| Симптом | Решение |
|---|---|
| `Could not load definitions from resource org/apache/tools/ant/antlib.xml` | `ant` не установлен: `sudo apt install ant` |
| `scp: taskdef A class ... could not be found` | Нет `ant-jsch.jar`: `sudo apt install ant-optional` + скопировать `jsch-*.jar` |
| `javac: error: invalid source release` | Проверить `java.source`/`java.target` в `build.properties`; доступные JDK: `/usr/lib/jvm/` |
| `junitlauncher` не найден | Нужен Ant ≥ 1.10.6: `ant -version` |
| `junit-platform-launcher` не в classpath | Запустить `./gradlew copyAntDeps` снова |
| Компиляция: `class file has wrong version` | Несовпадение JDK между Gradle и Ant; установить `java.source=17` |
