/**
 * Created by PD on 9/30/16.
 */

(function() {
    var myApp = angular.module('BlankApp', ['ngMaterial']);

    myApp.controller('MyController', ['$scope', function($scope) {
        $scope.fuckthis = 'Hola!';
    }]);
})();
