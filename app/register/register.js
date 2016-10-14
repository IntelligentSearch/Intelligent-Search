'use strict';

var app = angular.module('myApp.register', ['ngRoute'])

.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
  $httpProvider.defaults.useXDomain = true;
  delete $httpProvider.defaults.headers.common["X-Requested-With"];
  $httpProvider.defaults.headers.common["Accept"] = "application/json";
  $httpProvider.defaults.headers.common["Content-Type"] = "application/json";
  $routeProvider.when('/register', {
    templateUrl: 'app/register/register.html',
    controller: 'RegisterCtrl',
    css: 'app/register/register.css'
  });
}])
.controller('RegisterCtrl', function($scope, $http) {

    $scope.base_url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest";

    $scope.account = {
      first: '',
      last: '',
      email: '',
      password: ''
    };

    $scope.createAccount = function() {
      // POST request for creating an account:
      $http({
        method: 'POST',
        url: $scope.base_url + '/create-user',
        withCredentials: true,
        // headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {name: $scope.account.email, password: $scope.account.password, first: $scope.account.first, last: $scope.account.last}
      }).then(function successCallback(response) {
        // this callback will be called asynchronously
        // when the response is available
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.

      });
    }
});
