package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmImpl.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        log.debug("MpaService: запрос на получение всех рейтингов.");
        return mpaStorage.findAll();
    }

    public Mpa get(Integer mpaId) {
        log.debug("MpaService: запрос на получение рейтинга с id: {}.", mpaId);
        validateMpaExists(mpaId);
        return mpaStorage.get(mpaId);
    }

    public void validateMpaExists(Integer mpaId) {
        log.debug("MpaService: запрос на проверку наличия рейтинга с id: {} в БД.", mpaId);
        if (!mpaStorage.validateDataExists(mpaId)) {
            String message = "Рейтинга c таким id не существует.";
            throw new MpaDoesNotExistException(message);
        }
    }
}
