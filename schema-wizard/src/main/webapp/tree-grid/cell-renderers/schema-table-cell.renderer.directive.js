(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('schematablecell',['numberFilter', function (numberFilter) {
        var linker = function (scope, element, attrs) {
            console.log("schematablecell");

            // when using otherTableData it does not get updated on reuse but linkData does, don't know why
            scope.data = scope.$eval(decodeURI(attrs['otherTableData']));
            //console.log("scope.data");
            //console.log(scope.data);

            // linkData works for currentSample but not for currentSchema, don't know why
            //var linkData = attrs.linkData;

            var node = scope.$eval(attrs.node);

            if (node && node.children && node.children.length == 0) {
                scope.justify = "center";
                var label1 = attrs.label1;
                //console.log("label1");
                //console.log(label1);
                var label1Splits = label1.split('.');
                var property1 = null;
                switch (label1Splits.length) {
                    // use the first 3 cases if otherTableData is working
                    case 1: property1 = scope.data.sProfile[node.path][label1Splits[0]]; break;
                    case 2: property1 = scope.data.sProfile[node.path][label1Splits[0]][label1Splits[1]]; break;
                    case 3: property1 = scope.data.sProfile[node.path][label1Splits[0]][label1Splits[1]][label1Splits[2]]; break;

                    // use the last 3 cases if the linkData work-around is being used
                    //case 1: property1 = scope[linkData].sProfile[node.path][label1Splits[0]]; break;
                    //case 2: property1 = scope[linkData].sProfile[node.path][label1Splits[0]][label1Splits[1]]; break;
                    //case 3: property1 = scope[linkData].sProfile[node.path][label1Splits[0]][label1Splits[1]][label1Splits[2]]; break;
                }
                //console.log(property1);
                // if label1 is syntactically a number then eval to a number
                if (/^[0-9]*\.?[0-9]+$/.test(property1)) {
                    var castNodeLabel1 = Number(property1);
                    scope.justify = "right";
                    if (Number.isInteger(castNodeLabel1)) {
                        scope.label1 = castNodeLabel1;
                    } else {
                        scope.label1 = numberFilter(castNodeLabel1, 2);
                    }
                } else {
                    scope.label1 = property1;
                }
                // check for two labels for this node
                if (attrs.label2) {
                    scope.justify = "center";
                    var label2 = attrs.label2;
                    //console.log("label2");
                    //console.log(label2);
                    var label2Splits = label2.split('.');
                    var property2 = null;
                    switch (label2Splits.length) {
                        // use the first 3 cases if otherTableData is working
                        case 1: property2 = scope.data.sProfile[node.path][label2Splits[0]]; break;
                        case 2: property2 = scope.data.sProfile[node.path][label2Splits[0]][label2Splits[1]]; break;
                        case 3: property2 = scope.data.sProfile[node.path][label2Splits[0]][label2Splits[1]][label2Splits[2]]; break;

                        // use the last 3 cases if the linkData work-around is being used
                        //case 1: property2 = scope[linkData].sProfile[node.path][label2Splits[0]]; break;
                        //case 2: property2 = scope[linkData].sProfile[node.path][label2Splits[0]][label2Splits[1]]; break;
                        //case 3: property2 = scope[linkData].sProfile[node.path][label2Splits[0]][label2Splits[1]][label2Splits[2]]; break;
                    }
                    //console.log(property2);
                    if (/^[0-9]*\.?[0-9]+$/.test(property2)) {
                        var castNodeLabel2 = Number(property2);
                        if (Number.isInteger(castNodeLabel2)) {
                            scope.label2 = castNodeLabel2;
                        } else {
                            scope.label2 = numberFilter(castNodeLabel2, 2);
                        }
                    } else {
                        scope.label2 = property2;
                    }
                } else {
                    scope.label2 = null;
                }
            } else {
                scope.label1 = "";
            };
            if (scope.label1 == "n/a") scope.label1 = undefined;
            if (scope.label2 == "n/a") scope.label2 = undefined;
        };
        return {
            restrict: "E",
            templateUrl: "tree-grid/templates/schema-table-cell.template.html",
            transclude: true,
            replace: true,
            scope: {
                node: '@',
                nodeId: '@'
            },
            link: linker
        }
    }]); // schematablecell
})();
