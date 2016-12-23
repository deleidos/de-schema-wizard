(function () {

    var uploadApp = angular.module('uploadApp');

    uploadApp.directive('fileModel', ['$parse', '$log',
        function ($parse, $log) {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var model = $parse(attrs.fileModel);
                    var modelSetter = model.assign;
                    scope.sampleFile = model;
                    element.bind('change', function () {
                        // if the file open dialog is raised and the file name
                        // is cleared and cancel is pressed then a reset is needed
                        document.getElementById('file-upload-name').innerHTML = "";
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
                                    numberOfDataSamples = element[0].files[i]
                                }
                                for (var i = 0; i < element[0].files.length; i++) {
                                     //this is a work-around for IE
                                    document.getElementById('file-upload-name').innerHTML +=
                                        element[0].files[i].name + " ";
                                    document.getElementById('file-upload-btn').disabled = false;
                                }
                            }
                        });
                    });
                } // link
            };
        }]); // fileModel

    uploadApp.service('fileUpload', ['$http', '$log',
        function ($http, $log) {
            this.uploadFileToUrl = function (file, uploadUrl) {
                $log.debug("file(s)");
                $log.debug(file);
                var fd = new FormData();
                angular.forEach(file, function (value, key) {
                    fd.append(key, value);
                });
/*'Access-Control-Allow-Origin': "http://localhost:63342/tree-table-directive",*/
                return $http.post(uploadUrl, fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined,
                        'enctype': "multipart/form-data",
                        'Access-Control-Allow-Origin': "http://localhost:63342",
                        'Access-Control-Allow-Methods': "POST"
                    }
                })
            }; // uploadFileToUrl
        }]); // fileUpload

    uploadApp.controller('fileUploadCtrl',
        function ($scope, $rootScope, $log, $http, fileUpload) {
            // enable/disable the upload button when all parameters are set
            $scope.$watch(function (scope) {
                    return document.getElementById('file-upload').files.length > 0;
                },
                function () {
                    if (document.getElementById('file-upload').files.length > 0) {
                        document.getElementById('file-upload-btn').disabled = false;
                    } else {
                        document.getElementById('file-upload-btn').disabled = true;
                    }
                }
            ); // watchFileUpload

            $scope.uploadFile = function () {
                var file = $scope.sampleFile;
                $scope.numberOfFiles = document.getElementById('file-upload').files.length;
                $scope.filesTotalSize = 0;
                for (var i = 0; i < document.getElementById('file-upload').files.length; i++) {
                    $scope.filesTotalSize = document.getElementById('file-upload').files[i].size + $scope.filesTotalSize;
                }
                $log.debug("fileUploadCtrl file(s) to upload:");
                console.dir(file);
                $rootScope.$broadcast("sampleFilesSelected", {
                    newSampleFiles: file
                });

                fileUpload.uploadFileToUrl(
                    file,
                    "http://localhost:8080/schwiz/rest/upload?" +
                    "domain=transportation&" +
                    "tolerance=strict&" +
                    "schemaGuid=null&" +
                    "numberOfFiles=" + $scope.numberOfFiles + "&" +
                    "filesTotalSize=" + $scope.filesTotalSize)
                    .success(function (data) {
                        $log.debug("Data returned:");
                        $log.debug(data);
                        $log.info("File(s) uploaded successfully.");
                    })
                    .error(function (data) {
                        $log.debug("file upload failed")
                    })
            }; // uploadFile
        }); // fileUploadCtrl
})();
