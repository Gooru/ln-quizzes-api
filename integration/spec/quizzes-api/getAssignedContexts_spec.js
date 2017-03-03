const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

QuizzesCommon.startTest('Get assigned contexts for anonymous', function () {
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

QuizzesCommon.startTest('Get assigned contexts', function () {
    let collection = Config.getCollection('TestCollection01');
    let classId = Config.getClass('TestClass01').id;
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collection.id, classId, true, {}, authToken, function (contextResponse) {
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.getAssignedContexts(assigneeAuthToken, function (assignedContexts) {
                    expect(assignedContexts.length).not.toBeLessThan(1);
                });
            });
        });
    });
});

QuizzesCommon.startTest("Verifies that there are not repeated Assigned Contexts in the response", function () {
    QuizzesCommon.getAuthorizationToken("Student01", function (authToken) {
        QuizzesCommon.doGet('Get Assigned Contexts', '/v1/contexts/assigned', 200, authToken,
            function (assignedContexts) {
                var foundContexts = {};
                assignedContexts.forEach(function (context) {
                    var foundContext = foundContexts[context.contextId];
                    expect(foundContext).toBe(undefined);
                    if (!foundContext) {
                        foundContexts[context.contextId] = context.contextId;
                    }
                });
            });
    });
});