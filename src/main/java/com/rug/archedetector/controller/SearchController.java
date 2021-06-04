package com.rug.archedetector.controller;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/email/search")
    public List<Email> getAllMailingLists() {
        return searchService.searchKeyword("Cuevas", 1L);
    }
}
