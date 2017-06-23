var curveApp = angular.module('curveApp', ['ngRoute','rzModule','leaflet-directive', 'angular-geohash', 'benharold.haversine']);

curveApp.config(['$routeProvider', function($routeProvider) {
	$routeProvider
    	.when('/', {
    		templateUrl: '/pages/dashboard.html',
    		controller : 'dashboardController',
			controllerAs: 'dc'
    	})
    	.otherwise('/');
}]);

curveApp.controller('mainController', function() {

});

