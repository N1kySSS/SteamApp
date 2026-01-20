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
        rootModel.add(Link.of("http://localhost:25502/swagger-ui.html", "Documentation"));
        rootModel.add(Link.of("http://localhost:25502/graphiql", "Graphql"));
        rootModel.add(Link.of("http://localhost:9090/query", "Prometheus"));
        rootModel.add(Link.of("http://localhost:9411/zipkin/", "Zipkin"));
        rootModel.add(Link.of("http://localhost:15672/", "RabbitMQ"));
        rootModel.add(Link.of("http://localhost:25509/api/notifications/stats", "WS статистика"));
        return rootModel;
    }
}
