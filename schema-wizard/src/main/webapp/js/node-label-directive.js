(function () {

    var treeTableApp = angular.module('treeTableApp');

    treeTableApp.directive('nodelabel', function () {
        return {
            restrict: "E",
            template: "<span>{{(nodeLabel == \"~\" ? \"&nbsp;\" : nodeLabel)}}</span>",
            transclude: true,
            replace: true,
            scope: {
                nodeLabel: '@'
            }
        }
    });
})();
