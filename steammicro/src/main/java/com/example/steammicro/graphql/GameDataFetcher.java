package com.example.steammicro.graphql;

import com.example.steamapi.dto.request.CreateGameRequest;
import com.example.steamapi.dto.request.UpdateGameRequest;
import com.example.steamapi.dto.response.GameResponse;
import com.example.steamapi.dto.response.PagedResponse;
import com.example.steammicro.service.DeveloperService;
import com.example.steammicro.service.GameService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.Map;

@DgsComponent
public class GameDataFetcher {

    private final GameService gameService;
    private final DeveloperService developerService;

    public GameDataFetcher(GameService gameService, DeveloperService developerService) {
        this.gameService = gameService;
        this.developerService = developerService;
    }

    @DgsQuery
    public GameResponse gameById(@InputArgument Long id) {
        return gameService.findGameById(id);
    }

    @DgsQuery
    public PagedResponse<GameResponse> games(@InputArgument Long developerId, @InputArgument int page, @InputArgument int size) {
        return gameService.findAllGamesPagination(developerId, page, size);
    }

    @DgsQuery
    public List<GameResponse> allGames() {
        return gameService.findAllGames();
    }

    @DgsQuery
    public List<GameResponse> favouriteGames() {
        return gameService.getFavouriteGames();
    }


    @DgsMutation
    public GameResponse createGame(@InputArgument("input") Map<String, Object> input) {
        CreateGameRequest request = new CreateGameRequest(
                (String) input.get("title"),
                Long.parseLong(input.get("developerId").toString()),
                Integer.parseInt(input.get("price").toString()),
                (String) input.get("description"),
                (String) input.get("imageUrl")
        );
        return gameService.createGame(request);
    }

    @DgsMutation
    public GameResponse updateGame(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        UpdateGameRequest request = new UpdateGameRequest(
                input.get("price") != null ? Integer.parseInt(input.get("price").toString()) : null,
                (String) input.get("description"),
                (String) input.get("imageUrl")
                );
        return gameService.updateGame(id, request);
    }

    @DgsMutation
    public Long deleteGame(@InputArgument Long id) {
        gameService.deleteGame(id);
        return id;
    }

    @DgsMutation
    public GameResponse makeGameFavourite(@InputArgument Long id) {
        return gameService.makeGameFavourite(id);
    }
}
