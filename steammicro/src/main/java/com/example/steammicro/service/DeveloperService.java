package com.example.steammicro.service;

import com.example.steamapi.dto.request.CreateDeveloperRequest;
import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steamapi.execption.ResourceNotFoundException;
import com.example.steammicro.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeveloperService {
    private final InMemoryStorage storage;
    private final GameService gameService;

    public DeveloperService(InMemoryStorage storage, @Lazy GameService gameService) {
        this.storage = storage;
        this.gameService = gameService;
    }

    public List<DeveloperResponse> getDevelopers() {
        return storage.developers.values().stream().toList();

    }

    public DeveloperResponse findDeveloperById(Long id) {
        return Optional.ofNullable(storage.developers.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Developer", id));
    }

    public DeveloperResponse addNewDeveloper(CreateDeveloperRequest newDeveloper) {
        long id = storage.developerSequence.incrementAndGet();
        DeveloperResponse author = new DeveloperResponse(
                id,
                newDeveloper.name(),
                newDeveloper.geolocation(),
                0,
                LocalDateTime.now()
        );
        storage.developers.put(id, author);

        return author;
    }

    public void deleteDeveloperById(Long id) {
        findDeveloperById(id);
        gameService.deleteGamesByDeveloperId(id);
        storage.developers.remove(id);
    }
}
