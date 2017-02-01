const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Collection Endpoint", function () {
    frisby.create('Call Get Collection Endpoint')
        .get(QuizzesApiUrl + '/v1/collections/4474b36c-7d74-4189-980c-34a0eea539b0?type=collection')
        .inspectRequest()

        .expectStatus(200)
        .inspectJSON()
        .expectJSON({
            "id": "4474b36c-7d74-4189-980c-34a0eea539b0",
            "metadata": {
                "title": "Understand Equal Groups as Multiplication"
            },
            "resources": [
                {
                    "id": "b121ac06-f58e-465b-a23e-f73ac8051ca0",
                    "isResource": false,
                    "sequence": 1,
                    "metadata": {
                        "title": "<p>Select the correct statements<br data-mce-bogus=\"1\"></p>",
                        "type": "multiple_choice",
                        "body": "<p>Select the correct statements<br data-mce-bogus=\"1\"></p>",
                        "correctAnswer": [
                            {
                                "value": "PHA+MyszKzM9OTxiciBkYXRhLW1jZS1ib2d1cz0iMSI+PC9wPg=="
                            },
                            {
                                "value": "PHA+MyBncm91cHMgb2YgMyA9IDk8YnIgZGF0YS1tY2UtYm9ndXM9IjEiPjwvcD4="
                            },
                            {
                                "value": "PHA+MyB4IDMgPSA5PGJyIGRhdGEtbWNlLWJvZ3VzPSIxIj48L3A+"
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "<p>3+3+3=9<br data-mce-bogus=\"1\"></p>",
                                    "isFixed": true,
                                    "value": "PHA+MyszKzM9OTxiciBkYXRhLW1jZS1ib2d1cz0iMSI+PC9wPg==",
                                    "sequence": 1
                                },
                                {
                                    "text": "<p>3 groups of 3 = 9<br data-mce-bogus=\"1\"></p>",
                                    "isFixed": true,
                                    "value": "PHA+MyBncm91cHMgb2YgMyA9IDk8YnIgZGF0YS1tY2UtYm9ndXM9IjEiPjwvcD4=",
                                    "sequence": 2
                                },
                                {
                                    "text": "<p>3 x 3 = 9<br data-mce-bogus=\"1\"></p>",
                                    "isFixed": true,
                                    "value": "PHA+MyB4IDMgPSA5PGJyIGRhdGEtbWNlLWJvZ3VzPSIxIj48L3A+",
                                    "sequence": 3
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "4a89b000-de6b-468d-a798-44b399820a15",
                    "isResource": true,
                    "sequence": 2,
                    "metadata": {
                        "title": "Multiplication as Equal Groups",
                        "type": "video_resource",
                        "url": "https://learnzillion.com/assignments/Z66RB3H"
                    }
                },
                {
                    "id": "ff8dda39-4486-4b3b-9e91-fbc90a679010",
                    "isResource": true,
                    "sequence": 3,
                    "metadata": {
                        "title": "Practice It!",
                        "type": "interactive_resource",
                        "url": "http://www.aaamath.com/mul39_x3.htm#section2"
                    }
                },
                {
                    "id": "0835a732-48ff-431c-a35c-74598b85dc05",
                    "isResource": true,
                    "sequence": 4,
                    "metadata": {
                        "title": "Multiplication Picnic",
                        "type": "interactive_resource",
                        "url": "http://www.sheppardsoftware.com/mathgames/earlymath/multiplicationPicnic.htm"
                    }
                },
                {
                    "id": "83bc6411-4ab9-48d2-bb55-1025a9a08d57",
                    "isResource": true,
                    "sequence": 5,
                    "metadata": {
                        "title": "Carls-Cookie-Capers",
                        "type": "interactive_resource",
                        "url": "http://www.multiplication.com/games/play/carls-cookie-capers"
                    }
                },
                {
                    "id": "5f41dfea-cf13-4ecc-b077-e772732d5bfc",
                    "isResource": false,
                    "sequence": 6,
                    "metadata": {
                        "title": "<p>Which multiplication sentence is shown in the groups? 4×_______=_______</p>",
                        "type": "none",
                        "body": "<p>Which multiplication sentence is shown in the groups? 4×_______=_______</p>",
                        "correctAnswer": [],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "5",
                                    "isFixed": true,
                                    "value": "NQ==",
                                    "sequence": 1
                                },
                                {
                                    "text": "20",
                                    "isFixed": true,
                                    "value": "MjA=",
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

QuizzesCommon.startTest("Get Collection Endpoint Type Assessment", function () {
    frisby.create('Call Get Collection Endpoint')
        .get(QuizzesApiUrl + '/v1/collections/25ecc609-64d8-4d1c-b461-95f31c97e733?type=assessment')
        .inspectRequest()

        .expectStatus(200)
        .inspectJSON()
        .expectJSON({
            "id": "25ecc609-64d8-4d1c-b461-95f31c97e733",
            "metadata": {
                "title": "Assessment #1"
            },
            "resources": [
                {
                    "id": "772cf85f-a6d7-42b6-83c9-c4bb6d270dcd",
                    "isResource": false,
                    "sequence": 1,
                    "metadata": {
                        "title": "MC - Select the correct operation result",
                        "type": "single_choice",
                        "body": "MC - Select the correct operation result",
                        "correctAnswer": [
                            {
                                "value": "NDA="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "40",
                                    "isFixed": true,
                                    "value": "NDA=",
                                    "sequence": 1
                                },
                                {
                                    "text": "50",
                                    "isFixed": true,
                                    "value": "NTA=",
                                    "sequence": 2
                                },
                                {
                                    "text": "60",
                                    "isFixed": true,
                                    "value": "NjA=",
                                    "sequence": 3
                                },
                                {
                                    "text": "70",
                                    "isFixed": true,
                                    "value": "NzA=",
                                    "sequence": 4
                                },
                                {
                                    "text": "80",
                                    "isFixed": true,
                                    "value": "ODA=",
                                    "sequence": 5
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "4b8994d5-f40b-4e82-9760-6ce31baa5f0c",
                    "isResource": false,
                    "sequence": 2,
                    "metadata": {
                        "title": "TF - The result is correct?",
                        "type": "true_false",
                        "body": "TF - The result is correct?",
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
                    "id": "afb3b054-4e38-4a3f-85bd-995023f9150b",
                    "isResource": false,
                    "sequence": 3,
                    "metadata": {
                        "title": "DD - Reorder the numbers from lowest to highest",
                        "type": "drag_and_drop",
                        "body": "DD - Reorder the numbers from lowest to highest",
                        "correctAnswer": [
                            {
                                "value": "MTA="
                            },
                            {
                                "value": "MjA="
                            },
                            {
                                "value": "MzA="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "10",
                                    "isFixed": true,
                                    "value": "MTA=",
                                    "sequence": 1
                                },
                                {
                                    "text": "20",
                                    "isFixed": true,
                                    "value": "MjA=",
                                    "sequence": 2
                                },
                                {
                                    "text": "30",
                                    "isFixed": true,
                                    "value": "MzA=",
                                    "sequence": 3
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "83f97669-8c61-4adf-8403-68087d1ba8c7",
                    "isResource": false,
                    "sequence": 4,
                    "metadata": {
                        "title": "DD2 - Reorder the numbers from  highest to lowest",
                        "type": "drag_and_drop",
                        "body": "DD2 - Reorder the numbers from  highest to lowest",
                        "correctAnswer": [
                            {
                                "value": "MTA="
                            },
                            {
                                "value": "MjA="
                            },
                            {
                                "value": "MzA="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "10",
                                    "isFixed": true,
                                    "value": "MTA=",
                                    "sequence": 1
                                },
                                {
                                    "text": "20",
                                    "isFixed": true,
                                    "value": "MjA=",
                                    "sequence": 2
                                },
                                {
                                    "text": "30",
                                    "isFixed": true,
                                    "value": "MzA=",
                                    "sequence": 3
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "a686db4f-d31a-465b-87c9-8a821fa9f4a6",
                    "isResource": false,
                    "sequence": 5,
                    "metadata": {
                        "title": "FIB - Complete the operation",
                        "type": "none",
                        "body": "FIB - Complete the operation",
                        "correctAnswer": [
                            {
                                "value": "NDA="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "40",
                                    "isFixed": true,
                                    "value": "NDA=",
                                    "sequence": 1
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "9a808723-af22-4c39-b59d-95c60a4dcb0f",
                    "isResource": false,
                    "sequence": 6,
                    "metadata": {
                        "title": "FIB2 - Complete the operation",
                        "type": "none",
                        "body": "FIB2 - Complete the operation",
                        "correctAnswer": [
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
                                    "text": "5",
                                    "isFixed": true,
                                    "value": "NQ==",
                                    "sequence": 1
                                }
                            ]
                        }
                    }
                },
                {
                    "id": "e25c072c-4f32-493f-adad-22ba919552f4",
                    "isResource": false,
                    "sequence": 7,
                    "metadata": {
                        "title": "TF2 - The result is correct?",
                        "type": "true_false",
                        "body": "TF2 - The result is correct?",
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
                    "id": "dd16082f-2816-4cc8-9bf6-bb87d1028fa3",
                    "isResource": false,
                    "sequence": 8,
                    "metadata": {
                        "title": "MC2 - Select the correct operation result",
                        "type": "single_choice",
                        "body": "MC2 - Select the correct operation result",
                        "correctAnswer": [
                            {
                                "value": "NjA="
                            }
                        ],
                        "interaction": {
                            "shuffle": false,
                            "maxChoices": 0,
                            "prompt": "",
                            "choices": [
                                {
                                    "text": "60",
                                    "isFixed": true,
                                    "value": "NjA=",
                                    "sequence": 1
                                },
                                {
                                    "text": "50",
                                    "isFixed": true,
                                    "value": "NTA=",
                                    "sequence": 2
                                },
                                {
                                    "text": "60",
                                    "isFixed": true,
                                    "value": "NjA=",
                                    "sequence": 3
                                },
                                {
                                    "text": "70",
                                    "isFixed": true,
                                    "value": "NzA=",
                                    "sequence": 4
                                },
                                {
                                    "text": "80",
                                    "isFixed": true,
                                    "value": "ODA=",
                                    "sequence": 5
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