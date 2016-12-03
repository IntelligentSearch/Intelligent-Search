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
.controller('MapsCtrl', function(NgMap, $scope, $location, $http) {
  var busImage="app/maps/bus-icon.png"
  $scope.busIcon = {
    url: busImage,
    size: [32,32]
  }
  NgMap.getMap().then(function(map) {
    $scope.map = map;
  });
  $scope.routes;
    $scope.userStopLoc = [];
  $scope.stops;
  $scope.buses;
  $scope.hide = function() {
    $scope.times = [];
    $scope.userStopLoc = [];
    console.log($scope.userStopLoc);
  }
  $scope.times=[];
  $scope.getTimes = function(e, sid) {
    $scope.hide();
    var url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/live-stops/" + sid.p.id;
    $http({
      url: url,
      method: "GET"
    }).success(function(data,status,headers,config) {
      $scope.times = [];
      $scope.setTimes(data.stops);
    }).error(function(data,status,headers,config) {
      console.log("live stop error");
    });
  }
  $scope.setTimes = function(data) {
      for(var i = 0; i < data.length; i++) {
	var str = String(data[i].TimeTilArrival);
	str = str.replace(" min", "");
	var x = Number(str) < 20;
	if(data[i].RouteName == "13 Silver Loop" && x) {
	  $scope.times.push(data[i]);
	}
      }
  }
  $scope.silBuses = [];
  $scope.searched = function(e, n) {
    $scope.times = [];
    var loc = this.getPlace().geometry.location;
    var lat = loc.lat();
    var lng = loc.lng();
    console.log(lat);
    console.log(lng);
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
    }).error(function(data, status, headers, config) {
      console.log("search error");
    });



  }
  $scope.loadLive = function() {
    $http({
      url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/live-buses",
      method: "GET"
    }).success(function (data, status, headers, config) {
      if(data!= null) {
	$scope.buses = data;
	$scope.silBuses = [];
	for(var i = 0; i < $scope.buses.length; i++) {
	  var bus = $scope.buses[i];
	  if(bus['Route_Name'].includes("13 Silver Loop")) {
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
    }).error(function(data, status, headers, config) {
      console.log("ERROR");
    });
    setTimeout($scope.loadLive, 10000);
  }
  // $scope.loadLive();
  $scope.loadRoutes = function() {
    $http({
      url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
      method: "GET"
    }).success(function (data, status, headers, config) {
      if(data != null) {
	$scope.stops = data;
      }
    }).error(function(data, status, headers, config) {
      console.log("ERROR");
    });
  };
  $scope.user = 'hello';
  $scope.createPath = function(event) {
    $scope.path.push([event.latLng.lat(), event.latLng.lng()]);
  }
  $scope.location = {
    start : "40.431103, -86.914727",
    end   : "40.423703, -86.910800"
  };

  $scope.path = [
    [40.431382,-86.914017],
    [40.42671184010864,-86.90904378890991],
    [40.42571543714086,-86.90805673599243],
    [40.423820597330376,-86.90797090530396],
    [40.42395127765169,-86.90829277038574],
    [40.42408195771912,-86.913743019104],
    [40.42591145200572,-86.91376447677612],
    [40.42591145200572,-86.91457986831665],
    [40.425633764111915,-86.91483736038208],
    [40.42408195771912,-86.9148588180542],
    [40.42422897249152,-86.91908597946167],
    [40.42519272804189,-86.91910743713379],
    [40.42532340569747,-86.9257378578186],
    [40.42738154528389,-86.92578077316284],
    [40.42733254269231,-86.92198276519775],
    [40.42788790330638,-86.92168235778809],
    [40.431448635925655,-86.92155361175537],
    [40.431382,-86.914017]
  ];
  $scope.origpath1 = null;
  $scope.origpath2 = null;
  $scope.hideRoute = function(route, $index) {
    console.log($scope.map.shapes);
    var name = route.short_name + "";
    //TODO: dynamically change shape id with ng-repeat in map so it's not just silver for every route
    $scope.map.shapes.silver.setMap(null);
    console.log(name + " " + $scope.map.shapes);
    if(route.show == true) {
      $scope.map.shapes.silver.setMap($scope.map);
      // $scope.silver = false;
    }
      // $scope.silver = true;
  }
    $scope.getRoutes = function() {
        //Get all the routes for the switches in maps
        $http({
            url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes",
            method: "GET"
        }).success(function (data, status, headers, config) {
            if (data != null) {
                $scope.routes = data;
                //Set all switches defaulted to false then do custom route based on search or switched routes
                for (var i = 0; i < $scope.routes.length; i++) {
                    if ($scope.routes[i].short_name == 13) {
                        $scope.routes[i].show = true;
                    } else {
                        $scope.routes[i].show = false;

                    }
                }
                $scope.loadLive();
                $scope.loadRoutes();
                $scope.loadStops();
                console.log("Routes Set! ", $scope.routes);
            } else {
                console.log("No routes!")
            }
        }).error(function (data, status, headers, config) {
            console.log("ERROR");
        });
    }
    $scope.getRoutes()
    $scope.loadStops = function() {
        $http({
            url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
            method: "GET"
        }).success(function (data, status, headers, config) {
            if (data != null) {
                var stops = [];
                console.log("All Stops", data)
                for (var i = 0; i < data.length; i++) { //change 2 to length tomorrow
                    // console.log("----"+data[i].routes.long_name)
                    var show = false;
                    for (var r = 0; r < $scope.routes.length; r++) {
                        if ($scope.routes[r].show == true) {
                            console.log($scope.routes[r].long_name + " " + data[i].routes.long_name)
                        }
                        if ($scope.routes[r].show == true && ($scope.routes[r].long_name === data[i].routes.long_name)) {
                            show = true;
                        }
                    }
                    for (var j = 0; j < data[i].stops.length; j++) {
                        var latitude = data[i].stops[j].stop_lat;
                        var longitude = data[i].stops[j].stop_long;
                        var stopCode = data[i].stops[j].stop_code;
                        var location = [];
                        location.push(Number(latitude))
                        location.push(Number(longitude))
                        var stop_object = {};
                        stop_object['location'] = location;
                        stop_object['id'] = stopCode;
                        if (show == true) { //change to 7 tomorrow
                            console.log("Push stop")
                            stops.push(stop_object);
                        }
                    }
                    $scope.silStops = angular.copy(stops);
                    //PUSH IN BIG ARRAY
                    //$scope.array.push(location_object);
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("ERROR");
        });
    }

  $scope.useCurr = function() {
    $scope.location.start = "40.428103, -86.913727";
    console.log($scope);
  }

});


