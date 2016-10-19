'use strict';

var app = angular.module('myApp.maps', ['ngRoute'])

.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) { 
  $httpProvider.defaults.useXDomain = true;
  delete $httpProvider.defaults.headers.common["X-Requested-With"];
  $httpProvider.defaults.headers.common["Accept"] = 'application/x-www    -form-urlencoded';
  $httpProvider.defaults.headers.common["Content-Type"] = 'application    /x-www-form-urlencoded';
  $routeProvider.when('/maps', {
    templateUrl: 'app/maps/maps.html',
    controller: 'MapsCtrl',
    css: 'app/maps/maps.css'
  });
}])
.controller('MapsCtrl', function($scope, $location, $http) {
  var map; 
  var vm = this;
  vm.user = "hello"; 
  console.log($scope);
  $scope.initMap = function () {
    map = new google.maps.Map(document.getElementById('map'), {
      center: {lat: 40.4237, lng: -86.9212},
      zoom: 18
    });
  }
  //$scope.initMap();
});


