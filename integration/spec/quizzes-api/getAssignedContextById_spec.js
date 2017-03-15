const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpCodes;

QuizzesCommon.startTest('Get a not existing assigned context by id', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authToken) {
        let contextId = QuizzesCommon.generateUUID();
        QuizzesCommon.verifyHttpError('Get a not existing context',
            `/v1/contexts/${contextId}/assigned`, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Get an assigned context with a wrong id', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authToken) {
        let contextId = 'wrong-context-id';
        QuizzesCommon.verifyHttpError('Get a context with incorrect context id',
            `/v1/contexts/${contextId}/assigned`, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Get an assigned context for anonymous', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAnonymousToken(function (anonymousToken) {
                QuizzesCommon.verifyHttpError('Get an assigned context using an anonymous token',
                    `/v1/contexts/${contextResponse.id}/assigned`, HttpErrorCodes.BAD_REQUEST, anonymousToken);
            });
        });
    });
});

QuizzesCommon.startTest('Get a context with a non assigned student', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAuthorizationToken('StudentNotInClass', function (assigneeAuthToken) {
                QuizzesCommon.verifyHttpError('Get an assigned context using a non assigned student token',
                    `/v1/contexts/${contextResponse.id}/assigned`, HttpErrorCodes.FORBIDDEN, assigneeAuthToken);
            });
        });
    });
});


QuizzesCommon.startTest('Get an assigned context', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.getAssignedContextById(contextResponse.id, assigneeAuthToken, {
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
                    createdDate: function(value) {
                        expect(value).toBeDefined();
                    },
                    modifiedDate: function(value) {
                        expect(value).toBeDefined();
                    }
                }, function() {});
            });
        });
    });
});