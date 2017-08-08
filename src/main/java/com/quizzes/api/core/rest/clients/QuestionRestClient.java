package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.QuestionContentDto;
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

@Component
@Slf4j
public class QuestionRestClient extends NucleusRestClient {

    private static final String QUESTIONS_PATH = NUCLEUS_API_URL.concat("/questions/%s");

    public QuestionRestClient() {
        super(log);
    }

    public QuestionContentDto getQuestion(UUID questionId, String authToken) {
        String endpointUrl = configurationService.getContentApiUrl() +
                String.format(QUESTIONS_PATH, questionId);

        logRequest(endpointUrl);

        try {
            HttpEntity entity = setupHttpHeaders(authToken);
            ResponseEntity<QuestionContentDto> responseEntity = restTemplate.exchange(
                    endpointUrl,
                    HttpMethod.GET,
                    entity,
                    QuestionContentDto.class);

            QuestionContentDto questionContentDto = responseEntity.getBody();
            logResponse(endpointUrl, gsonPretty.toJson(questionContentDto));
            return  questionContentDto;

        } catch (HttpClientErrorException httpException) {
            if (httpException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.error("Question " + questionId + " not found in Gooru.", httpException);
                throw new ContentNotFoundException("Question " + questionId + " not found in Gooru.");
            } else {
                log.error("Question '" + questionId + "' could not be retrieved.", httpException);
                throw new ContentProviderException("Question " + questionId + " could not be retrieved from Gooru.",
                        httpException);
            }
        } catch (Exception e) {
            log.error("Question '" + questionId + "' could not be retrieved.", e);
            throw new ContentProviderException("Question " + questionId + " could not be retrieved from Gooru.", e);
        }
    }
}
