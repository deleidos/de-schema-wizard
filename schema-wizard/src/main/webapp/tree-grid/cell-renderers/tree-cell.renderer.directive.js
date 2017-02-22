(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    var previousDetailsNodeId = null;

    schemaWizardApp.directive('treecell', function () {
        var linker = function (scope, element, attrs) {
            //console.group("treecell");
            scope.cbMethod = function ($event, callback, parms) {
                var node = parms.node;
                var thisNode = scope.$eval(node);
                if (callback == "showInDetails") {
                    // only act upon leaf nodes which have not been manually entered
                    if (thisNode && thisNode.manualEntry !== true &&
                        thisNode.children && thisNode.children.length == 0) {
                        try {
                            if (previousDetailsNodeId) document.getElementById(previousDetailsNodeId).style.backgroundColor = "transparent";
                        } catch (e) {}
                        scope.callbackMethod()($event, callback, { 'node': thisNode });
                        var thisNodeId = "treecell-" + thisNode.id;
                        document.getElementById(thisNodeId).style.backgroundColor = "gold";
                        previousDetailsNodeId = thisNodeId;
                        return angular.toJson(thisNode);
                    }
                }
            };

            var node = scope.$eval(attrs.node);
            if (node && ((node.children && node.children.length > 0) || node.manualEntry == true)) {
                scope.cursor = "not-allowed";
            } else {
                scope.cursor = "pointer";
            }
            console.groupEnd();
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/tree-cell.template.html",
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
    }); // treecell
})();
