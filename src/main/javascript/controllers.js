var controllers = angular.module("controllers", []);

controllers.controller("HomeController", ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
    $scope.debug = false;
    $scope.title = "Hello";

    $http.get("service/alarmstatus")
    		.success(function (data) {
    			$scope.data = data;
    		})
    		.error(function (data) {
				console.log('Could not read from "service/alarmstatus"');
			});

    $scope.toggleDebug = function () {
        $scope.debug = !$scope.debug;
    };

    $scope.setAlarmStatus = function (status) {
        var alarm, res;
        alarm = {
                item: 'Dummy',
                status: status
        };
        res = $http.post('service/alarmstatus', alarm);
        res.success(function(data) {
            $scope.data = data;
        });
    };

    // Function to replicate setInterval using $timeout service.
    $scope.intervalFunction = function(){
      $timeout(function() {
        $http.get("service/alarmstatus").success(function (data) {
            $scope.data = data;
        });
        $scope.intervalFunction();
      }, 1000);
    };

    // Kick off the interval
    $scope.intervalFunction();


}]);
