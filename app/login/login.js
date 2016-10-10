'use strict';

var app = angular.module('myApp.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'login/login.html',
    controller: 'LoginCtrl',
    css: 'login/login.css'
  });
}])
.controller('LoginCtrl', [ '$scope', function($scope) {
  $scope.user = {
    userName: '',
    password: ''
  }
}]);
