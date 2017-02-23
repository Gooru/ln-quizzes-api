const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');

let randomCollectionId = QuizzesCommon.generateUUID();

QuizzesCommon.startTest('Get collection without type parameter', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyBadRequest(`/v1/collections/${randomCollectionId}`,
            'Get collection without type parameter', authToken);
    });
});

QuizzesCommon.startTest('Get collection with invalid parameter type', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyBadRequest(`/v1/collections/${randomCollectionId}?type=wrong_type`,
            'Get collection with invalid parameter type', authToken);
    });
});

QuizzesCommon.startTest('Get not existing collection', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyContentNotFound(`/v1/collections/${randomCollectionId}?type=collection`,
            'Get not existing collection', authToken);
    });
});

QuizzesCommon.startTest('Get collection endpoint', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.getCollectionById(collectionId, authToken, function (collection) {
            expect(collection.id).toEqual(collectionId);
            expect(collection.metadata.title).toEqual('Questions types collection [DO NOT CHANGE]');
            expect(collection.metadata.taxonomy).toBeDefined();
            expect(collection.isCollection).toEqual(true);
            expect(collection.resources.length).toEqual(10);
        });
    });
});