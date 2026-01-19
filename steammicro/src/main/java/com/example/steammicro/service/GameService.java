package com.example.steammicro.service;

import com.example.steamapi.dto.request.CreateGameRequest;
import com.example.steamapi.dto.request.UpdateGameRequest;
import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steamapi.dto.response.GameResponse;
import com.example.steamapi.dto.response.PagedResponse;
import com.example.steamapi.execption.IsGameAlreadyExistsException;
import com.example.steamapi.execption.ResourceNotFoundException;
import com.example.steammicro.config.RabbitMQConfig;
import com.example.steammicro.storage.InMemoryStorage;
import events.GameCreatedEvent;
import events.GameDeletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameService {

    private final InMemoryStorage storage;
    private final DeveloperService developerService;
    private final RabbitTemplate rabbitTemplate;

    public GameService(InMemoryStorage storage, @Lazy DeveloperService developerService, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.developerService = developerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public GameResponse findGameById(Long id) {
        return Optional.ofNullable(storage.games.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));
    }

    public PagedResponse<GameResponse> findAllGamesPagination(Long developerId, int page, int size) {
        Stream<GameResponse> gamesStream = storage.games.values().stream()
                .sorted((b1, b2) -> b1.getId().compareTo(b2.getId()));

        if (developerId != null) {
            gamesStream = gamesStream.filter(game -> game.getDeveloperId() != null && game.getDeveloperId().equals(developerId));
        }

        List<GameResponse> allGames = gamesStream.toList();

        int totalElements = allGames.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<GameResponse> pageContent = (fromIndex > toIndex) ? List.of() : allGames.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public List<GameResponse> findAllGames() {
        return storage.games.values().stream().toList();
    }

    public GameResponse createGame(CreateGameRequest request) {
        validateName(request.title());

        DeveloperResponse developer = developerService.findDeveloperById(request.developerId());

        long id = storage.gameSequence.incrementAndGet();
        var game = new GameResponse(
                id,
                request.title(),
                developer.getId(),
                request.description(),
                request.imageUrl(),
                request.price(),
                false,
                0f,
                LocalDateTime.now()
        );

        storage.games.put(id, game);

        var updatedDeveloper = new DeveloperResponse(
                developer.getId(),
                developer.getName(),
                developer.getGeolocation(),
                developer.getGameCount() + 1,
                developer.getRegisteredAt()
        );

        storage.developers.put(developer.getId(), updatedDeveloper);

        GameCreatedEvent event = new GameCreatedEvent(
                game.getId(),
                game.getTitle(),
                developer.getName()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_GAME_CREATED, event);

        return game;
    }

    public GameResponse updateGame(Long id, UpdateGameRequest request) {
        GameResponse existingGame = findGameById(id);

        var updatedGame = new GameResponse(
                id,
                existingGame.getTitle(),
                existingGame.getDeveloperId(),
                request.description(),
                request.imageUrl(),
                request.price(),
                existingGame.getFavourite(),
                existingGame.getDiscount(),
                existingGame.getCreatedAt()
        );

        storage.games.put(id, updatedGame);

        return updatedGame;
    }

    public void applyDiscount(Long gameId, Long finalPrice, Long percentDiscount) {
        GameResponse existingGame = findGameById(gameId);
        
        var updatedGame = new GameResponse(
                gameId,
                existingGame.getTitle(),
                existingGame.getDeveloperId(),
                existingGame.getDescription(),
                existingGame.getImageUrl(),
                finalPrice.intValue(),
                existingGame.getFavourite(),
                percentDiscount.floatValue(),
                existingGame.getCreatedAt()
        );

        storage.games.put(gameId, updatedGame);
    }

    public void deleteGame(Long id) {
        findGameById(id);

        GameDeletedEvent event = new GameDeletedEvent(id);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_GAME_DELETED, event);

        storage.games.remove(id);
    }

    public void deleteGamesByDeveloperId(Long developerId) {
        List<Long> gamesToDelete = storage.games.values().stream()
                .filter(game -> game.getDeveloperId() != null && game.getDeveloperId().equals(developerId))
                .map(GameResponse::getId) // проверить
                .toList();

        gamesToDelete.forEach(storage.games::remove);
    }


    public List<GameResponse> getFavouriteGames() {
        return storage.games.values().stream().filter(GameResponse::getFavourite).toList();
    }

    public GameResponse makeGameFavourite(Long id) {
        GameResponse game = findGameById(id);
        var updatedGame = new GameResponse(
                id,
                game.getTitle(),
                game.getDeveloperId(),
                game.getDescription(),
                game.getImageUrl(),
                game.getPrice(),
                true,
                game.getDiscount(),
                game.getCreatedAt()
        );

        storage.games.put(id, updatedGame);

        return updatedGame;
    }

    private void validateName(String name) {
        storage.games.values().stream()
                .filter(game -> game.getTitle().equalsIgnoreCase(name))
                .filter(game -> game.getId() != null) // разобраться и мб переделать
                .findAny()
                .ifPresent(game -> {throw new IsGameAlreadyExistsException(name);});
    }
}
