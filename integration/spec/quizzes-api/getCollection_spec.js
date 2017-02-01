const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Collection Endpoint", function () {
    frisby.create('Call Get Collection Endpoint')
        .get(QuizzesApiUrl + '/v1/collections/df763604-8e7a-43c8-b33c-6e0669e339f1?type=collection')
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
                    "id": "d01963df-3b2e-4371-9862-db71c656e8a3",
                    "isResource": true,
                    "sequence": 1,
                    "metadata": {
                        "title": "Youtube lesson",
                        "type": "video_resource",
                        "url": "https://www.youtube.com/watch?v=-wkr_vf18cw"
                    }
                },
                {
                    "id": "d03e2d0d-2dbd-43e2-8bb0-e75fbfb29840",
                    "isResource": true,
                    "sequence": 2,
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
                },
                {
                    "id": "e5161516-a47c-4dba-ba10-459ed43e14ad",
                    "isResource": false,
                    "sequence": 6,
                    "metadata": {
                        "title": "FIB - Question",
                        "type": "none",
                        "body": "FIB - Question"
                    }
                }
            ]
        })
        .toss();
});

QuizzesCommon.startTest("Get Collection Endpoint Type Assessment", function () {
    frisby.create('Call Get Collection Endpoint')
        .get(QuizzesApiUrl + '/v1/collections/84e1c431-eb14-49f1-bba2-bba21bdd8c77?type=assessment')
        .inspectRequest()

        .expectStatus(200)
        .inspectJSON()
        .expectJSON({
            "id": "84e1c431-eb14-49f1-bba2-bba21bdd8c77",
            "metadata": {
                "title": "Basic Math Assessment"
            },
            "resources": [
                {
                    "id": "19988b6b-7a45-4641-bb0a-1d5a68072b1b",
                    "isResource": false,
                    "sequence": 1,
                    "metadata": {
                        "title": "MC - Select the correct operation result",
                        "type": "single_choice",
                        "body": "MC - Select the correct operation result",
                        "correctAnswer": [
                            {
                                "value": "MjDCoA=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "18",
                                    "isFixed": true,
                                    "value": "MTg=",
                                    "sequence": 1
                                },
                                {
                                    "text": "20 ",
                                    "isFixed": true,
                                    "value": "MjDCoA==",
                                    "sequence": 2
                                },
                                {
                                    "text": "22",
                                    "isFixed": true,
                                    "value": "MjI=",
                                    "sequence": 3
                                },
                                {
                                    "text": "24",
                                    "isFixed": true,
                                    "value": "MjQ=",
                                    "sequence": 4
                                },
                                {
                                    "text": "16",
                                    "isFixed": true,
                                    "value": "MTY=",
                                    "sequence": 5
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "8bff2e55-a8fb-4780-8815-e467ecc91a49",
                    "isResource": false,
                    "sequence": 2,
                    "metadata": {
                        "title": "TF - The number is odd ?",
                        "type": "true_false",
                        "body": "TF - The number is odd ?",
                        "correctAnswer": [
                            {
                                "value": "RmFsc2U="
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
                },
                {
                    "id": "4b09c1db-c509-4a6b-80e3-a2890f4c05cd",
                    "isResource": false,
                    "sequence": 3,
                    "metadata": {
                        "title": "MC - Select the even number",
                        "type": "single_choice",
                        "body": "MC - Select the even number",
                        "correctAnswer": [
                            {
                                "value": "OTg5ODcxMg=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "100121",
                                    "isFixed": true,
                                    "value": "MTAwMTIx",
                                    "sequence": 1
                                },
                                {
                                    "text": "9898712",
                                    "isFixed": true,
                                    "value": "OTg5ODcxMg==",
                                    "sequence": 2
                                },
                                {
                                    "text": "11231",
                                    "isFixed": true,
                                    "value": "MTEyMzE=",
                                    "sequence": 3
                                },
                                {
                                    "text": "081115",
                                    "isFixed": true,
                                    "value": "MDgxMTE1",
                                    "sequence": 4
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "55b98e6a-1e03-408f-b482-e731b08cb9c9",
                    "isResource": false,
                    "sequence": 4,
                    "metadata": {
                        "title": "DD- Order the numbers from lowest to highest",
                        "type": "drag_and_drop",
                        "body": "DD- Order the numbers from lowest to highest",
                        "correctAnswer": [
                            {
                                "value": "MQ=="
                            },
                            {
                                "value": "Mg=="
                            },
                            {
                                "value": "Mw=="
                            },
                            {
                                "value": "NA=="
                            },
                            {
                                "value": "NQ=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "1",
                                    "isFixed": true,
                                    "value": "MQ==",
                                    "sequence": 1
                                },
                                {
                                    "text": "2",
                                    "isFixed": true,
                                    "value": "Mg==",
                                    "sequence": 2
                                },
                                {
                                    "text": "3",
                                    "isFixed": true,
                                    "value": "Mw==",
                                    "sequence": 3
                                },
                                {
                                    "text": "4",
                                    "isFixed": true,
                                    "value": "NA==",
                                    "sequence": 4
                                },
                                {
                                    "text": "5",
                                    "isFixed": true,
                                    "value": "NQ==",
                                    "sequence": 5
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "95048df2-8134-4875-b767-849facf90262",
                    "isResource": false,
                    "sequence": 5,
                    "metadata": {
                        "title": "MA - Select the correct operation results",
                        "type": "none",
                        "body": "MA - Select the correct operation results",
                        "correctAnswer": [
                            {
                                "value": "MisgMiA9IDQ="
                            },
                            {
                                "value": "MjUgLSA1ID0gMjDCoA=="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "2+ 2 = 4",
                                    "isFixed": true,
                                    "value": "MisgMiA9IDQ=",
                                    "sequence": 1
                                },
                                {
                                    "text": "5+5 = 11",
                                    "isFixed": true,
                                    "value": "NSs1ID0gMTE=",
                                    "sequence": 2
                                },
                                {
                                    "text": "25 - 5 = 20 ",
                                    "isFixed": true,
                                    "value": "MjUgLSA1ID0gMjDCoA==",
                                    "sequence": 3
                                },
                                {
                                    "text": "10 - 10 = 1",
                                    "isFixed": true,
                                    "value": "MTAgLSAxMCA9IDE=",
                                    "sequence": 4
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "f4a88b38-9fcd-4f9a-b505-cc1952849da5",
                    "isResource": false,
                    "sequence": 6,
                    "metadata": {
                        "title": "TF - The operation result is correct ?",
                        "type": "true_false",
                        "body": "TF - The operation result is correct ?",
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

QuizzesCommon.startTest("Get Collection Endpoint Should Without Type", function () {
    QuizzesCommon.verifyInternalServerError('/v1/collections/' + QuizzesCommon.generateUUID(), "Get Collection Endpoint Should Without Type");
});

QuizzesCommon.startTest("Get Collection Endpoint With Wrong Type", function () {
    QuizzesCommon.verifyInvalidRequest('/v1/collections/84e1c431-eb14-49f1-bba2-bba21bdd8c77?type=aesment', "Get Collection Endpoint With Wrong Type");
});

QuizzesCommon.startTest("Get Collection Endpoint Type Assessment Content Not Found", function () {
    QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=assessment', "Get Collection Endpoint Type Assessment Content Not Found");
});

QuizzesCommon.startTest("Get Assessment Endpoint Type Collection Not Found", function () {
    QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=collection', "Get Assessment Endpoint Type Collection Not Found");
});