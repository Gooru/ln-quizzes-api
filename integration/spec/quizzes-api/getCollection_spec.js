const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get collection without type parameter", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        QuizzesCommon.verifyBadRequest('/v1/collections/' + QuizzesCommon.generateUUID(), "Get collection without type parameter", authResponse.access_token);
    });
});

QuizzesCommon.startTest("Get collection with invalid parameter type", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        QuizzesCommon.verifyBadRequest('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=wrong_type', "Get collection with invalid parameter type", authResponse.access_token);
    });
});

QuizzesCommon.startTest("Get not existing collection", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=collection', "Get not existing collection", authResponse.access_token);
    });
});

QuizzesCommon.startTest("Get collection endpoint", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        var collection = Config.getCollection("TestCollection01");
        QuizzesCommon.getCollectionById(collection.id, authResponse.access_token, function(json) {
            expect(json.id).toEqual(collection.id);
            expect(json.metadata.title).toEqual("Questions types collection [DO NOT CHANGE]");
            expect(json.isCollection).toEqual(true);
            expect(json.resources.length).toEqual(10);
        });
    });
});