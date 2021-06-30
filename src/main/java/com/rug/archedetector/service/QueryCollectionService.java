package com.rug.archedetector.service;

import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.QueryCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueryCollectionService {
    @Autowired
    private final QueryCollectionRepository queryCollectionRepository;

    public QueryCollectionService(QueryCollectionRepository queryCollectionRepository) {
        this.queryCollectionRepository = queryCollectionRepository;
    }

    public List<QueryCollection> getAll(){
        return queryCollectionRepository.findAll();
    }

    public QueryCollection get(Long id){
        Optional<QueryCollection> q = queryCollectionRepository.findById(id);
        if(q.isPresent()){
            return q.get();
        }
        return new QueryCollection();
    }

    public QueryCollection save(QueryCollection queryCollection){
        return queryCollectionRepository.save(queryCollection);
    }

    public ResponseEntity<?> delete(Long id) {
        return queryCollectionRepository.findById(id).map(queryCollection -> {
            queryCollectionRepository.delete(queryCollection);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
