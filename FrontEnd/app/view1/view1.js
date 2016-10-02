'use strict';

var app = angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html',
    controller: 'View1Ctrl',
    css: 'app.css'
  });
}])

.controller('View1Ctrl', [function() {

}]);