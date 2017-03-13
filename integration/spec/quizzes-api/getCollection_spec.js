const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;

let randomCollectionId = QuizzesCommon.generateUUID();

QuizzesCommon.startTest('Get collection without type parameter', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyHttpError('Get collection without type parameter',
            `/v1/collections/${randomCollectionId}`, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Get collection with invalid parameter type', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyHttpError('Get collection with invalid parameter type',
            `/v1/collections/${randomCollectionId}?type=wrong_type`, HttpErrorCodes.BAD_REQUEST, authToken);
    });
});

QuizzesCommon.startTest('Get not existing collection', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.verifyHttpError('Get not existing assessment',
            `/v1/collections/${randomCollectionId}?type=collection`, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Get collection endpoint', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.getCollectionById(collectionId, authToken, function (collection) {
            expect(collection.id).toEqual(collectionId);
            expect(collection.metadata.title).toEqual('Collection Test #1 All Question Types');
            expect(collection.metadata.taxonomy).toBeDefined();
            expect(collection.isCollection).toEqual(true);
            expect(collection.resources.length).toEqual(10);
        });
    });
});