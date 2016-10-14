'use strict';

var app = angular.module('myApp.register', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/register', {
    templateUrl: 'app/register/register.html',
    controller: 'RegisterCtrl',
    css: 'app/register/register.css'
  });
}])
.controller('RegisterCtrl', function($scope, $http) {

    $scope.base_url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest";

    $scope.new_user = {
      first: '',
      last: '',
      email: '',
      password: ''
    };

    $scope.createAccount = function() {
      // POST request for creating an account:
      $http({
        method: 'GET',
        url: $scope.base_url + '/create-user/',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: {name: $scope.new_user.email, password: $scope.new_user.password, first: $scope.new_user.first, last: $scope.new_user.last}
      }).then(function successCallback(response) {
        // this callback will be called asynchronously
        // when the response is available
      }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.

      });
    }
});
