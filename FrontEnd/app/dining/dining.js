'use strict';

var app = angular.module('myApp.dining', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/dining', {
    templateUrl: 'dining/dining.html',
    controller: 'DiningCtrl',
    css: 'dining/dining.css'
  });
}])

.controller('DiningCtrl', [function() {

}]);