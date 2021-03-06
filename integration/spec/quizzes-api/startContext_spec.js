const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');
const HttpErrorCodes = QuizzesCommon.httpCodes;

let eventContext = {
    'eventSource': 'dailyclassactivity',
    'sourceUrl': 'http://nile-qa.gooru.org/',
    'pathId': 1,
    'timezone': 'America/Costa_Rica'
};

//TODO: Fix getAssignedContextById
// QuizzesCommon.startTest('Start context and validate response', function () {
//     QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
//         let collectionId = Config.getCollection('TestCollection01').id;
//         let classId = Config.getClass('TestClass01').id;
//         QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
//             let contextId = contextResponse.id;
//             let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
//             QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
//                 QuizzesCommon.getAssignedContextById(contextId, assigneeAuthToken, {
//                     'contextId': contextResponse.id,
//                     'hasStarted': false,
//                     'profileId': profileId
//                 }, function () {
//                 });
//
//                 Frisby.create('Verify start context data')
//                     .post(QuizzesApiUrl + `/v1/contexts/${contextId}/start`)
//                     .addHeader('Authorization', `Token ${assigneeAuthToken}`)
//                     .inspectRequest()
//                     .expectStatus(200)
//                     .expectJSON({
//                         'contextId': contextResponse.id,
//                         'collectionId': collectionId,
//                         'currentResourceId': undefined,
//                         'events': []
//                     })
//                     .inspectJSON()
//                     .afterJSON(function () {
//                         QuizzesCommon.getAssignedContextById(contextId, assigneeAuthToken, {
//                             'contextId': contextResponse.id,
//                             'hasStarted': true,
//                             'profileId': profileId
//                         }, function () {
//                         })
//                     })
//                     .toss();
//             });
//         });
//     });
// });


//TODO: Fix getAssignedContextById
// QuizzesCommon.startTest('Start context twice', function () {
//     QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
//         let collectionId = Config.getCollection('TestCollection01').id;
//         let classId = Config.getClass('TestClass01').id;
//         QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
//             let contextId = contextResponse.id;
//             let profileId = QuizzesCommon.getProfileIdFromToken(authToken);
//             QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
//                 QuizzesCommon.getAssignedContextById(contextId, assigneeAuthToken, {
//                     'contextId': contextResponse.id,
//                     'hasStarted': false,
//                     'profileId': profileId
//                 }, function () {
//                 });
//
//                 QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
//                     QuizzesCommon.getAssignedContextById(contextId, assigneeAuthToken, {
//                         'contextId': contextResponse.id,
//                         'hasStarted': true,
//                         'profileId': profileId
//                     }, function () {
//                         QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
//                             Frisby.create('Start context twice, there is no current resource ID')
//                                 .post(QuizzesApiUrl + `/v1/contexts/${contextId}/start`)
//                                 .addHeader('Authorization', `Token ${assigneeAuthToken}`)
//                                 .inspectRequest()
//                                 .expectStatus(200)
//                                 .expectJSON({
//                                     'contextId': contextResponse.id,
//                                     'collectionId': collectionId,
//                                     'currentResourceId': undefined,
//                                     'events': []
//                                 })
//                                 .inspectJSON()
//                                 .toss();
//                         });
//                     });
//                 });
//             });
//         });
//     });
// });

QuizzesCommon.startTest('Start context with wrong context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.verifyHttpError('Test context attempt by assignee',
                    `/v1/contexts/${QuizzesCommon.generateUUID()}/assigned`,
                    HttpErrorCodes.NOT_FOUND, assigneeAuthToken);
            });
        });
    });
});

QuizzesCommon.startTest('Start context with another student out of the class', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('StudentNotInClass', function (assigneeAuthToken) {
                QuizzesCommon.verifyHttpError('Test context attempt by assignee',
                    `/v1/contexts/${contextId}/assigned`,
                    HttpErrorCodes.FORBIDDEN, assigneeAuthToken);
            });
        });
    });
});

QuizzesCommon.startTest('Start context with teacher token', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.verifyHttpError('Test context attempt by assignee',
                `/v1/contexts/${contextId}/assigned`,
                HttpErrorCodes.FORBIDDEN, authToken);
        });
    });
});

QuizzesCommon.startTest('Start context for anonymous', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.startContext(contextId, authToken, function(contextResponse) {
                expect(contextResponse.contextId).toEqual(contextId);
                expect(contextResponse.collectionId).toEqual(collectionId);
                expect(contextResponse.currentResourceId).toBe(undefined);
                expect(contextResponse.events.length).toEqual(0);
            });
        })
    });
});

QuizzesCommon.startTest('Starts a Context, Answers a Question and the Resumes the Context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collection.id, classId, true, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    let previousResource = collection.resources[0];
                    let resourceId = collection.resources[1].id;

                    Frisby.create('Test valid onResource call')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .afterJSON(function() {
                            QuizzesCommon.startContext(contextId, assigneeAuthToken, function (resumeResponse) {
                                expect(resumeResponse.contextId).toEqual(contextId);
                                expect(resumeResponse.collectionId).toEqual(collection.id);
                                expect(resumeResponse.currentResourceId).toEqual(resourceId);
                                expect(resumeResponse.events.length).toEqual(1);
                                expect(resumeResponse.events[0].resourceId).toEqual(previousResource.id);
                            });
                        })
                        .toss();
                });
            });
        });
    });
});