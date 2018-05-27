app.controller("usercontroller", function($http, $scope, $timeout) {
	$scope.username = null;

	$scope.findusername = function(value) {
		if (value != null) {
			$http.get("/1.0/users?username=" + value).then(function(response) {
				$scope.data = response.data.response;
			}, function(response) {
				$scope.errorData = response.data.response;
				$timeout(function() {
					$scope.errorData = "";
				}, 2000);
			})
		}
	};
});