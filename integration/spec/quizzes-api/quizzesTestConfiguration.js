
let testUsers = {
    Teacher01: { firstname: 'testuser', lastname: 'eigth', identityId: 'testuser08@edify.cr' },
    Student01: { firstname: 'testuser', lastname: 'nine', identityId: 'testuser09@edify.cr' },
    Student02: { firstname: 'testuser', lastname: 'ten', identityId: 'testuser10@edify.cr' },
    StudentNotInClass: { firstname: 'testuser', lastname: 'twelve', identityId: 'testuser12@edify.cr' }
};

let testClasses = {
    TestClass01: { id: '04805631-f43f-4364-9d40-7bdc7f1b7178', owner: 'Teacher01', assignees: ['Student01'] }
};

let testCollections = {
    TestCollection01: {
        id: 'ca13e08c-6e2d-4c10-93cf-7b8111f3b705',
        owner: 'Teacher01',
        resources: [
            {
                id: '6025d1ec-ef23-4fd9-a8d7-6fa95ef96b84',
                correctAnswer: [
                    {
                        'value': 'VHJ1ZQ=='
                    }
                ]
            },
            {
                id: 'faac0bc8-57f8-475c-9f90-804cabd53658',
                correctAnswer: [
                    {
                        'value': 'The big bad wolf blew down the house.,47'
                    }
                ]
            },
        ]
    }
};

let testAssessments = {
    TestAssessment01: {
        id: '95e0c189-e62a-4d32-9738-eac1fae6cf3e',
        owner: 'Teacher01',
        resources: [
            {
                id: '4b937ce8-1989-4faf-99e3-79befaba3998',
                correctAnswer: [
                    {
                        'value': 'MTAwIC0gMTAgPSDCoDkwwqA='
                    }
                ]
            },
            {
                id: '73293fef-8312-4d20-8c55-0fde5740ae6a',
                correctAnswer: [
                    {
                        'value': 'VHJ1ZQ=='
                    }
                ]
            },
        ]

    },
    TestAssessment02: {
        id: 'de41f2ec-355a-4e4c-87e5-0eaf08625ae9',
        owner: 'Teacher01',
        resources: [
            {
                id: 'dddafcba-041d-41ee-83ad-bd622023df6a',
                correctAnswer: [
                    {
                        'value': 'VHJ1ZQ=='
                    }
                ]
            },
            {
                id: '0b40a08e-a342-47d2-9264-b68dc408efdf',
                correctAnswer: [
                    {
                        'value': 'NTAwICogMiA9IDEwMDAw'
                    }
                ]
            },
            {
                id: 'ed7a9785-3f93-454d-ad8d-be0f0508c6c8',
                correctAnswer: [
                    {
                        'value': 'MTAw'
                    },
                    {
                        'value': 'NTAw'
                    }
                ]
            },
            {
                id: 'ace8949f-aa82-4d76-9f1d-cb58e6d1031b',
                correctAnswer: [
                    {
                        'value': 'wolf'
                    },
                    {
                        'value': 'house'
                    }
                ]
            }
        ]
    }
};

let testCourses = {
    Course01: { id: 'd95adfc6-c303-437a-b267-9106f2435568' }
};

let testUnits = {
    Unit01: { id: 'ddb09f7f7-ae00-4ee0-8160-ff7480928d06' }
};

let testLessons = {
    Lesson01: { id: '60297409-efb2-458a-b0d2-b4eedcb17ae8' }
};

let quizzesTestConfiguration = {
    quizzesApiUrl: (process.env['QUIZZES_SERVER_URL'] || 'http://localhost:8080') + '/quizzes/api',

    contentProviderApiUrl: (process.env['QUIZZES_CONTENT_PROVIDER_SERVER_URL'] || 'http://nile-qa.gooru.org')
        + '/api/nucleus-auth',

    getUser: function(userId) {
        return testUsers[userId]
    },

    getClass: function(classId) {
        return testClasses[classId]
    },

    getCollection: function(collectionId) {
        return testCollections[collectionId]
    },

    getAssessment: function(assessmentId) {
        return testAssessments[assessmentId]
    },

    getCourse: function(courseId) {
        return testCourses[courseId];
    },

    getUnit: function(unitId) {
        return testUnits[unitId];
    },

    getLesson: function(lessonId) {
        return testLessons[lessonId];
    }
};

module.exports = quizzesTestConfiguration;