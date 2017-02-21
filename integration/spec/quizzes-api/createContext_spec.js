const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;
const Config = require('./quizzesTestConfiguration.js');

let baseContext = {
    'collectionId': Config.getCollection('TestCollection01').id,
    'classId': Config.getClass('TestClass01').id,
    'isCollection': true,
    'contextData': {
        'contextMap': {
        },
        'metadata': {}
    }
};

QuizzesCommon.startTest('Create context with wrong collection id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = 'wrong-assessment-id';

        QuizzesCommon.verifyHttpErrorPost('Create context with wrong collection id',
            '/v1/contexts', context, HttpErrorCodes.BAD_REQUEST,  authToken);
    });
});

QuizzesCommon.startTest('Create context with not existing collection id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = QuizzesCommon.generateUUID();

        QuizzesCommon.verifyHttpErrorPost('Create context with not existing collection id',
            '/v1/contexts', context, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Create context without a collection id', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let context = Object.assign({}, baseContext);
        context.collectionId = null;

        QuizzesCommon.verifyHttpErrorPost('Create context without a collection id',
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

QuizzesCommon.startTest('Create context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});

QuizzesCommon.startTest('Create context with anonymous user', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            expect(contextResponse).not.toBe(null);
            expect(contextResponse.id).toBeDefined();
        });
    });
});