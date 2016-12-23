(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('schemaaliasestablecell',['numberFilter', function (numberFilter) {
        var linker = function (scope, element, attrs) {
            console.log("schemaaliasestablecell");

            scope.data = scope.$eval(decodeURI(attrs['otherTableData']));

            var node = scope.$eval(attrs.node);

            if (scope.data.sProfile[node.path] && scope.data.sProfile[node.path].aliasNames) {
                scope.aliases = [];
                for (var i = 0, len = scope.data.sProfile[node.path].aliasNames.length; i < len; i++) {
                    if (scope.data.sProfile[node.path].aliasNames[i]['alias-name'] != node.path &&
                        scope.aliases.indexOf(scope.data.sProfile[node.path].aliasNames[i]['alias-name']) < 0) {
                        scope.aliases.push(scope.data.sProfile[node.path].aliasNames[i]['alias-name']);
                    }
                }
                scope.firstAlias = scope.aliases[0];
            }
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/schema-aliases-table-cell.template.html",
            transclude: true,
            replace: true,
            scope: {
                node: '@',
                nodeId: '@'
            },
            link: linker
        }
    }]); // schemaaliasestablecell
})();
