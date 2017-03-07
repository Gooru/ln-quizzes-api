const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

QuizzesCommon.startTest('Get created contexts for anonymous', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAnonymousToken(function (anonymousToken) {
                QuizzesCommon.verifyHttpError('Get created contexts using an anonymous token',
                    `/v1/contexts/${contextResponse.id}/created`, HttpErrorCodes.FORBIDDEN, anonymousToken);
            });
        });
    });
});

QuizzesCommon.startTest('Get created contexts', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getCreatedContexts(authToken, function (contexts) {
                expect(contexts.length).not.toBeLessThan(1);
            });
        });
    });
});