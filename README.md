# java-filmorate

## ER-диаграмма

![](/filmorate_er.png)

## Значения полей:

- status: APPROVED, PENDING;
- rating_id: G, PG, PG-13, R, NC-17;

## Примеры SQL-запросов

### Получение списка 5 популярных фильмов по рейтингу:
```
SELECT f.film_id,
       COUNT(l.user_id)
FROM film AS f
JOIN likes AS l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY COUNT(l.user_id) DESC
LIMIT 5;
```

### Получение списка друзей пользователя:
```
SELECT u.user_id,
       f.friend_id
FROM user AS u
JOIN friend AS f ON u.user_id = f.user_id
GROUP BY u.user_id;
ORDER BY u.name;
```

### Получение списка названий фильмов c рейтингом R:
```
SELECT f.name,
       f.duration
FROM film AS f
JOIN rating AS r ON f.rating_id = r.rating_id
WHERE r.rating_id = 'R'
GROUP BY f.name
ORDER BY f.name;
```