(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('tablecell',['numberFilter', function (numberFilter) {
        var linker = function (scope, element, attrs) {
            //console.log("tablecell");

            // when using otherTableData it does not get updated on reuse but linkData does, don't know why
            //var data = decodeURI(attrs['otherTableData']);
            //data = scope.$eval(data);
            //console.log("data.dsName");
            //console.log(data.dsName);

            var linkData = attrs.linkData;

            var node = scope.$eval(attrs.node);
            var label1 = attrs.label1;
            var label2 = attrs.label2;

            if (node && node.children && node.children.length == 0) {
                var label1Splits = label1.split('.');
                var property1 = null;
                //console.log("data.dsProfile[node.path]");
                //console.log(data.dsProfile[node.path]);
                switch (label1Splits.length) {
                    // use the first 3 cases if otherTableData is working
                    //case 1: property1 = data.dsProfile[node.path][label1Splits[0]]; break;
                    //case 2: property1 = data.dsProfile[node.path][label1Splits[0]][label1Splits[1]]; break;
                    //case 3: property1 = data.dsProfile[node.path][label1Splits[0]][label1Splits[1]][label1Splits[2]]; break;

                    // use the last 3 cases if the linkData work-around is being used
                    case 1: property1 = scope[linkData].dsProfile[node.path][label1Splits[0]]; break;
                    case 2: property1 = scope[linkData].dsProfile[node.path][label1Splits[0]][label1Splits[1]]; break;
                    case 3: property1 = scope[linkData].dsProfile[node.path][label1Splits[0]][label1Splits[1]][label1Splits[2]]; break;
                }
                scope.justify = "center";
                // if nodeLabel1 is syntactically a number then eval to a number
                if (/^[0-9]*\.?[0-9]+$/.test(property1)) {
                    var castNodeLabel1 = Number(property1);
                    scope.justify = "right";
                    if (Number.isInteger(castNodeLabel1)) {
                        scope.nodeLabel1 = castNodeLabel1;
                    } else {
                        scope.nodeLabel1 = numberFilter(castNodeLabel1, 2);
                    }
                } else {
                    scope.nodeLabel1 = property1;
                }
                // check for two labels for this node
                if (label2) {
                    var label2Splits = label2.split('.');
                    var property2 = null;
                    switch (label2Splits.length) {
                        // use the first 3 cases if otherTableData is working
                        //case 1: property2 = data.dsProfile[node.path][label2Splits[0]]; break;
                        //case 2: property2 = data.dsProfile[node.path][label2Splits[0]][label2Splits[1]]; break;
                        //case 3: property2 = data.dsProfile[node.path][label2Splits[0]][label2Splits[1]][label2Splits[2]]; break;

                        // use the last 3 cases if the linkData work-around is being used
                        case 1: property2 = scope[linkData].dsProfile[node.path][label2Splits[0]]; break;
                        case 2: property2 = scope[linkData].dsProfile[node.path][label2Splits[0]][label2Splits[1]]; break;
                        case 3: property2 = scope[linkData].dsProfile[node.path][label2Splits[0]][label2Splits[1]][label2Splits[2]]; break;
                    }
                    scope.justify = "center";
                    if (/^[0-9]*\.?[0-9]+$/.test(property2)) {
                        var castNodeLabel2 = Number(property2);
                        if (Number.isInteger(castNodeLabel2)) {
                            scope.nodeLabel2 = castNodeLabel2;
                        } else {
                            scope.nodeLabel2 = numberFilter(castNodeLabel2, 2);
                        }
                    } else {
                        scope.nodeLabel2 = property2;
                    }
                } else {
                    scope.nodeLabel2 = null;
                }
            } else {
                scope.nodeLabel1 = "&nbsp;";
            };

            scope.cbMethod = function ($event, callback, parms) {
                scope.callbackMethod()($event, callback, parms);
            };
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/table-cell.template.html",
            transclude: true,
            replace: true,
            link: linker
        }
    }]); // tablecell
})();
