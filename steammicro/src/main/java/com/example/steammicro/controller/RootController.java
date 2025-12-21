package com.example.steammicro.controller;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {
    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(
                linkTo(methodOn(DeveloperController.class).getAllDevelopers()).withRel("developers"),
                linkTo(methodOn(GameController.class).getAllGamesPagination(null, 0, 10)).withRel("games")
        );
        rootModel.add(Link.of("http://localhost:8080/swagger-ui.html", "documentation"));
        rootModel.add(Link.of("http://localhost:8080/graphiql", "graphql"));
        return rootModel;
    }
}
