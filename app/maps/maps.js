'use strict';

var app = angular.module('myApp.maps', ['ngRoute'])

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
  NgMap.getMap().then(function(map) {
    $scope.map = map;
  }); 
  $scope.user = 'hello'; 
  $scope.location = {
    start : "40.431103, -86.914727",
    end   : "40.423703, -86.910800"
  };
  $scope.gold = true;
  $scope.silver = true;
  $scope.path = [
    [40.431382, -86.914017],
    [40.427921, -86.910373],
    [40.424026, -86.910363],
    [40.424150, -86.916646],
    [40.431376, -86.916680],
    [40.431382, -86.914017]
  ];
  $scope.path2 = [
    [40.433940, -86.921808],
    [40.434046, -86.923020],
    [40.433246, -86.923267],
    [40.433442, -86.924812]
  ];
  $scope.origpath1 = null;
  $scope.origpath2 = null;
  $scope.silver = false;

  $scope.hideSilver = function() {
    $http({
      url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
      method: "GET"
    }).success(function (data, status, headers, config) {
      if(data != null) {
	console.log(data);
      }
    }).error(function(data, status, headers, config) {
      console.log("ERROR");
    });
    console.log($scope.map.shapes);
    $scope.map.shapes.silver.setMap(null);
    console.log($scope.map.shapes);
    if($scope.silver) {
      $scope.map.shapes.silver.setMap($scope.map);
      $scope.silver = false;
    } else
      $scope.silver = true;
  }
  $http({
    url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/get-all-routes-stops/",
    method: "GET"
  }).success(function (data, status, headers, config) {
    if(data != null) {
      console.log("data found");
    }
  }).error(function(data, status, headers, config) {
    console.log("ERROR");
  });
  $scope.useCurr = function() {
    $scope.location.start = "40.428103, -86.913727";
    console.log($scope);
  }
});


