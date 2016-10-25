'use strict';

var app = angular.module('myApp.settings', ['ngRoute', 'ngCookies'])

    .config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common["X-Requested-With"];
        $httpProvider.defaults.headers.common["Accept"] = 'application/x-www-form-urlencoded';
        $httpProvider.defaults.headers.common["Content-Type"] = 'application/x-www-form-urlencoded';
        $routeProvider.when('/settings', {
            templateUrl: 'app/settings/settings.html',
            controller: 'SettingsCtrl',
            css: 'app/settings/settings.css'
        });
    }])
    .controller('SettingsCtrl', function ($scope, $location, $http, $cookies) {
        var response = $cookies.getObject('user');
        console.log(response);
        if (response != undefined) {
            $scope.userID = response.user.UserID;
            $scope.preferences = response.prefs;
            angular.forEach($scope.preferences, function (value, key) {
               console.log(key + ' : '+ value)
            });
            console.log("Prefs: " +response.prefs);
            $scope.name = {
                firstName: response.user.FirstName,
                lastName: response.user.LastName
            }

            $scope.password = {
                oldPassword: '',
                newPassword: ''
            }


            $scope.base_url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest";

            $scope.auth = function ($scope, $http, $timeout, $location, $log, $cookies) {

                //POST request for updating name
                $http({
                    method: 'POST',
                    url: $scope.base_url + '/update-name',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({
                        userID: $scope.userID,
                        newFirst: $scope.name.firstName,
                        newLast: $scope.name.lastName
                    })
                }).then(function successCallback(response) {

                }, function errorCallback(response) {
                    alert('Updating name failed' + angular.toJson(response));
                });

                //POST request for updating password
                $http({
                    method: 'POST',
                    url: $scope.base_url + '/update-password',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({
                        userID: $scope.userID,
                        newPassword: $scope.password.newPassword,
                        oldPassword: $scope.password.oldPassword
                    })
                }).then(function successCallback(response) {

                }, function errorCallback(response) {
                    alert('Settings failed' + angular.toJson(response));
                });

                // POST request for setting preferences
                $http({
                    method: 'POST',
                    url: $scope.base_url + '/set-pref',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({userID: $scope.userID, prefs: $scope.preferences})
                }).then(function successCallback(response) {
                    // this callback will be called asynchronously
                    // when the response is available
                    var userID = response.data.user.UserID;
                    if (userID != -1) {
                       // $cookies.put('user', userID);
                        $cookies.put('user_name', $scope.user.userName);
                        $location.path("/dining");
                    } else {
                        alert('Settings failed');
                    }

                }, function errorCallback(response) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                    alert('Settings failed' + angular.toJson(response));
                });
            }
        }
    });
