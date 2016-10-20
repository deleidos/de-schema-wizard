(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('treeTableController', ['$scope', '$route', '$window', '$timeout', 'Globals', 'Utilities',
		function($scope, $route, $window, $timeout, Globals, Utilities) {

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

            $scope.showBrowseMask();

            $scope.$on("setCurrentSample", function(event, args) {
                console.log("tree-table-grid-controller::onSetCurrentSample");
                $scope.currentSample = args.sample;
                $scope.treeTable.data = args.sample.dsStructuredProfile;
                console.log("$scope.treeTable.data");
                console.log($scope.treeTable.data);
                $scope.dataSize = angular.toJson($scope.treeTable.data).length;
                console.log($scope.dataSize);

                // NOTE: Directives must be named with all lowercase characters and no punctuation.
                //       This limitation arises because AngularJS requires hyphenated names as attributes
                //       and camel case for the directive name. This workaround eliminates this problem.
                // NOTE: If a property is an array element use a dotted notation such as a.0.b not a[0].b
                // TODO: when changing between single and double property columns, the nested directives are not
                //       reinitializing property1 and property2 (label1 and label2 in nested directives)
                //       need to figure out how to get them to update so always use double property columns for now
                //       hence "true || " which is faster anyway
                if (true || $scope.currentSample.dsContainsStructuredData) {
                    $scope.columns = [
                        { 'property1': 'field', 'name': 'Field', 'callback': 'doCallBack', 'complexCell': 'true' },
                        { 'property1': 'mainType',             'property2': 'detailType',   'name': 'Main / Detail Type' },
                        { 'property1': 'detailMin',            'property2': 'detailMax',    'name': 'Min / Max' },
                        { 'property1': 'detailAvg',            'property2': 'detailStdDev', 'name': 'Avg / Std Dev' },
                        { 'property1': 'detailNumDistinct',    'property2': 'presence',     'name': 'Distinct / Presence' },
                        { 'property1': 'interpretation.iName',                              'name': 'Interpretation' }
                    ];
                    $scope.columnSizingFactor = 7;
                } else {
                    $scope.columns = [
                        { 'property1': 'field', 'name': 'Field', 'callback': 'doCallBack', 'complexCell': 'true' },
                        { 'property1': 'mainType',             'name': 'Main Type' },
                        { 'property1': 'detailType',           'name': 'Detail Type' },
                        { 'property1': 'detailMin',            'name': 'Minimum' },
                        { 'property1': 'detailMax',            'name': 'Maximum' },
                        { 'property1': 'detailAvg',            'name': 'Average' },
                        { 'property1': 'detailStdDev',         'name': 'Std Dev' },
                        { 'property1': 'detailNumDistinct',    'name': 'Distinct' },
                        { 'property1': 'presence',             'name': 'Presence' },
                        { 'property1': 'interpretation.iName', 'name': 'Interpretation'}
                    ];
                    $scope.columnSizingFactor = 11;
                }
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
                        $scope.currentSample.dsProfile[node.path],
                        node.field);
                    $scope.detailPanel = Globals.getDetailModels().detailPanels.panel1;
                }; // showInDetails

                $timeout(function () {
                    $scope.fadeBrowseMask();
                    try {
                        // show first field in details after data loads
                        $scope.showInDetails({"node": $scope.firstLeafNode});
                        document.getElementById($scope.firstLeafNodeId).style.backgroundColor = "gold";
                    } catch (e) { console.log(e.toString()); }

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
                        1000)
                }, Math.round($scope.dataSize / 100));
            }); // onsetCurrentSample

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
                    (parseInt(document.getElementById("sampleDetailsPanelBody").style.height.slice(0, -2)) - 30) +
                    "px; ";
                var overflowX = "overflow-x: hidden; ";
                var overflowY = (last ? "overflow-y: scroll;" : "overflow-y: hidden;");
                var retVal = height + overflowX + overflowY;
                return retVal
            }; // getColumnStyle

            $scope.scrollTreeTable = function(scrollAmount) {
                var sTop = document.getElementById('colN').scrollTop -= scrollAmount;
                for (var i = 0; i < $scope.columns.length - 1; i++) {
                    document.getElementById('col' + i).scrollTop = sTop;
                }
            }; // scrollTreeTable

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
        }
	]); // treeTableController
})();


