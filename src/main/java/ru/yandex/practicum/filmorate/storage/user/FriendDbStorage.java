package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RowMapper;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.StorageDbCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository("FriendDbStorage")
public class FriendDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Storage<User> userStorage;
    private final StorageDbCommon storageDbCommon;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") Storage<User> userStorage,
                           StorageDbCommon storageDbCommon) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.storageDbCommon = storageDbCommon;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        log.debug("FriendStorage: запрос к БД от пользователя c id: {} на добавление в друзья пользователя с id: {}.",
                userId, friendId);
        String sql = "INSERT INTO FRIENDS(user_id, friend_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql, userId, friendId);
        List<User> result = new ArrayList<>();
        result.add(userStorage.get(userId));
        result.add(userStorage.get(friendId));
        return result;
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        log.debug("FriendStorage: запрос к БД от пользователя c id: {} на удаление из друзей пользователя с id: {}.",
                userId, friendId);
        String sqlDelete = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDelete, userId, friendId);
        List<User> result = new ArrayList<>();
        result.add(userStorage.get(userId));
        result.add(userStorage.get(friendId));
        return result;
    }

    public List<User> getAllFriends(Integer userId) {
        log.debug("FriendStorage: запрос к БД на получение списка всех друзей пользователя id: {}.", userId);
        String sql = "SELECT uf.friend_id, u.user_id, u.user_email, u.user_name, u.user_login, u.user_birthday " +
                "FROM FRIENDS AS uf " +
                "LEFT JOIN USERS AS u ON uf.friend_id = u.user_id " +
                "WHERE uf.user_id = ?";
        List<User> result = jdbcTemplate.query(sql, RowMapper::mapRowToUser, userId);
        result.forEach(storageDbCommon::setFriend);
        return result;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        log.debug("FriendStorage: запрос к БД на получение общих друзей пользователей c id: {} и id: {}.",
                userId, otherId);
        String sql = "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ? and friend_id IN (SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE user_id = ?)";
        List<Integer> common_id = jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, userId, otherId);
        return common_id.stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }
}
