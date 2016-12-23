(function () {

    var treeTableApp = angular.module('treeTableApp');

    treeTableApp.directive('matchingproperty', function (Utils) {
        var linker = function (scope, element, attrs) {
            scope.cbMethod = function ($event, callback, parms) {
//TODO: add return to callback
                return scope.callbackMethod()($event, callback, parms);
            };

            if (attrs.data) {
                var dsIndex = scope.$eval(decodeURI(attrs.data));
                //console.log("matchingproperty dsIndex: " + dsIndex);
                scope.ds = Utils.getDataSample(dsIndex);
                //console.log("matchingproperty Utils.getDataSample(dsIndex)");
                //console.log(scope.ds);

                scope.property = scope.nodeLabel1;
            }
        };
        return {
            restrict: "E",
            templateUrl: "matching-property.template.html",
            transclude: true,
            replace: true,
            scope: {
                nodeLabel1: '@',
                nodeLabel2: '@',
                cellMinWidth: '@',
                cellMinHeight: '@',
                callbackMethod: '&'
            },
            link: linker
        }
    });
})();
