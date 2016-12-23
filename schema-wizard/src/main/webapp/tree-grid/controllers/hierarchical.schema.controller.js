(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('hierarchicalSchemaDetailsCtrl',
        function($rootScope, $scope, $resource, $location, $route, $routeParams, $window, $timeout, Utilities, Globals, schemaData, $confirm, statusCodesFactory) {

            schemaData.$promise.then(function () {
                console.group("hierarchicalSchemaDetailsCtrl");
                $scope.treeTable = {};
                $scope.firstLeafNodeId = null;

                $scope.showBrowseMask = function () {
                    $scope.browseMaskOpacity = 0.8;
                    document.getElementById('sampleMask').style.opacity = $scope.browseMaskOpacity;
                    document.getElementById("sampleMask").style.display = "block";
                }; // showBrowseMask

                $scope.hideBrowseMask = function () {
                    document.getElementById("sampleMask").style.display = "none";
                }; // hideBrowseMask

                $scope.fadeBrowseMask = function () {
                    $scope.browseMaskOpacity -= 0.1;
                    if ($scope.browseMaskOpacity < 0) {
                        document.getElementById('sampleMask').style.display = "none";
                    } else {
                        document.getElementById('sampleMask').style.opacity =
                            $scope.browseMaskOpacity;
                        $timeout($scope.fadeBrowseMask, 50);
                    }
                }; // fadeBrowseMask

//TODO: needs to be started earlier                $scope.showBrowseMask();

                $scope.currentSchema = schemaData;
                console.log($scope.currentSchema);
                // set schema for use in modify existing schema workflow if invoked
                Utilities.setSchema($scope.currentSchema);

                $scope.treeTable.data = $scope.currentSchema.sStructuredProfile;
                console.log("$scope.treeTable.data");
                console.log($scope.treeTable.data);
                $scope.dataSize = angular.toJson($scope.treeTable.data).length;
                console.log($scope.dataSize);

                $scope.columns = [
                    { 'property1': 'field', 'name': 'Property', 'width': '20', 'callback': 'doCallBack', 'complexCell': 'true' },
                    { 'property1': 'aliasNames', 'name': 'Aliases', 'width': '240',
                        'table': { 'directive': 'schemaaliasestablecell', 'data': $scope.currentSchema }},
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
                        (parseInt(document.getElementById("schemaDetailsPanelBody").style.height.slice(0, -2)) - 30) +
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
                        if (node.children.length == 0 &&
                            $scope.currentSchema.sProfile.hasOwnProperty(node.field) &&
                            $scope.currentSchema.sProfile[node.field].presence != -1) {
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
                    $scope.fadeBrowseMask();
                    // show first field in details after data loads
                    $scope.showInDetails({"node": $scope.firstLeafNode});
                    try {
                        document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "gold";
                    } catch (e) {}
                }, 500);
                // try a second time to highlight this cell, it's not consistently getting set on first try
                $timeout(function () {
                    try {
                        document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "gold";
                    } catch (e) {}
                }, 1000);

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
                    }
                }; // doCallBack
            }, function (error) {
                console.log(error);
                statusCodesFactory.get().$promise.then(function (response) {
                    $confirm(
                        {
                            title: response.gettingSchemaDataFailed.title,
                            text: response.gettingSchemaDataFailed.message +
                            " (" + error.status + ")",
                            ok: 'OK'
                        },
                        {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                })
            }
        );
    }); // hierarchicalSchemaDetailsCtrl
})();
