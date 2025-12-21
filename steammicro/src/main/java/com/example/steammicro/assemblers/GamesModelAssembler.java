package com.example.steammicro.assemblers;

import com.example.steamapi.dto.response.GameResponse;
import com.example.steammicro.controller.DeveloperController;
import com.example.steammicro.controller.GameController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GamesModelAssembler implements RepresentationModelAssembler<GameResponse, EntityModel<GameResponse>> {

    @Override
    public EntityModel<GameResponse> toModel(GameResponse game) {
        return EntityModel.of(game,
                linkTo(methodOn(GameController.class).getGameById(game.getId())).withSelfRel(),
                linkTo(methodOn(DeveloperController.class).getDeveloperById(game.getDeveloperId())).withRel("developer"),
                linkTo(methodOn(GameController.class).getAllGamesPagination(null, 0, 10)).withRel("collection")
        );
    }
}
