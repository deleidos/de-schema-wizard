(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    var previousDetailsNodeId = null;

    schemaWizardApp.directive('finalizeproperty', [ '$timeout', '$confirm', function ($timeout, $confirm) {
        var linker = function (scope, element, attrs) {
            //console.group("treecell");
            var thisNode;

            var openRenameEdit = function () {
                scope.renameProperty = true;
                $timeout(function () {
                    document.getElementById('treecell-' + thisNode.id + '-renameproperty').style.backgroundColor = "pink";
                    document.getElementById('treecell-' + thisNode.id + '-renameproperty').focus();
                }, 500);
            }; // openRenameEdit

            scope.addProperty = false;

            scope.cbMethod = function ($event, callback, parms) {
                var node = parms.node;
                thisNode = scope.$eval(node);
                console.log("thisNode");
                console.log( thisNode);

                if (callback == "showInDetails") {
                    // only act upon leaf nodes
                    if (thisNode && thisNode.children && thisNode.children.length == 0) {
                        try {
                            if (previousDetailsNodeId) document.getElementById(previousDetailsNodeId).style.backgroundColor = "transparent";
                        } catch (e) {
                        }
                        scope.callbackMethod()($event, callback, {'node': thisNode});
                        var thisNodeId = "treecell-" + thisNode.id;
                        document.getElementById(thisNodeId).style.backgroundColor = "gold";
                        previousDetailsNodeId = thisNodeId;
                        return angular.toJson(thisNode);
                    }
                } else if (callback == "changeNodeLabel") {
                    scope.addProperty = false;
                    //console.log(angular.toJson(parms));
                    scope.callbackMethod()($event, callback, parms);
                } else if (callback == "prepRenameProperty") {
                    if (document.getElementById("wizard-finalize-schema-back").disabled) {
                        openRenameEdit();
                    } else {
                        $confirm({
                                title: 'Confirm Rename Property',
                                text: "The 'Back' button will be disabled if a field \n" +
                                "name is changed. Press 'OK' to confirm.",
                                ok: 'OK',
                                cancel: 'Cancel'
                            }
                        ).then(function () {
                            document.getElementById("wizard-finalize-schema-back").disabled = true;
                            openRenameEdit();
                        }, function (error) {});
                    };
                } else if (callback == "renameProperty") {
                    document.getElementById('treecell-' + thisNode.id + '-renameproperty').style.backgroundColor = "transparent";
                    scope.renameProperty = false;
                    console.log("callback == 'renameProperty'");
                    console.log(thisNode.path);
                    console.log(thisNode.field);
                    console.log(parms.newproperty);
                    scope.callbackMethod()($event, callback, { 'path': thisNode.path, 'field': thisNode.field, 'newproperty': parms.newproperty });
                }
            };

            if (scope.nodeLabel1 == "new-property") scope.addProperty = true;
            var node = scope.$eval(attrs.node);
            if (node && node.children && node.children.length > 0) {
                scope.cursor = "not-allowed";
            } else {
                scope.cursor = "pointer";
            }
            console.groupEnd();
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/finalize-property.template.html",
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
    }]); // treecell
})();
