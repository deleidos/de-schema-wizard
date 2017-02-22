var schemaWizardApp = angular.module('schemaWizardApp');

schemaWizardApp.controller('hierarchicalMatchingCtrl',
    function($scope, $window, $parse, $timeout, Utilities, MaskUtilities, matchConfidenceThreshold, defaultInterpretationMatch) {

        $scope.model = {};

        // get data samples set in schema-wizard.controller (ref: case "wizard-match-fields")
        $scope.model.dataSamples = Utilities.getDataSamples();

        $scope.sortData = function (obj) {
            var sorting_array = [];
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    keyLowerCase = (key['toLowerCase'] ? key.toLowerCase() : key);
                    sorting_array.push({"sortKey": keyLowerCase, "originalKey": key});
                }
            }
            function compare(a, b) {
                if (a.sortKey < b.sortKey)
                    return -1;
                if (a.sortKey > b.sortKey)
                    return 1;
                return 0;
            }

            sorting_array.sort(compare);
            //console.log(sorting_array);
            var sorted_obj = {};
            for (var i = 0; i < sorting_array.length; i++) {
                sorted_obj[sorting_array[i]["originalKey"]] = obj[sorting_array[i]["originalKey"]];
            }
            return sorted_obj;
        }; // sortData
        // sorting bubbles up structures to the top; structures must be processed first
        for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
            $scope.model.dataSamples[i].dsProfile = $scope.sortData($scope.model.dataSamples[i].dsProfile);
        }
        // save the dataSamples for access via utilities
        Utilities.setDataSamples($scope.model.dataSamples);
        Utilities.setDataSamplesBackup($scope.model.dataSamples);
        console.info("Utilities.getDataSamples()");
        console.log(Utilities.getDataSamples());

        $scope.model.treeTable = {};
        $scope.interpretationMatch = defaultInterpretationMatch;
        $scope.confidenceThreshold = matchConfidenceThreshold;
        $scope.confidenceValues = {
            selectedConfidenceValue: $scope.confidenceThreshold.toString(),
            availableValues: []
        };
        for (var i = 100; i > 80; i--) {
            $scope.confidenceValues.availableValues.push({value: i});
        }

        $scope.TreeNode = function (name, displayName, strucType) {
            this.field = name;
            this.displayName = displayName;
            this.strucType = strucType;
            this.children;
            this.existingSchemaProperty = false;
        }; // TreeNode

        $scope.interpretationMatchesAllDataSamples = function (interpretationName, key, altKey) {
            console.groupCollapsed("interpretationMatchesAllDataSamples");
            for (var i = 0, ilen = $scope.model.dataSamples.length; i < ilen; i++) {
                if ($scope.model.dataSamples[i].dsProfile.hasOwnProperty(key) &&
                    $scope.model.dataSamples[i].dsProfile[key].interpretations.selectedOption.iName.toLowerCase() != interpretationName) {
                    //console.log("iName: " + $scope.model.dataSamples[i].dsProfile[key].interpretations.selectedOption.iName);
                    //console.log("returning false");
                    console.groupEnd();
                    return false;
                } else if ($scope.model.dataSamples[i].dsProfile.hasOwnProperty(altKey) &&
                           $scope.model.dataSamples[i].dsProfile[altKey].interpretations.selectedOption.iName.toLowerCase() != interpretationName) {
                    //console.log("iName: " + $scope.model.dataSamples[i].dsProfile[altKey].interpretations.selectedOption.iName);
                    //console.log("returning false");
                    console.groupEnd();
                    return false;
                }
            }
            //console.log("returning true");
            console.groupEnd();
            return true;
        }; // interpretationMatchesAllDataSamples

        // build 'matching-names' subset of 'matching-fields'
        // 'matching-names' identifies the highest confidence match in selectedOption
        $scope.buildMatchingNames = function () {
            console.info("buildMatchingNames");
            //console.log("$scope.confidenceThreshold: " + $scope.confidenceThreshold);
            if ($scope.modifySchemaMode === true) {
                console.log("$scope.currentSchema.sProfile");
                console.log($scope.currentSchema.sProfile);
            }

            for (var i = 0, ilen = $scope.model.dataSamples.length; i < ilen; i++) {
                for (var key in $scope.model.dataSamples[i].dsProfile) {
                    var dsProfileObj = $scope.model.dataSamples[i].dsProfile[key];
                    //console.log("dsProfileObj key: " + key);
                    //console.log(dsProfileObj);
                    if (dsProfileObj['matching-fields'].length > 0) {
                        dsProfileObj['matching-names'] = {
                            "availableOptions": [],
                            "selectedOption": null
                        };
                        // push the identity element so undo's can be performed
                        dsProfileObj['matching-names'].availableOptions.push(
                            {"id": 0, "name": key});
                        for (j = 0, jlen = dsProfileObj['matching-fields'].length; j < jlen; j++) {
                            var option =
                                {
                                    "id": j + 1,
                                    "name": dsProfileObj['matching-fields'][j]['matching-field'] +
                                    ':' +
                                    dsProfileObj['matching-fields'][j]['confidence'] +
                                    "__"
                                };
                            // when doing interpretation match add only the availableOptions that have matching interpretations
                            if ($scope.interpretationMatch === true) {
                                if ($scope.interpretationMatchesAllDataSamples(
                                        dsProfileObj.interpretations.selectedOption.iName.toLowerCase(),
                                        key,
                                        dsProfileObj['matching-fields'][j]['matching-field'])) {
                                    dsProfileObj['matching-names'].availableOptions.push(option);
                                }
                            } else {
                                dsProfileObj['matching-names'].availableOptions.push(option);
                            }

                            // the first confidence value is the highest so only consider element zero
                             if (j == 0 &&
                                 dsProfileObj['matching-fields'][j]['confidence'] >= $scope.confidenceThreshold) {

                                 if ($scope.interpretationMatch === false) {
                                     console.log("setting 1 (~I) selectedOption for key: " + key + "\toption: " + option);
                                     dsProfileObj['matching-names'].selectedOption =  dsProfileObj['matching-fields'][j]['matching-field'];

                                 } else if ($scope.modifySchemaMode === false) {

                                     if ($scope.interpretationMatchesAllDataSamples(
                                             dsProfileObj.interpretations.selectedOption.iName.toLowerCase(),
                                             key,
                                             dsProfileObj['matching-fields'][j]['matching-field'])) {

                                         console.log("setting 2 (I ~M) selectedOption for key: " + key + "\toption: " + option);
                                         dsProfileObj['matching-names'].selectedOption =  dsProfileObj['matching-fields'][j]['matching-field'];
                                     }

                                 } else if ($scope.currentSchema.sProfile.hasOwnProperty(key) &&
                                     $scope.currentSchema.sProfile[key].interpretations &&
                                     dsProfileObj.interpretations.selectedOption.iName ==
                                     $scope.currentSchema.sProfile[key].interpretations.selectedOption.iName) {

                                     console.log("setting 3a (I M) selectedOption for key: " + key + "\toption: " + option);
                                     dsProfileObj['matching-names'].selectedOption =  dsProfileObj['matching-fields'][j]['matching-field'];

                                 } else if ($scope.currentSchema.sProfile.hasOwnProperty(dsProfileObj['matching-fields'][j]['matching-field']) &&
                                     $scope.currentSchema.sProfile[dsProfileObj['matching-fields'][j]['matching-field']].interpretations &&
                                     dsProfileObj.interpretations.selectedOption.iName ==
                                     $scope.currentSchema.sProfile[dsProfileObj['matching-fields'][j]['matching-field']].interpretations.selectedOption.iName) {

                                     console.log("setting 3b (I M) selectedOption for key: " + key + "\toption: " + option);
                                     dsProfileObj['matching-names'].selectedOption =  dsProfileObj['matching-fields'][j]['matching-field'];
                                 }
                             }

                            // if no available options were added then remove the identity element
                            if (dsProfileObj['matching-names'].availableOptions.length == 0) {
                                dsProfileObj['matching-names'].availableOptions = [];
                                dsProfileObj['matching-names'].selectedOption = null;
                            }
                        }
                    }
                }
            }
            //console.log("buildMatchingNames $scope.model.dataSamples");
            //console.log(angular.copy($scope.model.dataSamples));
        }; //buildMatchingNames

        $scope.insertMatchingGridData = function (ttd, tn, qName) {
            var thisLevelContains = function (arr, el) {
                for (var i = 0, len = arr.length; i < len; i++) {
                    if (arr[i].field == el.field) return true;
                }
                return false;
            };
            //console.group("insertMatchingGridData qName: " + qName);
            var qualifiers = qName.split('.');
            var qualifiersHead = qualifiers[0];
            var found = false;
            for (var i = 0, len = ttd.length; i < len; i++) {
                var fieldQualifiers = ttd[i]['field'].split('.');
                var lastQualifierInField = fieldQualifiers[fieldQualifiers.length - 1];
                if (lastQualifierInField == qualifiersHead) {
                    var qNameTail = qName.substr(qualifiersHead.length + 1);
                    if (qNameTail != "") {
                        if (!ttd[i].children) ttd[i].children = [];
                        $scope.insertMatchingGridData(ttd[i].children, tn, qNameTail);
                        found = true;
                        break;
                    }
                }
            }
            // add if not a duplicate
            if (!found && !thisLevelContains(ttd, tn)) {
                ttd.push(tn);
            }
            //console.info("ttd");
            //console.log(angular.copy(ttd));
            //console.groupEnd();
        }; // insertMatchingGridData

        $scope.modifySchemaBuildMatchingGridData = function () {
            console.group("modifySchemaBuildMatchingGridData");
            $scope.currentSchema = Utilities.getSchema();
            // sorting bubbles up structures to the top; structures must be processed first
            $scope.currentSchema.sProfile = $scope.sortData($scope.currentSchema.sProfile);
            $scope.model.treeTable.data = [];
            $scope.existingSchemaProperties = {};
            for (property in $scope.currentSchema.sProfile) {
                //console.log(property);
                $scope.existingSchemaProperties[property] = $scope.currentSchema.sProfile[property];
            }
            for (property in $scope.currentSchema.sProfile) {
                //console.log(property);
                var tn= new $scope.TreeNode(
                    property,
                    $scope.currentSchema.sProfile[property]['display-name'],
                    $scope.currentSchema.sProfile[property]['struc-type']);
                tn.existingSchemaProperty = true;
                $scope.insertMatchingGridData($scope.model.treeTable.data, tn, property);
            }
            //console.info("$scope.model.treeTable.data");
            //console.log(angular.copy($scope.model.treeTable.data));
            //console.log("$scope.existingSchemaProperties");
            //console.log(angular.copy($scope.existingSchemaProperties));
            //console.groupEnd();
        }; // modifySchemaBuildMatchingGridData

        var FieldData = function(fieldName, dsIndex) {
            this.fieldName = fieldName;
            this.dsIndex = dsIndex;
        }; // FieldData

        /*
         return the items keys as an ordered list of FieldData objects
         */
        $scope.orderKeysByDepth = function (dataSamples) {
            var itemMapping = {};
            var maxDepth = 0;
            for (var dsIndex = 0, len = dataSamples.length; dsIndex < len; dsIndex++) {
                var dataSample = dataSamples[dsIndex]
                angular.forEach(dataSample.dsProfile, function(value, key) {
                    var depth = key.split(".").length - 1;
                    maxDepth = Math.max(maxDepth, depth);
                    var fieldData = new FieldData(key, dsIndex);
                    if (itemMapping[depth] != null) {
                        itemMapping[depth].push(fieldData);
                    } else {
                        itemMapping[depth] = [fieldData];
                    }
                });
            }

            var orderedItemList = [];
            for (var depth = 0; depth <= maxDepth; depth++) {
                angular.forEach(itemMapping[depth], function (value, key) {
                    orderedItemList.push(value);
                });
            }
            return orderedItemList;
        }; // orderKeysByDepth

        $scope.buildMatchingGridData = function () {
            console.group("buildMatchingGridData");
            $scope.model.dataSamples = Utilities.getDataSamples();
            if ($scope.modifySchemaMode === false) {
                $scope.model.treeTable.data = [];
            }
            $scope.existingProperties = {};

            var orderedKeys = $scope.orderKeysByDepth($scope.model.dataSamples);
            for (var keyIndex in orderedKeys) {
                var fieldData = orderedKeys[keyIndex]; // field defined by its sample index and name
                var i = fieldData["dsIndex"]; // the index of the data sample
                var property = fieldData["fieldName"]; // the field name
                //console.log(property);
                var tn;

                // determine whether there is a matching property
                var matchedProperty = undefined;
                if ($scope.model.dataSamples[i].dsProfile[property]['matching-names'] &&
                    $scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption) {

                    // if modifying an existing schema, look at schema properties first
                    if ($scope.modifySchemaMode === true &&
                        $scope.existingSchemaProperties.hasOwnProperty($scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption)) {
                            matchedProperty = $scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption;
                    } else if ($scope.existingProperties.hasOwnProperty($scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption)) {
                        matchedProperty = $scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption;
                    }
                }

                if (matchedProperty == property || matchedProperty == undefined) {
                    // set used-in-schema and merged-into-schema flags
                    if (matchedProperty &&
                        $scope.model.dataSamples[i].dsProfile[property]['matching-names'] &&
                        $scope.model.dataSamples[i].dsProfile[property]['matching-names'][0] &&
                        $scope.model.dataSamples[i].dsProfile[property]['matching-names'][0][id] == 0) {
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = true;
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = false;
                    } else if (matchedProperty) {
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = false;
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = true;
                    } else if ($scope.existingSchemaProperties &&
                               $scope.existingSchemaProperties.hasOwnProperty(property)) {
                        $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = false;
                        $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = true;
                    } else if ($scope.existingProperties.hasOwnProperty(property)) {
                        $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = false;
                        $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = true;
                    } else {
                        $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = true;
                        $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = false;
                    }

                    // add to the existingProperties array if not in existingSchemaProperties or existingProperties
                    if (($scope.modifySchemaMode === true &&
                            !$scope.existingSchemaProperties.hasOwnProperty(property) &&
                            !$scope.existingProperties.hasOwnProperty(property)) ||
                        ($scope.modifySchemaMode !== true &&
                            !$scope.existingProperties.hasOwnProperty(property)) &&
                        !$scope.model.dataSamples[i].dsProfile[property]['original-name']) {
                        $scope.existingProperties[property] = $scope.model.dataSamples[i].dsProfile[property];
                    }
                    // create a node for this property
                    tn = new $scope.TreeNode(
                        property,
                        $scope.model.dataSamples[i].dsProfile[property]['display-name'],
                        $scope.model.dataSamples[i].dsProfile[property]['struc-type']);
                    $scope.insertMatchingGridData($scope.model.treeTable.data, tn, property);

                // if there is a non-identity matching property then align fields in the matching grid
                } else if (matchedProperty) {
                    console.info("got a match\t" + matchedProperty + " with " + property);
                    $scope.model.dataSamples[i].dsProfile[matchedProperty] =
                        angular.copy($scope.model.dataSamples[i].dsProfile[property]);
                    delete $scope.model.dataSamples[i].dsProfile[property];
                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['matching-names'].selectedOption =
                        {"id": 1, "name": matchedProperty};
                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['original-name'] = property;
                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = false;
                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = true;
                    Utilities.setDataSamples($scope.model.dataSamples);
                    tn = new $scope.TreeNode(
                        matchedProperty,
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['display-name'],
                        $scope.model.dataSamples[i].dsProfile[matchedProperty]['struc-type']);
                    $scope.insertMatchingGridData($scope.model.treeTable.data, tn, matchedProperty);
                }
            }
            console.log("$scope.model.dataSamples");
            console.log(angular.copy($scope.model.dataSamples));
            console.log("$scope.existingSchemaProperties");
            console.log(angular.copy($scope.existingSchemaProperties));
            console.log("$scope.existingProperties");
            console.log(angular.copy($scope.existingProperties));
            // wrap model.treeTable.data with a root node
            $scope.model.treeTable.data =
                [{
                    "field": "/",
                    "displayName": "/",
                    "strucType": "object",
                    "children": $scope.model.treeTable.data
                }];
            //console.info("$scope.model.treeTable.data");
            //console.log($scope.model.treeTable.data);
            //console.log($scope.model.treeTable.data[0].children[0].field);
            console.groupEnd();
        }; // buildMatchingGridData

        $scope.selectInitialDetailsPanelProperties = function () {
            console.group("selectInitialDetailsPanelProperties");
            //console.log("$scope.existingProperties");
            //console.log($scope.existingProperties);
            var details1PropertyFound = false;
            var details2PropertyFound = false;
            for (key in $scope.existingProperties) {
                console.log("key: " + key);
                if (!details1PropertyFound &&
                    $scope.model.dataSamples[0].dsProfile.hasOwnProperty(key) &&
                    $scope.model.dataSamples[0].dsProfile[key]['struc-type'] != "object" &
                    $scope.model.dataSamples[0].dsProfile[key]['struc-type'] != "array") {
                    $scope.showInDetails1(null, { 'dataSource': $scope.model.dataSamples[0], 'property': key });
                    console.log("details1 property found");
                    details1PropertyFound = true;
                }
                if ($scope.model.dataSamples[1] && !details2PropertyFound &&
                    $scope.model.dataSamples[1].dsProfile.hasOwnProperty(key) &&
                    $scope.model.dataSamples[1].dsProfile[key]['struc-type'] != "object" &
                    $scope.model.dataSamples[1].dsProfile[key]['struc-type'] != "array") {
                    $scope.showInDetails2(null, { 'dataSource': $scope.model.dataSamples[1], 'property': key });
                    console.log("details2 property found");
                    details2PropertyFound = true;
                };
                if (details1PropertyFound && details2PropertyFound) break;
            }
            //console.log("$scope.model.dataSamples");
            //console.log($scope.model.dataSamples);
            console.groupEnd();
        }; // selectInitialDetailsPanelProperties

        $scope.buildColumns = function () {
            // NOTE: Directives must be named with all lowercase characters and no punctuation.
            //       This limitation arises because AngularJS requires hyphenated names as attributes
            //       and camel case for the directive name. This workaround eliminates this problem.
            $scope.model.columns = [
                {
                    'complexCell': 'true',
                    'property1': 'displayName',
                    'property2': 'strucType',
                    'name': 'Schema Property',
                    'callback': 'doCallBack',
                    'tree': {
                        'data': ($scope.currentSchema && $scope.currentSchema.sProfile
                                 ? $scope.currentSchema.sProfile : null)
                    }
                }
            ];
            for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                $scope.model.columns.push(
                    {
                        'complexCell': 'true',
                        'property1': 'field',
                        'name': $scope.model.dataSamples[i].dsName,
                        'callback': 'doCallBack',
                        'table': {
                            'data': i
                        }
                    }
                )
            }
        }; // buildColumns

        $scope.encodeCellData = function (index, last) {
            //console.info("encodeCellData");
            // data objects must be passed through the layers of directives as encoded JSON strings
            // decoding and restoration as javascript objects must occur in the directive
            for (var i = 0; i < $scope.model.columns.length; i++) {
                if ($scope.model.columns[i].tree && $scope.model.columns[i].tree.data) {
                    $scope.model.columns[i].tree.data = encodeURI(angular.toJson($scope.model.columns[i].tree.data));
                }
                if ($scope.model.columns[i].table && $scope.model.columns[i].table.data) {
                    $scope.model.columns[i].table.data = encodeURI(angular.toJson($scope.model.columns[i].table.data));
                }
            }
        }; // encodeCellData

        $scope.getColumnId = function (index, last) {
            //console.log(index + "\t" + last);
            if (last) {
                return 'colN';
            } else {
                return 'col' + index;
            }
        }; // getColumnId

        $scope.scrollTreeTable = function (scrollAmount) {
            var sTop = document.getElementById('colN').scrollTop -= scrollAmount;
            for (var i = 0; i < $scope.model.columns.length - 1; i++) {
                document.getElementById('col' + i).scrollTop = sTop;
            }
        }; // scrollTreeTable

        // Chrome & IE
        angular.element($window).bind('mousewheel', function (event) {
            // check whether the scroll event is for the tree-grid
            if (event.target.offsetParent.className.indexOf('panel-group') >= 0) {
                $scope.scrollTreeTable(event.originalEvent.wheelDelta / 4);
                event.preventDefault();
                event.stopImmediatePropagation();
            }
        });

        // Firefox
        angular.element($window).bind('DOMMouseScroll', function (event) {
            // check whether the scroll event is for the tree-grid
            if (event.target.offsetParent.className.indexOf('panel-group') >= 0) {
                $scope.scrollTreeTable(event.originalEvent.detail * -10);
                event.preventDefault();
                event.stopImmediatePropagation();
            }
        });

        // wait for grid to initialize then add listener for scrolling
        $timeout(function () {
                document.getElementById('colN').addEventListener('scroll', function (event) {
                    //console.log(event);
                    $scope.scrollTreeTable(0);
                    event.preventDefault();
                    event.stopImmediatePropagation();
                })
            },
            1000);

        $scope.rebuildModel = function () {
            //console.info("rebuildModel");
            $scope.model.treeTable.lookupTable = [];
            var idNum = 0;
            var traverse = function (subtree, path, parentId, childNum) {
                subtree.id = ++idNum;
                subtree.path = path;
                subtree.parentId = parentId;
                subtree.childNum = childNum;
                $scope.model.treeTable.lookupTable.push(subtree);
                if (subtree.hasOwnProperty('children')) {
                    parentId = idNum;
                    for (var i = 0, len = subtree.children.length; i < len; i++) {
                        traverse(subtree.children[i], subtree.path + '.children[' + childNum + ']', parentId, i);
                    }
                }
            };
            $scope.model.treeTable.data[0].id = 0;
            $scope.model.treeTable.data[0].path = "$scope.model.treeTable.data[0]";
            $scope.model.treeTable.data[0].parentId = -1;
            $scope.model.treeTable.data[0].childNum = -1;
            $scope.model.treeTable.lookupTable.push($scope.model.treeTable.data[0]);
            for (var i = 0, len = $scope.model.treeTable.data[0].children.length; i < len; i++) {
                traverse($scope.model.treeTable.data[0].children[i], "$scope.model.treeTable.data[0]", 0, i);
            }
            //console.info("$scope.model.treeTable.data");
            //console.log($scope.model.treeTable.data);
            //$scope.showLookupTable();
        }; // rebuildModel

        $scope.deleteFieldInModel = function (deletionIndex, rebuildAfter) {
            console.info("deleteFieldInModel: " + deletionIndex);
            eval($scope.model.treeTable.lookupTable[deletionIndex].path).children
                .splice(eval($scope.model.treeTable.lookupTable[deletionIndex].childNum), 1);
            if (rebuildAfter) $scope.rebuildModel();
        }; // deleteFieldInModel

        $scope.showLookupTable = function () {
            console.info("$scope.model.treeTable.data");
            console.log($scope.model.treeTable.data);
            console.log("$scope.model.treeTable.lookupTable");
            console.log("field      id  pId cId  path");
            for (var i = 0, len = $scope.model.treeTable.lookupTable.length; i < len; i++) {
                console.log(($scope.model.treeTable.lookupTable[i].field + "          ").substr(0, 10) + "\t" +
                    $scope.model.treeTable.lookupTable[i].id + "\t" +
                    $scope.model.treeTable.lookupTable[i].parentId + "\t" +
                    $scope.model.treeTable.lookupTable[i].childNum + "\t" +
                    $scope.model.treeTable.lookupTable[i].path + "\t" +
                    "");
            }
        }; // showLookupTable

        $scope.dragStart = function ($event, parms) {
            console.info("dragStart " + angular.toJson(parms));
            //console.log("$event");
            //console.log($event);
            $scope.draggedNodeIndex = parms.index;
        }; // dragStart

        $scope.dragDrop = function ($event, parms) {
            console.info("dragDrop " + angular.toJson(parms));
            //console.info("$event");
            //console.log($event);
            console.log("$event.shiftKey: " + $event.shiftKey);
            console.log("$event.ctrlKey: " + $event.ctrlKey);

            var retcode = false;

            // don't allow the root node to be dropped
            if ($scope.draggedNodeIndex == 0) return retcode;

            $scope.droppedOnNodeIndex = parms.index;
            console.info("$scope.droppedOnNodeIndex: " + $scope.droppedOnNodeIndex);

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
                }
            };

            var draggedNodeCopy = angular.copy($scope.model.treeTable.lookupTable[$scope.draggedNodeIndex]);
            var droppedOnNodeCopy = angular.copy($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex]);

            var droppedOnNode = eval($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex].path);
            var childNum = eval($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex].childNum);
            var emptyNode = {"displayName": "???", "strucType": "object", "children": []};

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
            var draggedNodeParent = angular.copy(eval($scope.model.treeTable.lookupTable[draggedNodeCopy.parentId]));
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

        $scope.changeNodeLabel = function (parms) {
            console.info("changeNodeLabel " + angular.toJson(parms));
            var changeNode = eval($scope.model.treeTable.lookupTable[parms.index]);
            changeNode.field = parms.newName;
            changeNode['displayName'] = parms.newName;
            $scope.rebuildModel();
        }; // changeNodeLabel

        $scope.changeNodeStruc = function (parms) {
            console.info("changeNodeStruc " + angular.toJson(parms));

            // don't allow the root node to be changed
            if (parms.index == 0) return;

            var changeNode = eval($scope.model.treeTable.lookupTable[parms.index]);
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

        $scope.previouslyShownInDetails1;
        $scope.showInDetails1 = function ($event, parms) {
            console.info("showInDetails1");
            //console.log("dataSource: " + parms['dataSource']);
            //console.log("property: " + parms['property']);
            var schemaProperty = parms['schemaProperty'];
            //console.log("schemaProperty: " + schemaProperty);
            //console.log("$scope.modifySchemaMode: " + $scope.modifySchemaMode);

            // turn off highlighting of either a schema property or a data sample property
            if ($scope.previouslyShownInDetails1) $scope.previouslyShownInDetails1['shown-in-details1'] = false;
            if (parms['dataSource'] != undefined) {
                $scope.previouslyShownInDetails1 = parms['dataSource'].dsProfile[parms['property']];
            };
            var previousSchemaPropertyShownInDetails1Id = Utilities.getMatchingShownInDetails1Id();
            //console.log("previousShownInDetails1Id: " + previousSchemaPropertyShownInDetails1Id);
            if (previousSchemaPropertyShownInDetails1Id) {
                document.getElementById(previousSchemaPropertyShownInDetails1Id).style.backgroundColor = 'transparent';
            }
            // the following line works in conjunction with the schemaproperty directive
            Utilities.setMatchingShownInDetails1Id(undefined);

            // push a profile into details panel 1
            $scope.detailModels.detailPanels.panel1 = [];
            if (schemaProperty && schemaProperty === true) {
                $scope.detailModels.detailPanels.panel1.push($scope.currentSchema.sProfile[parms['property']]);
                $scope.detailModels.detailPanels.panel1[0]["property-name"] = parms['property'];
            } else {
                $scope.detailModels.detailPanels.panel1.push(parms['dataSource'].dsProfile[parms['property']]);
                parms['dataSource'].dsProfile[parms['property']]['shown-in-details1'] = true;
                if (parms['dataSource'].dsName.length > 30) {
                    $scope.detailModels.detailPanels.panel1[0]["dsName"] = "..." + parms['dataSource'].dsName.slice(-20);
                } else {
                    $scope.detailModels.detailPanels.panel1[0]["dsName"] = parms['dataSource'].dsName;
                }
                if (parms['dataSource'].dsProfile[parms['property']]['original-name']) {
                    $scope.detailModels.detailPanels.panel1[0]["property-name"] =
                        parms['dataSource'].dsProfile[parms['property']]['original-name'];
                    $scope.detailModels.detailPanels.panel1[0]["confidence"] =
                        parms['dataSource'].dsProfile[parms['property']]['matching-fields'][0]['confidence'];
                } else {
                    $scope.detailModels.detailPanels.panel1[0]["property-name"] = parms['property'];
                }
            }
            // set the default viz for the histogram
            if ($scope.detailModels.detailPanels.panel1[0].detail['freq-histogram'].type == "map") {
                $scope.detailModels.detailPanels.panel1[0].viz = "map";
            }
            else if($scope.detailModels.detailPanels.panel1[0].detail['detail-type'] == "text") {
                $scope.detailModels.detailPanels.panel1[0].viz = "example";
            }
            else {
                $scope.detailModels.detailPanels.panel1[0].viz = "hbc";
            }
            //console.log("detailModels.detailPanels.panel1[0]");
            //console.log($scope.detailModels.detailPanels.panel1[0]);
        }; // showInDetails1

        $scope.previouslyShownInDetails2;
        $scope.showInDetails2 = function ($event, parms) {
            console.info("showInDetails2");
            console.log(parms['dataSource'].dsProfile[parms['property']]);
            if ($scope.previouslyShownInDetails2) $scope.previouslyShownInDetails2['shown-in-details2'] = false;
            $scope.previouslyShownInDetails2 = parms['dataSource'].dsProfile[parms['property']];

            $scope.detailModels.detailPanels.panel2 = [];
            $scope.detailModels.detailPanels.panel2.push(parms['dataSource'].dsProfile[parms['property']]);
            parms['dataSource'].dsProfile[parms['property']]['shown-in-details2'] = true;
            if (parms['dataSource'].dsName.length > 30) {
                $scope.detailModels.detailPanels.panel2[0]["dsName"] = "..." + parms['dataSource'].dsName.slice(-20);
            } else {
                $scope.detailModels.detailPanels.panel2[0]["dsName"] = parms['dataSource'].dsName;
            }
            // set the default viz for the histogram
            if ($scope.detailModels.detailPanels.panel2[0].detail['freq-histogram'].type == "map") {
                $scope.detailModels.detailPanels.panel2[0].viz = "map";
            }
            else if($scope.detailModels.detailPanels.panel2[0].detail['detail-type'] == "text") {
                $scope.detailModels.detailPanels.panel2[0].viz = "example";
            }
            else {
                $scope.detailModels.detailPanels.panel2[0].viz = "hbc";
            }
            if (parms['dataSource'].dsProfile[parms['property']]['original-name']) {
                $scope.detailModels.detailPanels.panel2[0]["property-name"] =
                    parms['dataSource'].dsProfile[parms['property']]['original-name'];
                $scope.detailModels.detailPanels.panel2[0]["confidence"] =
                    parms['dataSource'].dsProfile[parms['property']]['matching-fields'][0]['confidence'];
            } else {
                $scope.detailModels.detailPanels.panel2[0]["property-name"] = parms['property'];
            }
            //console.log("detailModels.detailPanels.panel2[0]");
            //console.log($scope.detailModels.detailPanels.panel2[0]);
        }; // showInDetails2

        $scope.changeInterpretation = function ($event, parms) {
            console.info("changeInterpretation");
            console.log(parms['dataSource']['dsName']);
            console.log(parms['property']);
            var splits = parms['dataSource'].dsProfile[parms['property']]['interpretations'].selectedOption['name'].split(':');
            console.log(splits[0]);
        }; // changeInterpretation

        $scope.changeMatchedProperty = function ($event, parms) {
            console.group("changeMatchedProperty");
            // find out which data sample this event refers to
            var dsIndex = -1;
            for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                if ($scope.model.dataSamples[i].dsName == parms['dataSource']['dsName']) {
                    dsIndex = i;
                    break;
                }
            }
            console.log("dsIndex: " + dsIndex);
            console.log("parms['property']: " + parms['property']);
            console.info($scope.model.dataSamples[dsIndex].dsProfile[parms['property']]);
            console.log("parms");
            console.log(parms);
            console.log(parms['dataSource'].dsProfile[parms['property']]['matching-names'].selectedOption.id);

            if ($scope.model.dataSamples[dsIndex].dsProfile[parms['property']]['used-in-schema'] === true &&
                parms['dataSource'].dsProfile[parms['property']]['matching-names'].selectedOption.id == 0) return;

            var splits = parms['dataSource'].dsProfile[parms['property']]['matching-names'].selectedOption['name'].split(':');
            var matchedProperty = splits[0];
            var matchedConfidence = splits[1];
            $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty] =
                angular.copy($scope.model.dataSamples[dsIndex].dsProfile[parms['property']]);
            $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['field'] = matchedProperty;
            if (!matchedConfidence) {
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['used-in-schema'] = true;
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['merged-into-schema'] = false;
            } else {
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['used-in-schema'] = false;
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['merged-into-schema'] = true;
            }

            // keep the display-name set to the original name
            if ($scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] != null) {
                // returning a node to its original place so set original-name to original value which is null
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] = null;
            } else {
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] = parms['property'];
            }
            delete $scope.model.dataSamples[dsIndex].dsProfile[parms['property']];
            Utilities.setDataSamples($scope.model.dataSamples);

            if ($scope.modifySchemaMode === true) {
                $scope.modifySchemaBuildMatchingGridData()
            }
            $scope.buildMatchingGridData();
            $scope.rebuildModel($scope.model.treeTable.data);
            console.groupEnd();
        }; // changeMatchedProperty

        $scope.doCallBack = function ($event, callback, parms) {
            console.info("doCallBack callback: " + callback);
            //console.log($event);
            //console.log("doCallBack parms: " + angular.toJson(parms));
            switch (callback) {
                case "dragStart":
                    $scope.dragStart($event, parms);
                    break;
                case "dragDrop":
                    return $scope.dragDrop($event, parms);
                    break;
                case "changeNodeLabel":
                    $scope.changeNodeLabel(parms);
                    break;
                case "showInDetails1":
                    $scope.showInDetails1($event, parms);
                    break;
                case "showInDetails2":
                    $scope.showInDetails2($event, parms);
                    break;
                case "changeInterpretation":
                    $scope.changeInterpretation($event, parms);
                    break;
                case "changeMatchedProperty":
                    $scope.changeMatchedProperty($event, parms);
                    break;
            }
        }; // doCallBack

        $scope.init = function () {
            $scope.modifySchemaMode = Utilities.getModifySchemaMode();
            if ($scope.modifySchemaMode === true) {
                $scope.modifySchemaBuildMatchingGridData()
            }
            $scope.buildMatchingNames();
            $scope.buildMatchingGridData();
            Utilities.setDataSamples($scope.model.dataSamples);
            $scope.selectInitialDetailsPanelProperties();
            $scope.buildColumns();
            $scope.encodeCellData();
            $timeout(function () {
                $scope.rebuildModel($scope.model.treeTable.data);
                document.getElementById("tree-view").style.width = (250 + 150 * $scope.model.dataSamples.length) + "px";
                MaskUtilities.fadeBrowseMask();
            }, 600);
        }; //init
        $scope.init();

        $scope.resetMatches = function (interpretationMatch) {
            console.group("resetMatches");
            $scope.model.dataSamples = Utilities.getDataSamplesBackup();
            Utilities.setDataSamples($scope.model.dataSamples);
            $scope.interpretationMatch = defaultInterpretationMatch;
            $scope.confidenceThreshold = matchConfidenceThreshold;
            $scope.confidenceValues.selectedConfidenceValue = $scope.confidenceThreshold.toString();
            $scope.init();
        }; // resetMatches

        $scope.repeatMatching = function (interpretationMatch) {
            console.info("repeatMatching interpretationMatch: " + interpretationMatch);
            $scope.model.dataSamples = Utilities.getDataSamplesBackup();
            Utilities.setDataSamples($scope.model.dataSamples);
            $scope.confidenceThreshold = $scope.confidenceValues.selectedConfidenceValue;
            if (interpretationMatch) $scope.interpretationMatch = interpretationMatch;
            $scope.init();
        }; // repeatMatching

        $scope.removeDs = function (index) {
            console.info("removeDs index: " + index);
            //console.log($scope.model.dataSamples.length);
            $scope.model.dataSamples.splice(index - 1, 1);
            //console.log($scope.model.dataSamples.length);
            Utilities.setDataSamples($scope.model.dataSamples);
            Utilities.setDataSamplesBackup($scope.model.dataSamples);
            //console.log(Utilities.getDataSamplesBackup());
            $scope.init();
        }; // removeDs
    }
); // hierarchicalMatchingCtrl
