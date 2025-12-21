package com.example.steammicro.controller;

import com.example.steamapi.api.GameApi;
import com.example.steamapi.dto.request.CreateGameRequest;
import com.example.steamapi.dto.request.UpdateGameRequest;
import com.example.steamapi.dto.response.GameResponse;
import com.example.steamapi.dto.response.PagedResponse;
import com.example.steammicro.assemblers.GamesModelAssembler;
import com.example.steammicro.config.RabbitMQConfig;
import com.example.steammicro.service.GameService;
import events.GameDiscountAddedEvent;
import grpc.demo.AnalyticsServiceGrpc;
import grpc.demo.GameDiscountRequest;
import jakarta.validation.Valid;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController implements GameApi {

    private final GameService gameService;
    private final GamesModelAssembler gameModelAssembler;
    private final PagedResourcesAssembler<GameResponse> pagedResourcesAssembler;

    @GrpcClient("analytics-service")
    private AnalyticsServiceGrpc.AnalyticsServiceBlockingStub analyticsStub;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public GameController(GameService gameService, GamesModelAssembler gameModelAssembler, PagedResourcesAssembler<GameResponse> pagedResourcesAssembler, RabbitTemplate rabbitTemplate) {
        this.gameService = gameService;
        this.gameModelAssembler = gameModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ResponseEntity<EntityModel<GameResponse>> createGame(@Valid CreateGameRequest request) {
        GameResponse createdGame = gameService.createGame(request);
        EntityModel<GameResponse> entityModel = gameModelAssembler.toModel(createdGame);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<GameResponse> makeGameFavourite(Long id) {
        GameResponse updatedGame = gameService.makeGameFavourite(id);
        return gameModelAssembler.toModel(updatedGame);
    }

    @Override
    public EntityModel<GameResponse> getGameById(Long id) {
        GameResponse game = gameService.findGameById(id);
        return gameModelAssembler.toModel(game);
    }

    @Override
    public CollectionModel<EntityModel<GameResponse>> getAllGames() {
        List<GameResponse> games = gameService.findAllGames();
        return gameModelAssembler.toCollectionModel(games);
    }

    @Override
    public PagedModel<EntityModel<GameResponse>> getAllGamesPagination(Long developerId, int page, int size) {
        PagedResponse<GameResponse> pagedResponse = gameService.findAllGamesPagination(developerId, page, size);
        Page<GameResponse> bookPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );

        return pagedResourcesAssembler.toModel(bookPage, gameModelAssembler);
    }

    @Override
    public CollectionModel<EntityModel<GameResponse>> getFavouriteGames() {
        List<GameResponse> games = gameService.getFavouriteGames();
        return gameModelAssembler.toCollectionModel(games);
    }

    @Override
    public EntityModel<GameResponse> updateGame(Long id, @Valid UpdateGameRequest request) {
        GameResponse updatedGame = gameService.updateGame(id, request);
        return gameModelAssembler.toModel(updatedGame);
    }

    @Override
    public void deleteGameById(Long id) {
        gameService.deleteGame(id);
    }

    //Возможно в дальнейшем сделать private и вызывать при создании игры
    @PostMapping("/api/games/{id}/discount")
    public String addDiscount(@PathVariable Long id, int price) {
        // Вызов gRPC
        var request = GameDiscountRequest.newBuilder().setGameId(id).setGamePrice(price).build();
        try {
            var gRpcResponse = analyticsStub.calculateGameDiscount(request);

            // Отправка события в Fanout
            var event = new GameDiscountAddedEvent(gRpcResponse.getGameId(), gRpcResponse.getGameFinalPrice(), gRpcResponse.getGamePercentDiscount());

            // Для Fanout routingKey не важен, оставляем пустым ""
            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);

            return "Discount is " + gRpcResponse.getGamePercentDiscount() + "% and final price is " + gRpcResponse.getGameFinalPrice();
        } catch (Exception e) {
            return "Exception in calculating discount. Discount is 0%";
        }
    }
}
