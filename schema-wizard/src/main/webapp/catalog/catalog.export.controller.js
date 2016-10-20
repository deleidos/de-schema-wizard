(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('exportController', function ($scope, $uibModal, $window, $confirm, $http) {
        $scope.exportSchema = function (schema) {

            //open modal for REST
            $scope.restModal = function(){
                $confirm({
                        title: 'Rest Endpoint',
                        text: "Enter a REST Endpoint:",
                        ok: 'Submit',
                        cancel:'Cancel'
                    },
                    {templateUrl: 'catalog/catalog.export.confirm.modal.template.html'}
                ).then(function () {
                   $scope.restEndpoint =  document.getElementById('restInput').value;
                    var schemaData = angular.toJson(schema, true);
                    schemaData = schemaData.replace(/\n/g, "\r\n");
                    var file = schemaData;
                    var fd = new FormData();
                    fd.append('file', schemaData);
                         $http.post($scope.restEndpoint, fd, {
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined,
                            'enctype': "multipart/form-data"
                        }
                    });
                    console.log("Sent to endpoint: " + $scope.restEndpoint);
                    // console.log(schemaData);
                })

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
                    } catch(ex){
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
                    vizWizArrayObject.interpretation = value['interpretation']['iName'];
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
                } catch(ex){
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
            //Zip for DigitalEdge
            $scope.zipExtractDigitalEdge = function () {
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
            };

            // export viz
            var vizData = angular.toJson(vizWizExportObject, true);
            vizData = vizData.replace(/\n/g, "\r\n")
            var blobViz = new Blob([vizData], {type: "octet/stream"})
            var urlViz = $window.URL || $window.webkitURL;
            $scope.fileUrlWiz = urlViz.createObjectURL(blobViz);
            $scope.vizWizDataObject = $scope.name + ".json"

            //export to Ingest
            var ingestData = angular.toJson(ingestExportObject, true);
            ingestData = ingestData.replace(/\n/g, "\r\n")
            var blobIngest = new Blob([ingestData], {type: "octet/stream"})
            var urlIngest = $window.URL || $window.webkitURL;
            $scope.fileUrlIngest = urlIngest.createObjectURL(blobIngest);
            $scope.ingestDataObject = $scope.name + ".json"

            // File export for other file types
            var schemaData = angular.toJson(schema, true);
            schemaData = schemaData.replace(/\n/g, "\r\n")
            //console.log(schemaData)
            var blob = new Blob([schemaData], {type: "octet/stream"})
            var url = $window.URL || $window.webkitURL;
            $scope.fileUrl = url.createObjectURL(blob);
            $scope.json = $scope.name + ".json";
            $scope.txt = $scope.name + ".txt";
            $scope.doc = $scope.name + ".doc";
            $scope.zip = $scope.name + ".zip";

            $scope.zipExtract = function () {
                var zip = new JSZip();
                zip.file($scope.json, schemaData);
                zip.file($scope.txt, schemaData);
                zip.file($scope.doc, schemaData);
                var content = zip.generate({type: "blob"});
                // see FileSaver.js
                saveAs(content, $scope.name);
            };

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'schema-wizard/schema-wizard.export.html',
                controller: 'exportInstanceController',
                scope: $scope,
                size: '560',
                backdrop: 'static'
            });
            modalInstance.result.then(function () {
            }, function () {
                console.log("exportSchema Modal dismissed");
            });
        }; // exportSchema
    }); // exportController

    // $uibModalInstance represents a modal window (instance) dependency.
    schemaWizardApp.controller('exportInstanceController', function ($scope, $uibModalInstance) {
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }); // exportInstanceController

})();
