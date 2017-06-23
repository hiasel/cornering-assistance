(function() {
	'use strict';

	curveApp.controller('dashboardController', DashboardController);
    DashboardController.$inject = ["$http", "$scope", "geohash", "haversine"];

    var vm = null;

	function DashboardController($http, $scope, geohash, haversine) {
		vm = this;
		vm.center = {
            lat: 48.210033,
            lng: 14.363449,
            zoom: 6
        };
		vm.rules = {};


		// Calculate the approximate height/width of the bounding box that encloses the geohash of a sample point with the current precision
		vm.calcGeoHash = function() {
            var hash = geohash.encode (vm.center.lat, vm.center.lng, vm.params.geoHashPrecision);
            var bb = geohash.decode_bbox(hash);
            // [minlat, minlon, maxlat, maxlon]
            var top = {
                latitude: bb[2],
                longitude: bb[1]
            };
            var bottom = {
                latitude: bb[0],
                longitude: bb[1]
            };
            var left = {
                latitude: bb[0],
                longitude: bb[1]
            };
            var right = {
                latitude: bb[0],
                longitude: bb[3]
            };
            var height = haversine(top, bottom) * 1000;
            var width = haversine(left, right) * 1000;

            vm.bbWidth = Math.round(width);
            vm.bbHeight = Math.round(height);
            console.log(hash);
            console.log(height);
            console.log(width);
        };

		// Setup the Map
        vm.paths = {};
		vm.markers = {};
		vm.layers = {
            baselayers: {
                xyz: {
                    name: 'OpenStreetMap (XYZ)',
                    url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                    type: 'xyz'
                }
            },
            overlays: {
                markers: {
                    name: 'markers',
                    visible: true,
                    type: 'group'
                },
                circles: {
                    name: 'circles',
                    visible: true,
                    type: 'group'
                },
                paths: {
                    name: 'paths',
                    visible: true,
                    type: 'group'
                }
            }
        };



		// Receive Data from MongoDB Cache

        $http.get('/getParams')
            .success(function(data){
                vm.params = data;
            });

        $http.get('/getRules')
            .success(function(data){
                vm.rules = data;
                vm.slider_dangerous.max = vm.rules.dangerousUpperLimit;
                vm.slider_medium.min = vm.rules.dangerousUpperLimit + 1;
                vm.slider_medium.max = vm.rules.mediumUpperLimit;
                vm.slider_easy.min = vm.rules.mediumUpperLimit + 1;

                vm.slider_dangerous.options.ceil = vm.params.radiusThreshold;
                vm.slider_medium.options.ceil = vm.params.radiusThreshold;
                vm.slider_easy.options.ceil = vm.params.radiusThreshold;
            });

        vm.getCurves = function() {
            $http.get('/getCurves')
                .success(function(data){
                    vm.curves = data;
                    angular.forEach(data, function (curve, key) {
                        var curveId = curve["id"];
                        var centerPoint = curve["centerPoint"];
                        var centerPointCoords = centerPoint["coordinates"];
                        var startPoint = curve["startPoint"];
                        var startPointCoords = startPoint["coordinates"];
                        var endPoint = curve["endPoint"];
                        var endPointCoords = endPoint["coordinates"];

                        var centerPointLatLon = {
                            lat: centerPointCoords[0],
                            lng: centerPointCoords[1]
                        };
                        var startPointLatLon = {
                            lat: startPointCoords[0],
                            lng: startPointCoords[1]
                        };
                        var endPointLatLon = {
                            lat: endPointCoords[0],
                            lng: endPointCoords[1]
                        };
                        var radius = Math.floor(curve["radius"]);
                        var length = Math.floor(curve["length"]);
                        var message = "Radius: " + radius + "; Length: " + length;
                        vm.paths[curveId] = {
                            type: "polyline",
                            latlngs: [ startPointLatLon, centerPointLatLon, endPointLatLon ],
                            layer: 'paths'
                        };
                        var circleId = curveId+"_circle";
                        var circleCenterX = (startPointCoords[0] + centerPointCoords[0] + endPointCoords[0])/3;
                        var circleCenterY = (startPointCoords[1] + centerPointCoords[1] + endPointCoords[1])/3;
                        vm.paths[circleId] = {
                            weight: 2,
                            color: '#ff612f',
                            latlngs: {
                                lat: circleCenterX,
                                lng: circleCenterY
                            },
                            radius: radius,
                            type: 'circle',
                            layer: 'circles'
                        };
                        vm.markers[curveId] = {
                            lat: centerPointCoords[0],
                            lng: centerPointCoords[1],
                            message: message,
                            layer: 'markers'
                        };
                    });
                });
        };

        initSliders();


        vm.saveParams = function () {
            $http.post('/updateParams', vm.params)
                .success(function() {
                    alert("Updated");
                });
        };

        vm.saveRules = function () {
            vm.rules.dangerousUpperLimit = vm.slider_dangerous.max;
            vm.rules.mediumUpperLimit = vm.slider_medium.max;
            $http.post('/updateRules', vm.rules)
                .success(function() {
                    alert("Updated");
                });
        };
	}

	// Initalize the Sliders for Rules
	function initSliders() {
        vm.slider_dangerous = {
            min: 0,
            max: 300,
            options: {
                floor: 0,
                ceil: 200,
                onChange: function () {             vm.slider_dangerous.min = 0;             vm.slider_medium.min = vm.slider_dangerous.max+1;         }
            }
        };
        vm.slider_medium = {
            min: 0,
            max: 300,
            options: {
                floor: 0,
                ceil: 200,
                onChange: function () {             vm.slider_easy.min = vm.slider_medium.max+1;             vm.slider_dangerous.max = vm.slider_medium.min-1;         }
            }
        };
        vm.slider_easy = {
            min: 0,
            max: 300,
            options: {
                floor: 0,
                ceil: 200,
                onChange: function () {             vm.slider_easy.max = vm.params.radiusThreshold;             vm.slider_medium.max = vm.slider_easy.min-1;         }
            }
        };
    }



})();
