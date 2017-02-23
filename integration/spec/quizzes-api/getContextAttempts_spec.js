const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');


let testAttempts = function(description, contextId, profileId, attempts, authToken) {
    Frisby.create(description)
        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextId}/profiles/${profileId}`)
        .addHeader('Authorization', `Token ${authToken}`)
        .inspectRequest()
        .expectStatus(200)
        .expectJSONLength('attempts', attempts)
        .inspectJSON()
        .toss();
};

QuizzesCommon.startTest('Get Attempts with context not started', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
            testAttempts('Test context attempts with no attempts started', contextId, profileId, 0, authToken);
        });
    });
});

QuizzesCommon.startTest('Get Attempts started but no finished', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    // The owner is ok but the owner doesn't have started contexts,
                    // so this is an empty attempts for a wrong profileId
                    testAttempts('Test context attempts with started context by owner (wrong profile ID)',
                        contextId, profileId, 0, authToken);

                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                    // The owner is ok and the profileId is the correct id of the assignee
                    testAttempts('Test context attempts with started context by assigneeId',
                        contextId, assigneeProfileId, 0, authToken);

                    Frisby.create('Test context attempts with started context with assignee token (wrong owner)')
                    // With a wrong token this is a content not found error
                        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextId}/profiles/${profileId}`)
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss();
                })
            })
        });
    });
});

QuizzesCommon.startTest('Get Attempts started and finished', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                        // The owner is ok but the owner doesn't have started contexts,
                        // so this is an empty attempts for a wrong profileId
                        testAttempts('Test context attempts with finished context by owner (wrong profileId)',
                            contextId, profileId, 0, authToken);

                        let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                        // The owner is ok and the profileId is the correct id of the assignee
                        testAttempts('Test context attempts with started context by assigneeId',
                            contextId, assigneeProfileId, 1, authToken);

                        testAttempts('Test context attempts with started context by assigneeId and assignee Token',
                            contextId, assigneeProfileId, 1, assigneeAuthToken);

                        Frisby.create('Test context attempts with finished context with assignee token (wrong owner)')
                        // With a wrong token this is a content not found error
                            .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextId}/profiles/${profileId}`)
                            .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                            .inspectRequest()
                            .expectStatus(404)
                            .toss();
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Get Attempts started and finished for two assignees', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
            QuizzesCommon.getAuthorizationToken('Student01', function (assignee1AuthToken) {
                QuizzesCommon.startContext(contextId, assignee1AuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assignee1AuthToken, function () {
                        QuizzesCommon.getAuthorizationToken('Student02', function (assignee2AuthToken) {
                            QuizzesCommon.startContext(contextId, assignee2AuthToken, function () {
                                QuizzesCommon.finishContext(contextId, assignee2AuthToken, function () {
                                    // The owner is ok but the owner doesn't have started contexts,
                                    // so this is an empty attempts for a wrong profileId
                                    testAttempts('Test context attempts with finished context by owner ' +
                                        '(wrong profileId)', contextId, profileId, 0, authToken);

                                    let assignee1ProfileId = QuizzesCommon.getProfileIdFromToken(assignee1AuthToken);
                                    // The owner is ok and the profileId is the correct id of the assignee
                                    testAttempts('Test context attempts with finished context by assigneeId number 1',
                                        contextId, assignee1ProfileId, 1, authToken);

                                    let assignee2ProfileId = QuizzesCommon.getProfileIdFromToken(assignee2AuthToken);
                                    // The owner is ok and the profileId is the correct id of the assignee
                                    testAttempts('Test context attempts with finished context by assigneeId number 2',
                                        contextId, assignee2ProfileId, 1, authToken);

                                    Frisby.create('Test context attempts with finished context with assignee 2 ' +
                                        'looking for assignee 1 attempts token (wrong owner)')
                                    // With a wrong token this is a content not found error
                                        .get(QuizzesApiUrl +
                                            `/v1/attempts/contexts/${contextId}/profiles/${assignee1ProfileId}`)
                                        .addHeader('Authorization', `Token ${assignee2AuthToken}`)
                                        .inspectRequest()
                                        .expectStatus(404)
                                        .toss();
                                });
                            });
                        });
                    });
                });
            });
        })
    });
});

QuizzesCommon.startTest('Get Attempts started and finished two times for the same assignee', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                        QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                            QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                // The owner is ok but the owner doesn't have started contexts,
                                // so this is an empty attempts for a wrong profileId
                                testAttempts('Test context attempts with finished context by owner (wrong profileId)',
                                    contextId, profileId, 0, authToken);

                                let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                // The owner is ok and the profileId is the correct id of the assignee
                                testAttempts('Test context attempts with finished context by assigneeId number 1',
                                    contextId, assigneeProfileId, 2, authToken);

                                Frisby.create('Test context attempts with finished context with assignee 2 looking ' +
                                    'for assignee 1 attempts token (wrong owner)')
                                // With a wrong token this is a content not found error
                                    .get(QuizzesApiUrl +
                                        `/v1/attempts/contexts/${contextId}/profiles/${assigneeProfileId}`)
                                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                                    .inspectRequest()
                                    .expectStatus(404)
                                    .toss();
                            });
                        });
                    });
                });
            });
        });
    });
});