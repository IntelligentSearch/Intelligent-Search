'use strict';

var app = angular.module('myApp.maps', ['ngRoute', 'ngMap'])

    .config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common["X-Requested-With"];
        $httpProvider.defaults.headers.common["Accept"] = 'application/x-www    -form-urlencoded';
        $httpProvider.defaults.headers.common["Content-Type"] = 'application    /x-www-form-urlencoded';
        //$httpProvider.defaults.headers.common["Access-Control-Allow-Origin"] = "*";

        $routeProvider.when('/maps', {
            templateUrl: 'app/maps/maps.html',
            controller: 'MapsCtrl',
            css: 'app/maps/maps.css'
        });
    }])
    .controller('MapsCtrl', function (NgMap, $scope, $location, $http) {
        var busImage = "app/maps/bus-icon.png"
	$scope.showRoute = false;
        $scope.busIcon = {
            url: busImage,
            size: [32, 32]
        }
        NgMap.getMap().then(function (map) {
            $scope.map = map;
        });
	//user location
	
	$scope.currLat;
	$scope.currLong;
	$scope.destLat;
	$scope.destLong;
	//show directions form
	$scope.dirClicked = false;
	$scope.dirClick = function() {
	  $scope.dirClicked = !$scope.dirClicked;
	}
        // $scope.routes;
        $scope.userStopLoc = [];
        // $scope.stops;
        $scope.routes = [];
        $scope.buses;
	$scope.fRoute = [];
        $scope.hide = function () {
            $scope.times = [];
            $scope.userStopLoc = [];
            console.log($scope.userStopLoc);
        }
        $scope.times = [];
        $scope.getTimes = function (e, sid) {
            $scope.hide();
            var url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/live-stops/" + sid.p.id;
            $http({
                url: url,
                method: "GET"
            }).success(function (data, status, headers, config) {
                $scope.times = [];
                $scope.setTimes(data.stops);
            }).error(function (data, status, headers, config) {
                console.log("live stop error");
            });
        }
        $scope.setTimes = function (data) {
            for (var i = 0; i < data.length; i++) {
                var str = String(data[i].TimeTilArrival);
                str = str.replace(" min", "");
                var x = Number(str) < 20;
                if (data[i].RouteName == "13 Silver Loop" && x) {
                    $scope.times.push(data[i]);
                }
            }
        }
        $scope.silBuses = [];
        $scope.allStops = [];
	$scope.routed = function(e, n) {
	  $scope.showRoute=true;
	  var loc = this.getPlace().geometry.location;
	  $scope.destLat = loc.lat();
	  $scope.destLong = loc.lng();
	  if($scope.searchEntry != null) {
	    $scope.getDirections();
	  }
	}
	$scope.getDirections = function() {
	  var url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/routing/" + $scope.currLat + "/" + $scope.currLong + "/" + $scope.destLat + "/" + $scope.destLong + "/";
	  $http({
	    url: url,
	    method: "GET"
	  }).success(function(data, status, headers, config) {
	    console.log(data);
	    var fastest = 1000;
	    var num;
	    angular.forEach(data, function(value,key) {
		if(key.includes("Arvial Time")) {
		  if(Number(value) > 0 && Number(value) < fastest) {
		    fastest = Number(value);
		    num = String(key).charAt(key.length-1);
		  }
		}
	    });
	    if(Number(fastest) == 1000) {
	      fastest = "> 15 minutes";
	      num = 1;
	    }
	    var timeToPush={};
	    var name = "Route " + num;
	    timeToPush['time'] = fastest + " minutes";
	    timeToPush['route'] = data[name];
	    $scope.fRoute = timeToPush;
	  }).error(function(data, status, headers, config) {
	    console.log("error routing");
	  });
	  
	}
        $scope.searched = function (e, n) {
            $scope.times = [];
            var loc = this.getPlace().geometry.location;
            var lat = loc.lat();
            var lng = loc.lng();
            $scope.currLat=lat;
            $scope.currLong=lng;
	    if($scope.destinationEntry != null) {
	      $scope.getDirections();
	    }
            var url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-close-stop-by-route/617e32b0-900c-4a4c-a049-4c82422ccdf2/" + lng + "/" + lat + "/";
            $http({
                url: url,
                method: "GET"
            }).success(function (data, status, headers, config) {
                var userStop = data;
                $scope.userStopLoc = [];
                $scope.userStopLoc.push(data.stop_lat);
                $scope.userStopLoc.push(data.stop_lon);
                console.log($scope.userStopLoc);
                console.log(userStop.stop_name);
                $scope.setTimes(data.stop_times.stops);
            }).error(function (data, status, headers, config) {
                console.log("search error");
            });


        }
        $scope.loadLive = function () {
            $http({
                url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/live-buses",
                method: "GET"
            }).success(function (data, status, headers, config) {
                if (data != null) {
                    $scope.buses = data;
                    $scope.silBuses = [];
                    for (var i = 0; i < $scope.buses.length; i++) {
                        var bus = $scope.buses[i];
                        if (bus['Route_Name'].includes("13 Silver Loop")) {
                            var loc = [];
                            loc.push(bus.Lat);
                            loc.push(bus.Long);
                            var toAdd = {};
                            toAdd['location'] = loc;
                            $scope.silBuses.push(toAdd);
                        }
                    }
                } else {
                    console.log("hello?");
                }
            }).error(function (data, status, headers, config) {
                console.log("ERROR");
            });
            setTimeout($scope.loadLive, 10000);
        }
        // $scope.getAllRoutes = function() {
        //   $http({
        //     url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
        //     method: "GET"
        //   }).success(function (data, status, headers, config) {
        //     if(data != null) {
        // $scope.allStops = data;
        //     }
        //   }).error(function(data, status, headers, config) {
        //     console.log("ERROR");
        //   });
        // };
        // $scope.user = 'hello';
        // $scope.createPath = function(event) {
        //   $scope.path.push([event.latLng.lat(), event.latLng.lng()]);
        // }
        // $scope.location = {
        //   start : "40.431103, -86.914727",
        //   end   : "40.423703, -86.910800"
        // };

        //Get all the list of routes and display them with the stops
        $scope.getAllPaths = function () {
            console.log("Get All Paths in", $scope.allStops)
            var name = "";
            $scope.allStops.forEach(function (stop) {
                $http({
                    url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/hard-route/" + stop.routes.long_name,
                    method: "GET"
                }).success(function (data, status, headers, config) {
                    if (data != undefined) {
                        name = "" + stop.routes.short_name + " " + stop.routes.long_name;
                        var locations = [];
                        for (var j = 0; j < data.length; j++) {
                            var latitude = data[j].Latitude;
                            var longitude = data[j].Longitude;
                            var location = [];
                            location.push(Number(latitude))
                            location.push(Number(longitude))
                            locations.push(location)
                        }
                        if (locations.length != 0) {
                            console.log("Locations",locations)
                            $scope.routes.push({
                                show: false,
                                color: stop.routes.color,
                                route_name: name,
                                path: locations
                            });
                            // $scope.routes[name] = data;
                        }
                    }
                }).error(function (data, status, headers, config) {
                    console.log("ERROR");
                });
            });
            console.log("All Routes", $scope.routes)
        };

        $scope.path = [
            [40.431382, -86.914017],
            [40.42671184010864, -86.90904378890991],
            [40.42571543714086, -86.90805673599243],
            [40.423820597330376, -86.90797090530396],
            [40.42395127765169, -86.90829277038574],
            [40.42408195771912, -86.913743019104],
            [40.42591145200572, -86.91376447677612],
            [40.42591145200572, -86.91457986831665],
            [40.425633764111915, -86.91483736038208],
            [40.42408195771912, -86.9148588180542],
            [40.42422897249152, -86.91908597946167],
            [40.42519272804189, -86.91910743713379],
            [40.42532340569747, -86.9257378578186],
            [40.42738154528389, -86.92578077316284],
            [40.42733254269231, -86.92198276519775],
            [40.42788790330638, -86.92168235778809],
            [40.431448635925655, -86.92155361175537],
            [40.431382, -86.914017]
        ];
        $scope.origpath1 = null;
        $scope.origpath2 = null;
        $scope.hideRoute = function (route, index) {
            var name = route.short_name + " " + route.long_name;
            console.log(name,$scope.routes[name])
            for (var i = 0; i < $scope.routes.length; i++) {
                var tempRoute = $scope.routes[i];
                if (tempRoute['route_name'].includes(name)) {
                   $scope.routes[i].show = route.show
                }
            }
            console.log($scope.map);
            console.log("Routes " + $scope.allStops[index].routes.show, route)
            $scope.allStops[index].routes.show = route.show;
            //TODO: dynamically change shape id with ng-repeat in map so it's not just silver for every route
            // $scope.map.shapes["route-"+index].setMap(null);
            // console.log(name + " " +  $scope.map.shapes["route-"+index]);
            if (route.show == true) {
                console.log("Showing " + route.long_name)
                // $scope.map.shapes[route.route_name].setMap($scope.map);
                // $scope.silver = false;
            } else {
                console.log("Hiding " + route.long_name)
            }
            $scope.filterMarkers();
        }
        $scope.silStops = {}
        $scope.getAllStops = function () {
            $http({
                url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
                method: "GET"
            }).success(function (data, status, headers, config) {
                if (data != null) {
                    for (var i = 0; i < data.length; i++) { //change 2 to length tomorrow
                        data[i].routes.show = false;
                    }
                    $scope.allStops = data;
                    $scope.loadLive();
                    // $scope.getAllRoutes();
                    console.log("All Stops", $scope.allStops)
                    $scope.getAllPaths();
                }
            }).error(function (data, status, headers, config) {
                console.log("ERROR");
            });
        }
        $scope.filterMarkers = function () {
            var stops = [];
            for (var i = 0; i < $scope.allStops.length; i++) { //loops over all the bus stops
                if ($scope.allStops[i].routes.show == true) { //checks if the markers need to be displayed on the front end
                    console.log("Push stop")
                    for (var j = 0; j < $scope.allStops[i].stops.length; j++) {
                        var latitude = $scope.allStops[i].stops[j].stop_lat;
                        var longitude = $scope.allStops[i].stops[j].stop_long;
                        var stopCode = $scope.allStops[i].stops[j].stop_code;
                        var location = [];
                        location.push(Number(latitude))
                        location.push(Number(longitude))
                        var stop_object = {};
                        stop_object['location'] = location;
                        stop_object['id'] = stopCode;
                        stops.push(stop_object);
                    }
                }
            }
            // console.log("Stops", stops);

            $scope.silStops = angular.copy(stops);
        }
        $scope.getAllStops();

        $scope.useCurr = function () {
            $scope.location.start = "40.428103, -86.913727";
            console.log($scope);
        }

    });



