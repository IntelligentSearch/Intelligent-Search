'use strict';

var app = angular.module('myApp.dining', ['ngRoute', 'ngCookies'])

    .config(['$routeProvider', '$httpProvider', '$mdThemingProvider', function ($routeProvider, $httpProvider, $mdThemingProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common["X-Requested-With"];
        $httpProvider.defaults.headers.common["Accept"] = "application/json";
        $httpProvider.defaults.headers.common["Content-Type"] = "application/json";

        $mdThemingProvider.theme('default')
            .primaryPalette('amber', {
                'default': 'A700', // by default use shade 400 from the pink palette for primary intentions
                'hue-1': 'A700', // use shade 100 for the <code>md-hue-1</code> class
                'hue-2': 'A700', // use shade 600 for the <code>md-hue-2</code> class
                'hue-3': 'A700' // use shade A100 for the <code>md-hue-3</code> class
            })
            .accentPalette('orange', {
                'default': 'A700', // by default use shade 400 from the pink palette for primary intentions
                'hue-1': 'A700', // use shade 100 for the <code>md-hue-1</code> class
                'hue-2': 'A700', // use shade 600 for the <code>md-hue-2</code> class
                'hue-3': 'A700' // use shade A100 for the <code>md-hue-3</code> class
            });

        $routeProvider.when('/dining', {
            templateUrl: 'app/dining/dining.html',
            controller: 'DiningCtrl',
            css: 'app/dining/dining.css'
        });
    }])

    .controller('DiningCtrl', ['$scope', '$http', '$timeout', '$mdSidenav', '$log', '$cookies', function ($scope, $http, $timeout, $mdSidenav, $log, $cookies) {
        $scope.showSpinner = true;
        $scope.lastSearchString = "";
        var offset = -5.0;

        /* This function should not be added to every controller but rather live as a common function in Utility.js */
        /* We can just use document.cookie instead of $cookie it really doesn't matter if we use angular or not for this */
        /* We cannot have the same function live in so many different places */
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

        $scope.getUserName = function () {
            return $cookies.get('user_name');
        };


        $scope.getAlerts = function () {

            var response = {
                "alerts": [{
                    "Station": "Abby Road Deli",
                    "Breakfast": 0,
                    "Dinner": 1,
                    "Late Lunch": 1,
                    "Item_ID": "0c5ea9f5-df6e-4a66-a3a8-f2fd49534fa9",
                    "Name": "Caesar Salad",
                    "Location": "Windsor",
                    "Lunch": 1
                }, {
                    "Station": "Potato, Potäto",
                    "Breakfast": 0,
                    "Dinner": 1,
                    "Late Lunch": 0,
                    "Item_ID": "b24583e1-1446-489d-98f4-96ff76f0f6c6",
                    "Name": "Western Beef Barbecue",
                    "Location": "Ford",
                    "Lunch": 0
                }],
                "favs": [{
                    "Food_ID": "ceeea7de-f6ee-4155-976a-89ffccca9950",
                    "Name": "Eggs Benedict Casserole"
                }, {"Location": "Ford"}, {
                    "Food_ID": "46a95bfe-8cae-4107-b8ea-9eef19070111",
                    "Name": "Hamburgers and Toppings"
                }, {
                    "Food_ID": "98676547-45a9-46ae-8da8-e6a8b7ad88ab",
                    "Name": "Sausage Pizza"
                }, {
                    "Food_ID": "0dae092d-a36d-46af-a327-3407680f7f40",
                    "Name": "Noodles with BBQ Chinese Pork"
                }, {
                    "Food_ID": "0c5ea9f5-df6e-4a66-a3a8-f2fd49534fa9",
                    "Name": "Caesar Salad"
                }, {"Food_ID": "b24583e1-1446-489d-98f4-96ff76f0f6c6", "Name": "Western Beef Barbecue"}],
                "user": {"UserName": "test1", "UserID": 13, "FirstName": "test", "LastName": "user13"},
                "prefs": {
                    "Peanuts": false,
                    "Shellfish": false,
                    "Fish": false,
                    "Veg": false,
                    "Soy": false,
                    "Gluten": false,
                    "Wheat": false,
                    "Eggs": false,
                    "Tree_Nuts": false,
                    "Milk": false
                }
            }
            //return response
            response = $cookies.getObject('user');
            if (response != undefined && response.alerts != undefined) {
                 var alerts = response.alerts;
                 console.log("alerts:" + alerts);
                return alerts
            } else {
                return undefined
            }
        }
        $scope.alerts = $scope.getAlerts();


        var clientDate = new Date();
        var utc = clientDate.getTime() + (clientDate.getTimezoneOffset() * 60000);

        $scope.selectedDate = new Date(utc + (3600000 * offset));

        $scope.searchString = "";
        $scope.showSearch = false;
        $scope.showAlerts = false;

        $scope.toggleLeft = buildDelayedToggler('left');
        /**
         * Supplies a function that will continue to operate until the
         * time is up.
         */
        function debounce(func, wait, context) {
            var timer;

            return function debounced() {
                var context = $scope,
                    args = Array.prototype.slice.call(arguments);
                $timeout.cancel(timer);
                timer = $timeout(function () {
                    timer = undefined;
                    func.apply(context, args);
                }, wait || 10);
            };
        }

        /**
         * Build handler to open/close a SideNav; when animation finishes
         * report completion in console
         */
        function buildDelayedToggler(navID) {
            return debounce(function () {
                // Component lookup should always be available since we are not using `ng-if`
                $mdSidenav(navID)
                    .toggle()
                    .then(function () {
                        $log.debug("toggle " + navID + " is done");
                    });
            }, 200);
        }

        function buildToggler(navID) {
            return function () {
                // Component lookup should always be available since we are not using `ng-if`
                $mdSidenav(navID)
                    .toggle()
                    .then(function () {
                        $log.debug("toggle " + navID + " is done");
                    });
            }
        }

        var imagePath = 'img/list/60.jpeg';

        $scope.toppings = [
            {name: 'Scrambled Egg', wanted: true},
            {name: 'Sausage', wanted: false},
            {name: 'Pancakes', wanted: true},
            {name: 'Oatmeal', wanted: false}
        ];

        var tabs = [
                {title: 'Earhart'},
                {title: 'Ford'},
                {title: 'Hillenbrand'},
                {title: 'The Gathering Place'},
                {title: 'Wiley'},
                {title: 'Windsor'}
            ],
            selected = null,
            previous = null;
        $scope.tabs = tabs;
        $scope.selectedIndex = 0;
        $scope.$watch('selectedIndex', function (current, old) {
            $scope.getMealData();
            previous = selected;
            selected = tabs[current];
            if (old + 1 && (old != current)) $log.debug('Goodbye ' + previous.title + '!');
            if (current + 1)                $log.debug('Hello ' + selected.title + '!');
        });

        $scope.$watch('selectedDate', function () {
            $scope.getMealData();
        });

        $scope.getMealData = function () {
            $scope.showSpinner = true;

            var stringDate = $scope.selectedDate.toISOString().substring(0, 10);
            stringDate = stringDate.substring(5, 7) + "-" + stringDate.substring(8, 10) + "-" + stringDate.substring(0, 4);

            $scope.userID = "0";
            if($scope.getUserObj() != null) {
                $scope.userID = $scope.getUserObj().user.UserID;
            }

            $http({
                url: getAPIURL() + "menu/" + tabs[$scope.selectedIndex].title + "/" + stringDate + "/" + $scope.userID,
                method: "GET"
            }).success(function (data, status, headers, config) {
                if (data != null) {
                    $scope.mealData = data;
                }
                $timeout(function () {
                    $scope.showSpinner = false;
                }, 2000);
            }).error(function (data, status, headers, config) {
                $scope.showSpinner = false;
            });
        };

        $scope.getMealData();

        $scope.$watch('searchString', function () {
            if ($scope.searchString != undefined && $scope.searchString != null && $scope.searchString != "") {

                $scope.searchResult = [];

                var pathParam = $scope.searchString.split(' ').join('%20');
                $http({
                    url: getAPIURL() + "search/" + pathParam,
                    method: "GET",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    }
                }).success(function (data, status, headers, config) {
                    $scope.lastSearchString = $scope.searchString;
                    if (data != null) {
                        if (data[0].Error == "Feature not Supported") {
                            $scope.featureNotSupported = true;
                        }
                        else {
                            $scope.featureNotSupported = false;
                        }
                        var response = $cookies.getObject('user');
                        if (response != undefined) {
                            var preferences = response.prefs;
                            for (var i = data.length - 1; i >= 0; i--) {
                                if ((data[i]["Peanuts"] && preferences["Peanuts"])
                                    || (data[i]["Shellfish"] && preferences["Shellfish"])
                                    || (data[i]["Eggs"] && preferences["Eggs"])
                                    || (data[i]["Fish"] && preferences["Fish"])
                                    || (data[i]["Milk"] && preferences["Milk"])
                                    || (data[i]["Tree_nuts"] && preferences["Tree_nuts"])
                                    || (data[i]["Veg"] && preferences["Veg"])
                                    || (data[i]["Soy"] && preferences["Soy"])
                                    || (data[i]["Wheat"] && preferences["Wheat"])
                                    || (data[i]["Gluten"] && preferences["Gluten"])) {
                                    data.splice(i, 1);
                                }
                            }
                        }
                        $scope.searchResult = data;
                    }
                }).error(function (data, status, headers, config) {
                });
            }
        });

        $scope.toggleFavorite = function (item) {
            var userID = $scope.getUserObj().user.UserID;
            $scope.favorites = $cookies.getObject('user_' + userID + '_favorites');

            if (item.isFavorite === undefined || !item.isFavorite) {
                $http({
                    url: getAPIURL() + "favorite-item/",
                    method: "POST",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({
                        userID: $scope.getUserObj().user.UserID,
                        itemID: item.ID
                    })
                }).success(function (data, status, headers, config) {
                    item.isFavorite = true;

                    for (var i in $scope.mealData.Meals) {
                        for (var j in $scope.mealData.Meals[i].Stations) {
                            for (var k in $scope.mealData.Meals[i].Stations[j].Items) {
                                var element = $scope.mealData.Meals[i].Stations[j].Items[k];
                                if (element.ID == item.ID) {
                                    element.isFavorite = true;
                                }
                            }
                        }
                    }

                    $scope.favorites.push(item);
                    $cookies.putObject('user_' + userID + '_favorites', $scope.favorites);

                }).error(function (data, status, headers, config) {
                    //error
                });
            } else {
                $http({
                    url: getAPIURL() + "unfavorite-item/",
                    method: "POST",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({
                        userID: $scope.getUserObj().user.UserID,
                        itemID: item.ID
                    })
                }).success(function (data, status, headers, config) {
                    item.isFavorite = false;

                    for (var i in $scope.mealData.Meals) {
                        for (var j in $scope.mealData.Meals[i].Stations) {
                            for (var k in $scope.mealData.Meals[i].Stations[j].Items) {
                                var element = $scope.mealData.Meals[i].Stations[j].Items[k];
                                if (element.ID == item.ID) {
                                    element.isFavorite = false;
                                }
                            }
                        }
                    }

                    for (var index in $scope.favorites) {
                        if ($scope.favorites[index].Food_ID === item.Food_ID) {
                            $scope.favorites.splice(index, 1);
                            $cookies.putObject('user_' + userID + '_favorites', $scope.favorites);
                            break;
                        }
                    }
                }).error(function (data, status, headers, config) {
                    //error
                });
            }
        }

    }]).controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log, $location, $cookies) {
        $scope.logout = function () {
            $cookies.remove('user');
            $cookies.remove('user_name');
            $location.path("/dining");
        };

        $scope.close = function () {
            // Component lookup should always be available since we are not using `ng-if`
            $mdSidenav('left').close()
                .then(function () {
                    $log.debug("close LEFT is done");
                });

        };
    }).controller('ListCtrl', function ($scope) {

    });
