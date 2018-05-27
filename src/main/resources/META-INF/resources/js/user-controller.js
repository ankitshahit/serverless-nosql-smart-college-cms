app.controller("usercontroller", function($http, $scope, $timeout) {
	var usersAPI = "/1.0/users";
	$scope.username = null;

	$scope.findusername = function(value) {
		console.log("findusername -> " + value);
		if (value != null) {
			$http.get(usersAPI + "?username=" + value).then(function(response) {
				$scope.data = response.data.response;
			}, function(response) {
				$scope.errorData = response.data.response;
			})
		}
	};

	$scope.table_headers = [ "username", "email", "created on" ];
});