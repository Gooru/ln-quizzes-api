const QuizzesCommon = require('./quizzesCommon.js');

QuizzesCommon.startTest("Verifies that there are not repeated Assigned Contexts in the response", function() {
    QuizzesCommon.getAuthorizationToken("Student01", function(authorizationResponse) {
        QuizzesCommon.doGet('Get Assigned Contexts', '/v1/contexts/assigned', 200,
            QuizzesCommon.resolveAccessToken(authorizationResponse), function(assignedContexts) {
                var foundContexts = {};
                assignedContexts.forEach(function(context) {
                    var foundContext = foundContexts[context.contextId];
                    expect(foundContext).toBe(undefined);
                    if (!foundContext) {
                        foundContexts[context.contextId] = context.contextId;
                    }
                });
            });
    });
});