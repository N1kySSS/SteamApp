package com.example.steammicro.controller;

import com.example.steamapi.api.DeveloperApi;
import com.example.steamapi.dto.request.CreateDeveloperRequest;
import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steammicro.assemblers.DeveloperModelAssembler;
import com.example.steammicro.service.DeveloperService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeveloperController implements DeveloperApi {

    private final DeveloperService developerService;
    private final DeveloperModelAssembler developerModelAssembler;

    @Autowired
    public DeveloperController(DeveloperService developerService, DeveloperModelAssembler developerModelAssembler) {
        this.developerService = developerService;
        this.developerModelAssembler = developerModelAssembler;

    }

    @Override
    public ResponseEntity<EntityModel<DeveloperResponse>> createDeveloper(@Valid CreateDeveloperRequest request) {
        DeveloperResponse createdDeveloper = developerService.addNewDeveloper(request);
        EntityModel<DeveloperResponse> entityModel = developerModelAssembler.toModel(createdDeveloper);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public CollectionModel<EntityModel<DeveloperResponse>> getAllDevelopers() {
        List<DeveloperResponse> developers = developerService.getDevelopers();
        return developerModelAssembler.toCollectionModel(developers);
    }

    @Override
    public EntityModel<DeveloperResponse> getDeveloperById(Long id) {
        DeveloperResponse developers = developerService.findDeveloperById(id);
        return developerModelAssembler.toModel(developers);
    }

    @Override
    public void deleteDeveloperById(Long id) {
        developerService.deleteDeveloperById(id);
    }
}
