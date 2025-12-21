package com.example.steammicro.graphql;

import com.example.steamapi.dto.request.CreateDeveloperRequest;
import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steammicro.service.DeveloperService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.Map;

@DgsComponent
public class DeveloperDataFetcher {

    private final DeveloperService developerService;

    public DeveloperDataFetcher(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @DgsQuery
    public List<DeveloperResponse> developers() {
        return developerService.getDevelopers();
    }

    @DgsQuery
    public DeveloperResponse developerById(@InputArgument Long id) {
        return developerService.findDeveloperById(id);
    }

    @DgsMutation
    public DeveloperResponse createDeveloper(@InputArgument("input") Map<String, String> input) {
        CreateDeveloperRequest request = new CreateDeveloperRequest(
                input.get("name"),
                input.get("geolocation")
        );
        return developerService.addNewDeveloper(request);
    }

    @DgsMutation
    public Long deleteDeveloper(@InputArgument Long id) {
        developerService.deleteDeveloperById(id);
        return id;
    }
}
