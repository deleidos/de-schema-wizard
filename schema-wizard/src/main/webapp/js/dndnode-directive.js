(function () {

    var treeTableApp = angular.module('treeTableApp');

    treeTableApp.directive('dndnode', function () {
        var linker = function (scope, element, attrs) {
            scope.cbMethod = function ($event, callback, parms) {
                //console.log("$event");
                //console.log($event);
                scope.callbackMethod()($event, callback, parms);
                scope.editField = false;
            };
            scope.dndMethod = function (callback, parms) {
                //console.log("dndMethod");
                //console.log("callback: " + callback);
                //console.log(angular.toJson(parms));
                //console.log("event");
                //console.log(event);
                var retcode = scope.callbackMethod()(event, callback, parms);
                console.log("retcode");
                console.log(retcode);
                if (callback == "dragDrop" && event.ctrlKey && retcode == true) {
                    scope.editField = true;
                }
            };
            if (attrs.nodeLabel1 == "???") scope.editField = true;
            var node = scope.$eval(attrs.node);
            if (node.children && node.children.length > 0) scope.hasChildren = true;
        };
        return {
            restrict: "E",
            templateUrl: "dndnode.html",
            transclude: true,
            replace: true,
            scope: {
                node: '@',
                nodeId: '@',
                nodeLabel1: '@',
                nodeLabel2: '@',
                callbackMethod: "&"
            },
            link: linker
        }
    });
})();
