var exec = require('cordova/exec');

var TwitterConnect = {
	login: function (successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'login', []);
	},
	logout: function (successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'logout', []);
	},
	showUser: function (args, successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'showUser', [{"include_entities" : args.include_entities}]);
	},
	verifyCredentials: function (args, successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'verifyCredentials', [{"include_entities" : args.include_entities,
																					"skip_status" : args.skip_status,
																					"include_email" : args.include_email}]);
	},
	sendTweet: function (msg, successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'sendTweet', [{"status" : msg}]);
	},
	openComposer: function (text, successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'openComposer', [text]);
	},
	showTimeline: function (query, successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterConnect', 'showTimeline', [query]);
	}
};

module.exports = TwitterConnect;
