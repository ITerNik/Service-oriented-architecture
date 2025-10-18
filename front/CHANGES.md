# Сводка изменений в Front приложении

## Что было сделано

### 1. ✅ Удалено лишнее
- Удален `RouteController.java` - старый контроллер с бизнес-логикой
- Удален `RouteService.java` - сервис с логикой расчетов
- Удалены модели (`City.java`, `Climate.java`, `Coordinates.java`, `Human.java`)
- Очищена структура проекта от ненужных зависимостей

### 2. ✅ Создан API прокси
**Файл:** `ApiProxyController.java`
- Перехватывает ВСЕ запросы с префиксом `/api`
- Случайным образом выбирает один из двух бэкенд-сервисов
- Проксирует запрос на выбранный бэкенд
- Поддерживает все HTTP методы (GET, POST, PUT, DELETE, PATCH)
- Прозрачно передает заголовки и тело запроса
- Поддерживает query параметры

### 3. ✅ Настроена статическая раздача файлов
**Файл:** `SpaController.java`
- Все запросы БЕЗ префикса `/api` отдают статику
- Поддержка SPA (Single Page Application)
- Все не-API маршруты перенаправляются на `index.html`

### 4. ✅ Обновлена конфигурация
**Файл:** `application.properties`
```properties
backend.service1.url=https://localhost:34566
backend.service2.url=https://localhost:34567
```
- URL обоих бэкенд-сервисов вынесены в конфигурацию
- Легко изменить без перекомпиляции

### 5. ✅ Создана демо-страница
**Файл:** `static/index.html`
- Красивая информационная страница
- Показывает как работает прокси
- Встроенные тесты API
- Интерактивные примеры

### 6. ✅ Обновлены файлы
- `FrontApplication.java` - переименован и упрощен
- `AppConfig.java` - настроена раздача статики
- `README.md` - полная документация

## Архитектура

```
┌─────────┐
│ Клиент  │
└────┬────┘
     │
     ▼
┌──────────────────┐
│  Front Service   │
│  (этот проект)   │
└────┬────┬────────┘
     │    │
     ▼    ▼
┌────────┐  ┌────────┐
│Backend1│  │Backend2│
│:34566  │  │:34567  │
└────────┘  └────────┘
```

## Как использовать

### Настройка
Отредактируйте `application.properties`:
```properties
backend.service1.url=https://localhost:34566
backend.service2.url=https://localhost:34567
```

### Сборка
```bash
cd front
mvn clean package
```

### Развертывание
Скопируйте WAR файл на WildFly:
```bash
cp target/front-0.0.1-SNAPSHOT.war $WILDFLY_HOME/standalone/deployments/
```

### Использование

#### Статические файлы:
- `http://localhost:8080/` → index.html
- `http://localhost:8080/index.html` → статика
- `http://localhost:8080/styles.css` → статика

#### API запросы (проксируются):
- `http://localhost:8080/api/cities` → бэкенд
- `http://localhost:8080/api/cities/1` → бэкенд
- `http://localhost:8080/api/route/...` → бэкенд

## Структура проекта

```
front/
├── src/main/
│   ├── java/ru/ifmo/front/
│   │   ├── FrontApplication.java          # Главный класс
│   │   ├── config/
│   │   │   └── AppConfig.java             # Конфигурация
│   │   └── controller/
│   │       ├── ApiProxyController.java    # 🔥 API прокси
│   │       └── SpaController.java         # 🔥 SPA контроллер
│   └── resources/
│       ├── static/
│       │   └── index.html                 # 🔥 Демо-страница
│       └── application.properties         # 🔥 Конфигурация
└── pom.xml
```

## Преимущества

✅ **Простота** - минимум кода, максимум функциональности
✅ **Гибкость** - легко добавить больше бэкендов
✅ **Балансировка** - автоматическое распределение нагрузки
✅ **Статика** - полноценный веб-сервер для фронтенда
✅ **SPA** - поддержка современных фреймворков (React, Vue, Angular)
✅ **Прозрачность** - клиенты не знают о существовании прокси

## Готово к использованию! 🚀
