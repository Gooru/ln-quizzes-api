const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get collection without type parameter", function () {
    QuizzesCommon.verifyBadRequest('/v1/collections/' + QuizzesCommon.generateUUID(), "Get collection without type parameter");
});

QuizzesCommon.startTest("Get collection with invalid parameter type", function () {
    QuizzesCommon.verifyBadRequest('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=wrong_type', "Get collection with invalid parameter type");
});

QuizzesCommon.startTest("Get not existing collection", function () {
    QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=collection', "Get Collection Endpoint Type Assessment Content Not Found");
});

QuizzesCommon.startTest("Get collection endpoint", function () {
    QuizzesCommon.getCollectionById(QuizzesCommon.questionTypeDemoCollection, function(json) {
        expect(json.id).toEqual(QuizzesCommon.questionTypeDemoCollection);
        expect(json.metadata.title).toEqual("Questions types collection [DO NOT CHANGE]");
        expect(json.isCollection).toEqual(true);
        expect(json.resources.length).toEqual(10);
    });
});