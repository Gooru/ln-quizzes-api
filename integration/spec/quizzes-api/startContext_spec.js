const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

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
                Frisby.create('Test context attempt by assignee')
                    .get(QuizzesApiUrl + `/v1/contexts/${classId}/assigned`)
                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                    .inspectRequest()
                    .expectStatus(404)
                    .inspectJSON()
                    .toss();
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
                Frisby.create('Test context attempt by assignee')
                    .get(QuizzesApiUrl + `/v1/contexts/${contextId}/assigned`)
                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                    .inspectRequest()
                    .expectStatus(404)
                    .inspectJSON()
                    .toss();
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
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                Frisby.create('Test context attempt by assignee')
                    .get(QuizzesApiUrl + `/v1/contexts/${contextId}/assigned`)
                    .addHeader('Authorization', `Token ${authToken}`)
                    .inspectRequest()
                    .expectStatus(404)
                    .inspectJSON()
                    .toss();
            });
        });
    });
});

QuizzesCommon.startTest('Start context for anonymous', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            Frisby.create('Verify start context data')
                .post(QuizzesApiUrl + `/v1/contexts/${contextId}/start`)
                .addHeader('Authorization', `Token ${authToken}`)
                .inspectRequest()
                .expectStatus(200)
                .expectJSON({
                    'contextId': contextResponse.id,
                    'collectionId': collectionId,
                    'currentResourceId': undefined,
                    'events': []
                })
                .inspectJSON()
                .toss();
        })
    });
});
