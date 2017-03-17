const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');

let defaultTimeSpent = 1000;
let defaultReaction = 3;
let wrongAnswer = [ { value: 'wrong-answer' } ];

QuizzesCommon.startTest('Test finished context summary with all correctly answered questions', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': collection.resources[1].metadata.correctAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                // Third question - correct
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[3].id, {
                                    'previousResource': {
                                        'answer': collection.resources[2].metadata.correctAnswer,
                                        'reaction': defaultReaction,
                                        'resourceId': collection.resources[2].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    // Fourth question - correct
                                    QuizzesCommon.onResourceEvent(contextId, collection.resources[0].id, {
                                        'previousResource': {
                                            'answer': collection.resources[3].metadata.correctAnswer,
                                            'reaction': defaultReaction,
                                            'resourceId': collection.resources[3].id,
                                            'timeSpent': defaultTimeSpent
                                        }
                                    }, assigneeAuthToken, function () {
                                        QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                            let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                            let resourceCount = collection.resources.length;
                                            QuizzesCommon.getAttempts(contextId, authToken, {
                                                'collectionId': assessmentId,
                                                'contextId': contextId,
                                                'profileAttempts': [
                                                    {
                                                        'currentResourceId': collection.resources[0].id,
                                                        'profileId': assigneeProfileId,
                                                        'eventSummary': {
                                                            'totalTimeSpent': defaultTimeSpent * resourceCount,
                                                            'averageReaction': defaultReaction,
                                                            'averageScore': 100,
                                                            'totalCorrect': resourceCount,
                                                            'totalAnswered': resourceCount
                                                        }
                                                    }
                                                ]
                                            }, function() {});
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

QuizzesCommon.startTest('Test finished context summary with all incorrectly answered questions', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': wrongAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': wrongAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[3].id, {
                                    'previousResource': {
                                        'answer': wrongAnswer,
                                        'reaction': defaultReaction,
                                        'resourceId': collection.resources[2].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    // Fourth question - incorrect
                                    QuizzesCommon.onResourceEvent(contextId, collection.resources[0].id, {
                                        'previousResource': {
                                            'answer': wrongAnswer,
                                            'reaction': defaultReaction,
                                            'resourceId': collection.resources[3].id,
                                            'timeSpent': defaultTimeSpent
                                        }
                                    }, assigneeAuthToken, function () {
                                        QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                            let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                            let resourceCount = collection.resources.length;
                                            QuizzesCommon.getAttempts(contextId, authToken, {
                                                'collectionId': assessmentId,
                                                'contextId': contextId,
                                                'profileAttempts': [
                                                    {
                                                        'currentResourceId': collection.resources[0].id,
                                                        'profileId': assigneeProfileId,
                                                        'eventSummary': {
                                                            'totalTimeSpent': defaultTimeSpent * resourceCount,
                                                            'averageReaction': defaultReaction,
                                                            'averageScore': 0,
                                                            'totalCorrect': 0,
                                                            'totalAnswered': resourceCount
                                                        }
                                                    }
                                                ]
                                            }, function() {});
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

QuizzesCommon.startTest('Test finished context summary with all questions skipped', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - skipped (not sending answer)
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - skipped (not sending answer)
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                // Third question - skipped (sending null answer)
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[3].id, {
                                    'previousResource': {
                                        'answer': null,
                                        'reaction': defaultReaction,
                                        'resourceId': collection.resources[2].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    // Fourth question - skipped (sending null answer)
                                    QuizzesCommon.onResourceEvent(contextId, collection.resources[0].id, {
                                        'previousResource': {
                                            'answer': null,
                                            'reaction': defaultReaction,
                                            'resourceId': collection.resources[3].id,
                                            'timeSpent': defaultTimeSpent
                                        }
                                    }, assigneeAuthToken, function () {
                                        QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                            let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                            let resourceCount = collection.resources.length;
                                            QuizzesCommon.getAttempts(contextId, authToken, {
                                                'collectionId': assessmentId,
                                                'contextId': contextId,
                                                'profileAttempts': [
                                                    {
                                                        'currentResourceId': collection.resources[0].id,
                                                        'profileId': assigneeProfileId,
                                                        'eventSummary': {
                                                            'totalTimeSpent': defaultTimeSpent * resourceCount,
                                                            'averageReaction': defaultReaction,
                                                            'averageScore': 0,
                                                            'totalCorrect': 0,
                                                            'totalAnswered': resourceCount
                                                        }
                                                    }
                                                ]
                                            }, function() {
                                                expect(json.profileAttempts[0].events).toContain({
                                                    'score': 0,
                                                    'isSkipped': true,
                                                    'resourceId': collection.resources[0].id,
                                                    'timeSpent': 1000,
                                                    'reaction': 3
                                                });
                                                expect(json.profileAttempts[0].events).toContain({
                                                    'score': 0,
                                                    'isSkipped': true,
                                                    'resourceId': collection.resources[1].id,
                                                    'timeSpent': 1000,
                                                    'reaction': 3
                                                });
                                                expect(json.profileAttempts[0].events).toContain({
                                                    'score': 0,
                                                    'isSkipped': true,
                                                    'resourceId': collection.resources[2].id,
                                                    'timeSpent': 1000,
                                                    'reaction': 3
                                                });
                                                expect(json.profileAttempts[0].events).toContain({
                                                    'score': 0,
                                                    'isSkipped': true,
                                                    'resourceId': collection.resources[3].id,
                                                    'timeSpent': 1000,
                                                    'reaction': 3
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

QuizzesCommon.startTest('Test finished context summary for 1 correct and 1 incorrect, the other 2 skipped', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': wrongAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                    let resourceCount = collection.resources.length;
                                    QuizzesCommon.getAttempts(contextId, authToken, {
                                        'collectionId': assessmentId,
                                        'contextId': contextId,
                                        'profileAttempts': [
                                            {
                                                'currentResourceId': collection.resources[2].id,
                                                'profileId': assigneeProfileId,
                                                'eventSummary': {
                                                    'totalTimeSpent': defaultTimeSpent * 2,
                                                    'averageReaction': defaultReaction,
                                                    'averageScore': 25,
                                                    'totalCorrect': 1,
                                                    'totalAnswered': resourceCount
                                                }
                                            }
                                        ]
                                    }, function() {});
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Test finished context summary for 1 correct and 1 incorrect, the other 2 skipped', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - skipped
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': null,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent * 5
                                }
                            }, assigneeAuthToken, function () {

                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[0].id, {
                                    'previousResource': {
                                        'answer': wrongAnswer,
                                        'reaction': defaultReaction,
                                        'resourceId': collection.resources[2].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    // Second question - revisited and correct
                                    QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                        'previousResource': {
                                            'answer': collection.resources[1].metadata.correctAnswer,
                                            'reaction': defaultReaction,
                                            'resourceId': collection.resources[1].id,
                                            'timeSpent': defaultTimeSpent
                                        }
                                    }, assigneeAuthToken, function () {

                                        QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                            let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                            let resourceCount = collection.resources.length;
                                            QuizzesCommon.getAttempts(contextId, authToken, {
                                                'collectionId': assessmentId,
                                                'contextId': contextId,
                                                'profileAttempts': [
                                                    {
                                                        'currentResourceId': collection.resources[2].id,
                                                        'profileId': assigneeProfileId,
                                                        'eventSummary': {
                                                            'totalTimeSpent': defaultTimeSpent * 8,
                                                            'averageReaction': defaultReaction,
                                                            'averageScore': 50,
                                                            'totalCorrect': 2,
                                                            'totalAnswered': resourceCount
                                                        }
                                                    }
                                                ]
                                            }, function() {});
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

// this one checks that any skipped question doesn't have an answer data (answer, reaction, time spent, ...)
// and that every answered question has that info
QuizzesCommon.startTest('Test finished context summary with 2 answered questions and 2 skipped', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': collection.resources[1].metadata.correctAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                    let resourceCount = collection.resources.length;
                                    QuizzesCommon.getAttempts(contextId, authToken, {
                                        'collectionId': assessmentId,
                                        'contextId': contextId,
                                        'profileAttempts': [
                                            {
                                                'currentResourceId': collection.resources[2].id,
                                                'profileId': assigneeProfileId,
                                                'eventSummary': {
                                                    'totalTimeSpent': defaultTimeSpent * 2,
                                                    'averageReaction': defaultReaction,
                                                    'averageScore': 50,
                                                    'totalCorrect': 2,
                                                    'totalAnswered': resourceCount
                                                }
                                            }
                                        ]
                                    }, function(json) {
                                        expect(json.profileAttempts[0].events).toContain({
                                            'score': 100,
                                            'isSkipped': false,
                                            'resourceId': collection.resources[0].id,
                                            'timeSpent': 1000,
                                            'reaction': 3,
                                            'answer': [ { 'value': 'VHJ1ZQ==' } ]
                                        });
                                        expect(json.profileAttempts[0].events).toContain({
                                            'score': 100,
                                            'isSkipped': false,
                                            'resourceId': collection.resources[1].id,
                                            'timeSpent': defaultTimeSpent,
                                            'reaction': defaultReaction,
                                            'answer': [ { 'value': 'NTAwICogMiA9IDEwMDAw' } ]
                                        });
                                        expect(json.profileAttempts[0].events).toContain({
                                            'score': 0,
                                            'isSkipped': true,
                                            'resourceId': collection.resources[3].id,
                                            'timeSpent': 0,
                                            'reaction': 0
                                        });
                                        expect(json.profileAttempts[0].events).toContain({
                                            'score': 0,
                                            'isSkipped': true,
                                            'resourceId': collection.resources[2].id,
                                            'timeSpent': 0,
                                            'reaction': 0
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

QuizzesCommon.startTest('Test finished context summary when a question answer and reaction are changed', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': wrongAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - skipped
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[0].id, {
                                'previousResource': {
                                    'answer': null,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                // First question again - now correct and with different reaction
                                let updatedReaction = 5;
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                                    'previousResource': {
                                        'answer': collection.resources[0].metadata.correctAnswer,
                                        'reaction': updatedReaction,
                                        'resourceId': collection.resources[0].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                                        let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                        let resourceCount = collection.resources.length;
                                        QuizzesCommon.getAttempts(contextId, authToken, {
                                            'collectionId': assessmentId,
                                            'contextId': contextId,
                                            'profileAttempts': [
                                                {
                                                    'currentResourceId': collection.resources[1].id,
                                                    'profileId': assigneeProfileId,
                                                    'eventSummary': {
                                                        'totalTimeSpent': defaultTimeSpent * 3,
                                                        'averageReaction': (defaultReaction + updatedReaction) / 2,
                                                        'averageScore': 25,
                                                        'totalCorrect': 1,
                                                        'totalAnswered': resourceCount
                                                    }
                                                }
                                            ]
                                        }, function() {});
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

QuizzesCommon.startTest('Test an unfinished context summary with 2 questions answered correctly', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': collection.resources[1].metadata.correctAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                let answeredCount = 2;
                                QuizzesCommon.getAttempts(contextId, authToken, {
                                    'collectionId': assessmentId,
                                    'contextId': contextId,
                                    'profileAttempts': [
                                        {
                                            'currentResourceId': collection.resources[2].id,
                                            'profileId': assigneeProfileId,
                                            'eventSummary': {
                                                'totalTimeSpent': defaultTimeSpent * 2,
                                                'averageReaction': defaultReaction,
                                                'averageScore': 100,
                                                'totalCorrect': answeredCount,
                                                'totalAnswered': answeredCount
                                            }
                                        }
                                    ]
                                }, function() {});
                            });
                        });
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Test an unfinished context summary with 2 questions answered incorrectly', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - incorrect
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': wrongAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - incorrect
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': wrongAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                let answeredCount = 2;
                                let correctCount = 0;
                                QuizzesCommon.getAttempts(contextId, authToken, {
                                    'collectionId': assessmentId,
                                    'contextId': contextId,
                                    'profileAttempts': [
                                        {
                                            'currentResourceId': collection.resources[2].id,
                                            'profileId': assigneeProfileId,
                                            'eventSummary': {
                                                'totalTimeSpent': defaultTimeSpent * 2,
                                                'averageReaction': defaultReaction,
                                                'averageScore': 0,
                                                'totalCorrect': correctCount,
                                                'totalAnswered': answeredCount
                                            }
                                        }
                                    ]
                                }, function() {});
                            });
                        });
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Test an unfinished context summary with 2 questions correct and 1 incorrect', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment02').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken,function (collection){
            let classId = Config.getClass('TestClass01').id;
            QuizzesCommon.createContext(assessmentId, classId, false, QuizzesCommon.generateRandomContextMap(), authToken, function (contextResponse) {
                let contextId = contextResponse.id;
                QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                    QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {

                        // First question - correct
                        QuizzesCommon.onResourceEvent(contextId, collection.resources[1].id, {
                            'previousResource': {
                                'answer': collection.resources[0].metadata.correctAnswer,
                                'reaction': defaultReaction,
                                'resourceId': collection.resources[0].id,
                                'timeSpent': defaultTimeSpent
                            }
                        }, assigneeAuthToken, function () {

                            // Second question - correct
                            QuizzesCommon.onResourceEvent(contextId, collection.resources[2].id, {
                                'previousResource': {
                                    'answer': collection.resources[1].metadata.correctAnswer,
                                    'reaction': defaultReaction,
                                    'resourceId': collection.resources[1].id,
                                    'timeSpent': defaultTimeSpent
                                }
                            }, assigneeAuthToken, function () {

                                // Third question - incorrect
                                QuizzesCommon.onResourceEvent(contextId, collection.resources[3].id, {
                                    'previousResource': {
                                        'answer': wrongAnswer,
                                        'reaction': defaultReaction,
                                        'resourceId': collection.resources[2].id,
                                        'timeSpent': defaultTimeSpent
                                    }
                                }, assigneeAuthToken, function () {

                                    let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                                    let answeredCount = 3;
                                    let correctCount = 2;
                                    QuizzesCommon.getAttempts(contextId, authToken, {
                                        'collectionId': assessmentId,
                                        'contextId': contextId,
                                        'profileAttempts': [
                                            {
                                                'currentResourceId': collection.resources[3].id,
                                                'profileId': assigneeProfileId,
                                                'eventSummary': {
                                                    'totalTimeSpent': defaultTimeSpent * 3,
                                                    'averageReaction': defaultReaction,
                                                    'averageScore': 66,
                                                    'totalCorrect': correctCount,
                                                    'totalAnswered': answeredCount
                                                }
                                            }
                                        ]
                                    }, function() {});
                                });
                            });
                        });
                    });
                });
            });
        });
    });
});