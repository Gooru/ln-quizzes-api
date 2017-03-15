const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpCodes;
const Config = require('./quizzesTestConfiguration.js');

let baseContext = {
    'collectionId': Config.getCollection('TestCollection01').id,
    'classId': Config.getClass('TestClass01').id,
    'isCollection': true,
    'contextData': {
        'contextMap': {},
        'metadata': {}
    }
};

QuizzesCommon.startTest('Create context with wrong collection id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = 'wrong-collection-id';

        QuizzesCommon.verifyHttpErrorPost('Create context with wrong collection id',
            '/v1/contexts', context, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Create context with not existing collection or assessment id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = QuizzesCommon.generateUUID();

        QuizzesCommon.verifyHttpErrorPost('Create context with not existing collection id',
            '/v1/contexts', context, HttpErrorCodes.NOT_FOUND, authToken);

        context.isCollection = false;
        QuizzesCommon.verifyHttpErrorPost('Create context with not existing assessment id',
            '/v1/contexts', context, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Create context without a collection id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = null;

        QuizzesCommon.verifyHttpErrorPost('Create context without a collection id',
            '/v1/contexts', context, HttpErrorCodes.BAD_REQUEST, authToken);

        context.isCollection = false;
        QuizzesCommon.verifyHttpErrorPost('Create context without a assessment id',
            '/v1/contexts', context, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Create context without contextData', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.contextData = null;

        QuizzesCommon.verifyHttpErrorPost('Create context without contextData',
            '/v1/contexts', context, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Create context with collection', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Create context with assessment', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(assessmentId, classId, false, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Create context with anonymous user', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Create context with anonymous user should throw error', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        QuizzesCommon.verifyHttpErrorPost('Create context with anonymous user',
            '/v1/contexts', baseContext, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Create context with collection for preview', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.createContext(collectionId, null, true, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Returns existing Context with Context map info', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collectionId, classId, true, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.createContext(collectionId, classId, true, contextMap, authToken,
                function (existingContextResponse) {
                    expect(existingContextResponse.id).toEqual(contextId);
                });
        });
    });
});

QuizzesCommon.startTest('Assignee creates a Context with Context map info on behalf the Context owner', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collectionId, classId, true, contextMap, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Assignee1 creates a Context with Context map info on behalf the Context owner and ' +
    'Assignee2 retrieves existing Context', function () {
    QuizzesCommon.getAuthorizationToken('Student01', function (authTokenAssignee1) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collectionId, classId, true, contextMap, authTokenAssignee1,
            function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student02', function (authTokenAssignee2) {
                    QuizzesCommon.createContext(collectionId, classId, true, contextMap, authTokenAssignee2,
                        function (existingContextResponse) {
                            expect(existingContextResponse.id).toEqual(contextId);
                        });
                });
            });
    });
});

QuizzesCommon.startTest('Invalid Assignee tries to create a Context with Context map info on behalf the Context owner',
    function () {
        QuizzesCommon.getAuthorizationToken('StudentNotInClass', function (authToken) {
            let collectionId = Config.getCollection('TestCollection01').id;
            let classId = Config.getClass('TestClass01').id;
            let contextMap = QuizzesCommon.generateRandomContextMap();
            QuizzesCommon.createContextWithStatus(collectionId, classId, true, contextMap, authToken,
                HttpErrorCodes.FORBIDDEN, function (error) {
                    expect(error.status).toEqual(HttpErrorCodes.FORBIDDEN);
                });
        });
    });