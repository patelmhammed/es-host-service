package com.meesho.msearch.host.web;

import com.meesho.msearch.host.service.SearchTestService;
import com.meesho.msearch.host.web.dto.VersionedIndexRequest;
import com.meesho.msearch.host.web.dto.VersionedSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/es")
public class HostRestController {
    private final SearchTestService searchTestService;

    public HostRestController(SearchTestService searchTestService) {
        this.searchTestService = searchTestService;
    }

    @PostMapping("/search")
    public Mono<ResponseEntity<?>> search(@Valid @RequestBody VersionedSearchRequest request) {
        return searchTestService.search(request).map(ResponseEntity::ok);
    }

    @PostMapping("/index")
    public Mono<ResponseEntity<?>> index(@Valid @RequestBody VersionedIndexRequest request) {
        return searchTestService.index(request).map(ResponseEntity::ok);
    }
}
