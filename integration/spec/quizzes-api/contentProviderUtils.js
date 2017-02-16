const API_URL = require('./quizzesTestConfiguration.js').contentProviderApiUrl;
const config = require('./quizzesTestConfiguration.js');
const frisby = require('frisby');

var contentProviderUtils = {
    getAuthorizationToken : function(userId, afterJsonFunction) {
        var authorizationUser = config.getUser(userId);
        console.log("Autorization user " + authorizationUser.identityId);
        frisby.create('Gets the authorization token for ' + userId)
            .post(API_URL + '/v2/authorize', {
                "client_key": "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==",
                "client_id": "ba956a97-ae15-11e5-a302-f8a963065976",
                "grant_type": "google",
                "user": {
                    "firstname": authorizationUser.firstname,
                    "lastname": authorizationUser.lastname,
                    "identity_id": authorizationUser.identityId
                }
            }, {json: true})
            .inspectRequest()
            .expectStatus(201)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (authorizationResponse) {
                afterJsonFunction(authorizationResponse);
            })
            .toss();
    }

}

module.exports = contentProviderUtils;
