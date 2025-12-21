package com.example.steammicro.assemblers;

import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steammicro.controller.DeveloperController;
import com.example.steammicro.controller.GameController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DeveloperModelAssembler implements RepresentationModelAssembler<DeveloperResponse, EntityModel<DeveloperResponse>> {

    @Override
    public EntityModel<DeveloperResponse> toModel(DeveloperResponse developer) {
        return EntityModel.of(developer,
                linkTo(methodOn(DeveloperController.class).getDeveloperById(developer.getId())).withSelfRel(),
                linkTo(methodOn(GameController.class).getAllGamesPagination(developer.getId(), 0, 10)).withRel("games"),
                linkTo(methodOn(DeveloperController.class).getAllDevelopers()).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<DeveloperResponse>> toCollectionModel(Iterable<? extends DeveloperResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(DeveloperController.class).getAllDevelopers()).withSelfRel());
    }
}
