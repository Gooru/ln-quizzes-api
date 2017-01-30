const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Collection Endpoint", function () {
    frisby.create('Call Get Collection Endpoint')
        .get(QuizzesApiUrl + '/v1/collections/df763604-8e7a-43c8-b33c-6e0669e339f1')
        .inspectRequest()

        .expectStatus(200)
        .inspectJSON()
        .expectJSON({
            "id": "df763604-8e7a-43c8-b33c-6e0669e339f1",
            "metadata": {
                "title": "Collection Test"
            },
            "resources": [
                {
                    "id": "d03e2d0d-2dbd-43e2-8bb0-e75fbfb29840",
                    "isResource": true,
                    "sequence": 1,
                    "metadata": {
                        "title": "test",
                        "type": "image_resource",
                        "url": "//cdn.gooru.org/710d5ce1-149d-465b-aa93-e85ecd220ecd.png"
                    }
                },
                {
                    "id": "942abf90-b0ca-48c2-a6ef-36725ed35294",
                    "isResource": true,
                    "sequence": 3,
                    "metadata": {
                        "title": "Math web site",
                        "type": "webpage_resource",
                        "url": "http://www.emathematics.net/"
                    }
                },
                {
                    "id": "9025a395-ff64-4bf4-8709-ba8ee8ea82f5",
                    "isResource": false,
                    "sequence": 4,
                    "metadata": {
                        "title": "DD - Order the results from highest to lowest",
                        "type": "drag_and_drop",
                        "body": "DD - Order the results from highest to lowest",
                        "correctAnswer": [
                            {
                                "value": "MTAxMCAtIDEwwqA="
                            },
                            {
                                "value": "NTAwLSAwMQ=="
                            },
                            {
                                "value": "MTAtIDXCoA=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "1010 - 10 ",
                                    "isFixed": true,
                                    "value": "MTAxMCAtIDEwwqA=",
                                    "sequence": 1
                                },
                                {
                                    "text": "500- 01",
                                    "isFixed": true,
                                    "value": "NTAwLSAwMQ==",
                                    "sequence": 2
                                },
                                {
                                    "text": "10- 5 ",
                                    "isFixed": true,
                                    "value": "MTAtIDXCoA==",
                                    "sequence": 3
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "02ee8e05-7015-4f9a-bbf3-19475f90b329",
                    "isResource": false,
                    "sequence": 5,
                    "metadata": {
                        "title": "Escoja la A",
                        "type": "true_false",
                        "body": "Escoja la A",
                        "correctAnswer": [
                            {
                                "value": "VHJ1ZQ=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "True",
                                    "isFixed": true,
                                    "value": "VHJ1ZQ==",
                                    "sequence": 1
                                },
                                {
                                    "text": "False",
                                    "isFixed": true,
                                    "value": "RmFsc2U=",
                                    "sequence": 2
                                }
                            ]
                        }
                    }
                }
            ]
        })
        .toss();
});

QuizzesCommon.startTest("Get Collection Endpoint", function () {
    QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID(), "Call Get Collection Endpoint");
});