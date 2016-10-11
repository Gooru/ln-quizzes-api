package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
public class ContextServiceImpl implements ContextService {

    @Autowired
    CollectionService collectionService;

    @Autowired
    ContextRepository contextRepository;

    @Override
    public ResponseEntity<Context> getContext(String externalCollectionId, ContextDTO contextDTO) {
        Collection collection = collectionService.getOrCreateCollection(externalCollectionId);
        Context context = contextRepository.findByCollectionId(collection.getId());
        if (context != null) {
            return new ResponseEntity<>(context, HttpStatus.OK);
        }

        return new ResponseEntity<>(contextRepository.save(new Context(collection)), HttpStatus.CREATED);
    }

}
