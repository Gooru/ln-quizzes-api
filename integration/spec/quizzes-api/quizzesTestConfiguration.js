
var testUsers = {
    Teacher01: { firstname: 'testuser', lastname: 'eigth', identityId: 'testuser08@edify.cr' },
    Student01: { firstname: 'testuser', lastname: 'nine', identityId: 'testuser09@edify.cr' },
    Student02: { firstname: 'testuser', lastname: 'ten', identityId: 'testuser10@edify.cr' },
    StudentNotInClass: { firstname: 'testuser', lastname: 'twelve', identityId: 'testuser12@edify.cr' }
};

var testClasses = {
    TestClass01: { id: '04805631-f43f-4364-9d40-7bdc7f1b7178', owner: 'Teacher01', assignees: ['Student01'] }
};

var testCollections = {
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

var testAssessments = {
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
                id: 'ad4d0fe0-4edc-4a47-982b-205c0f670bce',
                correctAnswer: [
                    {
                        'value': 'VHJ1ZQ=='
                    }
                ]
            }
        ]
    }
};

var quizzesTestConfiguration = {
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
    }
};


module.exports = quizzesTestConfiguration;