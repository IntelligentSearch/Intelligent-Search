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

        $scope.getUserID = function () {
            var response = $cookies.getObject('user');
            if (response != undefined && response.data != undefined) {
                var userID = response.data.user.UserID;
                console.log("GetUserId:" + userID);
                return userID;
            } else {
                return response;
            }
        }

        $scope.getUserName = function () {
            return $cookies.get('user_name');
        }

        var clientDate = new Date();
        var utc = clientDate.getTime() + (clientDate.getTimezoneOffset() * 60000);

        $scope.selectedDate = new Date(utc + (3600000 * offset));

        $scope.searchString = "";
        $scope.showSearch = false;

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
            var stringDate = $scope.selectedDate.toISOString().substring(0, 10);
            stringDate = stringDate.substring(5, 7) + "-" + stringDate.substring(8, 10) + "-" + stringDate.substring(0, 4);

            $http({
                url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/menu/" + tabs[$scope.selectedIndex].title + "/" + stringDate,
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
                    url: "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/search/" + pathParam,
                    method: "GET"
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

    }]).controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log, $location, $cookies) {

        $scope.getUserID = function () {
            var response = $cookies.getObject('user');
            if (response != undefined && response.data != undefined) {
                var userID = response.data.user.UserID;
                return userID;
            } else {
                return response;
            }
        }

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

    })
