package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.store.FileDbStore;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FileServices {
	private FileDbStore dbStore;
	
	public Optional<File> findFileById(int id) {
		return  dbStore.findFile(id);
	}
}
