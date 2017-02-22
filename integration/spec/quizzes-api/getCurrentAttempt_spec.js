const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');

QuizzesCommon.startTest('Get Current Attempt with context not started', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextResponseId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken("Student01", function (assigneeAuthToken) {
                Frisby.create('Test current attempt with no attempts started by context owner')
                    .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                    .addHeader('Authorization', `Token ${authToken}`)
                    .inspectRequest()
                    .expectStatus(200)
                    .expectJSONLength('profileAttempts', 0)
                    .inspectJSON()
                    .toss();

                Frisby.create('Test current attempt with no attempts started by assignee')
                    .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                    .inspectRequest()
                    .expectStatus(404)
                    .inspectJSON()
                    .toss();
            })
        })
    });
});

QuizzesCommon.startTest('Get Current attempts, context started but not finished', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextResponseId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                QuizzesCommon.startContext(contextResponseId, assigneeAuthToken, function (startResponse) {
                    Frisby.create('Test current attempt by owner')
                        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                        .addHeader('Authorization', `Token ${authToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSONLength('profileAttempts', 1)
                        .expectJSON('profileAttempts', [{
                            'profileId': assigneeProfileId
                        }])
                        .inspectJSON()
                        .toss();

                    Frisby.create('Test current attempt with started context with assignee token (wrong owner)')
                        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss()

                })
            })
        })
    });
});

QuizzesCommon.startTest('Get Current Attempt started and finished', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextResponseId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                QuizzesCommon.startContext(contextResponseId, assigneeAuthToken, function (startResponse) {
                    QuizzesCommon.finishContext(contextResponseId, assigneeAuthToken, function () {
                        Frisby.create('Test current attempt with finished context by owner')
                            .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                            .addHeader('Authorization', `Token ${authToken}`)
                            .inspectRequest()
                            .expectStatus(200)
                            .expectJSONLength('profileAttempts', 1)
                            .expectJSON('profileAttempts', [{
                                'profileId': assigneeProfileId
                            }])
                            .inspectJSON()
                            .toss();

                        Frisby.create('Test context attempts with finished context with assignee token (wrong owner)')
                            .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                            .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                            .inspectRequest()
                            .expectStatus(404)
                            .toss()

                    })
                })
            })
        })
    });
});

QuizzesCommon.startTest('Get Current Attempts for two assignees, 1 complete, 1 not finished', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextResponseId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assignee1AuthToken) {
                QuizzesCommon.startContext(contextResponseId, assignee1AuthToken, function (startResponse1) {
                    QuizzesCommon.finishContext(contextResponseId, assignee1AuthToken, function () {
                        QuizzesCommon.getAuthorizationToken('Student02', function (assignee2AuthToken) {
                            QuizzesCommon.startContext(contextResponseId, assignee2AuthToken, function (startResponse2) {
                                let assignee1ProfileId = QuizzesCommon.getProfileIdFromToken(assignee1AuthToken);
                                let assignee2ProfileId = QuizzesCommon.getProfileIdFromToken(assignee2AuthToken);

                                Frisby.create('Test current context attempts with two assignees by owner')
                                    .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                                    .addHeader('Authorization', `Token ${authToken}`)
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectJSON({
                                        'profileAttempts': function (attempts) {
                                            expect(attempts.length).toBe(2);
                                            expect(attempts).toContain(objectContaining({
                                                'profileId': assignee1ProfileId,
                                                'isComplete': true
                                            }));
                                            expect(attempts).toContain(objectContaining({
                                                'profileId': assignee2ProfileId,
                                                'isComplete': false
                                            }));
                                        }
                                    })
                                    .inspectJSON()
                                    .toss();

                                Frisby.create('Test context attempts with two assignees by assignee number 1')
                                    .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                                    .addHeader('Authorization', `Token ${assignee1AuthToken}`)
                                    .inspectRequest()
                                    .expectStatus(404)
                                    .inspectJSON()
                                    .toss();

                                Frisby.create('Test context attempts with two assignees by assignee number 2')
                                    .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextResponseId}`)
                                    .addHeader('Authorization', `Token ${assignee2AuthToken}`)
                                    .inspectRequest()
                                    .expectStatus(404)
                                    .toss();
                            })
                        })
                    })
                })
            })
        })
    });
});
