# Аутентификация и авторизация с использованием Spring Security и JWT

### Описание:
Базовое веб-приложение с использованием Spring Security и JWT для аутентификации и авторизации пользователей

### Требования:
- Настроить базовую конфигурацию Spring Security для приложения
- Использовать JWT для аутентификации пользователей
- Создать контроллеры для аутентификации и регистрации пользователей
- Реализовать сохранение пользователей в базу данных PostgreSQL
- Добавить поддержку ролей пользователей и настройть авторизацию на основе ролей
---

### Технологии:
- Java 21
- Spring Boot
- Hibernate
- Postgres
- Maven
- Lombok
- Swagger
---

### Запуск приложения:
1. Склонируйте репозиторий командой: git clone git@github.com:weare4saken/spring-jwt-auth.git
2. Запустить метод `main` в классе `SpringJwtAuthApplication`
3. Swagger будет доступен по [ссылке](http://localhost:8080/swagger-ui/index.html#/)
4. Для аутентификации доступны следующие пользователи:
```
[User]
username: user
password: pwd12345

[Admin]
username: admin
password: pwd12345
```