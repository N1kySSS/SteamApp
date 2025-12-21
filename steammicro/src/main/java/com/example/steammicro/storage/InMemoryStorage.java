package com.example.steammicro.storage;

import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steamapi.dto.response.GameResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, DeveloperResponse> developers = new ConcurrentHashMap<>();
    public final Map<Long, GameResponse> games = new ConcurrentHashMap<>();

    public final AtomicLong developerSequence = new AtomicLong(0);
    public final AtomicLong gameSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        DeveloperResponse developer1 = new DeveloperResponse(developerSequence.incrementAndGet(), "Me", "Russia", 1, LocalDateTime.now());
        DeveloperResponse developer2 = new DeveloperResponse(developerSequence.incrementAndGet(), "Valve", "Amerika", 0, LocalDateTime.now());
        developers.put(developer1.getId(), developer1);
        developers.put(developer2.getId(), developer2);

        long gameId1 = gameSequence.incrementAndGet();
        games.put(gameId1, new GameResponse(gameId1, "Forza Horizon 4", developer1.getId(), "гонки крутые", "https://www.progamer.ru/uploads/2021/05/forza4cover2.jpg", 2000, false, 0f, LocalDateTime.now()));

        long gameId2 = gameSequence.incrementAndGet();
        games.put(gameId2, new GameResponse(gameId2, "CS 2", developer2.getId(), "+тильт", "https://images.steamusercontent.com/ugc/19809229419782079/9E09A87AE9B299BC1F0FC1CBA9F20DB16289442A/?imw=512&amp;imh=298&amp;ima=fit&amp;impolicy=Letterbox&amp;imcolor=%23000000&amp;letterbox=true", 1499, false, 0f, LocalDateTime.now()));

        long gameId3 = gameSequence.incrementAndGet();
        games.put(gameId3, new GameResponse(gameId3, "Dota 2", developer2.getId(), "хз", "https://avatars.mds.yandex.net/i?id=af8545a97bad04c6a3a2c3acb9b9ecf6_l-10849338-images-thumbs&n=13", 0, false, 0f, LocalDateTime.now()));
    }
}
