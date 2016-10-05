/**
 * Created by PD on 9/30/16.
 */

(function() {
    var myApp = angular.module('BlankApp', ['ngMaterial']);

    myApp.config(['$httpProvider', '$mdThemingProvider', function($httpProvider, $mdThemingProvider) {
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
    }]);

    myApp.controller('MyController', ['$scope', '$http', '$timeout', '$mdSidenav', '$log', function($scope, $http, $timeout, $mdSidenav, $log) {

        $scope.showSpinner = true;
        var offset = -5.0;

        var clientDate = new Date();
        var utc = clientDate.getTime() + (clientDate.getTimezoneOffset() * 60000);

        $scope.selectedDate = new Date(utc + (3600000*offset));

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
                timer = $timeout(function() {
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
            return debounce(function() {
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
                { title: 'Earhart'},
                { title: 'Ford'},
                { title: 'Hillenbrand'},
                { title: 'The Gathering Place'},
                { title: 'Wiley'},
                { title: 'Windsor'}
            ],
            selected = null,
            previous = null;
        $scope.tabs = tabs;
        $scope.selectedIndex = 0;
        $scope.$watch('selectedIndex', function(current, old){
            $scope.getMealData();
            previous = selected;
            selected = tabs[current];
            if ( old + 1 && (old != current)) $log.debug('Goodbye ' + previous.title + '!');
            if ( current + 1 )                $log.debug('Hello ' + selected.title + '!');
        });

        $scope.$watch('selectedDate', function() {
           $scope.getMealData();
        });

        $scope.getMealData = function() {
            $scope.showSpinner = true;

            var stringDate = $scope.selectedDate.toISOString().substring(0, 10);
            stringDate = stringDate.substring(5, 7) + "-" + stringDate.substring(8, 10) + "-" + stringDate.substring(0,4);

            $http({
                url: "https://api.hfs.purdue.edu/menus/v1/locations/" + tabs[$scope.selectedIndex].title + "/" + stringDate,
                method: "GET"
            }).success(function(data, status, headers, config) {
                if(data != null) {
                    $scope.mealData = data;
                }

                $timeout(function() {
                    $scope.showSpinner = false;
                }, 2000);


            }).error(function (data, status, headers, config) {
                $scope.showSpinner = false;
            });
        };

        $scope.getMealData();


    }]).controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
        $scope.close = function () {
            // Component lookup should always be available since we are not using `ng-if`
            $mdSidenav('left').close()
                .then(function () {
                    $log.debug("close LEFT is done");
                });

        };
    }).controller('ListCtrl', function($scope) {

    })
})();
