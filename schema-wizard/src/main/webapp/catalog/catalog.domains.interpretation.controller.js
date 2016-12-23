(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('interpretationCtrl', ['$rootScope', '$scope', '$cookies', '$resource',
        '$location', '$route', '$routeParams', '$timeout', '$confirm', 'domainName', 'domainId',
        'interpretationData', 'interpretationResource', 'statusCodesFactory', 'pythonValidateResource',
        'pythonTestResource', '$q', 'uiTourService',
        function ($rootScope, $scope, $cookies, $resource, $location, $route, $routeParams, $timeout,
                  $confirm, domainName, domainId, interpretationData, interpretationResource,
                  statusCodesFactory, pythonValidateResource, pythonTestResource, $q, TourService) {
            if($cookies.get('schwiz.tours.interpretations') !== "visited"){
                $timeout(function () { TourService.getTourByName('catalog').startAt('200'); }, 2500);
                $cookies.put('schwiz.tours.interpretations', "visited");
            }
            $scope.interpretationTour = $rootScope.tourInformation.interpretationsTour;
            $scope.domainName = domainName;
            console.log("$scope.domainName: " + $scope.domainName);
            $scope.domainId = domainId;
            console.log("$scope.domainId: " + $scope.domainId);
            $scope.editor = null;
            $scope.selInterpret = null;
            $scope.newInterpretation = null;
            $scope.selInterpretSampleData = null;
            $scope.selInterpretMatchingNames = null;
            interpretationData.$promise.then(function (response) {
                console.log(response);
                // retrieve the interpretations object
                $scope.interpretations = interpretationData;
                console.log($scope.interpretations);
                $timeout(function() {
                    $scope.initEditor();
                    // initialize the forms
                    $scope.clearAllForms();
                    // if the first key's value is '$promise' then there are no interpretations defined
                    if (Object.keys($scope.interpretations)[0] === "$promise") {
                        $scope.addNewInterpretation();
                    } else {
                        $scope.loadInterpretation(Object.keys($scope.interpretations)[0]);
                    }
                    $scope.autoSave = function () {
                        console.log("autoSave");
                        /*
                        console.log("autoSave \n" +
                            " 0) " + $scope.newInterpretation + " \n" +
                            " 1) " + $scope.interpretationForm.$valid + " \n" +
                            " 2) " + $scope.interpretationForm.$dirty + " \n" +
                            " 3) " + $scope.interpretationForm.iDescription.$valid + " \n" +
                            " 4) " + $scope.interpretationForm.mainType.$valid + " \n" +
                            " 5) " + $scope.interpretationForm.detailType.$valid + " \n" +
                            " 6) " + $scope.interpretationForm.numDistinctValues.$valid + " \n" +
                            " 7) " + $scope.interpretationForm.min.$valid + " \n" +
                            " 8) " + $scope.interpretationForm.minLength.$valid + " \n" +
                            " 9) " + $scope.interpretationForm.length.$valid + " \n" +
                            "10) " + $scope.interpretationForm.max.$valid + " \n" +
                            "11) " + $scope.interpretationForm.maxLength.$valid + " \n" +
                            "12) " + $scope.interpretationForm.sampleData.$valid + " \n" +
                            "13) " + $scope.interpretationForm.matchingNames.$valid + " \n" +
                            "14) " + $scope.interpretationForm.editorValidationMsg.$dirty);
                        */
                        if (($scope.newInterpretation
                              && $scope.newInterpretation.iName.length > 1
                              && $scope.interpretationForm.iDescription.$valid
                              && $scope.interpretationForm.mainType.$valid
                              && $scope.interpretationForm.detailType.$valid
                              && $scope.interpretationForm.numDistinctValues.$valid
                              && $scope.interpretationForm.min.$valid
                              && $scope.interpretationForm.minLength.$valid
                              && $scope.interpretationForm.length.$valid
                              && $scope.interpretationForm.max.$valid
                              && $scope.interpretationForm.maxLength.$valid
                              && $scope.interpretationForm.sampleData.$valid
                             )
                             || (($scope.interpretationForm.$dirty
                                   &&  !($scope.interpretationForm.iDescription.$invalid
                                          || $scope.interpretationForm.mainType.$invalid
                                          || $scope.interpretationForm.detailType.$invalid
                                          || $scope.interpretationForm.numDistinctValues.$invalid
                                          || $scope.interpretationForm.min.$invalid
                                          || $scope.interpretationForm.minLength.$invalid
                                          || $scope.interpretationForm.length.$invalid
                                          || $scope.interpretationForm.max.$invalid
                                          || $scope.interpretationForm.maxLength.$invalid
                                          || $scope.interpretationForm.sampleData.$invalid
                                        )
                                  )
                                 || $scope.interpretationForm.editorValidationMsg.$dirty
                                )
                           ) {
                            $scope.updateInterpretation();
                            if ($scope.interpretationForm.editorValidationMsg.$dirty) {
                                $timeout($scope.validatePythonScript, 500);
                            }
                        }
                        $timeout($scope.autoSave, 2000);
                    };
                    $timeout($scope.autoSave, 2000);
                }, 1000);
            }, function (error) {
                statusCodesFactory.get().$promise.then(function (response) {
                    $confirm(
                        {
                            title: response.interpretationFailedToLoad.title,
                            text: response.interpretationFailedToLoad.message +
                                    " (" + error.status + ")",
                            ok: 'OK'
                        },
                        {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                })
            }); // startup resource resolved

            $scope.defaultPythonBodyScript =
                "def validateInterpretation(field_profile):\n" +
                "    return True\n";
            $scope.defaultPythonEpilogScript =
                "\nfield_profile = locals()['field_profile']\n" +
                "is_valid_interpretation = validateInterpretation(field_profile)\n";

            $scope.clearAllForms = function () {
                $scope.editor.getSession().clearAnnotations();
                $scope.editor.selectAll();
                $scope.editor.removeLines();

                // work-around for angularjs bug ($setViewValue & $render)
                $scope.selInterpretSampleData = "";
                $scope.interpretationForm.sampleData.$setViewValue($scope.selInterpretSampleData);
                $scope.interpretationForm.sampleData.$render();

                $scope.interpretationForm.console.value = null;
                $scope.consoleOutput = "";

                // work-around for angularjs bug ($setViewValue & $render)
                $scope.selInterpretMatchingNames = "";
                $scope.interpretationForm.matchingNames.$setViewValue($scope.selInterpretMatchingNames);
                $scope.interpretationForm.matchingNames.$render();

                $scope.newInterpretation = null;
            }; // clearAllForms

            $scope.resetForm = function () {
                $scope.interpretationForm.iName.$setUntouched();
                $scope.interpretationForm.iName.$setPristine();
                $scope.interpretationForm.iDescription.$setUntouched();
                $scope.interpretationForm.iDescription.$setPristine();
                $scope.interpretationForm.mainType.$setPristine();
                $scope.interpretationForm.detailType.$setPristine();
                $scope.interpretationForm.$setPristine();
            }; // resetForm

            $scope.resetDetailType = function () {
                //$scope.interpretationForm.detailType = "";
                $scope.selInterpret.iConstraints['detail-type'] = "";
            }; // resetDetailType

            $scope.loadInterpretation = function (interpretationName) {
                $scope.clearAllForms();
                $scope.selInterpret = $scope.interpretations[interpretationName];
                console.log("selInterpret interpretationName: " + interpretationName);
                console.log($scope.selInterpret);
                //console.log("$scope.selInterpret.iScript");
                //console.log($scope.selInterpret.iScript);
                var iScr = atob($scope.selInterpret.iScript).split('\n');
                var iScrBodyLen;
                for (iScrBodyLen = iScr.length - 1; iScrBodyLen > 0; iScrBodyLen--) {
                    if (iScr[iScrBodyLen] === "field_profile = locals()['field_profile']") {
                        break;
                    }
                }
                console.log("iScrBodyLen: " + iScrBodyLen);
                //console.log(iScr.slice(0, iScrBodyLen).join('\n'));
                $scope.editor.getSession().setValue(iScr.slice(0, iScrBodyLen).join('\n'));

                $scope.selInterpretSampleData = "";
                if ($scope.selInterpret.iSampleData) {
                    for (var i = 0; i < $scope.selInterpret.iSampleData.length; i++) {
                        $scope.selInterpretSampleData +=
                            $scope.selInterpret.iSampleData[i] + "\n";
                    }
                    // strip off the trailing newline character
                    if ($scope.selInterpret.iSampleData.length > 0) {
                        $scope.selInterpretSampleData = $scope.selInterpretSampleData.slice(0, -1);
                    }
                }
                console.log("$scope.selInterpretSampleData.toString()");
                console.log($scope.selInterpretSampleData.toString());

                // work-around for angularjs bug ($setViewValue & $render)
                $scope.interpretationForm.sampleData.$setViewValue($scope.selInterpretSampleData);
                $scope.interpretationForm.sampleData.$setValidity('required', $scope.selInterpretSampleData.length > 0);
                $scope.interpretationForm.sampleData.$setPristine();
                $scope.interpretationForm.sampleData.$render();
                /*
                console.log("$scope.interpretationForm.sampleData validation states:");
                console.log("$untouched: " + $scope.interpretationForm.sampleData.$untouched);
                console.log("$touched: " + $scope.interpretationForm.sampleData.$touched);
                console.log("$pristine: " + $scope.interpretationForm.sampleData.$pristine);
                console.log("$dirty: " + $scope.interpretationForm.sampleData.$dirty);
                console.log("$invalid: " + $scope.interpretationForm.sampleData.$invalid);
                console.log("$valid: " + $scope.interpretationForm.sampleData.$valid);
                */

                if (!$scope.selInterpret.iConstraints.quantized) {
                    $scope.selInterpret.iConstraints.quantized = "Unknown";
                }
                if (!$scope.selInterpret.iConstraints.ordinal) {
                    $scope.selInterpret.iConstraints.ordinal = "Unknown";
                }
                if (!$scope.selInterpret.iConstraints.categorical) {
                    $scope.selInterpret.iConstraints.categorical = "Unknown";
                }
                if (!$scope.selInterpret.iConstraints.relational) {
                    $scope.selInterpret.iConstraints.relational = "Unknown";
                }
                $scope.selInterpretMatchingNames = "";
                if ($scope.selInterpret.iMatchingNames) {
                    for (var i = 0; i < $scope.selInterpret.iMatchingNames.length; i++) {
                        $scope.selInterpretMatchingNames +=
                            $scope.selInterpret.iMatchingNames[i] + "\n";
                    }
                    // strip off the trailing newline character
                    if ($scope.selInterpret.iMatchingNames.length > 0) {
                        $scope.selInterpretMatchingNames = $scope.selInterpretMatchingNames.slice(0, -1);
                    }
                }
                console.log("$scope.selInterpretMatchingNames.toString()");
                console.log($scope.selInterpretMatchingNames.toString());

                // work-around for angularjs bug ($setViewValue & $render)
                $scope.interpretationForm.matchingNames.$setViewValue($scope.selInterpretMatchingNames);
                $scope.interpretationForm.matchingNames.$setValidity('required', $scope.selInterpretMatchingNames.length > 0);
                $scope.interpretationForm.matchingNames.$setPristine();
                $scope.interpretationForm.matchingNames.$render();

                if ($scope.selInterpret.iValid) {
                    $timeout(function() {
                        $scope.resetForm();
                        $scope.setEditorFormValidator('$setPristine')
                    }, 200);
                } else {
                    $timeout(function() {
                        $scope.resetForm();
                        $scope.setEditorFormValidator('$setDirty');
                        $scope.validatePythonScript()
                    }, 200);
                }
            }; // loadInterpretation

            $scope.isNumber = function (formField) {
                console.log("isNumber formField: " + formField);
                $scope.interpretationForm[formField].$commitViewValue();
                var x = $scope.interpretationForm[formField].$modelValue;
                if ($scope.interpretationForm[formField].$modelValue == null) {
                    $scope.interpretationForm[formField].$setValidity('number', true);
                    return true;
                }
                var retVal = false;
                try {
                    // if it passes the conversion then return true; o/w an exception is raised
                    if (Number(x)) retVal = true;
                } catch (e) {
                    retVal = false;
                }
                console.log("isNumber: '" + x + "'   " + retVal);
                $scope.interpretationForm[formField].$setValidity('number', retVal);
                console.log(formField + " valid: " + $scope.interpretationForm[formField].$valid);
                console.log(formField + " invalid: " + $scope.interpretationForm[formField].$invalid);
                return retVal;
            }; // isNumber

            $scope.isInteger = function (formField) {
                console.log("isInteger formField: " + formField);
                $scope.interpretationForm[formField].$commitViewValue();
                if ($scope.interpretationForm[formField].$modelValue == null) {
                    $scope.interpretationForm[formField].$setValidity('number', true);
                    return true;
                }
                var x = $scope.interpretationForm[formField].$modelValue;
                var retVal = false;
                try {
                    retVal = parseInt(Number(x), 10) === Number(x);
                } catch (e) {
                    retVal = false;
                }
                console.log("isInteger: '" + x + "'   " + retVal);
                $scope.interpretationForm[formField].$setValidity('number', retVal);
                console.log(formField + " valid: " + $scope.interpretationForm[formField].$valid);
                console.log(formField + " invalid: " + $scope.interpretationForm[formField].$invalid);
                return retVal;
            }; // isInteger

            $scope.updateInterpretation = function () {
                if ($scope.selInterpret === null || $scope.selInterpret.iName === "") return;
                console.log("$scope.selInterpret save/update");
                console.log($scope.selInterpret);
                //console.log("$scope.interpretationForm");
                //console.log($scope.interpretationForm);

                $scope.selInterpret.iScript = btoa($scope.editor.getValue() + $scope.defaultPythonEpilogScript);
                console.log("$scope.selInterpret.iScript");
                console.log(atob($scope.selInterpret.iScript));

                $scope.interpretationForm.sampleData.$commitViewValue();
                if ($scope.interpretationForm.sampleData.$modelValue) {
                    $scope.selInterpret.iSampleData = $scope.interpretationForm.sampleData.$modelValue.split("\n");
                }
                console.log("$scope.selInterpret.iSampleData");
                console.log($scope.selInterpret.iSampleData);

                $scope.interpretationForm.matchingNames.$commitViewValue();
                if ($scope.interpretationForm.matchingNames.$modelValue) {
                    $scope.selInterpret.iMatchingNames = $scope.interpretationForm.matchingNames.$modelValue.split("\n");
                }
                console.log("$scope.selInterpret.iMatchingNames");
                console.log($scope.selInterpret.iMatchingNames);
                // save new interpretations or update existing ones
                if ($scope.newInterpretation) {
                    console.log("save new interpretation");
                    $scope.interpretations[$scope.selInterpret["iName"]] = $scope.selInterpret;
                    interpretationResource.save(
                        {
                            domainId: $scope.selInterpret.iDomainId,
                            data: $scope.selInterpret
                        })
                        .$promise.then(function (response) {
                            console.log("added new interpretation successfully")
                            $scope.selInterpret.iId = response.returnValue;
                            $scope.newInterpretation = null;
                            $scope.interpretationForm.editorValidationMsg.$setPristine();
                            $scope.resetForm();
                            $scope.selInterpret.iValid = true;
                        }, function (error) {
                            console.log(error);
                            $scope.selInterpret.iValid = false;
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.creatingInterpretationsFailed.title,
                                        text: response.creatingInterpretationsFailed.title +
                                                " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    );
                } else if ($scope.selInterpret) {
                    console.log("update interpretation");
                    interpretationResource.update(
                        {
                            domainId: $scope.selInterpret.iDomainId,
                            data: $scope.selInterpret
                        })
                        .$promise.then(function (response) {
                            console.log("update interpretation successfully")
                            $scope.interpretationForm.editorValidationMsg.$setPristine();
                            $scope.resetForm();
                            $scope.selInterpret.iValid = true;
                        }, function (error) {
                            console.log(error);
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.updatingInterpretationsFailed.title,
                                        text: response.updatingInterpretationsFailed.message +
                                                " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    );
                }
            }; // updateInterpretation

            $scope.addNewInterpretation = function () {
                $scope.clearAllForms();
                $scope.newInterpretation =
                {
                    "iId": null,
                    "iDomainId": $scope.domainId,
                    "iName": "",
                    "iDescription": "",
                    "iConstraints": { quantized: "Unknown", ordinal: "Unknown", categorical: "Unknown", relational: "Unknown" },
                    "iScript": $scope.defaultPythonBodyScript,
                    "iValid": false,
                    "iSampleData": [],
                    "iMatchingNames": [],
                    "iConfidence": null
                };
                $scope.selInterpret = $scope.newInterpretation;
                $scope.editor.getSession().setValue($scope.selInterpret.iScript);
                $scope.selInterpretSampleData = "";
                $scope.selInterpretMatchingNames = "";
                $timeout(function() {
                    $scope.resetForm();
                    // reset from dirty back to pristine since the change was setting the default python script
                    $scope.selInterpret.iValid = true;
                    $scope.setEditorFormValidator('$pristine');
                    $timeout($scope.focusIname, 200);
                }, 400);
            }; // addNewInterpretation

            $scope.focusIname = function () {
                document.getElementById('newInterpretationName').focus();
            }; // focusIname

            $scope.cancelNewInterpretation = function () {
                $scope.newInterpretation = null;
                $scope.selInterpret = null;
                if (Object.keys($scope.interpretations)[0] === "$promise") {
                    $scope.navigateTo('/catalog');
                } else {
                    $scope.loadInterpretation(Object.keys($scope.interpretations)[0]);
                }
            }; // cancelNewInterpretation

            $scope.validatePythonScript = function () {
                console.log("validatePythonScript");
                // don't attempt to validate an unsaved interpretation;
                // it does not have an iId yet which is required for validation
                if ($scope.newInterpretation) return;
                pythonValidateResource.validate(
                    {
                        interpretationId: $scope.selInterpret.iId
                    })
                    .$promise.then(function (response) {
                        console.log("python script validated successfully");
                        console.log(response);
                        if (response.annotations.length > 0) {
                            console.log("python script validation had warnings");
                            console.log(response.annotations);
                            $scope.editor.getSession().setAnnotations(response.annotations);
                            $scope.setEditorFormValidator('$pristine', true);
                        } else {
                            $scope.editor.getSession().setAnnotations([]);
                            $scope.setEditorFormValidator('$pristine');
                        };
                        $scope.selInterpret.iValid = true;
                    }, function (error) {
                        console.log(error);
                        $scope.setEditorFormValidator('$dirty', true);
                        $scope.selInterpret.iValid = false;
                        if (error.status === 417) {
                            console.log("python script failed validation");
                            console.log(error.data.annotations);
                            $scope.editor.getSession().setAnnotations(error.data.annotations);
                        } else if (error.status === 500) {
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.validatingPythonFailed.title,
                                        text: response.validatingPythonFailed.message +
                                        " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    { templateUrl: 'schema-wizard/schema-wizard.confirm.template.html' })
                            })
                        }
                    }
                );
            }; // validatePythonScript

            $scope.testPythonScript = function () {
                console.log("testPythonScript");
                console.log($scope.selInterpret);
                if (!$scope.selInterpret.iValid) return;
                $scope.interpretationForm.console.value = null;
                $scope.consoleOutput = "";
                pythonTestResource.test(
                    {
                        interpretationId: $scope.selInterpret.iId
                    })
                    .$promise.then(function (response) {
                        console.log("python script tested successfully")
                        //console.log(response.consoleOutput);
                        $scope.consoleOutput = "Schema Wizard Interpretation Engine Python Test Runner\n\n" +
                                               response.consoleOutput['script-trace'] + "\n" +
                                               "Analysis of this interpretation returned: " +
                                               response.consoleOutput['script-result'];
                    }, function (error) {
                        console.log(error);
                        if (error.status === 417) {
                            console.log("python script execution threw exceptions");
                            console.log(error.data.consoleOutput);
                            $scope.consoleOutput = "Schema Wizard Interpretation Engine Python Test Runner\n\n" +
                                error.data.consoleOutput['script-trace'];
                        } else if (error.status === 500) {
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.validatingPythonFailed.title,
                                        text: response.validatingPythonFailed.message +
                                        " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    { templateUrl: 'schema-wizard/schema-wizard.confirm.template.html' })
                            })
                        }
                    }
                );
            }; // testPythonScript

            $scope.checkForDupNames = function () {
                $scope.interpretationForm.iName.$commitViewValue();
                var newInterpretationName = $scope.interpretationForm.iName.$modelValue;
                console.log("checkForDupNames newInterpretationName: " + newInterpretationName);
                if ($scope.interpretations.hasOwnProperty(newInterpretationName)) {
                    $confirm(
                        {
                            title: 'Create Interpretation',
                            text: "The interpretation '" + newInterpretationName + "' already exists in this domain.",
                            ok: 'OK'
                        },
                        {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                    .then(function () {
                        $scope.interpretationForm.iName.$setViewValue("");
                        $scope.interpretationForm.iName.$render();
                        $scope.interpretationForm.iName.$setValidity('required', false);
                        $timeout($scope.focusIname, 200);
                    });
                    return false;
                } else {
                    return true;
                }
            }; // checkForDupNames

            $scope.addIdentityToMatchingNames = function () {
                $scope.interpretationForm.iName.$commitViewValue();
                var newInterpretationName = $scope.interpretationForm.iName.$modelValue;
                console.log("addIdentityToMatchingNames newInterpretationName: " + newInterpretationName);
                $scope.newInterpretation['iMatchingNames'] = [];
                $scope.newInterpretation['iMatchingNames'].push(newInterpretationName);
                $scope.selInterpretMatchingNames = newInterpretationName + "\n";
                // work-around for angularjs bug ($setViewValue & $render)
                $scope.interpretationForm.matchingNames.$setViewValue($scope.selInterpretMatchingNames);
                $scope.interpretationForm.matchingNames.$render();
            }; // addNameToMatchingNames

            $scope.removeInterpretation = function (interpretationName) {
                console.log("removeInterpretation: " + interpretationName +
                    "(" + $scope.interpretations[interpretationName].iId + ")");
                $confirm({
                        title: 'Confirm Delete Interpretation',
                        text: "Confirm the request to delete this interpretations.",
                        ok: 'OK',
                        cancel: 'Cancel'
                    }
                ).then(function () {
                    console.log("deleting interpretation");
                    interpretationResource.remove(
                        {
                            domainId: $scope.interpretations[interpretationName].iDomainId,
                            interpretationId: $scope.interpretations[interpretationName].iId,
                            data: $scope.interpretations[interpretationName].iId
                        }
                    ).$promise.then(function (response) {
                            if ($scope.selInterpret && $scope.selInterpret.iName === interpretationName) {
                                $scope.clearAllForms();
                                // load 1st interpretation
                                $timeout(function() {
                                    if (Object.keys($scope.interpretations)[0] === "$promise") {
                                        $scope.addNewInterpretation();
                                    } else {
                                        $scope.loadInterpretation(Object.keys($scope.interpretations)[0]);
                                    }
                                }, 400);
                            }
                            delete $scope.interpretations[interpretationName];
                        }, function (error) {
                            console.log("error.status: " + error.status);
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.updatingInterpretationsFailed.title,
                                        text: response.updatingInterpretationsFailed.message +
                                                " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    )
                })
            }; // removeInterpretation

            $scope.initEditor = function () {
                //console.log("starting editor");
                $scope.editor=ace.edit('editor');
                $scope.editor.setTheme("ace/theme/" + $scope.editorThemes.selectedTheme);
                $scope.editor.getSession().setMode("ace/mode/python");
                $scope.editor.getSession().setUseWorker(false);
                $scope.editor.$blockScrolling = Infinity;
                $scope.editor.resize();

                $scope.$on("resizeEditor", function(event, args) {
                    //console.log("resize editor");
                    $scope.editor.resize();
                }); // onResizeEditor

                $scope.editor.on("change", function(e) {
                    //console.log("editor changed, setting form to dirty");
                    $scope.interpretationForm.editorValidationMsg.$setDirty();
                    $scope.setEditorFormValidator('$dirty');
                    if ($scope.selInterpret) $scope.selInterpret.iValid = false;
                }); // editorOnChange
            }; // initEditor

            $scope.setEditorFormValidator = function (state, problem) {
                $timeout(function() {
                    $scope.$apply(function() {
                        switch (state) {
                            case "$pristine":
                                if (problem) {
                                    $scope.editorValidationState = "Script validation found warnings";
                                } else {
                                    $scope.editorValidationState = "Script validation passed";
                                }
                                $scope.editorValidationMsgStyle = { "color": "green" };
                                $scope.interpretationForm.editorValidationMsg.$setPristine();
                                break;
                            case "$dirty":
                                if (problem) {
                                    $scope.editorValidationState = "Script validation found errors";
                                } else {
                                    $scope.editorValidationState = "Script requires validation";
                                }
                                $scope.editorValidationMsgStyle = { "color": "red" };
                                $scope.interpretationForm.editorValidationMsg.$setDirty();
                                break;
                            default:
                                break;
                        }
                    })
                }, 100)
            }; // setEditorFormValidator

            $scope.changeEditorTheme = function () {
                //console.log("$scope.changeEditorTheme: '" + $scope.editorThemes.selectedTheme + "'");
                $scope.editor.setTheme("ace/theme/" + $scope.editorThemes.selectedTheme);
                $cookies.put('schwiz.editor.theme', $scope.editorThemes.selectedTheme);
            }; // changeEditorTheme

            $scope.editorThemes = {
                selectedTheme: ($cookies.get('schwiz.editor.theme') ? $cookies.get('schwiz.editor.theme') : 'chrome'),
                availableThemes: [
                    { name: 'ambiance' },
                    { name: 'chaos' },
                    { name: 'chrome' },
                    { name: 'clouds' },
                    { name: 'clouds_midnight' },
                    { name: 'cobalt' },
                    { name: 'crimson_editor' },
                    { name: 'dawn' },
                    { name: 'dreamweaver' },
                    { name: 'eclipse' },
                    { name: 'github' },
                    { name: 'idle_fingers' },
                    { name: 'iplastic' },
                    { name: 'katzenmilch' },
                    { name: 'kr_theme' },
                    { name: 'kuroir' },
                    { name: 'merbivore' },
                    { name: 'merbivore_soft' },
                    { name: 'monokai' },
                    { name: 'mono_industrial' },
                    { name: 'pastel_on_dark' },
                    { name: 'solarized_dark' },
                    { name: 'solarized_light' },
                    { name: 'sqlserver' },
                    { name: 'terminal' },
                    { name: 'textmate' },
                    { name: 'tomorrow' },
                    { name: 'tomorrow_night' },
                    { name: 'tomorrow_night_blue' },
                    { name: 'tomorrow_night_bright' },
                    { name: 'tomorrow_night_eighties' },
                    { name: 'twilight' },
                    { name: 'vibrant_ink' },
                    { name: 'xcode' }
                ]
            }

        }]);
})();
