/**
 * Created by Apu on 11/7/16.
 */
'use strict';

var app = angular.module('myApp.favorites', ['ngRoute', 'ngCookies'])

    .config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common["X-Requested-With"];
        $httpProvider.defaults.headers.common["Accept"] = "application/json";
        $httpProvider.defaults.headers.common["Content-Type"] = "application/json";
        $routeProvider.when('/favorites', {
            templateUrl: 'app/favorites/favorites.html',
            controller: 'FavoritesCtrl',
            css: 'app/favorites/favorites.css'
        });
    }])
    .controller('FavoritesCtrl', function ($scope, $location, $http, $cookies, $httpParamSerializer) {

        $scope.getUserObj = function () {
            var response = $cookies.getObject('user');
            if (response != undefined && response.data != undefined) {
                var userID = response.data.user.UserID;
                console.log("GetUserId:" + userID);
                return userID;
            } else {
                return response;
            }
        };

        var userID = $scope.getUserObj().user.UserID;
        $scope.favorites = $cookies.getObject('user_' + userID + '_favorites');

        for(var index in $scope.favorites) {
            $scope.favorites[index].isFavorite = true;
        }

        $scope.unfavoriteItem = function(item) {
            $http({
                url: getAPIURL() + "unfavorite-item/",
                method: "POST",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({
                    userID: $scope.getUserObj().user.UserID,
                    itemID: item.Food_ID
                })
            }).success(function (data, status, headers, config) {
                item.isFavorite = false;
                for(var index in $scope.favorites) {
                    if($scope.favorites[index].Food_ID === item.Food_ID) {
                        $scope.favorites.splice(index, 1);
                        $cookies.putObject('user_' + userID + '_favorites', $scope.favorites);
                        break;
                    }
                }
            }).error(function (data, status, headers, config) {
                //error
            });
        }
    });