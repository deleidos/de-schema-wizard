(function () {

    var treeTableApp = angular.module('treeTableApp');

    treeTableApp.directive('selection', function () {
        var linker = function (scope, element, attrs) {
            scope.cbMethod = function ($event, callback, parms) {
                scope.callbackMethod()($event, callback, parms);
            }
            scope.data = scope.$eval(decodeURI(attrs.data));
        };
        return {
            restrict: "E",
            templateUrl: "selection.html",
            transclude: true,
            replace: true,
            scope: {
                nodeLabel1: '@',
                nodeLabel2: '@',
                callbackMethod: '&'
            },
            link: linker
        }
    });
})();
