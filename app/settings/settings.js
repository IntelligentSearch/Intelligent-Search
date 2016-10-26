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
        $scope.base_url = "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest";
        if (response != undefined) {
            $scope.userID = response.user.UserID;
            $scope.preferences = response.prefs;
            angular.forEach($scope.preferences, function (value, key) {
                console.log(key + ' : ' + value)
            });
            console.log("Prefs: " + response.prefs);
            $scope.name = {
                firstName: response.user.FirstName,
                lastName: response.user.LastName
            }

            $scope.password = {
                oldPassword: '',
                newPassword: ''
            }
            console.log($scope.base_url);


        }
        $scope.auth = function () {
            console.log("user: " + $scope.userID);
            if (($scope.name.firstName != undefined && $scope.name.lastName != undefined)
            && (response.user.FirstName != $scope.name.firstName || response.user.LastName != $scope.name.lastName)) {
                console.log("Changing name");
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
                    var response = $cookies.getObject('user');
                    response.user.FirstName = $scope.name.firstName;
                    response.user.LastName = $scope.name.lastName;
                    $cookies.putObject('user', response);
                }, function errorCallback(response) {
                    alert('Updating name failed' + angular.toJson(response));
                });
            }
            console.log("user: " + $scope.userID);
            //POST request for updating password
            if ($scope.password.newPassword != undefined && $scope.password.oldPassword) {
                console.log("Changing password");
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

            }
            console.log("user: " + $scope.userID, "prefs: " + $scope.preferences);
            // POST request for setting preferences
            $http({
                method: 'POST',
                url: $scope.base_url + '/set-pref',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({userID: $scope.userID, prefs: $scope.preferences})
            }).then(function successCallback(response) {
                // this callback will be called asynchronously
                // when the response is available
               console.log(response);
            }, function errorCallback(response) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                alert('Settings failed' + angular.toJson(response));
            });
        }
        $scope.onChange = function (index, key) {
            $scope.preferences[key] = !$scope.preferences[key];
            console.log(key + " " + $scope.preferences[key]);
        }
    });
