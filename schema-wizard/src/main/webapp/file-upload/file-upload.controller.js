(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive('fileModel', ['$parse', '$confirm',
        function ($parse, $confirm) {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var model = $parse(attrs.fileModel);
                    var modelSetter = model.assign;
                    scope.sampleFile = model;
                    element.bind('change', function () {
                        // if the file open dialog is raised and the file name
                        // is cleared and cancel is pressed then a reset is needed
                        document.getElementById('file-upload-btn').disabled = true;
                        // status always needs reset if choosing another file
                        scope.$apply(function () {
                            modelSetter(scope, element[0].files);
                            if (document.getElementById('file-upload').files) {
                                // This iterates over to see if the total files size is greater than 100MB
                                const maxFilesSize = 104857600;
                                var totalFilesSize = 0;
                                var numberOfDataSamples = element[0].files.length;
                                for (var i = 0; i < element[0].files.length; i++) {
                                    totalFilesSize = element[0].files[i].size + totalFilesSize;
                                    numberOfDataSamples = element[0].files[i];
                                    if (totalFilesSize > maxFilesSize) {
                                        $confirm(
                                            {
                                                title: 'File(s) size is too large',
                                                text: "The total file(s) size(s) are over the recommended limit of 100 MB.",
                                                ok: 'OK'
                                            },
                                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                            .then(function () {
                                                totalFilesSize = 0;
                                                document.getElementById('file-upload-btn').disabled = true;
                                            });
                                    }
                                }
                                for (var i = 0; i < element[0].files.length; i++) {
                                    if (element[0].files[i].name.indexOf(".xlsx") !== -1) {
                                        $confirm(
                                            {
                                                title: 'Schema Wizard does not support this file type',
                                                text: "Schema Wizard does not support the file type for: \n" + element[0].files[i].name,
                                                ok: 'OK'
                                            },
                                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                            .then(function () {
                                            });
                                    }// try and prevent users from uploading unsupported files.
                                     //this is a work-around for IE

                                }
                            }
                        });
                    });
                } // link
            };
        }]); // fileModel

    schemaWizardApp.service('fileUpload', ['$http', '$rootScope',
        function ($http, $rootScope) {
            this.uploadFileToUrl = function (file, uploadUrl) {
                //console.log("file(s)");
                for (i = 0; i < $rootScope.originalSeedFileArray.length; i++) {
                    for (j = 0; j < file.length; j++) {
                        if (file[j].label == $rootScope.originalSeedFileArray[i].name) {
                            // console.log("pushed to" + file[j].label);
                            // console.log($rootScope.originalSeedFileArray[i].name);
                            // console.log(file[j].label);
                            file[j].file = $rootScope.originalSeedFileArray[i];
                        }
                        else {
                            // console.log("originalSeedFileArray[" + i + "], file[" + j + "]")
                            // console.log("originalSeedFileArray is " + $rootScope.originalSeedFileArray[i].name)
                            // console.log("file is " + file[j].label)
                        }
                    }
                }
                console.log(file)
                var fd = new FormData();
                var seedFile = $rootScope.seedFileName;
                var keyValue = -1;
                angular.forEach(file, function (value, key) {
                    if (value.label == seedFile) {
                        fd.append(++keyValue, value.file);
                    }
                });
                angular.forEach(file, function (value, key) {
                    if (value.label != seedFile) {
                        fd.append(++keyValue, value.file);
                    }
                });
                return $http.post(uploadUrl, fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined,
                        'enctype': "multipart/form-data"
                    }
                })
            }; // uploadFileToUrl
        }]); // fileUpload

    schemaWizardApp.controller('fileUploadCtrl',
        function ($scope, $rootScope, $http, UploadParameters, fileUpload, $confirm) {
            window.onbeforeunload = function() {
                return "Reloading will clear your session";
            };
            document.getElementById('titleRef').style.pointerEvents = 'none';
            //TODO think of a better way to handle scoping issues to a Service
            $rootScope.originalSeedFileArray = [];
            $scope.seedFileName = "";
            $scope.seedFileObject = {
                selected: null,
                lists: {"seed": []}
            };
            $scope.clearFiles = function () {
                $scope.seedFileObject = {
                    selected: null,
                    lists: {"seed": []}
                };
                document.getElementById('file-upload').value = "";
            };
            $scope.dropCallback = function (event, index, item, external, type) {
                if (external) {
                    if (allowedType === 'itemType' && !item.label && !item.file) return false;
                    if (allowedType === 'containerType' && !angular.isArray(item)) return false;
                }
                return item;
            };

            $scope.removeFile = function (item) {
                for (i = 0; i < document.getElementById('file-upload').files.length; i++) {
                    if (item.label == $scope.seedFileObject.lists.seed[i].label) {
                        $scope.seedFileObject.lists.seed.splice(i, 1);
                    }
                }
            };

            $scope.$watch('seedFileObject', function (model) {
                if (document.getElementById('file-upload').files.length > 0) {
                    console.log($scope.seedFileObject.lists.seed[0].label);
                    $scope.seedFileName = $scope.seedFileObject.lists.seed[0].label;
                    //TODO think of a better way to handle scoping issues to a Service
                    $rootScope.seedFileName = $scope.seedFileName;
                }

            }, true);
            $scope.selected = null;
            $scope.hideMask = function () {
                document.getElementById("mask").style.display = "none";
            };

            $scope.setSchemaDomain = function ($event, schemaDomain) {
                if ($event !== null) $event.preventDefault();
                $scope.schemaDomain = schemaDomain;
                $rootScope.$broadcast("setSchemaDomain", {
                    schemaDomain: $scope.schemaDomain
                });
            }; // setSchemaDomain

            $scope.setSchemaTolerance = function ($event, schemaTolerance) {
                if ($event) $event.preventDefault();
                $scope.schemaTolerance = schemaTolerance;
            }; // setSchemaTolerance
            // set default to "Strict"
            $scope.setSchemaTolerance(null, "Strict");

            $scope.domains = UploadParameters.get().domainsArray;
            console.log("Domains");
            console.log($scope.domains);
            if ($scope.domains.length === 0) {
                $scope.schemaDomain = "Not Available";
            }
            ;
            $scope.setSchemaDomain(null, $scope.schemaDomain);

            // disable the domain button when modifying an existing schema; o/w enable it
            document.getElementById('btn-append-to-domain-button').disabled = !(($scope.schemaDomain === null || $scope.schemaDomain === undefined || $scope.schemaDomain === '')
            && $scope.schemaDomain !== "Not Available");

            // set the domain to the one specified in the URL query string if provided, only for new schema
            if (($scope.schemaDomain === null || $scope.schemaDomain === undefined || $scope.schemaDomain === '')
                && $scope.preferredSchemaDomain !== null && $scope.preferredSchemaDomain !== undefined) {
                $scope.schemaDomain = $scope.preferredSchemaDomain;
            }
            ;
            $scope.setSchemaDomain(null, $scope.schemaDomain);
            //console.log("$scope.schemaDomain");
            //console.log("'" + $scope.schemaDomain + "'");

            // enable/disable the upload button when all parameters are set

            $scope.$watch(function (scope) {
                    return scope.schemaDomain && scope.schemaTolerance &&
                        document.getElementById('file-upload').files.length > 0;
                },
                function () {
                    if (document.getElementById('file-upload').files.length > 0 &&
                        $scope.schemaDomain != null && $scope.schemaTolerance != null) {
                        document.getElementById('file-upload-btn').disabled = false;
                    } else {
                        document.getElementById('file-upload-btn').disabled = true;
                    }
                });

            $scope.$watch(function (scope) {
                    return document.getElementById('file-upload').files.length > 0;
                },
                function () {
                    if(document.getElementById('file-upload').files.length > 0){
                        for (i = 0; i < document.getElementById('file-upload').files.length; i++) {
                            if (document.getElementById('file-upload').files[i].name.indexOf(".xlsx") == -1) {
                                $scope.fileDialogHasBeenOpened = +1;
                                $rootScope.originalSeedFileArray.push(document.getElementById('file-upload').files[i]);
                                $scope.seedFileObject.lists.seed.push({
                                    label: document.getElementById('file-upload').files[i].name,
                                    file: document.getElementById('file-upload').files[i],
                                    originalIndex: i
                                });
                            }
                        }
                    }
                    if (document.getElementById('file-upload').files.length > 0 &&
                        $scope.schemaDomain != null && $scope.schemaTolerance != null) {
                        document.getElementById('file-upload-btn').disabled = false;
                    } else {
                        document.getElementById('file-upload-btn').disabled = true;
                    }
                }
            ); // watchFileUpload

            $scope.uploadFile = function () {
                $scope.numberOfFiles = $scope.seedFileObject.lists.seed.length;
                var file = $scope.seedFileObject.lists.seed;
                $scope.filesTotalSize = 0;
                for (var i = 0; i < $scope.numberOfFiles; i++) {
                    $scope.filesTotalSize = document.getElementById('file-upload').files[i].size + $scope.filesTotalSize;
                }
                //console.log("fileUploadCtrl file(s) to upload:");
                //console.dir(file);

                $rootScope.$broadcast("sampleFilesSelected", {
                    newSampleFiles: file
                });

                fileUpload.uploadFileToUrl(
                    file,
                    "rest/upload?" +
                    "domain=" + $scope.schemaDomain + "&" +
                    "tolerance=" + $scope.schemaTolerance + "&" +
                    "schemaGuid=" + ($scope.modifySchemaMode ? $scope.currentSchema.sId : null) + "&" +
                    "numberOfFiles=" + $scope.numberOfFiles + "&" +
                    "filesTotalSize=" + $scope.filesTotalSize)
                    .success(function (data) {
                        console.log("Data returned:");
                        console.log(data);
                        console.log("File(s) uploaded successfully.");
                        // create an event for the enclosing controller so
                        // it can update the data sources in it's scope
                        $rootScope.$broadcast("dataSamplesReceived", {
                            newDataSamples: data
                        });
                        $rootScope.$broadcast("closeWebSocket", {});
                    })
                    .error(function (data) {
                        console.log("file upload failed")
                        console.log(data);
                        data = data.trim();
                        if (data == "Server timed out trying to reach the Python Interpretation Engine.") {
                            $confirm(
                                {
                                    title: 'File Upload Failed - Interpretation Engine',
                                    text: "Server timed out trying to reach the Python Interpretation Engine. Check the documentation if this persists.",
                                    ok: 'OK'
                                },
                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                .then(function () {
                                    $rootScope.$broadcast("closeWebSocket", {});
                                    console.log(data);
                                    $scope.hideMask();
                                    $scope.navigateTo("/catalog");
                                });
                        }
                        else {
                            $confirm(
                                {
                                    title: 'File Upload Failed',
                                    text: "Uploading of the file(s) has failed. Click 'Ok' to try again.",
                                    ok: 'OK'
                                },
                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                .then(function () {
                                    $rootScope.$broadcast("closeWebSocket", {});
                                    console.log(data);
                                    $scope.hideMask();
                                    $scope.navigateTo("/catalog");
                                });

                        }
                    })

            }; // uploadFile
        }); // fileUploadCtrl
})();
