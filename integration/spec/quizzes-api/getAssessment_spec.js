const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Assessment Endpoint", function () {
    frisby.create('Call Get Assessment Endpoint')
        .get(QuizzesApiUrl + '/v1/assessments/84e1c431-eb14-49f1-bba2-bba21bdd8c77')
        .addHeader('profile-id', '84e1c431-eb14-49f1-bba2-bba21bdd8c77')
        .addHeader('client-id', 'quizzes')
        .inspectRequest()

        .expectStatus(200)
        .inspectJSON()
        .expectJSON({
            "id": "84e1c431-eb14-49f1-bba2-bba21bdd8c77",
            "resources": [
                {
                    "id": "f4a88b38-9fcd-4f9a-b505-cc1952849da5",
                    "isResource": false,
                    "sequence": 1,
                    "metadata": {
                        "correctAnswer": [
                            {
                                "value": "VHJ1ZQ=="
                            }
                        ],
                        "title": "TF - The operation result is correct ?",
                        "type": "true_false",
                        "body": "TF - The operation result is correct ?",
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
                    "id": "19988b6b-7a45-4641-bb0a-1d5a68072b1b",
                    "isResource": false,
                    "sequence": 2,
                    "metadata": {
                        "correctAnswer": [
                            {
                                "value": "MjDCoA=="
                            }
                        ],
                        "title": "MC - Select the correct operation result",
                        "type": "single_choice",
                        "body": "MC - Select the correct operation result",
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
                    "sequence": 3,
                    "metadata": {
                        "correctAnswer": [
                            {
                                "value": "RmFsc2U="
                            }
                        ],
                        "title": "TF - The number is odd ?",
                        "type": "true_false",
                        "body": "TF - The number is odd ?",
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
                    "sequence": 4,
                    "metadata": {
                        "correctAnswer": [
                            {
                                "value": "OTg5ODcxMg=="
                            }
                        ],
                        "title": "MC - Select the even number",
                        "type": "single_choice",
                        "body": "MC - Select the even number",
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
                    "sequence": 5,
                    "metadata": {
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
                        "title": "DD- Order the numbers from lowest to highest",
                        "type": "drag_and_drop",
                        "body": "DD- Order the numbers from lowest to highest",
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
                    "sequence": 6,
                    "metadata": {
                        "correctAnswer": [
                            {
                                "value": "MisgMiA9IDQ="
                            },
                            {
                                "value": "MjUgLSA1ID0gMjDCoA=="
                            }
                        ],
                        "title": "MA - Select the correct operation results",
                        "type": "none",
                        "body": "MA - Select the correct operation results",
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
                }
            ],
            "metadata": {
                "title": "Basic Math Assessment"
            }
        })
        .toss();
});