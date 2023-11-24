package com.rug.archedetector.controller;

import com.rug.archedetector.model.QueryCollection;
import com.rug.archedetector.service.QueryCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class QueryCollectionController {
    @Autowired
    private QueryCollectionService queryCollectionService;

    @GetMapping("/query-collection")
    public List<QueryCollection> getQueryCollections() {
        return queryCollectionService.getAll();
    }

    @GetMapping("/query-collection/{id}")
    public QueryCollection getQueryCollection(@PathVariable long id) {
        return queryCollectionService.get(id);
    }

    @PostMapping("/query-collection")
    public QueryCollection createQueryCollection(@RequestBody QueryCollection queryCollection) {
        return queryCollectionService.save(queryCollection);
    }

    @DeleteMapping("/query-collection/{id}")
    public ResponseEntity<?> deleteQueryCollection(@PathVariable long id) {
        return queryCollectionService.delete(id);
    }
}
