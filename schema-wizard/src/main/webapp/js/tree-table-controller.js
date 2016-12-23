    angular.module('treeTableControllers', [])
        .controller('treeTableController',
		function($scope, model, $window, $parse, $timeout) {

            $scope.treeTable = model;

            // NOTE: Directives must be named with all lowercase characters and no punctuation.
            //       This limitation arises because AngularJS requires hyphenated names as attributes
            //       and camel case for the directive name. This workaround eliminates this problem.
/*
            $scope.columns = [
                { 'complexCell': 'true',
                  'property1': 'field',
                  'property2': 'strucType',
                  'name': 'Field',
                  'callback': 'doCallBack',
                  'tree': {
                      'directive': 'dndnode'
                  }
                },
                { 'property1': 'mainType',
/!*
                  'property2': 'detailType',
                  'name': 'Main Type / Detail Type',
*!/
                  'name': 'Main Type',
                  'table': {
                      'directive': 'paragraph'
                  }
                },
                { 'property1': 'detailType',
                  'name': 'Detail Type',
                  'table': {
                      'directive': 'paragraph'
                  }
                },
                { 'complexCell': 'true',
                  'property1': 'other',
                  'name': 'Other',
                  'callback': 'doCallBack',
                  'table': {
                      'directive': 'selection',
                      'data': [
                          { id: 1, name: 'alpha' },
                          { id: 2, name: 'beta' },
                          { id: 3, name: 'gamma' }
                      ]
                  }
                }
            ];
*/
            $scope.columns = [
                { 'complexCell': 'true',
                    'property1': 'field',
                    'property2': 'strucType',
                    'name': 'Field',
                    'callback': 'doCallBack',
                    'tree': {
                        'directive': 'dndnode'
                    }
                },
                { 'property1': 'ds1',
                  'name': 'ds1',
                  'table': {
                      'directive': 'paragraph'
                  }
                },
                { 'property1': 'ds2',
                  'name': 'ds2',
                  'table': {
                      'directive': 'paragraph'
                  }
                },
                { 'property1': 'ds3',
                    'name': 'ds3',
                    'table': {
                        'directive': 'paragraph'
                    }
                }
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

            $scope.getColumnId = function(index, last) {
                //console.log(index + "\t" + last);
                if (last) {
                    return 'colN';
                } else {
                    return 'col' + index;
                }
            }; // getColumnId

            $scope.scrollTreeTable = function(scrollAmount) {
                var sTop = document.getElementById('colN').scrollTop -= scrollAmount;
                for (var i = 0; i < $scope.columns.length - 1; i++) {
                    document.getElementById('col' + i).scrollTop = sTop;
                }
            }; // scrollTreeTable

            // Chrome & IE
            angular.element($window).bind('mousewheel', function (event) {
                //console.log(event.originalEvent.wheelDelta);
                $scope.scrollTreeTable(event.originalEvent.wheelDelta / 4);
                event.preventDefault();
                event.stopImmediatePropagation();
            });

            // Firefox
            angular.element($window).bind('DOMMouseScroll', function (event) {
                //console.log(event.originalEvent.detail);
                $scope.scrollTreeTable(event.originalEvent.detail * -10);
                event.preventDefault();
                event.stopImmediatePropagation();
            });

            // wait for grid to initialize then add listener for scrolling
            $timeout(function () { document.getElementById('colN').addEventListener('scroll', function (event) {
                    //console.log(event);
                    $scope.scrollTreeTable(0)
                    event.preventDefault();
                    event.stopImmediatePropagation();
                })},
                1000);

            $scope.rebuildModel = function () {
                $scope.treeTable.lookupTable = [];
                var idNum = 0;
                var traverse = function (subtree, path, parentId, childNum) {
                    subtree.id = ++idNum;
                    subtree.path = path;
                    subtree.parentId = parentId;
                    subtree.childNum = childNum;
                    $scope.treeTable.lookupTable.push(subtree);
                    if (subtree.hasOwnProperty('children')) {
                        parentId = idNum;
                        for (var i = 0, len = subtree.children.length; i < len; i++) {
                            traverse(subtree.children[i], subtree.path + '.children[' + childNum + ']', parentId, i);
                        }
                    }
                };
                $scope.treeTable.data[0].id = 0;
                $scope.treeTable.data[0].path = "$scope.treeTable.data[0]";
                $scope.treeTable.data[0].parentId = -1;
                $scope.treeTable.data[0].childNum = -1;
                $scope.treeTable.lookupTable.push($scope.treeTable.data[0]);
                for (var i = 0, len = $scope.treeTable.data[0].children.length; i < len; i++) {
                    traverse($scope.treeTable.data[0].children[i], "$scope.treeTable.data[0]", 0, i);
                }
                //$scope.showLookupTable();
            }; // rebuildModel
            $timeout(function () {
                console.log($scope.treeTable.data[0].children)
            }, 500);
            $timeout($scope.rebuildModel, $scope.treeTable.data, 600);

            $scope.deleteFieldInModel = function (deletionIndex, rebuildAfter) {
                console.log("deleteFieldInModel: " + deletionIndex);
                eval($scope.treeTable.lookupTable[deletionIndex].path).children
                    .splice(eval($scope.treeTable.lookupTable[deletionIndex].childNum), 1);
                if (rebuildAfter) $scope.rebuildModel();
            }; // deleteFieldInModel

            $scope.showLookupTable = function () {
                console.log("$scope.treeTable.data");
                console.log($scope.treeTable.data);
                console.log("$scope.treeTable.lookupTable");
                console.log("field      id  pId cId  path");
                for (var i = 0, len = $scope.treeTable.lookupTable.length; i < len; i++) {
                    console.log(($scope.treeTable.lookupTable[i].field + "          ").substr(0, 10) + "\t" +
                        $scope.treeTable.lookupTable[i].id + "\t" +
                        $scope.treeTable.lookupTable[i].parentId + "\t" +
                        $scope.treeTable.lookupTable[i].childNum + "\t" +
                        $scope.treeTable.lookupTable[i].path + "\t" +
                        "");
                }
            }; // showLookupTable

            $scope.dragStart = function ($event, parms) {
                console.log("dragStart " + angular.toJson(parms));
                //console.log("$event");
                //console.log($event);
                $scope.draggedNodeIndex = parms.index;
            }; // dragStart

            $scope.dragDrop = function ($event, parms) {
                console.log("dragDrop " + angular.toJson(parms));
                //console.log("$event");
                //console.log($event);
                console.log("$event.shiftKey: " + $event.shiftKey);
                console.log("$event.ctrlKey: " + $event.ctrlKey);

                var retcode = false;

                // don't allow the root node to be dropped
                if ($scope.draggedNodeIndex == 0) return retcode;

                $scope.droppedOnNodeIndex = parms.index;
                console.log("$scope.droppedOnNodeIndex: " + $scope.droppedOnNodeIndex);

                // don't allow the node to be dropped on itself
                if ($scope.draggedNodeIndex == $scope.droppedOnNodeIndex) return retcode;

                var mergeNodes = function (droppedOnNode, draggedNodeCopy) {
                    //$scope.findFieldInModel(draggedNodeCopy.id);
                    //$scope.findFieldInModel(droppedOnNodeCopy.id);
                    //console.log("droppedOnNode.children[childNum].field: " +
                    //            droppedOnNode.children[childNum].field);
                    droppedOnNode.children[childNum].field = draggedNodeCopy.field + " & " + droppedOnNode.children[childNum].field;
                    for (var i = 0, len = draggedNodeCopy.children.length; i < len; i++) {
                        droppedOnNode.children[childNum].children.push(angular.copy(draggedNodeCopy.children[i]));
                    };
                };

                var draggedNodeCopy = angular.copy($scope.treeTable.lookupTable[$scope.draggedNodeIndex]);
                var droppedOnNodeCopy = angular.copy($scope.treeTable.lookupTable[$scope.droppedOnNodeIndex]);

                var droppedOnNode = eval($scope.treeTable.lookupTable[$scope.droppedOnNodeIndex].path);
                var childNum = eval($scope.treeTable.lookupTable[$scope.droppedOnNodeIndex].childNum);
                var emptyNode = { "field" : "???", "strucType" : "object", "children" : [] };

                if ($scope.droppedOnNodeIndex == 0) { // move draggedNode to root
                    droppedOnNode.children.push(draggedNodeCopy);
                } else if (droppedOnNode.children[childNum].children) { // droppedOnNode has children
                    if ($event.ctrlKey && draggedNodeCopy.children) { // draggedNode is moved and merged with droppedOnNode
                        mergeNodes(droppedOnNode, draggedNodeCopy);
                        retcode = true;
                    } else { // draggedNode becomes child of droppedOnNode
                        droppedOnNode.children[childNum].children.push(draggedNodeCopy);
                    }
                } else { // draggedNode and droppedOnNode become children of newly created parent node
                    droppedOnNode.children[childNum] = emptyNode;
                    droppedOnNode.children[childNum].children.push(draggedNodeCopy);
                    droppedOnNode.children[childNum].children.push(droppedOnNodeCopy);
                }

                // THE ORDER OF THE REMAINING OPERATIONS IS CRITICAL
                // DO NOT REARRANGE THEM NO MATTER HOW UNINTRUSIVE YOU THINK THAT MIGHT BE
                // copy potentially empty structure node
                var draggedNodeParent = angular.copy(eval($scope.treeTable.lookupTable[draggedNodeCopy.parentId]));
                // delete original copy of dragged node
                $scope.deleteFieldInModel($scope.draggedNodeIndex, false);
                // if the remaining child is the one dragged then delete the empty parent structure
                if ((draggedNodeParent.strucType == "object" || draggedNodeParent.strucType == "array") &&
                    draggedNodeParent.children && draggedNodeParent.children.length == 1) {
                    $scope.deleteFieldInModel(draggedNodeParent.id, false);
                }

                // refresh structures
                $scope.rebuildModel();

                return retcode;
            }; // dragDrop

            $scope.externalTreeMethod = function (parms) {
                console.log("externalTreeMethod " + angular.toJson(parms));
            }; // externalTreeMethod

            $scope.externalTableMethod = function (parms) {
                console.log("externalTableMethod " + angular.toJson(parms));
            }; // externalTableMethod

            $scope.externalSelectMethod = function (parms) {
                console.log("externalSelectMethod selectedItem: " + angular.toJson(parms));
            }; // externalSelectMethod

            $scope.changeNodeLabel = function (parms) {
                console.log("changeNodeLabel " + angular.toJson(parms));
                var changeNode = eval($scope.treeTable.lookupTable[parms.index]);
                changeNode.field = parms.newName;
                $scope.rebuildModel();
            }; // changeNodeLabel

            $scope.changeNodeStruc = function (parms) {
                console.log("changeNodeStruc " + angular.toJson(parms));

                // don't allow the root node to be changed
                if (parms.index == 0) return;

                var changeNode = eval($scope.treeTable.lookupTable[parms.index]);
                if (parms.currentStruc == 'object') {
                    changeNode.strucType = 'array';
                } else if (parms.currentStruc == 'array') {
                    if (parms.hasChildren) {
                        changeNode.strucType = 'object';
                    } else {
                        changeNode.strucType = undefined;
                    }
                } else if (changeNode.mainType == undefined) {
                    changeNode.strucType = 'object';
                } else {
                    changeNode.strucType = 'array';
                }
                $scope.rebuildModel();
            }; // changeNodeStruc

            $scope.doCallBack = function ($event, callback, parms) {
                //console.log("doCallBack callback: " + callback);
                //console.log("doCallBack $event");
                //console.log($event);
                //console.log("doCallBack parms: " + angular.toJson(parms));
                switch(callback) {
                    case "externalTreeMethod": $scope.externalTreeMethod(parms); break;
                    case "externalTableMethod": $scope.externalTableMethod(parms); break;
                    case "externalSelectMethod": $scope.externalSelectMethod(parms); break;
                    case "dragStart": $scope.dragStart($event, parms); break;
                    case "dragDrop": return $scope.dragDrop($event, parms); break;
                    case "changeNodeLabel": $scope.changeNodeLabel(parms); break;
                    case "changeNodeStruc": $scope.changeNodeStruc(parms); break;
                }
            }; // doCallBack

/* ***************************************** temporary methods ************************************************************************* */
            //console.log("UUID: " + uuid.v4());

            $scope.findFieldInModel = function (lookupIndex) {
                console.log("findFieldInModel");
                console.log($scope.treeTable.lookupTable[lookupIndex]);
                //console.log($scope.treeTable.data[0]);
            }; // findFieldInModel

            $scope.replaceFieldInModel = function (lookupIndex) {
                console.log("replaceFieldInModel");
                $scope.findFieldInModel(lookupIndex);
                var parentNode = eval($scope.treeTable.lookupTable[lookupIndex].path);
                var childNum = eval($scope.treeTable.lookupTable[lookupIndex].childNum);
                var newNode = { "field" : "???", "children" : [] };
                parentNode.children[childNum] = newNode;
                parentNode.children[childNum].children.push(angular.copy($scope.treeTable.lookupTable[3]));
                parentNode.children[childNum].children.push(angular.copy($scope.treeTable.lookupTable[11]));
                $scope.rebuildModel();
            }; // replaceFieldInModel

            $scope.addFieldToModel = function (lookupIndex) {
                console.log("addFieldToModel");
                $scope.findFieldInModel(lookupIndex);
                var parentNode = eval($scope.treeTable.lookupTable[lookupIndex].path);
                var childNum = eval($scope.treeTable.lookupTable[lookupIndex].childNum);
                var newNode = { "field" : "zeta", "mainType" : "number", "detailType" : "integer" };
                if (parentNode.id == 0) {
                    parentNode.children.push(newNode);
                } else if (!parentNode.children[childNum].children) {
                    parentNode.children[childNum].children = [];
                    $scope.findFieldInModel(lookupIndex);
                    parentNode.children[childNum].children.push(newNode);
                }
                $scope.rebuildModel();
            }; // addFieldToModel

            $scope.changeModel = function () {
                $scope.treeTable.data =
                    [
                        { "field" : "alpha", "mainType" : "~", "detailType" : "~", "children" : [
                            { "field" : "a1", "mainType" : "number", "detailType" : "integer" },
                            { "field" : "a2", "mainType" : "number", "detailType" : "integer", "children" : [
                                { "field" : "a2a", "mainType" : "number", "detailType" : "integer" },
                                { "field" : "a2b", "mainType" : "number", "detailType" : "integer" }
                            ] },
                            { "field" : "a3", "mainType" : "number", "detailType" : "integer" }
                        ]},
                        { "field" : "beta", "mainType" : "number", "detailType" : "integer" },
                        { "field" : "delta", "mainType" : "number", "detailType" : "integer" },
                        { "field" : "epsilon", "mainType" : "number", "detailType" : "integer" }
                    ]
/*
                { "field" : "gamma", "mainType" : "number", "detailType" : "integer", "children" : [
                    { "field" : "g1", "mainType" : "number", "detailType" : "integer" },
                    { "field" : "g2", "mainType" : "number", "detailType" : "integer" }
                ] },
*/
                $scope.rebuildModel();
            }; // changeModel
        }
	); // treeTableController

treeTableApp.directive('singleClick', ['$parse', '$timeout', function ($parse, $timeout) {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            var fn = $parse(attr['singleClick']);
            var clicks = 0, timer = null;
            element.on('click', function (event) {
                clicks++;  //count clicks
                if (clicks === 1) {
                    timer = $timeout(function () {
                        fn(scope, {$event: event});
                        clicks = 0;         //after action performed, reset counter
                    }, 300);
                } else {
                    $timeout.cancel(timer);
                    clicks = 0;             //after action performed, reset counter
                }
            });
        }
    };
}]); // singleClick
