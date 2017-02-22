(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('schemaproperty', function (Utilities) {
        var linker = function (scope, element, attrs) {
            console.log("schemaproperty");
            var node = scope.$eval(attrs.node);
            //console.log(node);
            scope.path = node.field;
            //console.log("scope.path");
            //console.log(scope.path);
            if (node.children && node.children.length > 0) scope.hasChildren = true;
            if (node.existingSchemaProperty === true) scope.existingSchemaProperty = true;

            scope.cbMethod = function ($event, callback, parms) {
                if (callback == "showInDetails1" &&
                    (node.existingSchemaProperty !== true || (node.children && node.children.length > 0))) return;
                scope.callbackMethod()($event, callback, parms);
                if (callback == "showInDetails1") {
                    document.getElementById('div' + scope.nodeId).style.backgroundColor = 'gold';
                    var previousShownInDetails1Id = 'div' + scope.nodeId;
                    Utilities.setMatchingShownInDetails1Id(previousShownInDetails1Id);
                    //console.log("previousShownInDetails1Id: " + previousShownInDetails1Id);
                }
                scope.editField = false;
            };

            scope.getStyle = function () {
                var style = {};
                if (node.existingSchemaProperty === true) {
                    style['color'] = 'blue';
                } else {
                    style['cursor'] = 'not-allowed';
                }
                if (node.children && node.children.length > 0) style['cursor'] = 'not-allowed';
                return style;
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

            if (attrs.data) {
                scope.data = scope.$eval(decodeURI(attrs.data));
                //console.log("scope.data" + "\t" + scope.nodeLabel1);
                //console.log(scope.data);
                scope.properties = scope.data;
                scope.property = scope.path;
            }
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/schema-property.template.html",
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
