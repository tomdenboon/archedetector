package com.rug.archedetector.service;

import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.model.QueryCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryCollectionService {
    private final QueryCollectionRepository queryCollectionRepository;

    @Transactional(readOnly = true)
    public List<QueryCollection> getAll(){
        return queryCollectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public QueryCollection get(Long id){
        return this.queryCollectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public QueryCollection save(QueryCollection queryCollection){
        return queryCollectionRepository.save(queryCollection);
    }

    @Transactional
    public ResponseEntity<?> delete(Long id) {
        return queryCollectionRepository.findById(id).map(queryCollection -> {
            queryCollectionRepository.delete(queryCollection);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
