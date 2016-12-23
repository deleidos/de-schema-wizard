(function () {

    var treeTableApp = angular.module('treeTableApp');

    treeTableApp.directive('paragraph', function () {
        var linker = function (scope, element, attrs) {
            scope.cbMethod = function ($event, callback, parms) {
                scope.callbackMethod()($event, callback, parms);
            }
        };
        return {
            restrict: "E",
            templateUrl: "paragraph.html",
            transclude: true,
            replace: true,
            scope: {
                nodeLabel1: '@',
                nodeLabel2: '@',
                callbackMethod: "&"
            },
            link: linker
        }
    });
})();
