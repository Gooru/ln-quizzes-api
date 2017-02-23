const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

QuizzesCommon.startTest('Start and finish context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {});
                });
            });
        });
    });
});

QuizzesCommon.startTest('Finish a never started context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.verifyHttpErrorPost('Finish a never started context',
                    `/v1/contexts/${contextId}/finish`, {}, HttpErrorCodes.NOT_FOUND, assigneeAuthToken);
            });
        });
    });
});

QuizzesCommon.startTest('Finish a started context with a different student', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.getAuthorizationToken('Student02', function (assignee2AuthToken) {
                        QuizzesCommon.verifyHttpErrorPost('Finish a started context with a different student',
                            `/v1/contexts/${contextId}/finish`, {}, HttpErrorCodes.NOT_FOUND, assignee2AuthToken);
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Finish a context with wrong collection id', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authToken) {
        let contextId = 'wrong-context-id';
        QuizzesCommon.verifyHttpErrorPost('Finish a context with wrong collection id',
            `/v1/contexts/${contextId}/finish`, {}, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Finish a context with not existing collection id', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authToken) {
        let contextId = QuizzesCommon.generateUUID();
        QuizzesCommon.verifyHttpErrorPost('Finish a context with not existing collection id',
            `/v1/contexts/${contextId}/finish`, {}, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Start and finish context for anonymous', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.startContext(contextId, authToken, function () {
                QuizzesCommon.finishContext(contextId, authToken, function () {});
            });
        });
    });
});

QuizzesCommon.startTest('Start and finish context for preview', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
                QuizzesCommon.startContext(contextId, authToken, function () {
                    QuizzesCommon.finishContext(contextId, authToken, function () {

                    });
                });
        });
    });
});