app.controller("usercontroller", function($http, $scope, $timeout) {
	var usersAPI = "/1.0/users";
	$scope.showErrorDiv = false;
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

	$http.get(usersAPI + "?limit=10").then(function(resp) {
		console.log("success -> ");
		// $scope.getusers = resp.data.response;
		$scope.getusers = "{}";
	}, function(resp) {
		console.log("failure -> ");
		$scope.showErrorDiv = true;
		$scope.errorData = resp.data.summary_message;
	});

	$scope.table_headers = [ "username", "email", "created on",
			"View Attributes" ];
});