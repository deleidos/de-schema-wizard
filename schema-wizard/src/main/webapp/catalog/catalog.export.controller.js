(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('exportCtrl', function ($scope, $uibModal, $window, $confirm, $http, $location) {
        $scope.exportSchema = function (schema) {
            $scope.fileContentButton = "File Format";
            $scope.selectFileContentType = ['Schema Wizard Format', 'VizWiz Format', 'DC2F Format', 'Ingest Format', 'DigitalEdge Format', 'SQL'];
            $scope.fileTypeButton = "File Type";
            $scope.selectFileType = [".doc", ".json", ".txt", ".zip"];
            $scope.fileDestinationButton = "Choose File Destination";
            $scope.selectDestination = ['Download', 'REST'];
            $scope.fileTypeToDownload = "";
            $scope.sqlType = "";
            $scope.name="";
            $scope.sqlArray = ['MySQL', 'SQLServer', 'Oracle10g','H2','Postgres'];

            $scope.setFileContentType = function (file) {
                $scope.fileContentButton = file;
                $scope.fileTypeButton = "File Type";
                if ($scope.fileContentButton == 'SQL') {
                    $scope.selectFileType = $scope.sqlArray;
                }
                else {
                    $scope.selectFileType = [".doc", ".json", ".txt", ".zip"];
                }
            };
            $scope.setFileFormatType = function (file) {
                $scope.fileTypeButton = file;
                if($scope.sqlArray.indexOf(file)!=-1){
                    $scope.sqlType = file.toLowerCase();
                }
                if ($scope.sqlType != "") {
                    $scope.sqlDownload = "";
                    $scope.sqlData = {"schema-guid": schema.sId, "export-type":"sql", "sql-type": $scope.sqlType};
                    var restURL =
                        $location.protocol() + "://" +
                        $location.host() + ":" +
                        $location.port() +
                        "/schwiz/rest/export";
                    $scope.sqlType = "";
                    $http({
                        method: 'POST',
                        url: restURL,
                        data: $scope.sqlData
                    }).then(function successCallback(response) {
                        $scope.sqlDownload = response.data['export-text'];
                        $scope.name = schema.sName;
                        $scope.fileTypeButton = ".txt";

                        var sqlData = $scope.sqlDownload;
                        var blobSql = new Blob([sqlData], {type: "octet/stream"});
                        var urlSql = $window.URL || $window.webkitURL;
                        $scope.fileUrlSql = urlSql.createObjectURL(blobSql);

                        $scope.fileTypeToDownload = $scope.fileUrlSql;

                    }, function errorCallback(response) {
                        //error on sql type
                        console.log(response);
                    });
                }
            };
            $scope.setFileDestination = function (file) {
                $scope.fileDestinationButton = file;
            };

            $scope.$watch(function ($scope) {
                    return $scope.fileTypeButton
                },
                function () {


                    if ($scope.fileTypeButton != ".json") {
                        $scope.fileDestinationButton = "Download";
                    }
                });

            $scope.$watch(function ($scope) {
                    return $scope.fileContentButton
                },
                function () {

                    if ($scope.fileContentButton == "DigitalEdge Format") {
                        $scope.fileTypeButton = ".zip";
                        $scope.fileDestinationButton = "Download";
                    }
                    switch ($scope.fileContentButton) {
                        case"Schema Wizard Format":
                            $scope.fileTypeToDownload = $scope.fileUrl;
                            $scope.zipDownloadType = schemaData;
                            console.log("Schema Wizard Download");
                            break;
                        case"VizWiz Format":
                            $scope.fileTypeToDownload = $scope.fileUrlWiz;
                            $scope.zipDownloadType = vizData;
                            console.log("Viz Download");
                            break;
                        case"SQL":
                            $scope.fileTypeToDownload = $scope.sqlDownload
                            console.log("SQL Download");
                            break;
                        case"Ingest Format":
                            $scope.fileTypeToDownload = $scope.fileUrlIngest;
                            $scope.zipDownloadType = ingestData;
                            console.log("Ingest Download");
                            break;
                        case"DigitalEdge Format":
                            console.log("DigitalEdge Download");
                            // Has own zip type
                            break;
                        case"DC2F Format":
                            $scope.fileTypeToDownload = $scope.fileUrl;
                            $scope.zipDownloadType = schemaData;
                            console.log("DC2F");
                            break;
                        default:
                            console.log("Unable to find specific File Type")
                    }
                });

            //open modal for REST
            $scope.restModal = function () {
                $scope.restEndpoint = document.getElementById('restInput').value;
                //$scope.zipDownloadType refers to the data type selected
                console.log($scope.zipDownloadType)
                var schemaData = angular.toJson($scope.zipDownloadType);
                console.log("Trying: " + $scope.restEndpoint);
                var removeExtraQuote = /^\"|\"$/g;
                schemaData = schemaData.replace(removeExtraQuote, " ");
                console.log(schemaData);
                $http({
                    method: 'POST',
                    url: $scope.restEndpoint,
                    data: schemaData
                }).then(function successCallback(response) {
                    console.log(response);
                    console.log("Sent to endpoint: " + $scope.restEndpoint);

                }, function errorCallback(response) {
                    console.log(response);
                });
            };

            $scope.name = schema.sName;
            //Export for Ingest
            var ingestExportObject = {};
            // Export for VizWiz
            var vizWizExportObject = {};
            vizWizExportObject.sId = schema.sId;
            vizWizExportObject.sName = $scope.name;
            vizWizExportObject.sProfile = [];
            //DigitalEdge
            var digitalEdgeExportObject = {};
            digitalEdgeExportObject.sProfile = {};

            var fieldCount = 0;
            angular.forEach(schema.sProfile, function (value, key) {
                fieldCount += 1;
                var vizWizArrayObject = {};
                if (value['presence'] === -1) {
                    vizWizArrayObject.fullName = key;
                    try {
                        vizWizExportObject.sProfile[key].mainType = (value['main-type']);
                    } catch (ex) {
                        vizWizArrayObject.mainType = "string";
                    }
//TODO: remove try/catch after detail gets set for user added fields (ref VersionOne B-06537)
                    try {
                        vizWizExportObject.sProfile[key].detailType = (value['detail']['detail-type']);
                    } catch (ex) {
                        vizWizArrayObject.detailType = "Unknown";
                    }
                    vizWizArrayObject.numberDistinctValues = 0;
                    vizWizArrayObject.interpretations = "Unknown";
                    vizWizExportObject.sProfile.push(vizWizArrayObject);
                } else {
                    // creates the array of objects for vizWiz
                    vizWizArrayObject.fullName = key;
                    vizWizArrayObject.displayName = value['display-name'];
                    vizWizArrayObject.interpretation = value['interpretations']['selectedOption']['iName'];
                    vizWizArrayObject.attributes = {};
                    vizWizArrayObject.attributes.identifier = value['attributes']['identifier'];
                    vizWizArrayObject.attributes.categorical = value['attributes']['categorical'];
                    vizWizArrayObject.attributes.quantitative = value['attributes']['quantitative'];
                    vizWizArrayObject.attributes.relational = value['attributes']['relational'];
                    vizWizArrayObject.attributes.ordinal = value['attributes']['ordinal'];
                    vizWizArrayObject.mainType = value['main-type'];
                    vizWizArrayObject.detailType = value['detail']['detail-type'];
                    vizWizArrayObject.presence = value['presence'];
                    vizWizArrayObject.numberDistinctValues = value['detail']['num-distinct-values'];
                    vizWizExportObject.sProfile.push(vizWizArrayObject);
                }
                // DigitalEdge
                digitalEdgeExportObject.sProfile[key] = (value['main-type']);
                //Ingest Object
                try {
                    ingestExportObject[key] = "get(" + (value['alias-names'][0]['alias-name']) + ")";
                } catch (ex) {
                    ingestExportObject[key] = "get(" + "null" + ")";
                }
            });
            //Export DigitalEdge
            var digitalEdgeData = angular.toJson(digitalEdgeExportObject, true);
            digitalEdgeData = digitalEdgeData.replace(/\n/g, "\r\n");
            var blobDigitalEdge = new Blob([digitalEdgeData], {type: "octet/stream"});
            var urlDigitalEdge = $window.URL || $window.webkitURL;
            $scope.fileUrlDigital = urlDigitalEdge.createObjectURL(blobDigitalEdge);
            $scope.digitalEdgeDataObject = $scope.name + ".json";
            $scope.zipIngestObject = $scope.name + ".json";

            $scope.genericZipDownload = function () {
                if ($scope.fileContentButton == "DigitalEdge Format") {
                    var zipDigitalEdge = new JSZip();
                    var emptyArrayForDigitalEdge = "[ ]";
                    $scope.jsonEnrichment = "datasources.json";
                    $scope.jsonDataSource = "enrichcfg.json";
                    $scope.jsonCanonical = "canonical.json";
                    zipDigitalEdge.file($scope.jsonEnrichment, emptyArrayForDigitalEdge);
                    zipDigitalEdge.file($scope.jsonDataSource, emptyArrayForDigitalEdge);
                    zipDigitalEdge.file($scope.jsonCanonical, digitalEdgeData);
                    var content = zipDigitalEdge.generate({type: "blob"});
                    // see FileSaver.js
                    saveAs(content, $scope.name);
                }
                else {
                    console.log("zip download");
                    var zip = new JSZip();
                    zip.file($scope.json, $scope.zipDownloadType);
                    zip.file($scope.txt, $scope.zipDownloadType);
                    zip.file($scope.doc, $scope.zipDownloadType);
                    var content = zip.generate({type: "blob"});
                    // see FileSaver.js
                    saveAs(content, $scope.name);
                }

            };

            // export viz
            var vizData = angular.toJson(vizWizExportObject, true);
            vizData = vizData.replace(/\n/g, "\r\n");
            var blobViz = new Blob([vizData], {type: "octet/stream"});
            var urlViz = $window.URL || $window.webkitURL;
            $scope.fileUrlWiz = urlViz.createObjectURL(blobViz);
            $scope.vizWizDataObject = $scope.name + ".json";

            //export to Ingest
            var ingestData = angular.toJson(ingestExportObject, true);
            ingestData = ingestData.replace(/\n/g, "\r\n");
            var blobIngest = new Blob([ingestData], {type: "octet/stream"});
            var urlIngest = $window.URL || $window.webkitURL;
            $scope.fileUrlIngest = urlIngest.createObjectURL(blobIngest);
            $scope.ingestDataObject = $scope.name + ".json";

            // File export for other file types
            var schemaData = angular.toJson(schema, true);
            schemaData = schemaData.replace(/\n/g, "\r\n");
            //console.log(schemaData)
            var blob = new Blob([schemaData], {type: "octet/stream"});
            var url = $window.URL || $window.webkitURL;
            $scope.fileUrl = url.createObjectURL(blob);
            $scope.json = $scope.name + ".json";
            $scope.txt = $scope.name + ".txt";
            $scope.doc = $scope.name + ".doc";
            $scope.zip = $scope.name + ".zip";

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'schema-wizard/schema-wizard.export.html',
                controller: 'exportInstanceCtrl',
                scope: $scope,
                size: '560',
                backdrop: 'static'
            });
            modalInstance.result.then(function () {
            }, function () {
                console.log("exportSchema Modal dismissed");
            });
        }; // exportSchema
    }); // exportCtrl

    // $uibModalInstance represents a modal window (instance) dependency.
    schemaWizardApp.controller('exportInstanceCtrl', function ($scope, $uibModalInstance) {
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }); // exportInstanceCtrl

})();
