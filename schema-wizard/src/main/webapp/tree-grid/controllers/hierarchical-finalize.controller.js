(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('hierarchicalFinalizeCtrl', [ '$scope', '$window', '$timeout',
                               '$confirm', 'Globals', 'Utilities', 'MaskUtilities',
        function($scope, $window, $timeout, $confirm, Globals, Utilities, MaskUtilities) {
            console.group("hierarchicalFinalizeController");
            $scope.treeTable = {};
            $scope.firstLeafNodeId = null;

            $scope.currentSchema = Utilities.getSchema();
            console.log($scope.currentSchema);

            $scope.treeTable.data = $scope.currentSchema.sStructuredProfile;
            // wrap model.treeTable.data with a root node
            /*
             $scope.model.treeTable.data =
             [{
             "field": "/",
             "displayName": "/",
             "strucType": "object",
             "children": $scope.model.treeTable.data
             }];
            */
            //console.info("$scope.model.treeTable.data");
            //console.log($scope.model.treeTable.data);
            console.log("$scope.treeTable.data");
            console.log($scope.treeTable.data);
            $scope.dataSize = angular.toJson($scope.treeTable.data).length;
            console.log($scope.dataSize);

            $scope.columns = [
                { 'property1': 'field', 'name': 'Property', 'width': '20', 'callback': 'doCallBack', 'complexCell': 'true',
                    'tree': { 'directive': 'finalizeproperty' } },
                { 'property1': 'aliasNames', 'name': 'Aliases', 'width': '240',
                    'table': { 'directive': 'schemaaliasestablecell', 'data': $scope.currentSchema } },
                { 'property1': 'mainType',             'property2': 'detailType',   'name': 'Main / Detail Type', 'width': '140',
                    'table': { 'directive': 'schematablecell', 'data': $scope.currentSchema } },
                { 'property1': 'detailMin',            'property2': 'detailMax',    'name': 'Min / Max', 'width': '220',
                    'table': { 'directive': 'schematablecell', 'data': $scope.currentSchema } },
                { 'property1': 'detailAvg',            'property2': 'detailStdDev', 'name': 'Avg / Std Dev', 'width': '190',
                    'table': { 'directive': 'schematablecell', 'data': $scope.currentSchema } },
                { 'property1': 'detailNumDistinct',    'property2': 'presence',     'name': 'Distinct / Presence', 'width': '115',
                    'table': { 'directive': 'schematablecell', 'data': $scope.currentSchema } },
                { 'property1': 'interpretations.selectedOption.iName',              'name': 'Interpretation', 'width': '140',
                    'table': { 'directive': 'schematablecell', 'data': $scope.currentSchema } }
            ];
            // data objects must be passed through the layers of directives as encoded JSON strings
            // decoding and restoration as javascript objects must occur in the directive
            for (var i = 0; i < $scope.columns.length; i++) {
                if ($scope.columns[i].tree && $scope.columns[i].tree.data) {
                    $scope.columns[i].tree.data = encodeURI(angular.toJson($scope.columns[i].tree.data));
                }
                if ($scope.columns[i].table && $scope.columns[i].table.data) {
                    $scope.columns[i].table.data = encodeURI(angular.toJson($scope.columns[i].table.data));
                }
            }

            // Chrome & IE
            angular.element($window).bind('mousewheel', function (event) {
                // check whether the scroll event is for the tree-table-grid
                if (event.target.offsetParent.className.indexOf('panel-group') >= 0) {
                    $scope.scrollTreeTable(event.originalEvent.wheelDelta / 4);
                    event.preventDefault();
                    event.stopImmediatePropagation();
                }
            });

            // Firefox
            angular.element($window).bind('DOMMouseScroll', function (event) {
                // check whether the scroll event is for the tree-table-grid
                if (event.target.offsetParent.className.indexOf('panel-group') >= 0) {
                    $scope.scrollTreeTable(event.originalEvent.detail * -10);
                    event.preventDefault();
                    event.stopImmediatePropagation();
                }
            });

            // wait for grid to initialize then add listener for scrolling
            $timeout(function () { document.getElementById('colN').addEventListener('scroll', function (event) {
                    //console.log(event);
                    $scope.scrollTreeTable(0);
                    event.preventDefault();
                    event.stopImmediatePropagation();
                })},
                1000);

            $scope.getColumnId = function(index, last) {
                if (last) {
                    return 'colN';
                } else {
                    return 'col' + index;
                }
            }; // getColumnId

            $scope.getColumnStyle = function(last) {
                var height =
                    "height: " +
                    (parseInt(document.getElementById("wizardFinalizeSchemaPanelBody").style.height.slice(0, -2)) - 30) +
                    "px; ";
                var overflowX = "overflow-x: hidden; ";
                var overflowY = (last ? "overflow-y: scroll;" : "overflow-y: hidden;");
                var retVal = height + overflowX + overflowY;
                return retVal
            }; // getColumnStyle

            $scope.scrollTreeTable = function(scrollAmount) {
                var sTop = document.getElementById('colN').scrollTop -= scrollAmount;
                for (var i = 0; i < $scope.columns.length - 1; i++) {
                    try {
                        document.getElementById('col' + i).scrollTop = sTop;
                    } catch (e) { /* do nothing */ }
                }
            }; // scrollTreeTable

            $scope.findFirstLeafNode = function (data) {
                var leaf = null;
                var find = function (node) {
                    if (node.children.length == 0) {
                        return node;
                    } else {
                        for (var j = 0, len = node.children.length; j < len; j++) {
                            leaf = find(node.children[j]);
                            if (leaf) return leaf;
                        }
                    }
                };
                for (var i = 0, len = data.length; i < len; i++) {
                    leaf = find(data[i]);
                    if (leaf) return leaf;
                }
            };
            $scope.firstLeafNode = $scope.findFirstLeafNode($scope.treeTable.data);
            $scope.firstLeafNodeId = "treecell-" + $scope.firstLeafNode.id;
            console.log("$scope.firstLeafNodeId");
            console.log($scope.firstLeafNodeId);

            $scope.showInDetails = function (parms) {
                console.log("showInDetails node: " + angular.toJson(parms));
                var node = parms.node;
                console.log("Show '" + node.path + "' in details pane");
                // if the first leaf node is still highlighted then remove the hightlight here where it's in scope;
                // otherwise this node would have to be passed into the other directives; a small price to pay!
                try {
                    document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "transparent";
                } catch (e) { console.log(e.toString()); }
                Utilities.showInGenericDetails(
                    Globals,
                    $scope.currentSchema.sProfile[node.path],
                    node.field);
                $scope.detailPanel = Globals.getDetailModels().detailPanels.panel1;
            }; // showInDetails

            $timeout(function () {
                // show first field in details after data loads
                $scope.showInDetails({"node": $scope.firstLeafNode});
                try {
                    document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "gold";
                } catch (e) {}
                MaskUtilities.fadeBrowseMask();
            }, Math.round($scope.dataSize / 100) + 300);
            // try a second time to highlight this cell, it's not consistently getting set on first try
            $timeout(function () {
                // show first field in details after data loads
                $scope.showInDetails({"node": $scope.firstLeafNode});
                try {
                    document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "gold";
                } catch (e) {}
            }, Math.round($scope.dataSize / 100) + 800);

            $scope.renameProperty = function (parms) {
                console.group("finishRenameProperty");
                //console.log(angular.toJson(parms));
                var parmsObj = eval(parms);
                
                if (parmsObj.newproperty === parmsObj.field) {
                    return; // don't do anything if the name doesn't change
                }
                
                var dotIndex = parmsObj.path.lastIndexOf('.');
                var newPath = parmsObj.newproperty;
                if (dotIndex > -1) {
                    newPath = parmsObj.path.slice(0, dotIndex) + '.' + parmsObj.newproperty;
                }
                
                // recursively check tree nodes to find matching node
                var findMatchingPath = function (dataList, pathToMatch) {
                    for (var i = 0; i < dataList.length; i++) {
                        var treeNode = dataList[i];
                        if (treeNode.path === pathToMatch) {
                            return treeNode;
                        } 
                        
                        if (treeNode.children.length > 0) {
                            var matchingNode = findMatchingPath(treeNode.children, pathToMatch);
                            if (matchingNode != null) {
                                return matchingNode;
                            }
                        } 
                    }
                    return null;
                }
                
                var matchingNode = findMatchingPath($scope.treeTable.data, parmsObj.path);
                if (matchingNode == null) {
                    console.log("Could not find tree node with path " + parmsObj.path + ".");
                    // show error modal?
                    return;
                } else {
                    matchingNode.field = parmsObj.newproperty;
                    matchingNode.path = newPath;

                    // don't update sProfile until we know the associated treeNode
                    $scope.currentSchema.sProfile[newPath] = angular.copy($scope.currentSchema.sProfile[parmsObj.path]);
                    delete $scope.currentSchema.sProfile[parmsObj.path];
                    console.log($scope.currentSchema);
                    console.groupEnd();
                }
                
            }; // renameProperty

            $scope.addNewProperty = function () {
                $scope.addNewProp = function () {
                    document.getElementById("wizard-finalize-schema-back").disabled = true;

                    $scope.currentSchema.sStructuredProfile.unshift(
                        { "field": "new-property", "displayName": "new-property" }
                    );

                    $timeout( function () {
                        document.getElementById('newproperty').style.backgroundColor = "pink";
                        document.getElementById('newproperty').focus();
                    }, 500);

                };
                if (document.getElementById("wizard-finalize-schema-back").disabled) {
                    $scope.addNewProp();
                } else {
                    $confirm({
                            title: 'Confirm Add New Property',
                            text: "The 'Back' button will be disabled if a field \n" +
                            "name is changed. Press 'OK' to confirm.",
                            ok: 'OK',
                            cancel: 'Cancel'
                        }
                    ).then(function () {
                        $scope.addNewProp();
                    }, function (error) {
                        /* do nothing; don't show an explanative message when the user knows what they just clicked! */
                    });
                }
            }; // addNewProperty

            $scope.changeNodeLabel = function (parms) {
                console.group("changeNodeLabel");
                console.log(angular.toJson(parms));
                $scope.currentSchema.sProfile[parms['newproperty']] = {};
                $scope.currentSchema.sProfile[parms['newproperty']]['manually-added'] = true;
                $scope.currentSchema.sProfile[parms['newproperty']]['main-type'] = 'string';
                $scope.currentSchema.sProfile[parms['newproperty']]['presence'] = -1;
                $scope.currentSchema.sProfile[parms['newproperty']]['detail'] = {};
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['detail-type'] = 'phrase';
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['min-length'] = -1;
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['max-length'] = -1;
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['average-length'] = -1;
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['std-dev-length'] = -1;
                $scope.currentSchema.sProfile[parms['newproperty']]['detail']['num-distinct-values'] = 0;
                $scope.currentSchema.sProfile[parms['newproperty']]['interpretations'] = {};
                $scope.currentSchema.sProfile[parms['newproperty']]['interpretations']['selectedOption'] = {};
                $scope.currentSchema.sProfile[parms['newproperty']]['interpretations']['selectedOption']['iName'] = "Unknown";
                console.log($scope.currentSchema);
                console.groupEnd();
            }; // changeNodeLabel

            $scope.externalTreeMethod = function ($event, parms) {
                console.log("externalTreeMethod " + angular.toJson(parms));
                console.log($event);
            }; // externalTreeMethod

            $scope.externalTableMethod = function ($event, parms) {
                console.log("externalTableMethod " + angular.toJson(parms));
                console.log($event);
            }; // externalTableMethod

            $scope.externalSelectMethod = function ($event, parms) {
                console.log("externalSelectMethod selectedItem: " + angular.toJson(parms));
                console.log($event);
            }; // externalSelectMethod

            $scope.doCallBack = function ($event, callback, parms) {
                console.log("doCallBack callback: " + callback);
                console.log("doCallBack parms: " + angular.toJson(parms));
                switch(callback) {
                    case "showInDetails": $scope.showInDetails(parms); break;
                    case "externalTreeMethod": $scope.externalTreeMethod($event, parms); break;
                    case "externalTableMethod": $scope.externalTableMethod($event, parms); break;
                    case "externalSelectMethod": $scope.externalSelectMethod($event, parms); break;
                    case "changeNodeLabel": $scope.changeNodeLabel(parms); break;
                    case "renameProperty": $scope.renameProperty(parms); break;
                }
            }; // doCallBack

        }]); // hierarchicalFinalizeCtrl
})();
