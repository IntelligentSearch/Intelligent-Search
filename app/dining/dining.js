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
            var response = $cookies.get('user');
            if (response.data != undefined) {
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
                url: "https://cs307.cs.purdue.edu:8443/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/menu/" + tabs[$scope.selectedIndex].title + "/" + stringDate,
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

                // $scope.searchResult = {"Menu":[{"DiningCourt":"Wiley","Calcium":"114.342","Shellfish":false,"Iron":"0.7182","Eggs":true,"Vitamin A":"172.5283","Food_ID":"b564cad0-7218-4655-95b3-bcc1facaf116","Dinner":1,"Vitamin C":"5.3054","Dietary Fiber":"0.535","Fish":false,"Serving Size":"16 Cut Serving","Gluten":true,"Sugar":"0.2363","FoodName":"Crispy Philly Cheesesteak Pizza","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.2571","Total fat":"19.6955","Calories":"269.297","Cholesterol":"443.8049","Protein":"8.0535","Lunch":1,"Station":"Mozzarella Fresca","Tree_nuts":false,"Calories from fat":"180","Sodium":"248.8168","Total Carbohydrate":"14.0513","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"49.6116","Shellfish":false,"Iron":"1.1542","Eggs":true,"Vitamin A":"73.5254","Food_ID":"807ea77b-d0d1-4028-9015-c2d02a9b25e6","Dinner":1,"Vitamin C":"0.1012","Dietary Fiber":"2.2275","Fish":false,"Serving Size":"14 Cut Serving","Gluten":true,"Sugar":"33.503","FoodName":"German Chocolate Cake","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.3449","Total fat":"18.7208","Calories":"374.6184","Cholesterol":"42.5242","Protein":"3.5437","Lunch":1,"Station":"Delectables","Tree_nuts":true,"Calories from fat":"171","Sodium":"406.0053","Total Carbohydrate":"50.0369","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"188.9352","Shellfish":false,"Iron":"1.6596","Eggs":false,"Vitamin A":"640.8479","Food_ID":"36a2e399-0026-4d2f-9bd4-d09966e0a83a","Dinner":0,"Vitamin C":"2.1637","Dietary Fiber":"1.1467","Fish":false,"Serving Size":"1 Each Serving","Gluten":false,"Sugar":"0.4366","FoodName":"Stuffed Shells","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"4.8851","Total fat":"9.7296","Calories":"226.1865","Cholesterol":"63.2903","Protein":"11.2803","Lunch":1,"Station":"Romeo & Parmesan","Tree_nuts":false,"Calories from fat":"90","Sodium":"666.4259","Total Carbohydrate":"23.0779","Veg":true,"Soy":true,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"15.0089","Shellfish":false,"Iron":"0.6004","Eggs":false,"Vitamin A":"146.6257","Food_ID":"a8ff0f0b-8638-4f95-8973-376439dc4612","Dinner":1,"Vitamin C":"20.2043","Dietary Fiber":"1.5009","Fish":false,"Serving Size":"1/2 Cup","Gluten":false,"Sugar":"0.0","FoodName":"Tropical Fruit Salad","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.0219","Total fat":"0.1155","Calories":"99.2899","Cholesterol":"0.0","Protein":"0.4734","Lunch":1,"Station":"Fresh Tastes","Tree_nuts":false,"Calories from fat":"0","Sodium":"2.3091","Total Carbohydrate":"25.8154","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"50.3986","Shellfish":false,"Iron":"1.8144","Eggs":false,"Vitamin A":"251.9953","Food_ID":"2a711a54-3323-4806-a597-5d14c03f0813","Dinner":0,"Vitamin C":"0.0","Dietary Fiber":"0.0","Fish":false,"Serving Size":"Burger","Gluten":false,"Sugar":"0.0","FoodName":"Turkey Burger","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.0394","Total fat":"17.6402","Calories":"226.796","Cholesterol":"88.1987","Protein":"20.1599","Lunch":1,"Station":"Open Flame","Tree_nuts":false,"Calories from fat":"162","Sodium":"755.987","Total Carbohydrate":"0.0","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"4.9953","Shellfish":false,"Iron":"0.2822","Eggs":false,"Vitamin A":"57.4053","Food_ID":"c6d9a056-399b-4b3e-aa4a-c946a7f19d59","Dinner":1,"Vitamin C":"11.7964","Dietary Fiber":"1.6329","Fish":false,"Serving Size":"1/2 Cup Serving","Gluten":false,"Sugar":"0.7893","FoodName":"Red Potatoes","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.2562","Total fat":"1.3266","Calories":"89.9046","Cholesterol":"0.0","Protein":"1.6989","Lunch":1,"Station":"Churrascaria","Tree_nuts":false,"Calories from fat":"9","Sodium":"18.0691","Total Carbohydrate":"18.2724","Veg":true,"Soy":true,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"8.5048","Shellfish":false,"Iron":"0.8562","Eggs":false,"Vitamin A":"0.0","Food_ID":"0bc9d39f-5e44-47df-820b-64f0c23c551d","Dinner":0,"Vitamin C":"3.572","Dietary Fiber":"1.134","Fish":false,"Serving Size":"Each","Gluten":false,"Sugar":"0.8902","FoodName":"Hash Brown Patty","Milk":false,"Breakfast":1,"Peanuts":false,"Saturated fat":"2.5475","Total fat":"6.5204","Calories":"123.6038","Cholesterol":"0.0","Protein":"1.7917","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"63","Sodium":"19.2777","Total Carbohydrate":"15.9324","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"20.3073","Shellfish":false,"Iron":"1.2865","Eggs":false,"Vitamin A":"110.6091","Food_ID":"26841ee6-b550-4d1a-b03f-08f280f37bcb","Dinner":1,"Vitamin C":"1.7137","Dietary Fiber":"0.0892","Fish":false,"Serving Size":"Each","Gluten":false,"Sugar":"0.0191","FoodName":"Cilantro Marinated Chicken Breast","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"2.4876","Total fat":"8.8415","Calories":"225.4393","Cholesterol":"95.2547","Protein":"33.8836","Lunch":0,"Station":"Open Flame","Tree_nuts":false,"Calories from fat":"81","Sodium":"1793.4379","Total Carbohydrate":"0.5591","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"112.4504","Shellfish":false,"Iron":"0.3912","Eggs":false,"Vitamin A":"187.9296","Food_ID":"596e7281-a1e6-484c-b4a8-4828b5210000","Dinner":1,"Vitamin C":"1.0836","Dietary Fiber":"0.443","Fish":false,"Serving Size":"16 Cut Serving","Gluten":true,"Sugar":"0.0","FoodName":"Crispy Cheese Pizza","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"2.9944","Total fat":"6.3459","Calories":"138.0466","Cholesterol":"4.2524","Protein":"5.336","Lunch":1,"Station":"Mozzarella Fresca","Tree_nuts":false,"Calories from fat":"54","Sodium":"169.0009","Total Carbohydrate":"14.1352","Veg":true,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"96.3883","Shellfish":false,"Iron":"0.6388","Eggs":false,"Vitamin A":"255.7011","Food_ID":"dc237d61-c08a-4bfc-9476-97bc2c5c0f0d","Dinner":1,"Vitamin C":"3.2455","Dietary Fiber":"0.9934","Fish":false,"Serving Size":"10 Cut Serving","Gluten":true,"Sugar":"24.6553","FoodName":"Banana Cream Pie","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.8021","Total fat":"11.8318","Calories":"288.3882","Cholesterol":"5.4432","Protein":"3.8288","Lunch":0,"Station":"Delectables","Tree_nuts":false,"Calories from fat":"108","Sodium":"471.2083","Total Carbohydrate":"42.4141","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"68.1284","Shellfish":false,"Iron":"1.2107","Eggs":true,"Vitamin A":"245.0203","Food_ID":"1545270b-7d55-4c99-80a3-b4135b98ad04","Dinner":1,"Vitamin C":"0.2194","Dietary Fiber":"0.6828","Fish":false,"Serving Size":"1/2 Cup Serving","Gluten":true,"Sugar":"0.2624","FoodName":"Turkey Tetrazzini","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"1.7983","Total fat":"6.1069","Calories":"171.0969","Cholesterol":"44.3319","Protein":"14.9746","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"54","Sodium":"269.9116","Total Carbohydrate":"13.194","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"39.7045","Shellfish":false,"Iron":"0.8878","Eggs":false,"Vitamin A":"5167.717","Food_ID":"f3b662e5-d38a-44a9-a2d4-1d9c6c15e5f1","Dinner":0,"Vitamin C":"9.6812","Dietary Fiber":"2.7775","Fish":false,"Serving Size":"1/2 Cup Serving","Gluten":false,"Sugar":"3.159","FoodName":"Whole Green Beans and Carrots","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.04","Total fat":"0.188","Calories":"38.7067","Cholesterol":"0.0","Protein":"1.4404","Lunch":1,"Station":"No Wheat No Meat","Tree_nuts":false,"Calories from fat":"0","Sodium":"29.166","Total Carbohydrate":"7.9957","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"7.56","Shellfish":false,"Iron":"0.4872","Eggs":false,"Vitamin A":"82.7677","Food_ID":"29c3e84c-e696-4f04-a9b9-0eb55088219d","Dinner":1,"Vitamin C":"1.26","Dietary Fiber":"0.476","Fish":false,"Serving Size":"Each","Gluten":true,"Sugar":"0.3528","FoodName":"Vegan Potato Samosas","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"1.1152","Total fat":"5.0008","Calories":"86.24","Cholesterol":"2.52","Protein":"1.3048","Lunch":0,"Station":"Open Flame","Tree_nuts":false,"Calories from fat":"45","Sodium":"228.76","Total Carbohydrate":"9.0216","Veg":false,"Soy":false,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"20.1841","Shellfish":false,"Iron":"0.6318","Eggs":true,"Vitamin A":"356.7466","Food_ID":"ab0cd19a-3c94-42a2-95f3-d526392e3bf9","Dinner":1,"Vitamin C":"6.6313","Dietary Fiber":"1.2373","Fish":false,"Serving Size":"1/2 Cup","Gluten":true,"Sugar":"4.6506","FoodName":"Macaroni Salad","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"1.502","Total fat":"8.1171","Calories":"180.1784","Cholesterol":"71.3015","Protein":"4.9078","Lunch":1,"Station":"Fresh Tastes","Tree_nuts":false,"Calories from fat":"72","Sodium":"682.4453","Total Carbohydrate":"21.9085","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"18.1862","Shellfish":false,"Iron":"0.9796","Eggs":false,"Vitamin A":"252.6686","Food_ID":"eb533c9a-ecee-4d94-ba23-4307bb8ebfb2","Dinner":0,"Vitamin C":"0.1049","Dietary Fiber":"1.3466","Fish":false,"Serving Size":"5x8 Cut Serving","Gluten":true,"Sugar":"15.3048","FoodName":"Caramel Apple Bar with Walnuts","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.2572","Total fat":"10.163","Calories":"222.5116","Cholesterol":"21.8093","Protein":"2.567","Lunch":1,"Station":"Delectables","Tree_nuts":true,"Calories from fat":"90","Sodium":"139.3362","Total Carbohydrate":"31.256","Veg":false,"Soy":false,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"47.6268","Shellfish":false,"Iron":"1.1238","Eggs":false,"Vitamin A":"15.2407","Food_ID":"475e55db-1d99-48d0-8c9d-1309d83f8238","Dinner":1,"Vitamin C":"0.0","Dietary Fiber":"0.0","Fish":false,"Serving Size":"12 oz Serving","Gluten":false,"Sugar":"0.0","FoodName":"Baby Back Ribs","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"4.5056","Total fat":"21.0322","Calories":"690.3429","Cholesterol":"110.4947","Protein":"38.635","Lunch":1,"Station":"Churrascaria","Tree_nuts":false,"Calories from fat":"189","Sodium":"1498.6777","Total Carbohydrate":"81.5994","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"0.0794","Shellfish":false,"Iron":"0.9579","Eggs":false,"Vitamin A":"0.0","Food_ID":"6ea795d3-67d5-4a39-9e81-d2ee5e9e64aa","Dinner":0,"Vitamin C":"0.0","Dietary Fiber":"2.1262","Fish":false,"Serving Size":"1/2 Cup Serving","Gluten":true,"Sugar":"0.0","FoodName":"Oatmeal","Milk":false,"Breakfast":1,"Peanuts":false,"Saturated fat":"0.2658","Total fat":"1.5947","Calories":"79.7333","Cholesterol":"0.0","Protein":"2.6578","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"18","Sodium":"128.1171","Total Carbohydrate":"14.352","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"10.9447","Shellfish":false,"Iron":"1.2658","Eggs":false,"Vitamin A":"212.5124","Food_ID":"e12cf4ab-2401-42c9-87df-ab59e0d19e28","Dinner":0,"Vitamin C":"1.6372","Dietary Fiber":"0.1892","Fish":true,"Serving Size":"1/3 Cup","Gluten":false,"Sugar":"8.9136","FoodName":"Smoked Western Beef Barbecue","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"2.7251","Total fat":"7.5152","Calories":"159.3081","Cholesterol":"35.3804","Protein":"11.1413","Lunch":1,"Station":"Open Flame","Tree_nuts":false,"Calories from fat":"72","Sodium":"394.2583","Total Carbohydrate":"10.9978","Veg":false,"Soy":true,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"18.1437","Shellfish":false,"Iron":"0.8505","Eggs":false,"Vitamin A":"4.5359","Food_ID":"f1555fe5-8872-4adb-8f51-51d91782b60f","Dinner":0,"Vitamin C":"46.72","Dietary Fiber":"2.3814","Fish":false,"Serving Size":"4 oz Serving","Gluten":false,"Sugar":"0.0","FoodName":"Strawberries","Milk":false,"Breakfast":1,"Peanuts":false,"Saturated fat":"0.0068","Total fat":"0.1247","Calories":"39.6893","Cholesterol":"0.0","Protein":"0.4876","Lunch":0,"Station":"Fresh Tastes","Tree_nuts":false,"Calories from fat":"0","Sodium":"2.268","Total Carbohydrate":"10.3532","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"0.0","Shellfish":false,"Iron":"0.7162","Eggs":true,"Vitamin A":"298.4169","Food_ID":"08b0e608-89e7-475a-b206-384a66025b92","Dinner":1,"Vitamin C":"0.0","Dietary Fiber":"0.9947","Fish":false,"Serving Size":"Cookie","Gluten":true,"Sugar":"0.0","FoodName":"Carnival Cookie","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"3.4815","Total fat":"6.9631","Calories":"159.1557","Cholesterol":"9.9472","Protein":"1.9895","Lunch":1,"Station":"Delectables","Tree_nuts":false,"Calories from fat":"63","Sodium":"124.3404","Total Carbohydrate":"23.8734","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"83.4609","Shellfish":false,"Iron":"1.5962","Eggs":true,"Vitamin A":"572.205","Food_ID":"6c883ba0-e283-4086-ab01-e181a6615435","Dinner":0,"Vitamin C":"0.0","Dietary Fiber":"0.0","Fish":false,"Serving Size":"1/3 Cup ","Gluten":false,"Sugar":"1.7422","FoodName":"Scrambled Eggs","Milk":true,"Breakfast":1,"Peanuts":false,"Saturated fat":"3.2327","Total fat":"10.2673","Calories":"152.9011","Cholesterol":"339.0601","Protein":"12.2792","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"90","Sodium":"281.6806","Total Carbohydrate":"1.9927","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"17.6901","Shellfish":false,"Iron":"0.1327","Eggs":false,"Vitamin A":"1366.5593","Food_ID":"f676dec5-b342-4daa-90d7-c854a1f19848","Dinner":0,"Vitamin C":"50.7116","Dietary Fiber":"1.6216","Fish":false,"Serving Size":"Serving","Gluten":false,"Sugar":"10.2897","FoodName":"Pink Grapefruit Half","Milk":false,"Breakfast":1,"Peanuts":false,"Saturated fat":"0.0206","Total fat":"0.1474","Calories":"47.1736","Cholesterol":"0.0","Protein":"0.9287","Lunch":0,"Station":"Fresh Tastes","Tree_nuts":false,"Calories from fat":"0","Sodium":"0.0","Total Carbohydrate":"11.9113","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"9.5721","Shellfish":false,"Iron":"0.9381","Eggs":false,"Vitamin A":"98.9337","Food_ID":"9e6eb50a-0778-49fd-a84e-d9d476f1a297","Dinner":1,"Vitamin C":"0.0","Dietary Fiber":"0.4786","Fish":false,"Serving Size":"1/2 Cup ","Gluten":false,"Sugar":"0.1245","FoodName":"Long Grain Rice","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.3992","Total fat":"2.0675","Calories":"107.2078","Cholesterol":"0.0","Protein":"2.0772","Lunch":1,"Station":"Romeo & Parmesan","Tree_nuts":false,"Calories from fat":"18","Sodium":"489.1356","Total Carbohydrate":"19.7281","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"8.0744","Shellfish":false,"Iron":"0.4855","Eggs":false,"Vitamin A":"196.0811","Food_ID":"6e6ec161-3296-4d76-b41a-61de65b71ddf","Dinner":1,"Vitamin C":"4.1307","Dietary Fiber":"0.1542","Fish":false,"Serving Size":"Piece","Gluten":true,"Sugar":"0.9786","FoodName":"Malawi Mango Chicken","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"1.4456","Total fat":"3.3463","Calories":"97.0625","Cholesterol":"34.0537","Protein":"11.2142","Lunch":1,"Station":"Churrascaria","Tree_nuts":true,"Calories from fat":"27","Sodium":"447.5355","Total Carbohydrate":"1.205","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"42.9011","Shellfish":false,"Iron":"1.2384","Eggs":false,"Vitamin A":"368.6952","Food_ID":"00bee7d7-b8d5-42ee-b468-f71fbd044d34","Dinner":1,"Vitamin C":"12.5405","Dietary Fiber":"4.3731","Fish":false,"Serving Size":"1/2 Cup ","Gluten":false,"Sugar":"2.0887","FoodName":"Lemon Herb Garbanzo Bean Salad","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.3139","Total fat":"3.3397","Calories":"100.0019","Cholesterol":"0.0","Protein":"4.497","Lunch":1,"Station":"Churrascaria","Tree_nuts":false,"Calories from fat":"27","Sodium":"291.9134","Total Carbohydrate":"14.747","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"0.0","Shellfish":false,"Iron":"0.0","Eggs":true,"Vitamin A":"0.0","Food_ID":"e598b5a8-621f-44fa-be37-3392227d4e77","Dinner":0,"Vitamin C":"0.0","Dietary Fiber":"0.2835","Fish":false,"Serving Size":"Ounce","Gluten":true,"Sugar":"0.0","FoodName":"Tempura  Chicken","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.567","Total fat":"2.268","Calories":"43.3747","Cholesterol":"9.9223","Protein":"3.9689","Lunch":1,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"18","Sodium":"149.9689","Total Carbohydrate":"3.6854","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"8.9918","Shellfish":false,"Iron":"0.8859","Eggs":true,"Vitamin A":"491.049","Food_ID":"6f51f6bc-8500-4da4-8468-d0255cd70708","Dinner":1,"Vitamin C":"0.9409","Dietary Fiber":"0.4701","Fish":false,"Serving Size":"Cookie","Gluten":true,"Sugar":"22.153","FoodName":"Glazed Lemon Cookie","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"2.1615","Total fat":"11.3172","Calories":"282.9676","Cholesterol":"10.9959","Protein":"2.1545","Lunch":1,"Station":"Delectables","Tree_nuts":false,"Calories from fat":"99","Sodium":"324.5565","Total Carbohydrate":"43.9681","Veg":false,"Soy":true,"Wheat":true},{"Breakfast":0,"DiningCourt":"Wiley","Peanuts":false,"Shellfish":false,"Calories":"0.0","Eggs":false,"Lunch":1,"Station":"Fresh Tastes","Tree_nuts":false,"Food_ID":"fcba9e57-43c3-40d4-a7ae-d1d68066069b","Dinner":1,"Fish":false,"Veg":false,"Soy":false,"Serving Size":"1/2 Cup Serving","Gluten":false,"Wheat":false,"FoodName":"Mango Chunks","Milk":false},{"DiningCourt":"Wiley","Calcium":"15.9211","Shellfish":false,"Iron":"0.7164","Eggs":false,"Vitamin A":"0.0","Food_ID":"1a5dd07f-834b-4465-8d4b-b2f4fcae7e01","Dinner":1,"Vitamin C":"4.1572","Dietary Fiber":"3.3611","Fish":false,"Serving Size":"4 oz Serving","Gluten":false,"Sugar":"0.2654","FoodName":"Crinkle Cut Fries","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"2.0662","Total fat":"13.0287","Calories":"275.9654","Cholesterol":"0.0","Protein":"3.0339","Lunch":1,"Station":"Open Flame","Tree_nuts":false,"Calories from fat":"117","Sodium":"185.7459","Total Carbohydrate":"36.6539","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"210.769","Shellfish":false,"Iron":"2.1824","Eggs":false,"Vitamin A":"376.087","Food_ID":"434b64b0-431a-41f9-95ac-85f544477952","Dinner":1,"Vitamin C":"13.3589","Dietary Fiber":"1.2437","Fish":false,"Serving Size":"Each","Gluten":true,"Sugar":"1.533","FoodName":"Chicken Florentine","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"8.8245","Total fat":"20.6776","Calories":"359.6027","Cholesterol":"96.8936","Protein":"37.2672","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"189","Sodium":"796.8845","Total Carbohydrate":"5.4307","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"0.0","Shellfish":false,"Iron":"0.0","Eggs":false,"Vitamin A":"0.0","Food_ID":"8250ca53-98cb-4ba9-9699-c760e1375c3d","Dinner":0,"Vitamin C":"0.0","Dietary Fiber":"0.567","Fish":false,"Serving Size":"1 per serving","Gluten":true,"Sugar":"0.0","FoodName":"Mini Spring Rolls","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.0","Total fat":"0.567","Calories":"35.1534","Cholesterol":"0.0","Protein":"0.8505","Lunch":1,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"9","Sodium":"155.6388","Total Carbohydrate":"6.5204","Veg":true,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"23.5974","Shellfish":false,"Iron":"0.5243","Eggs":false,"Vitamin A":"1547.2067","Food_ID":"e4f0914b-66a7-449e-8674-650d71a337e5","Dinner":1,"Vitamin C":"22.2014","Dietary Fiber":"2.0106","Fish":false,"Serving Size":"6 oz Ladle","Gluten":false,"Sugar":"4.0557","FoodName":"Vegetable Soup","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.0389","Total fat":"0.2336","Calories":"50.6253","Cholesterol":"0.0","Protein":"1.6371","Lunch":1,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"0","Sodium":"357.1001","Total Carbohydrate":"11.1353","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"317.2566","Shellfish":false,"Iron":"0.999","Eggs":true,"Vitamin A":"1270.2923","Food_ID":"84b1882b-00e7-4a4b-81a7-f0150694a70e","Dinner":0,"Vitamin C":"26.1654","Dietary Fiber":"1.3587","Fish":false,"Serving Size":"Cut 16 Serving","Gluten":true,"Sugar":"2.8149","FoodName":"Broccoli Brunch Pie","Milk":true,"Breakfast":1,"Peanuts":false,"Saturated fat":"12.4197","Total fat":"25.8279","Calories":"334.5296","Cholesterol":"91.9226","Protein":"15.3068","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"234","Sodium":"612.7528","Total Carbohydrate":"11.1587","Veg":true,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"15.2407","Shellfish":false,"Iron":"0.3157","Eggs":false,"Vitamin A":"108.8621","Food_ID":"6c4fcb26-84d8-4036-ad33-44d754781daf","Dinner":1,"Vitamin C":"4.3545","Dietary Fiber":"0.9798","Fish":false,"Serving Size":"4 oz Serving","Gluten":false,"Sugar":"17.6901","FoodName":"Red Grapes","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.1241","Total fat":"0.381","Calories":"72.9376","Cholesterol":"0.0","Protein":"0.6858","Lunch":1,"Station":"Delectables","Tree_nuts":false,"Calories from fat":"0","Sodium":"2.1772","Total Carbohydrate":"18.6698","Veg":false,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"159.4387","Shellfish":false,"Iron":"1.5956","Eggs":true,"Vitamin A":"241.2991","Food_ID":"1a046607-2fc1-4052-94a0-ba7b0deba29d","Dinner":0,"Vitamin C":"0.626","Dietary Fiber":"0.3717","Fish":false,"Serving Size":"1/2 Cup","Gluten":true,"Sugar":"0.6323","FoodName":"Cheeseburger Casserole","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"5.9318","Total fat":"12.2868","Calories":"204.0288","Cholesterol":"43.5592","Protein":"12.7613","Lunch":1,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"108","Sodium":"579.6658","Total Carbohydrate":"10.8713","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"13.3951","Shellfish":false,"Iron":"0.8216","Eggs":false,"Vitamin A":"0.2417","Food_ID":"738ffca8-3684-4a36-97da-c2152971cd2e","Dinner":0,"Vitamin C":"0.0298","Dietary Fiber":"1.28","Fish":false,"Serving Size":"Slice","Gluten":true,"Sugar":"1.0746","FoodName":"Sliced French Bread","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.2057","Total fat":"0.9972","Calories":"81.2638","Cholesterol":"0.0","Protein":"2.8785","Lunch":1,"Station":"Romeo & Parmesan","Tree_nuts":false,"Calories from fat":"9","Sodium":"145.5605","Total Carbohydrate":"15.5145","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"46.758","Shellfish":false,"Iron":"1.9189","Eggs":false,"Vitamin A":"1393.7776","Food_ID":"17557a64-61a9-460c-9126-6ac15ca369ed","Dinner":1,"Vitamin C":"3.7373","Dietary Fiber":"1.9019","Fish":false,"Serving Size":"1/2 Cup","Gluten":false,"Sugar":"0.9605","FoodName":"Vegetable Rice Pilaf (contains nuts)","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.5984","Total fat":"5.3195","Calories":"159.199","Cholesterol":"0.0198","Protein":"4.006","Lunch":0,"Station":"Classic Flavors","Tree_nuts":true,"Calories from fat":"45","Sodium":"647.7007","Total Carbohydrate":"24.0104","Veg":true,"Soy":true,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"231.652","Shellfish":false,"Iron":"3.2387","Eggs":false,"Vitamin A":"289.176","Food_ID":"d85b7e80-d003-45a4-aa45-a0f7e44ef1ff","Dinner":1,"Vitamin C":"5.5292","Dietary Fiber":"1.7184","Fish":false,"Serving Size":"Sandwich","Gluten":true,"Sugar":"0.244","FoodName":"Stromboli","Milk":true,"Breakfast":0,"Peanuts":false,"Saturated fat":"4.6391","Total fat":"12.7123","Calories":"406.3816","Cholesterol":"67.1805","Protein":"29.9393","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"117","Sodium":"1208.5948","Total Carbohydrate":"40.9626","Veg":false,"Soy":true,"Wheat":true},{"DiningCourt":"Wiley","Calcium":"42.8644","Shellfish":false,"Iron":"0.8675","Eggs":false,"Vitamin A":"558.2584","Food_ID":"4803d385-361f-4870-a7c5-be9fba3781bd","Dinner":1,"Vitamin C":"13.1655","Dietary Fiber":"2.6535","Fish":false,"Serving Size":"1/2 Cup","Gluten":false,"Sugar":"2.2555","FoodName":"Whole Green Beans","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"0.048","Total fat":"0.2143","Calories":"39.8027","Cholesterol":"0.0","Protein":"1.8268","Lunch":0,"Station":"Classic Flavors","Tree_nuts":false,"Calories from fat":"0","Sodium":"3.0617","Total Carbohydrate":"7.6952","Veg":true,"Soy":false,"Wheat":false},{"DiningCourt":"Wiley","Calcium":"20.4159","Shellfish":false,"Iron":"2.27","Eggs":false,"Vitamin A":"108.8589","Food_ID":"5ef7601b-1d0d-4e39-9cb9-0c10842e413f","Dinner":1,"Vitamin C":"2.5305","Dietary Fiber":"5.293","Fish":false,"Serving Size":"1/2 Cup Serving","Gluten":false,"Sugar":"1.1644","FoodName":"Lentils with Garlic and Onions","Milk":false,"Breakfast":0,"Peanuts":false,"Saturated fat":"1.1056","Total fat":"11.3973","Calories":"179.8277","Cholesterol":"0.0","Protein":"5.9694","Lunch":1,"Station":"No Wheat No Meat","Tree_nuts":false,"Calories from fat":"99","Sodium":"396.0401","Total Carbohydrate":"14.5503","Veg":true,"Soy":false,"Wheat":false}]};

                var pathParam = $scope.searchString.split(' ').join('%20');
                $http({
                    url: "https://cs307.cs.purdue.edu:8443/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/search/" + pathParam,
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
                        $scope.searchResult = data;
                    }
                }).error(function (data, status, headers, config) {
                });
            }
        });

    }]).controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log, $location, $cookies) {

        $scope.getUserID = function () {
            var response = $cookies.get('user');
            if (response.data != undefined) {
                var userID = response.data.user.UserID;
                return userID;
            } else {
                return response;
            }
        }

        $scope.logout =  function() {
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
