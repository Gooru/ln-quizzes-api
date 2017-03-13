const QuizzesCommon = require('./quizzesCommon.js');
const HttpErrorCodes = QuizzesCommon.httpErrorCodes;
const Config = require('./quizzesTestConfiguration.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');

// var verifyGetContextEvents = function (contextId, ownerProfileId, result, afterJsonFunction) {
//     return frisby.create('Get the context Events as an owner')
//         .get(QuizzesApiUrl + '/v1/contexts/' + contextId + '/events')
//         .addHeader('profile-id', ownerProfileId)
//         .addHeader('lms-id', 'quizzes')
//         .inspectRequest()
//         .expectStatus(200)
//         .inspectJSON()
//         .expectJSON(result)
//         .afterJSON(function () {
//             afterJsonFunction(afterJsonFunction);
//         })
// };

QuizzesCommon.startTest('Test finished context summary for 9 correctly answered questions + 1 free response', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.getCollectionById(collectionId, authToken,function (collectionResponse){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[1].id, {
                            "previousResource": {
                                "answer": collectionResponse.resources[0].metadata.correctAnswer,
                                "reaction": 3,
                                "resourceId": collectionResponse.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, assigneeAuthToken, function () {
                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[2].id, {
                                "previousResource": {
                                    "answer": collectionResponse.resources[1].metadata.correctAnswer,
                                    "reaction": 3,
                                    "resourceId": collectionResponse.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, assigneeAuthToken, function () {
                                // Third question - correct
                                QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[3].id, {
                                    "previousResource": {
                                        "answer": collectionResponse.resources[2].metadata.correctAnswer,
                                        "reaction": 3,
                                        "resourceId": collectionResponse.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, assigneeAuthToken, function () {
                                    // Fourth question - correct
                                    QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[4].id, {
                                        "previousResource": {
                                            "answer": collectionResponse.resources[3].metadata.correctAnswer,
                                            "reaction": 3,
                                            "resourceId": collectionResponse.resources[3].id,
                                            "timeSpent": 1000
                                        }
                                    }, assigneeAuthToken, function () {
                                        // Fifth question - correct
                                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[5].id, {
                                            "previousResource": {
                                                "answer": collectionResponse.resources[4].metadata.correctAnswer,
                                                "reaction": 3,
                                                "resourceId": collectionResponse.resources[4].id,
                                                "timeSpent": 1000
                                            }
                                        }, assigneeAuthToken, function () {
                                            // Sixth question - correct
                                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[6].id, {
                                                "previousResource": {
                                                    "answer": collectionResponse.resources[5].metadata.correctAnswer,
                                                    "reaction": 3,
                                                    "resourceId": collectionResponse.resources[5].id,
                                                    "timeSpent": 1000
                                                }
                                            }, assigneeAuthToken, function () {
                                                // Seventh question - correct
                                                QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[7].id, {
                                                    "previousResource": {
                                                        "answer": collectionResponse.resources[6].metadata.correctAnswer,
                                                        "reaction": 3,
                                                        "resourceId": collectionResponse.resources[6].id,
                                                        "timeSpent": 1000
                                                    }
                                                }, assigneeAuthToken, function () {
                                                    // Eighth question - correct
                                                    QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[8].id, {
                                                        "previousResource": {
                                                            "answer": collectionResponse.resources[7].metadata.correctAnswer,
                                                            "reaction": 3,
                                                            "resourceId": collectionResponse.resources[7].id,
                                                            "timeSpent": 1000
                                                        }
                                                    }, assigneeAuthToken, function () {
                                                        // Ninth question - correct
                                                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[9].id, {
                                                            "previousResource": {
                                                                "answer": collectionResponse.resources[8].metadata.correctAnswer,
                                                                "reaction": 3,
                                                                "resourceId": collectionResponse.resources[8].id,
                                                                "timeSpent": 1000
                                                            }
                                                        }, assigneeAuthToken, function () {
                                                            // Tenth question - wrong, Free Response score is 0
                                                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[9].id, {
                                                                "previousResource": {
                                                                    "answer": [ { value: 'any answer' } ],
                                                                    "reaction": 3,
                                                                    "resourceId": collectionResponse.resources[9].id,
                                                                    "timeSpent": 1000
                                                                }
                                                            }, assigneeAuthToken, function () {
                                                                QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                                                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                                                    Frisby.create('')
                                                                        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextId}`)
                                                                        .addHeader('Authorization', 'Token ' + authToken)
                                                                        .inspectRequest()
                                                                        .expectStatus(200)
                                                                        .expectHeaderContains('content-type', 'application/json')
                                                                        .expectJSON({
                                                                            "collectionId": collectionId,
                                                                            "contextId": contextId,
                                                                            "profileAttempts": [
                                                                                {
                                                                                    "currentResourceId": collectionResponse.resources[9].id,
                                                                                    "profileId": assigneeProfileId,
                                                                                    "eventSummary": {
                                                                                        "totalTimeSpent": 10000,
                                                                                        "averageReaction": 3,
                                                                                        "averageScore": 90,
                                                                                        "totalCorrect": 9,
                                                                                        "totalAnswered": 10
                                                                                    }
                                                                                }
                                                                            ]
                                                                        })
                                                                        .inspectJSON()
                                                                        .toss();

                                                                });
                                                            });
                                                        });
                                                    });
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Test finished context summary for 10 incorrectly answered questions + 1 free response', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.getCollectionById(collectionId, authToken,function (collectionResponse){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'A' } ],
                                "reaction": 3,
                                "resourceId": collectionResponse.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, assigneeAuthToken, function () {
                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'D' } ],
                                    "reaction": 3,
                                    "resourceId": collectionResponse.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, assigneeAuthToken, function () {
                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'D' } ],
                                        "reaction": 3,
                                        "resourceId": collectionResponse.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, assigneeAuthToken, function () {
                                    // Fourth question - incorrect
                                    QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[4].id, {
                                        "previousResource": {
                                            "answer": [ { value: 'D' } ],
                                            "reaction": 3,
                                            "resourceId": collectionResponse.resources[3].id,
                                            "timeSpent": 1000
                                        }
                                    }, assigneeAuthToken, function () {
                                        // Fifth question - incorrect
                                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[5].id, {
                                            "previousResource": {
                                                "answer": [ { value: 'D' } ],
                                                "reaction": 3,
                                                "resourceId": collectionResponse.resources[4].id,
                                                "timeSpent": 1000
                                            }
                                        }, assigneeAuthToken, function () {
                                            // Sixth question - incorrect
                                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[6].id, {
                                                "previousResource": {
                                                    "answer": [ { value: 'D' } ],
                                                    "reaction": 3,
                                                    "resourceId": collectionResponse.resources[5].id,
                                                    "timeSpent": 1000
                                                }
                                            }, assigneeAuthToken, function () {
                                                // Seventh question - incorrect
                                                QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[7].id, {
                                                    "previousResource": {
                                                        "answer": [ { value: 'D' } ],
                                                        "reaction": 3,
                                                        "resourceId": collectionResponse.resources[6].id,
                                                        "timeSpent": 1000
                                                    }
                                                }, assigneeAuthToken, function () {
                                                    // Eighth question - incorrect
                                                    QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[8].id, {
                                                        "previousResource": {
                                                            "answer": [ { value: 'D' } ],
                                                            "reaction": 3,
                                                            "resourceId": collectionResponse.resources[7].id,
                                                            "timeSpent": 1000
                                                        }
                                                    }, assigneeAuthToken, function () {
                                                        // Ninth question - incorrect
                                                        QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[9].id, {
                                                            "previousResource": {
                                                                "answer":[ { value: 'D' } ],
                                                                "reaction": 3,
                                                                "resourceId": collectionResponse.resources[8].id,
                                                                "timeSpent": 1000
                                                            }
                                                        }, assigneeAuthToken, function () {
                                                            // Tenth question - incorrect
                                                            QuizzesCommon.onResourceEvent(contextId, collectionResponse.resources[9].id, {
                                                                "previousResource": {
                                                                    "answer": [ { value: 'D' } ],
                                                                    "reaction": 3,
                                                                    "resourceId": collectionResponse.resources[9].id,
                                                                    "timeSpent": 1000
                                                                }
                                                            }, assigneeAuthToken, function () {
                                                                QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                                                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                                                    Frisby.create('')
                                                                        .get(QuizzesApiUrl + `/v1/attempts/contexts/${contextId}`)
                                                                        .addHeader('Authorization', 'Token ' + authToken)
                                                                        .inspectRequest()
                                                                        .expectStatus(200)
                                                                        .expectHeaderContains('content-type', 'application/json')
                                                                        .expectJSON({
                                                                            "collectionId": collectionId,
                                                                            "contextId": contextId,
                                                                            "profileAttempts": [
                                                                                {
                                                                                    "currentResourceId": collectionResponse.resources[9].id,
                                                                                    "profileId": assigneeProfileId,
                                                                                    "eventSummary": {
                                                                                        "totalTimeSpent": 10000,
                                                                                        "averageReaction": 3,
                                                                                        "averageScore": 0,
                                                                                        "totalCorrect": 0,
                                                                                        "totalAnswered": 10
                                                                                    }
                                                                                }
                                                                            ]
                                                                        })
                                                                        .inspectJSON()
                                                                        .toss();

                                                                });
                                                            });
                                                        });
                                                    });
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});

/*
QuizzesCommon.startTest("Test finished context summary for 2 correct and 1 incorrect answered questions, the other 7 skipped", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'D' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'B' } ],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'Incorrect value.' } ],
                                        "reaction": 3,
                                        "resourceId": collection.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    QuizzesCommon.finishContext(contextAssigned.id, assigneeProfile.id, function () {
                                        QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                            QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                                {
                                                    "collection": {
                                                        "id": collection.id
                                                    },
                                                    "contextId": contextAssigned.id,
                                                    "profileEvents": [
                                                        {
                                                            "currentResourceId": collection.resources[3].id,
                                                            "profileId": assigneeProfile.id,
                                                            "contextProfileSummary": {
                                                                "totalTimeSpent": 3000,
                                                                "averageReaction": 3,
                                                                "averageScore": 20,
                                                                "totalCorrect": 2,
                                                                "totalAnswered": 10
                                                            }
                                                        }
                                                    ]
                                                }, function () {
                                                }
                                            );
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
QuizzesCommon.startTest("Test finished context summary spent time calculation for multiple visits of the same question", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'D' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - skipped
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 5000
                                }
                            }, function () {
                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'Incorrect value.' } ],
                                        "reaction": 3,
                                        "resourceId": collection.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    // Second question - correct
                                    QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                        "previousResource": {
                                            "answer": [ { value: 'B' } ],
                                            "reaction": 3,
                                            "resourceId": collection.resources[1].id,
                                            "timeSpent": 1000
                                        }
                                    }, function () {
                                        QuizzesCommon.finishContext(contextAssigned.id, assigneeProfile.id, function () {
                                            QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                                QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                                    {
                                                        "collection": {
                                                            "id": collection.id
                                                        },
                                                        "contextId": contextAssigned.id,
                                                        "profileEvents": [
                                                            {
                                                                "currentResourceId": collection.resources[2].id,
                                                                "profileId": assigneeProfile.id,
                                                                "contextProfileSummary": {
                                                                    "totalTimeSpent": 8000,
                                                                    "averageReaction": 3,
                                                                    "averageScore": 20,
                                                                    "totalCorrect": 2,
                                                                    "totalAnswered": 10
                                                                }
                                                            }
                                                        ]
                                                    }, function () {
                                                    }
                                                );
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
// this one checks that any skipped question doesn't have an answer data (answer, reaction, time spent, ...)
// and that every answered question has that info
QuizzesCommon.startTest("Test finished context summary with 2 answered questions and 8 skipped", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'D' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'B' } ],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                QuizzesCommon.finishContext(contextAssigned.id, assigneeProfile.id, function () {
                                    QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                        verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                            {
                                                "collection": {
                                                    "id": collection.id
                                                },
                                                "contextId": contextAssigned.id,
                                                "profileEvents": [
                                                    {
                                                        "currentResourceId": collection.resources[2].id,
                                                        "profileId": assigneeProfile.id,
                                                        "contextProfileSummary": {
                                                            "totalTimeSpent": 2000,
                                                            "averageReaction": 3,
                                                            "averageScore": 20,
                                                            "totalCorrect": 2,
                                                            "totalAnswered": 10
                                                        }
                                                    }
                                                ]
                                            }, function () {
                                            }
                                        ).expectJSON("profileEvents.0.events.?", { "score": 100,
                                            "isSkipped": false,
                                            "resourceId": collection.resources[0].id,
                                            "timeSpent": 1000,
                                            "reaction": 3,
                                            "answer": [ { value: 'D' } ] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 100,
                                            "isSkipped": false,
                                            "resourceId": collection.resources[1].id,
                                            "timeSpent": 1000,
                                            "reaction": 3,
                                            "answer": [ { value: 'B' } ] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[2].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[3].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[4].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[5].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[6].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[7].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[8].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .expectJSON("profileEvents.0.events.?", { "score": 0,
                                            "isSkipped": true,
                                            "resourceId": collection.resources[9].id,
                                            "timeSpent": 0,
                                            "reaction": 0,
                                            "answer": [] })
                                        .toss();
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
QuizzesCommon.startTest("Test finished context summary when a question answer and reaction are changed", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'A' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - skipped
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[0].id, {
                                "previousResource": {
                                    "answer": [],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                // First question again - now correct
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'D' } ],
                                        "reaction": 5,
                                        "resourceId": collection.resources[0].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    QuizzesCommon.finishContext(contextAssigned.id, assigneeProfile.id, function () {
                                        QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                            QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                                {
                                                    "collection": {
                                                        "id": collection.id
                                                    },
                                                    "contextId": contextAssigned.id,
                                                    "profileEvents": [
                                                        {
                                                            "currentResourceId": collection.resources[1].id,
                                                            "profileId": assigneeProfile.id,
                                                            "contextProfileSummary": {
                                                                "totalTimeSpent": 3000,
                                                                "averageReaction": 4,
                                                                "averageScore": 10,
                                                                "totalCorrect": 1,
                                                                "totalAnswered": 10
                                                            }
                                                        }
                                                    ]
                                                }, function () {
                                                }
                                            );
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
QuizzesCommon.startTest("Test an unfinished context summary with 3 questions answered correctly", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'D' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'B' } ],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                // Third question - correct
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'Smaller amounts of water evaporate in the cool morning.' } ],
                                        "reaction": 3,
                                        "resourceId": collection.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                        QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                            {
                                                "collection": {
                                                    "id": collection.id
                                                },
                                                "contextId": contextAssigned.id,
                                                "profileEvents": [
                                                    {
                                                        "currentResourceId": collection.resources[3].id,
                                                        "profileId": assigneeProfile.id,
                                                        "contextProfileSummary": {
                                                            "totalTimeSpent": 3000,
                                                            "averageReaction": 3,
                                                            "averageScore": 100,
                                                            "totalCorrect": 3,
                                                            "totalAnswered": 3
                                                        }
                                                    }
                                                ]
                                            }, function () {
                                            }
                                        );
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
QuizzesCommon.startTest("Test an unfinished context summary with 3 questions answered incorrectly", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'A' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'A' } ],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'Incorrect value.' } ],
                                        "reaction": 3,
                                        "resourceId": collection.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                        QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                            {
                                                "collection": {
                                                    "id": collection.id
                                                },
                                                "contextId": contextAssigned.id,
                                                "profileEvents": [
                                                    {
                                                        "currentResourceId": collection.resources[3].id,
                                                        "profileId": assigneeProfile.id,
                                                        "contextProfileSummary": {
                                                            "totalTimeSpent": 3000,
                                                            "averageReaction": 3,
                                                            "averageScore": 0,
                                                            "totalCorrect": 0,
                                                            "totalAnswered": 3
                                                        }
                                                    }
                                                ]
                                            }, function () {
                                            }
                                        );
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/
/*
QuizzesCommon.startTest("Test context summary for 10 correctly answered questions", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextById(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id, {
                            "previousResource": {
                                "answer": [ { value: 'D' } ],
                                "reaction": 3,
                                "resourceId": collection.resources[0].id,
                                "timeSpent": 1000
                            }
                        }, function () {
                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[2].id, {
                                "previousResource": {
                                    "answer": [ { value: 'B' } ],
                                    "reaction": 3,
                                    "resourceId": collection.resources[1].id,
                                    "timeSpent": 1000
                                }
                            }, function () {
                                // Third question - correct
                                QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[3].id, {
                                    "previousResource": {
                                        "answer": [ { value: 'Smaller amounts of water evaporate in the cool morning.' } ],
                                        "reaction": 3,
                                        "resourceId": collection.resources[2].id,
                                        "timeSpent": 1000
                                    }
                                }, function () {
                                    // Fourth question - incorrect
                                    QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[4].id, {
                                        "previousResource": {
                                            "answer": [ { value: 'Incorrect value.' } ],
                                            "reaction": 3,
                                            "resourceId": collection.resources[3].id,
                                            "timeSpent": 1000
                                        }
                                    }, function () {
                                        // Fifth question - incorrect
                                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[5].id, {
                                            "previousResource": {
                                                "answer": [ { value: 'Incorrect value.' } ],
                                                "reaction": 3,
                                                "resourceId": collection.resources[4].id,
                                                "timeSpent": 1000
                                            }
                                        }, function () {
                                            QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                                QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                                    {
                                                        "collection": {
                                                            "id": collection.id
                                                        },
                                                        "contextId": contextAssigned.id,
                                                        "profileEvents": [
                                                            {
                                                                "currentResourceId": collection.resources[5].id,
                                                                "profileId": assigneeProfile.id,
                                                                "contextProfileSummary": {
                                                                    "totalTimeSpent": 5000,
                                                                    "averageReaction": 3,
                                                                    "averageScore": 60,
                                                                    "totalCorrect": 3,
                                                                    "totalAnswered": 5
                                                                }
                                                            }
                                                        ]
                                                    }, function () {
                                                    }
                                                );
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});
*/