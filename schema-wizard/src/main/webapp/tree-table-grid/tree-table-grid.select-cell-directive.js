(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('selectCell', function () {
        var linker = function (scope, element, attrs) {
            scope.cbMethod = function ($event, callback, parms) {
                scope.callbackMethod()($event, callback, parms);
            };
            scope.data = scope.$eval(decodeURI(attrs.data));
        };
        return {
            restrict: "E",
            templateUrl: "tree-table-grid/templates/select-cell.html",
            transclude: true,
            replace: true,
            scope: {
                nodeLabel: '@',
                callbackMethod: "&"
            },
            link: linker
        }
    }); // selectCell
})();
