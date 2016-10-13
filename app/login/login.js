'use strict';

var app = angular.module('myApp.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'app/login/login.html',
    controller: 'LoginCtrl',
    css: 'app/login/login.css'
  });
}])
.controller('LoginCtrl', [ '$scope', function($scope) {
  $scope.user = {
    userName: '',
    password: ''
  }
}]);
