package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.CollectionGetResponseDto;
import com.quizzes.api.common.dto.QuestionDataDto;
import com.quizzes.api.common.dto.ResourceDto;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.CollectionRepository;
import com.quizzes.api.realtime.model.CollectionOnAir;
import com.quizzes.api.realtime.repository.CollectionOnAirRepository;
import com.quizzes.api.realtime.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Gson gson;

    @Autowired
    JsonParser jsonParser;

    //TODO: Tests
    /**
     * A {@link Collection} is the Quizzes representation of an Assessment in a Content Provider
     * and in the Content Provider are original Assessments (an Assessment that it's create from scratch)
     * and copied Assessments (copied from another Assessment)
     * In an Original Assessment the Assessment ID is the same as the field Assessment parent ID, this means that
     * the Assessment has no parent or is not copied from other Assessment.
     * A copied Assessment has it's own unique ID and in the field parent ID has the ID of the Assessment it is being copied from
     * In Quizzes each {@link Collection} has it's owner, and that owner matches or represents a Content Provider owner.
     * A Quizzes owner can use a Assessment he owns in the Content Provider or a "Public" Assessment from a different owner
     * but in the case of Assessments owned by others then the Quizzes user should copy the Assessment and use that copy.
     * Some Examples are:
     * ++Collection ID = c1 with external ID (Assessment ID) = a1 external parent ID (parent Assessment ID) = a1 and owner ID = o1
     *   this is an original Assessment, not copied
     *
     *   en gooru tengo a2 parent a1 o2
     *   lo publico
     *
     *
     *   PROBLEMA, soy o2
     *   en gooru tengo a1 es de o1
     *   lo veo en el search
     *   siendo o2 selecciono a1 en itslearning
     *   creo 1 collection, se copia a1 en a2 de o2
     *   publico a2
     *   busco y selecciono a2
     *   busca en gooru y se trae a2 que es de o2
     *   crea un collection con a2 de o2
     *
     *
     *
     *
     *
     *
     * ++Collection ID = c2 with external ID (Assessment ID) = a2 external parent ID (parent Assessment ID) = a2 and owner ID = o2
     *   since the owner is different but the parent id is the same then a copy of the Assessment is required
     * ++Collection ID = c3 with external ID (Assessment ID) = a3 external parent ID (parent Assessment ID) = a1 and owner ID = o2
     *   this is an owner's o2 second copy of Assessment ID = a1
     * ++Collection ID = c4 with external ID (Assessment ID) = a4 external parent ID (parent Assessment ID) = a2 and owner ID = o2
     *   this is an owner's o2 copy of Assessment ID = a2
     *
     * One input parameter is the external ID (Assessment ID) on the Content Provider
     * so in Quizzes we should use that ID as both external ID and also external parent ID
     * because a o2 user might create a {@link Collection} using a1 as external ID
     * by any of external ID o external parent ID and also by the Quizzes owner {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile}
     * @param externalId Content Provider ID of the Assessment or the parent Assessment
     * @param ownerProfileId Quizzes owner {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile} ID
     * @return the {@link Collection} object
     */
    public Collection findByExternalIdAndOwner(String externalId, UUID ownerProfileId) {
        return collectionRepository.findByExternalIdorExternalParentIdandOwner(externalId, ownerProfileId);
    }

    //TODO: tests
    public Collection save(Collection collection) {
        return collectionRepository.save(collection);
    }

    public Collection findById(UUID id) {
        return collectionRepository.findById(id);
    }

    public CollectionGetResponseDto getCollection(UUID collectionId) {
        CollectionGetResponseDto result = null;
        Collection collection = collectionRepository.findById(collectionId);
        if (collection != null) {
            result = new CollectionGetResponseDto();
            result.setId(collectionId);
            result.setIsCollection(collection.getIsCollection());
            List<Resource> resources = resourceService.findByCollectionId(collectionId);
            List<ResourceDto> resourceList =
                    resources.stream().map(resource -> {
                        ResourceDto dataResourceDto = new ResourceDto();
                        dataResourceDto.setId(resource.getId());
                        dataResourceDto.setIsResource(resource.getIsResource());
                        dataResourceDto.setSequence(resource.getSequence());
                        dataResourceDto.setQuestionData(
                                gson.fromJson(resource.getResourceData(), QuestionDataDto.class));
                        return dataResourceDto;
                    }).collect(Collectors.toList());
            result.setResources(resourceList);
        }
        return result;
    }

    //TODO: WE NEED TO REMOVE THIS OLD METHODS - OLD REAL TIME METHODS
    @Autowired
    private EventService eventService;

    @Autowired
    private CollectionOnAirRepository collectionOnAirRepository;


    public CollectionOnAir findCollectionOnAir(String classId, String collectionId) {
        return collectionOnAirRepository.findFirstByClassIdAndCollectionId(classId, collectionId);
    }

    public Iterable<CollectionOnAir> findCollectionsOnAirByClass(String classId) {
        return collectionOnAirRepository.findByClassId(classId);
    }

    public CollectionOnAir addCollectionOnAir(String classId, String collectionId) {
        CollectionOnAir collectionOnAir = findCollectionOnAir(classId, collectionId);
        if (Objects.isNull(collectionOnAir)) {
            collectionOnAir = collectionOnAirRepository.save(new CollectionOnAir(classId, collectionId));
        }
        return collectionOnAir;
    }

    public void removeCollectionOnAir(String classId, String collectionId) {
        CollectionOnAir collectionOnAir = findCollectionOnAir(classId, collectionId);
        if (Objects.nonNull(collectionOnAir)) {
            collectionOnAirRepository.delete(collectionOnAir);
        }
    }

    public void completeCollectionForUser(String collectionUniqueId, String userId) {
        eventService.completeEventIndexByUser(collectionUniqueId, userId);
    }

    public void resetCollectionForUser(String collectionUniqueId, String userId) {
        eventService.deleteCollectionEventsByUser(collectionUniqueId, userId);
    }

}
