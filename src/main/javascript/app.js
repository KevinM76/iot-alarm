var app = angular.module('app', [
   'ngRoute',
   'controllers'
]);

app.config(['$routeProvider', '$httpProvider',
		function ($routeProvider, $httpProvider) {
		   $routeProvider.when('/', {
		      templateUrl: 'partials/home.html',
		      controller: 'HomeController'
		   }).otherwise({
		      redirectTo: '/'
		   });
		   
		   if (!$httpProvider.defaults.headers.get) {
		       $httpProvider.defaults.headers.get = {};    
		   }    
		   //disable IE ajax request caching
		   $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
		   // extra
		   $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
		   $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
		
}]);
