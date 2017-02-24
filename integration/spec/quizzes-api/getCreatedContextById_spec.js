const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

QuizzesCommon.startTest('Get a non existing context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let contextId = QuizzesCommon.generateUUID();
        QuizzesCommon.verifyHttpError('Get a non existing context',
            `/v1/contexts/${contextId}/created`, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Get a context with incorrect context id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let contextId = 'wrong-context-id';
        QuizzesCommon.verifyHttpError('Get a context with incorrect context id',
            `/v1/contexts/${contextId}/created`, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Get a created context for anonymous', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAnonymousToken(function (anonymousToken) {
                QuizzesCommon.verifyHttpError('Get a context with incorrect context id',
                    `/v1/contexts/${contextResponse.id}/created`, HttpErrorCodes.FORBIDDEN, anonymousToken);
            });
        });
    });
});

QuizzesCommon.startTest('Get a created context', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getCreatedContextById(contextResponse.id, authToken, {
                contextId: contextResponse.id,
                collectionId: collection.id,
                isCollection: collection.isCollection,
                classId: classId,
                isActive: true,
                startDate: 0,
                dueDate: 0,
                contextData:
                {
                    metadata: {
                        title: `Context for collectionId ${collection.id} and classId ${classId}`
                    },
                    contextMap: {}
                },

            }, function() {});
        });
    });
});