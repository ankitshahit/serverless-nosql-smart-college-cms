var app = angular.module('cmsApp', []);
app.run(function($rootScope) {
	$rootScope.color = 'blue';
	$rootScope.host = 'http://localhost:8080';
});
