# Front Service - Прокси и Статический Сервер

## Описание

Этот сервис выполняет две основные функции:
1. **API Прокси**: Перенаправляет все запросы с префиксом `/api` на один из бэкенд-сервисов (с балансировкой нагрузки)
2. **Статический Сервер**: Отдает статический контент (HTML, CSS, JS, изображения) для всех остальных запросов

## Архитектура

```
                           ┌─────────────────────────┐
                           │    Front Service        │
                           │  (API Proxy + Static)   │
                           └──────────┬──────────────┘
                                      │
                    ┌─────────────────┴────────────────┐
                    │                                  │
            /api/route/*                          /api/*
                    │                                  │
                    ▼                                  ▼
        ┌───────────────────────┐      ┌──────────────────────────┐
        │   Service 2 (:34567)  │      │   Service 1 (:34566)     │
        │  calculating-service  │      │ collection-managing-svc  │
        └───────────────────────┘      └──────────────────────────┘
```

## Маршрутизация

### API запросы (прокси)
Все запросы начинающиеся с `/api` автоматически проксируются на бэкенд:
- `GET /api/cities` → проксируется на бэкенд
- `POST /api/cities` → проксируется на бэкенд
- `PUT /api/cities/1` → проксируется на бэкенд
- и т.д.

### Статические файлы
Все остальные запросы отдают статический контент:
- `GET /` → `index.html`
- `GET /index.html` → статический файл
- `GET /styles.css` → статический файл
- `GET /script.js` → статический файл

## Конфигурация

Настройте URL бэкенд-сервисов в `application.properties`:

```properties
# URLs backend services
backend.service1.url=https://localhost:34566
backend.service2.url=https://localhost:34567
```

## Маршрутизация запросов

Прокси распределяет запросы между бэкенд-сервисами по следующим правилам:

- **`/api/route/*`** → Service 2 (calculating-service)
- **`/api/*`** (все остальные) → Service 1 (collection-managing-service)

## Структура проекта

```
front/
├── src/main/
│   ├── java/ru/ifmo/front/
│   │   ├── FrontApplication.java          # Главный класс приложения
│   │   ├── config/
│   │   │   └── AppConfig.java             # Конфигурация Spring MVC
│   │   └── controller/
│   │       ├── ApiProxyController.java    # Прокси для API запросов
│   │       └── SpaController.java         # Контроллер для SPA
│   └── resources/
│       ├── static/                        # Статические файлы
│       │   └── index.html
│       └── application.properties         # Настройки приложения
└── pom.xml
```

## Использование

1. Настройте URL бэкенд-сервисов в `application.properties`
2. Поместите ваши статические файлы в `src/main/resources/static/`
3. Соберите приложение: `./mvnw clean package`
4. Разверните WAR файл на WildFly

## Примеры запросов

### Через curl:
```bash
# API запрос (будет проксирован)
curl http://localhost:8080/api/cities

# Статический контент
curl http://localhost:8080/index.html
```

### Через JavaScript:
```javascript
// API запрос
fetch('/api/cities')
    .then(response => response.json())
    .then(data => console.log(data));
```

## Особенности

- ✅ Автоматическая балансировка нагрузки между бэкендами
- ✅ Поддержка всех HTTP методов (GET, POST, PUT, DELETE, PATCH)
- ✅ Прозрачное проксирование заголовков
- ✅ Поддержка query параметров
- ✅ Обработка ошибок
- ✅ SPA поддержка (все не-API маршруты → index.html)
