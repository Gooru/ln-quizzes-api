package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.RubricCategoryDto;
import com.quizzes.api.core.dtos.RubricCategoryLevelDto;
import com.quizzes.api.core.dtos.RubricDto;
import com.quizzes.api.core.dtos.content.RubricCategoryContentDto;
import com.quizzes.api.core.dtos.content.RubricCategoryLevelContentDto;
import com.quizzes.api.core.dtos.content.RubricContentDto;
import com.quizzes.api.core.enums.GradingType;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.ContentProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Slf4j
@Component
public class RubricRestClient extends NucleusRestClient {

    private static final String RUBRICS_PATH = NUCLEUS_API_URL.concat("/rubrics/%s");

    public RubricRestClient() {
        super(log);
    }

    public RubricDto getRubric(UUID rubricId, String authToken) {
        String endpointUrl = configurationService.getContentApiUrl() +
                String.format(RUBRICS_PATH, rubricId);

        logRequest(endpointUrl);

        try {
            HttpEntity entity = setupHttpHeaders(authToken);
            ResponseEntity<RubricContentDto> responseEntity = restTemplate.exchange(
                    endpointUrl,
                    HttpMethod.GET,
                    entity,
                    RubricContentDto.class);

            RubricContentDto rubricContentDto = responseEntity.getBody();
            logResponse(endpointUrl, gsonPretty.toJson(rubricContentDto));
            return toRubricDto(rubricContentDto);

        } catch (HttpClientErrorException httpException) {
            if (httpException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.error("Rubric " + rubricId + " not found in Gooru.", httpException);
                throw new ContentNotFoundException("Question " + rubricId + " not found in Gooru.");
            } else {
                log.error("Rubric '" + rubricId + "' could not be retrieved.", httpException);
                throw new ContentProviderException("Question " + rubricId + " could not be retrieved from Gooru.",
                        httpException);
            }
        } catch (Exception e) {
            log.error("Rubric '" + rubricId + "' could not be retrieved.", e);
            throw new ContentProviderException("Question " + rubricId + " could not be retrieved from Gooru.", e);
        }
    }

    private RubricDto toRubricDto(RubricContentDto rubricContentDto) {
        RubricDto.RubricDtoBuilder rubricDtoBuilder = RubricDto.builder()
                .id(rubricContentDto.getId())
                .title(rubricContentDto.getTitle())
                .description(rubricContentDto.getDescription())
                .thumbnail(rubricContentDto.getThumbnail())
                .url(rubricContentDto.getUrl())
                .metadata(rubricContentDto.getMetadata())
                .publishStatus(rubricContentDto.getPublishStatus())
                .publishDate(rubricContentDto.getPublishDate())
                .isRemote(rubricContentDto.getIsRemote())
                .feedback(rubricContentDto.getFeedback())
                .taxonomy(rubricContentDto.getTaxonomy())
                .overallFeedbackRequired(rubricContentDto.getOverallFeedbackRequired())
                .isRubric(rubricContentDto.getIsRubric())
                .creatorId(rubricContentDto.getCreatorId())
                .createdAt(rubricContentDto.getCreatedAt())
                .modifierId(rubricContentDto.getModifierId())
                .updatedAt(rubricContentDto.getUpdatedAt())
                .tenant(rubricContentDto.getTenant())
                .gradingType(GradingType.fromString(rubricContentDto.getGrader()));

        rubricContentDto.getCategories().forEach(rubricCategoryContentDto -> {
            rubricDtoBuilder.category(toRubricCategoryDto(rubricCategoryContentDto));
        });

        return rubricDtoBuilder.build();
    }

    private RubricCategoryDto toRubricCategoryDto(RubricCategoryContentDto rubricCategoryContentDto) {
        RubricCategoryDto.RubricCategoryDtoBuilder rubricCategoryDtoBuilder = RubricCategoryDto.builder()
                .level(rubricCategoryContentDto.getLevel())
                .scoring(rubricCategoryContentDto.getScoring())
                .title(rubricCategoryContentDto.getTitle())
                .feedback(rubricCategoryContentDto.getFeedback())
                .requiredFeedback(rubricCategoryContentDto.getRequiredFeedback());

        rubricCategoryContentDto.getLevels().forEach(rubricCategoryLevelContentDto -> {
            rubricCategoryDtoBuilder.level(toRubricCategoryLevelDto(rubricCategoryLevelContentDto));
        });

        return rubricCategoryDtoBuilder.build();
    }

    private RubricCategoryLevelDto toRubricCategoryLevelDto(RubricCategoryLevelContentDto rubricCategoryLevelContentDto) {
        return RubricCategoryLevelDto.builder()
                .name(rubricCategoryLevelContentDto.getName())
                .score(rubricCategoryLevelContentDto.getScore())
                .build();
    }
}
