'use strict';

var app = angular.module('myApp.register', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/register', {
    templateUrl: 'app/register/register.html',
    controller: 'RegisterCtrl',
    css: 'app/register/register.css'
  });
}])
.controller('RegisterCtrl', [ '$scope', function($scope) {
  $scope.user = {
    userName: '',
    password: ''
  }

    $scope.createAccount = function() {

    }
}]);
