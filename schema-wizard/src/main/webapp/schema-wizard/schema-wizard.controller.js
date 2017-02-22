(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');
    schemaWizardApp.constant("matchConfidenceThreshold", 94);
    schemaWizardApp.constant("defaultInterpretationMatch", false);
    schemaWizardApp.controller('schemaWizardCtrl', ['$scope', '$rootScope', '$window',
        '$cookies', '$location', '$http', '$routeParams', '$uibModal', '$timeout',
        '$interval', 'DomainInformation', 'Server', 'UploadParameters', 'version',
        'matchConfidenceThreshold', 'defaultInterpretationMatch', 'myModals', '$confirm', '$sce',
        'guidedTourStepFactory', '$q', 'uiTourService', 'statusCodesFactory', 'Globals', 'Utilities',
        function ($scope, $rootScope, $window, $cookies, $location, $http, $routeParams,
                  $uibModal, $timeout, $interval,
                  DomainInformation, Server, UploadParameters, version,
                  matchConfidenceThreshold, defaultInterpretationMatch, myModals, $confirm, $sce,
                  guidedTourStepFactory, $q, TourService, statusCodesFactory, Globals, Utilities, Idle, Keepalive) {

            $scope.userid = Globals.setDefaultUserId();

            $scope.userRole = "";

            $scope.$on('did-not-set-security-questions', function(event, args) {
                console.log("user has not set questions");
                $scope.waitForDom = function () {
                    $confirm({
                            title: 'Security Questions have not been set',
                            text: "Security questions have not been set.\nPress 'OK' to navigate to settings and set them.",
                            ok: 'OK',
                            cancel: 'Cancel'
                        }
                    ).then(function () {
                        $scope.navigateTo("/userPage");
                    })
                };
                $timeout($scope.waitForDom,4000);
            });

            $scope.$on("userRoleChanged", function (event, args) {
                console.log(args)
                $scope.userRole = args.userRole;
            }); // onUserIdChanged

            $scope.$on("userIdChanged", function (event, args) {
                $scope.userid = args.userId;
            }); // onUserIdChanged

            $scope.logout =  function () {
                var restURL =
                    $location.protocol() + "://" +
                    $location.host() + ":" +
                    $location.port() +
                    "/schwiz/rest/logout";
                $http({
                    method: 'POST',
                    url: restURL
                }).then(function successCallback(response) {
                    console.log(response);
                    Globals.setDefaultUserId();
                    $scope.navigateTo("/login");
                }, function errorCallback(response) {
                    console.log(response)
                });
            }; // logout

            $scope.fileIndex = -1;

            $scope.animationsEnabled = true;
            $scope.isCollapsed = true;
            $scope.modifySchemaMode = false;
            Utilities.setModifySchemaMode(false);
            $scope.confidenceThreshold = matchConfidenceThreshold;
            $scope.interpretationMatch = defaultInterpretationMatch;

            $scope.initializeModels = function () {
                $scope.model = {
                    "dataSamples": [],
                    "properties": {}
                };
                $scope.detailModels = {
                    selected1: null,
                    selected2: null,
                    dataList: {"data": []},
                    detailPanels: {"panel1": [], "panel2": []}
                };
            }; // initializeModels
            $scope.initializeModels();

            guidedTourStepFactory.get().$promise.then(function (response) {
                $rootScope.tourInformation = response;
                $scope.mainTour = $rootScope.tourInformation.mainTour;
                $scope.inspectionTour = $rootScope.tourInformation.inspectSampleTour;
                $scope.matchFieldsTour = $rootScope.tourInformation.matchFieldsTour;
                $scope.finalizeSchemaTour = $rootScope.tourInformation.finalizeSchemaTour;
                console.log($rootScope.tourInformation)
            });
            if ($scope.path == "/wizardInspectSamples") {
                if ($cookies.get('schwiz.tours.inspectSample') !== "visited") {
                    $timeout(function () {
                        TourService.getTourByName('catalog').startAt('300');
                    }, 2500);
                    $cookies.put('schwiz.tours.interpretations', "visited");
                }
            }
            $scope.fileIndex = -1;

            $rootScope.placeHolderForLongFieldName = "Hover over points to see full name";
            $rootScope.hoverLongFieldName = function (points, evt) {
                try {
                    $scope.index = points[0]['_index'];
                    $rootScope.placeHolderForLongFieldName = points[0]['_view'].label;
                    $scope.$apply();
                }
                catch(e){ }
            };

            $scope.transformTable = function () {
                $rootScope.$broadcast("transformTable", {});
                $scope.hasBeenClicked = false;
                setTimeout(fixHeader, 200);
                function fixHeader() {
                    var tableHeader = document.getElementById('customTableHeader');
                    tableHeader.style.position = "fixed";
                    $scope.hasBeenClicked = true;
                    //console.log($scope.hasBeenClicked)
                }

                if ($scope.hasBeenClicked == true) {
                    var tableHeader = document.getElementById('customTableHeader');
                    tableHeader.style.position = "relative";
                    //console.log($scope.hasBeenClicked)
                }
            }; // transformTable

            $scope.hidePanel = false;
            $scope.collapseDetailsPanels = function () {
                $scope.hidePanel = !$scope.hidePanel;
                if ($scope.hidePanel == true) {
                    $scope.transformTable();
                    document.getElementById("resizePanelDiv").style.width = "100%";
                    document.getElementById("collapseImage").src = "assets/img/collapse-panel-16x16.png";
                } else {
                    $scope.transformTable();
                    setTimeout(resizeOnMinimize, 300);
                    function resizeOnMinimize() {
                        document.getElementById('customTableHeader').style.position = "relative";
                    }
                    document.getElementById("resizePanelDiv").style.width = "";
                    document.getElementById("collapseImage").src = "assets/img/expand-panel-16x16.png";
                }
            }; // collapse details comparison panels

            $scope.showMask = function () {
                document.getElementById("mask").style.display = "block";
            }; // showMask

            $scope.hideMask = function () {
                document.getElementById("mask").style.display = "none";
            }; // hideMask

            $scope.preferredSchemaDomain = $location.search().domain;
            $scope.schemaTolerance = $location.search().tolerance;
            UploadParameters.setSchemaDomain($scope.schemaDomain);
            UploadParameters.setSchemaTolerance($scope.schemaTolerance);
            //console.log("UploadParameters");
            //console.log(UploadParameters.get());

            $scope.path = "";
            $scope.resizeWizard = function () {
                try {
                    console.log("|" + $scope.path + "|");
                    if ($scope.path) {
                        //console.log("window.innerWidth: " + window.innerWidth);
                        var newViewHeight = (window.innerHeight
                            - document.getElementById("banner").style.height
                            - 20 /* footer */
                        );
                        // the /interpretations path can have additional parameters so strip them off now
                        if ($scope.path.indexOf("/interpretations") > 0) {
                            $scope.path = "/interpretations";
                        }
                        switch ($scope.path) {
                            case "/interpretations":
                                try {
                                    document.getElementById("interpretationsContainer").style.height = newViewHeight - 45 + "px";
                                    document.getElementById("interpretationsMain").style.height = newViewHeight - 98 + "px";

                                    var descPaneHeight = parseInt(document.getElementById("top-split-pane").style.height.slice(0, -2));

                                    // editor pane
                                    var editorPaneTop = parseInt(document.getElementById("bottom-center-pane").style.top.slice(0, -2));
                                    document.getElementById("editor-pane").style.height = (editorPaneTop - descPaneHeight - 2) + "px";
                                    document.getElementById("editorComponentInner").style.height = (editorPaneTop - descPaneHeight - 4) + "px";
                                    document.getElementById("editorPanel").style.height = (editorPaneTop - descPaneHeight - 8) + "px";
                                    document.getElementById("editorBody").style.height = (editorPaneTop - descPaneHeight - 38) + "px";
                                    document.getElementById("editor").style.height = (editorPaneTop - descPaneHeight - 68) + "px";
                                    $rootScope.$broadcast("resizeEditor", {});

                                    // bottom center panes
                                    var bottomCenterPaneHeight = newViewHeight - editorPaneTop - 100;
                                    document.getElementById("bottom-center-pane").style.height = bottomCenterPaneHeight + "px";
                                    document.getElementById("dataPanel").style.height = (bottomCenterPaneHeight - 3) + "px";
                                    document.getElementById("dataPanelBody").style.height = (bottomCenterPaneHeight - 34) + "px";
                                    document.getElementById("sampleData").style.width =
                                        (parseInt(document.getElementById("dataPanelBody").offsetWidth) - 4) + "px";
                                    document.getElementById("sampleData").style.height = (bottomCenterPaneHeight - 36) + "px";
                                    document.getElementById("consolePanel").style.height = (bottomCenterPaneHeight - 3) + "px";
                                    document.getElementById("consolePanelBody").style.height = (bottomCenterPaneHeight - 34) + "px";
                                    document.getElementById("console").style.width =
                                        (parseInt(document.getElementById("consolePanelBody").offsetWidth) - 4) + "px";
                                    document.getElementById("console").style.height = (bottomCenterPaneHeight - 36) + "px";

                                    var centerPanelsLeft = parseInt(document.getElementById("interpretationItem").style.left.slice(0, -2));
                                    var centerPanelsRight = parseInt(document.getElementById("centerPanels").style.right.slice(0, -2));
                                    document.getElementById("editorBody").style.width = (window.innerWidth - centerPanelsLeft - centerPanelsRight - 38) + "px";
                                    document.getElementById("editor").style.width = (window.innerWidth - centerPanelsLeft - centerPanelsRight - 42) + "px";

                                    document.getElementById("matchingNames").style.width =
                                        (parseInt(document.getElementById("matchingNamesPanelBody").offsetWidth) - 16) + "px";
                                    document.getElementById("matchingNames").style.height =
                                        (newViewHeight
                                        - parseInt(document.getElementById("matchingNamesPane").style.top.slice(0, -2)) - 183) + "px";

                                } catch (e) {
                                    console.log(e.toString());
                                }
                                break;
                            case "/catalog":
                                document.getElementById("catalogPanel").style.height =
                                    newViewHeight - 123 + "px";
                                document.getElementById("schemaPanel").style.height =
                                    newViewHeight - 171 + "px";
                                document.getElementById("samplePanel").style.height =
                                    newViewHeight - 171 + "px";
                                document.getElementById("domainPanel").style.height =
                                    newViewHeight - 171 + "px";
                                document.getElementById("schemaPanelBody").style.height =
                                    newViewHeight - 246 + "px";
                                document.getElementById("samplePanelBody").style.height =
                                    newViewHeight - 197 + "px";
                                document.getElementById("domainPanelBody").style.height =
                                    newViewHeight - 246 + "px";
                                document.getElementById("schemaPanelTable").style.height =
                                    newViewHeight - 276 + "px";
                                document.getElementById("samplePanelTable").style.height =
                                    newViewHeight - 227 + "px";
                                document.getElementById("domainPanelTable").style.height =
                                    newViewHeight - 276 + "px";
                                break;
                            case "/schema":
                                document.getElementById("schemaDetailsContainer").style.height =
                                    newViewHeight - 45 + "px";
                                document.getElementById("schemaDetailsPanelBody").style.height =
                                    newViewHeight - 390 + "px";
                                document.getElementById("wizardDetailsPanelBody").style.height =
                                    newViewHeight - 390 + "px";
                                document.getElementById("wizardDetailsHbcCanvas").style.height =
                                document.getElementById("wizardDetailsVbcCanvas").style.height =
                                document.getElementById("wizardDetailsGphCanvas").style.height =
                                document.getElementById("wizardDetailsMapCanvas").style.height =
                                document.getElementById("wizardDetailsExampleCanvas").style.height =
                                    newViewHeight - 642 + "px";
                                break;
                            case "/sampleData":
                                document.getElementById("sampleDetailsContainer").style.height =
                                    newViewHeight - 45 + "px";
                                document.getElementById("sampleDetailsPanelBody").style.height =
                                    newViewHeight - 129 + "px";
                                document.getElementById("wizardDetailsPanelBody").style.height =
                                    newViewHeight - 129 + "px";
                                document.getElementById("wizardDetailsHbcCanvas").style.height =
                                document.getElementById("wizardDetailsVbcCanvas").style.height =
                                document.getElementById("wizardDetailsGphCanvas").style.height =
                                document.getElementById("wizardDetailsMapCanvas").style.height =
                                document.getElementById("wizardDetailsExampleCanvas").style.height =
                                    newViewHeight - 383 + "px";
                                break;
                            case "/wizardUploadSamples":
                                break;
                            case "/wizardInspectSamples":
                                document.getElementById("wizardInspectSamplesContainer").style.height =
                                    newViewHeight - 115 + "px";
                                document.getElementById("sampleDetailsPanelBody").style.height =
                                    newViewHeight - 199 + "px";
                                document.getElementById("wizardDetailsPanelBody").style.height =
                                    newViewHeight - 199 + "px";
                                document.getElementById("wizardDetailsHbcCanvas").style.height =
                                document.getElementById("wizardDetailsVbcCanvas").style.height =
                                document.getElementById("wizardDetailsGphCanvas").style.height =
                                document.getElementById("wizardDetailsMapCanvas").style.height =
                                document.getElementById("wizardDetailsExampleCanvas").style.height =
                                    newViewHeight - 452 + "px";
                                break;
                            case "/wizardMatchFields":
                                document.getElementById("wizardMatchFieldsContainer").style.height =
                                    newViewHeight - 107 + "px";
                                document.getElementById("wizardMatchFieldsProfilesPanelBody").style.height =
                                    newViewHeight - 223 + "px";
                                try { /*sizing for up to 20 columns which isn't expected to hapen*/
                                    for (var i = 0; i < 20; i++) {
                                        document.getElementById("col" + i).style.height = newViewHeight - 272 + "px";
                                    }
                                } catch (e) { /* do nothing */
                                } finally {
                                    document.getElementById("colN").style.height = newViewHeight - 272 + "px";
                                }

                                document.getElementById("wizardMatchFieldsProfilesPanelBody").style.width =
                                    document.getElementById("resizePanelDiv").style.width;


                                document.getElementById("wizardDetails1PanelBody").style.height =
                                    newViewHeight - 193 + "px";
                                document.getElementById("wizardDetails2PanelBody").style.height =
                                    newViewHeight - 193 + "px";
                                document.getElementById("wizardDetails1CanvasBody").style.maxHeight =
                                    newViewHeight - 422 + "px";
                                document.getElementById("wizardDetails2CanvasBody").style.maxHeight =
                                    newViewHeight - 422 + "px";
                                document.getElementById("wizardDetails1HbcCanvas").style.height =
                                document.getElementById("wizardDetails1VbcCanvas").style.height =
                                document.getElementById("wizardDetails1GphCanvas").style.height =
                                document.getElementById("wizardDetails1MapCanvas").style.height =
                                document.getElementById("wizardDetails1ExampleCanvas").style.height =
                                document.getElementById("wizardDetails2HbcCanvas").style.height =
                                document.getElementById("wizardDetails2VbcCanvas").style.height =
                                document.getElementById("wizardDetails2GphCanvas").style.height =
                                document.getElementById("wizardDetails2MapCanvas").style.height =
                                document.getElementById("wizardDetails2ExampleCanvas").style.height =
                                    newViewHeight - 437 + "px";
                                break;
                            case "/wizardFinalizeSchema":
                                document.getElementById("wizardFinalizeSchemaContainer").style.height =
                                    newViewHeight - 108 + "px";
                                document.getElementById("wizardFinalizeSchemaPanelBody").style.height =
                                    newViewHeight - 390 + "px";
                                document.getElementById("wizardDetailsPanelBody").style.height =
                                    newViewHeight - 391 + "px";
                                document.getElementById("wizardDetailsHbcCanvas").style.height =
                                document.getElementById("wizardDetailsVbcCanvas").style.height =
                                document.getElementById("wizardDetailsGphCanvas").style.height =
                                document.getElementById("wizardDetailsMapCanvas").style.height =
                                document.getElementById("wizardDetailsExampleCanvas").style.height =
                                    newViewHeight - 645 + "px";
                                break;
                            case "/wizardSave":
                                break;
                            default:
                                break;
                        }
                    }
                } catch (e) {
                    // if resizing fails because a view hasn't rendered yet
                    // then keep trying to resize it
                    $timeout($scope.resizeWizard, 300);
                }
            }; // resizeWizard

            // logout if detecting a browser reload has occurred
            angular.element($window).bind('load', function () {
                var startupUrl = $location.absUrl();
                startupUrl = startupUrl.slice(startupUrl.indexOf('#'));
                if (startupUrl != "#/login") {
                    $scope.logout();
                }
            }); // onOnLoad

            angular.element($window).bind('resize', function () {
                $scope.resizeWizard();
            });
            // resizeInterpretation is a custom event triggered by split-pane-modified.js
            // in order to resize the content of split panes in the interpretation dialog.
            angular.element($window).bind('resizeInterpretation', function () {
                $scope.resizeWizard();
            });
            $scope.$on("resizeWizard", function (event, args) {
                console.log("onResizeWizard delay: " + args.delay);
                $timeout($scope.resizeWizard, args.delay);
            }); // onResizeWizard
            $scope.resizeWizard();

            $scope.previousDialog = "";
            $scope.navigateTo = function (path, param1, param2, param3) {
                console.log("navigateTo: " + path);
                console.log("parameters: " + param1 + ", " + param2 + ", " + param3);
                var fullPath = path;
                (param1 ? fullPath += '/:' + param1 : fullPath);
                (param2 ? fullPath += '/:' + param2 : fullPath);
                (param3 ? fullPath += '/:' + param3 : fullPath);
                $location.path(fullPath);
                $scope.path = path;
                if (path == "/catalog") {
                    document.getElementById('titleRef').style.pointerEvents = 'auto';
                    $rootScope.$broadcast("closeWebSocket", {});
                }
                if (path == "/wizardUploadSample") {
                    $rootScope.tabNumber = 1;
                }
                if (path == "/sampleData") {
                    $rootScope.tabNumber = 2;
                }
                $timeout($scope.resizeWizard, 300);
            }; // navigateTo
            $scope.browseSchema = function (schema) {
                //console.log("$scope.browseSchema: " + schema.sId);
                $scope.navigateTo("/schema", schema.sId);
            }; // browseSchema
            $scope.browseSample = function (sample, rtnMethod, rtnParm) {
                $scope.rtnMethod = rtnMethod;
                $scope.rtnParm = rtnParm;
                $scope.navigateTo("/sampleData", sample.dsId);
            }; // browseSample
            $scope.browseDomain = function (domain) {
                $scope.navigateTo("/:" + domain.dName + "/:" + domain.dId + "/interpretations");
            }; // browseDomain

            $scope.wizardStateControl = function (newState) {
                switch (newState) {
                    case "wizard-upload-samples-new-schema":
                        if (DomainInformation.isEmpty()) {
                            $scope.emptyDomainTitle = 'Empty Domain List';
                            var baseUrl = 'assets/help/Content/Products/';
                            $scope.emptyDomainUrl = $sce.trustAsResourceUrl(baseUrl + 'Domains/Empty Domains.htm');
                            var modal = myModals.alert('generic', $scope.emptyDomainTitle, 'small', $scope.emptyDomainUrl);
                            modal.result.then(function (data) {
                                // do nothing
                            }, function (data) {
                                $scope.modifySchemaMode = false;
                                Utilities.setModifySchemaMode(false);
                                $scope.schemaDomain = null;
                                $scope.wizardStateControl('wizard-upload-samples');
                                console.log("modal gone")
                            });
                        } else {
                            $scope.modifySchemaMode = false;
                            Utilities.setModifySchemaMode(false);
                            $scope.schemaDomain = null;
                            $scope.wizardStateControl('wizard-upload-samples');
                        }
                        break;
                    case "wizard-upload-samples-existing-schema":
                        $scope.modifySchemaMode = true;
                        Utilities.setModifySchemaMode(true);
                        $scope.currentSchema = Utilities.getSchema();
                        $scope.schemaDomain = $scope.currentSchema.sDomainName;
                        console.log("wizard-upload-samples-existing-schema $scope.currentSchema.sDomainName: " + $scope.currentSchema.sDomainName);
                        $scope.wizardStateControl('wizard-upload-samples');
                        break;
                    case "wizard-upload-samples":
                        // open the websocket for progress bar updates
                        $rootScope.$broadcast("openWebSocket", {
                            sessionId: Server.getSessionId().sessionId
                        });
                        // clear working arrays each time the wizard starts
                        $scope.initializeModels();
                        $scope.navigateTo("/wizardUploadSamples");
                        break;
                    case "wizard-inspect-samples":
                        // when iterating over several data samples, reset the mask since the html not reinitialized
                        try {
                            $scope.browseMaskOpacity = 0.8;
                            document.getElementById('sampleMask').style.opacity = $scope.browseMaskOpacity;
                            document.getElementById("sampleMask").style.display = "block";
                        } catch (e) { /* do nothing -- the page isn't currently rendered */ }

                        if ($cookies.get('schwiz.tours.inspectSample') !== "visited") {
                            $timeout(function () {
                                TourService.getTourByName('catalog').startAt('300');
                            }, 2500);
                            $cookies.put('schwiz.tours.inspectSample', "visited");
                        }
                        if ($scope.model.dataSamples.length == 0) {
                            $scope.currentSampleIndex -= 1;
                            $confirm({
                                    title: 'No data Samples',
                                    text: " There are no Data Samples to Inspect\nPress 'OK' to return back to the Catalog",
                                    ok: 'OK'
                                },
                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'}
                            ).then(function () {
                                $scope.fileIndex = -1;
                                $scope.navigateTo("/catalog");
                                $rootScope.$broadcast("closeWebSocket", {});
                            })
                        }
                        $scope.currentSampleIndex += 1;
                        $scope.fileIndex += 1;
                        if ($scope.currentSampleIndex < $scope.model.dataSamples.length) {
                            if (!$scope.model.dataSamples[$scope.currentSampleIndex].dsName) {
                                $confirm({
                                        title: 'Schema Wizard does not support this file type',
                                        text: "Cannot determine the format of the data sample, or does not support: "
                                        + $scope.sampleFiles[$scope.fileIndex].name +
                                        "\n\nPress OK to discard and proceed to the next data sample.",
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'}
                                ).then(function () {
                                    $scope.model.dataSamples.splice($scope.currentSampleIndex, 1);
                                    // decrement the index since we just created a hole
                                    $scope.currentSampleIndex -= 1;
                                    $scope.wizardStateControl('wizard-inspect-samples');
                                })
                            }
                            //   }

                            $scope.model.dataSamples[$scope.currentSampleIndex].dsFileSize =
                                $scope.sampleFiles[$scope.currentSampleIndex].size;
                            $scope.currentSample = $scope.model.dataSamples[$scope.currentSampleIndex];

                            $scope.navigateTo("/wizardInspectSamples");
                            // broadcast to hierarchicalGenericController (after waiting for it to become available in the dom)
                            $timeout(function () {
                                        $rootScope.$broadcast("setCurrentSample", { sample: $scope.currentSample });
                                    }, 500);
                        } else {
                            $scope.wizardStateControl("wizard-match-fields");
                            $scope.fileIndex = -1;
                        }
                        break;
                    case "wizard-match-fields":
                        if ($cookies.get('schwiz.tours.matchFields') !== "visited") {
                            $timeout(function () {
                                TourService.getTourByName('catalog').startAt('400');
                            }, 2500);
                            $cookies.put('schwiz.tours.matchFields', "visited");
                        }
                        Utilities.setDataSamples($scope.model.dataSamples);
                        // reinitialize hidePanel
                        $scope.hidePanel = false;
                        $scope.navigateTo("/wizardMatchFields");
                        break;
                    case "wizard-finalize-schema":
                        $scope.hidePanel = false;

                        if ($cookies.get('schwiz.tours.finalizeSchema') !== "visited") {
                            $timeout(function () {
                                TourService.getTourByName('catalog').startAt('500');
                            }, 2500);
                            $cookies.put('schwiz.tours.finalizeSchema', "visited");
                        }
                        console.log("wizard-finalize-schema");
                        // overwrite the original dataSamples with the possibly altered ones after matching
                        $scope.model.dataSamples = Utilities.getDataSamples();
                        console.log($scope.model.dataSamples);
                        // open the websocket for progress bar updates
                        $rootScope.$broadcast("openWebSocket", {
                            sessionId: Server.getSessionId().sessionId
                        });
                        $scope.showMask();
                        // can't get this to work using $routeProvider and $resource
                        // TODO: try again in the future
                        var restURL =
                            $location.protocol() + "://" +
                            $location.host() + ":" +
                            $location.port() +
                            "/schwiz/rest/uploadModifiedSamples?" +
                            "schemaGuid=" + ($scope.modifySchemaMode ? $scope.currentSchema.sId : null) + "&" +
                            "domain=" + $scope.schemaDomain;
                        var schemaAnalysisData = {
                          "existing-schema": ($scope.modifySchemaMode ? $scope.currentSchema : null),
                          "data-samples" :  $scope.model.dataSamples
                        };
                        console.log(restURL);
                        $http({
                            method: 'POST',
                            url: restURL,
                            data: schemaAnalysisData
                        })
                        .success(function (data) {
                            console.log("post uploadModifiedSamples success");
                            console.log(data);
                            $scope.currentSchema = data;
                            if ($scope.currentSchema.sVersion == null) {
                                $scope.currentSchema.sVersion = "1.0";
                            }
                            Utilities.setSchema($scope.currentSchema);
                            $scope.hideMask();
                            $rootScope.$broadcast("closeWebSocket", {});
                            $scope.navigateTo("/wizardFinalizeSchema");
                        }, function (error) {
                            statusCodesFactory.get().$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.updateDataSamplesFailed.title,
                                        text: response.updateDataSamplesFailed.title +
                                        " (" + error.status + ")",
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        });
                        break;
                    case "wizard-save":
                        $scope.navigateTo("/wizardSave");
                        break;
                    case "wizard-complete":
                        console.log("wizard-complete");
                        console.log($scope.currentSchema);
                        document.getElementById('titleRef').style.pointerEvents = 'auto';
                        // can't get this to work using $routeProvider and $resource
                        // TODO: try again in the future
                        var restURL =
                            $location.protocol() + "://" +
                            $location.host() + ":" +
                            $location.port() +
                            "/schwiz/rest/schema";
                        console.log(restURL);
                        $http({
                            method: 'POST',
                            url: restURL,
                            data: $scope.currentSchema
                        })
                        .success(function (data) {
                            console.log("post saveSchema success");
                            console.log(data);
                            //$scope.hideMask();
                            //$rootScope.$broadcast("closeWebSocket", {});
                            $scope.navigateTo("/catalog");
                        });
                        break;
                    default:
                        break;
                }
            }; // wizardStateControl

            /* Titlebar Menu Items */
            $scope.setTheme = function ($event, theme) {
                if ($event) $event.preventDefault();
                var cssFile;
                switch (theme) {
                    // the digital edge 3 theme is in main.css, de3-theme.css won't be found
                    // but all the other '-theme.css' files will get disabled
                    case 'blue':   cssFile = "blue-theme.css"; break;
                    case 'green':  cssFile = "green-theme.css"; break;
                    case 'de3':    cssFile = "de3-theme.css"; break;
                    default:       cssFile = "de3-theme.css"; break;
                }
                for (var i = 0; i < document.styleSheets.length; i++) {
                    if (document.styleSheets[i].href) {
                        if (document.styleSheets[i].href.indexOf(cssFile) > 0) {
                            document.styleSheets[i].disabled = false;
                            $cookies.put('schwiz.theme', theme);
                        } else if (document.styleSheets[i].href.indexOf("-theme.css") > 0) {
                            document.styleSheets[i].disabled = true;
                        }
                    }
                }
                // the digital edge theme won't be set in a cookie above so check and set it here
                if (theme == "de") $cookies.put('schwiz.theme', theme);
            }; // setTheme
            $scope.setTheme(null, (!$cookies.get('schwiz.theme') ? 'leidos' : $cookies.get('schwiz.theme')));

            $scope.launchHelp = function () {
                var childWindowForHelp = window.open('assets/help/Default.htm', "", "width=950,height=850");
                childWindowForHelp.moveTo(300, 50);
            }; // launchHelp

            $scope.launchAbout = function ($event) {
                if ($event) $event.preventDefault();
                $confirm({
                        title: 'About Schema Wizard',
                        text: version,
                        ok: 'OK'
                    },
                    {templateUrl: 'schema-wizard/schema-wizard.about.template.html'}
                ).then(function () {
                });
            };

            // tour 'previous' button
            $scope.onTourPrev = function (tour) {
                console.log('Moving back...', tour);
                console.log(tour.getCurrentStep().order);
                switch (tour.getCurrentStep().order) {
                    case 30:
                        $rootScope.$broadcast("selectTab", {tabNumber: 2});
                        break;
                    case 40:
                        $rootScope.$broadcast("selectTab", {tabNumber: 3});
                        break;
                    default:
                        $rootScope.$broadcast("selectTab", {tabNumber: 1});
                        break;
                }
            }; // onTourPrev

            // tour 'next' button
            $scope.onTourNext = function (tour) {
                console.log('Moving next...', tour);
                console.log(tour.getCurrentStep().order);
                switch (tour.getCurrentStep().order) {
                    case 10:
                        $rootScope.$broadcast("selectTab", {tabNumber: 2});
                        break;
                    case 20:
                        $rootScope.$broadcast("selectTab", {tabNumber: 3});
                        break;
                    default:
                        $rootScope.$broadcast("selectTab", {tabNumber: 1});
                        break;
                }
            }; // onTourNext

            // tour 'end' button
            $scope.onTourEnd = function (tour) {
                console.log('Ending tour', tour);
                $cookies.put('schwiz.tours.catalog', "visited");
            }; // onTourNext

            // get the sample files from fileUploadCtrl
            $scope.$on("sampleFilesSelected", function (event, args) {
                $scope.sampleFiles = args.newSampleFiles;
                console.log("sampleFilesSelected:");
                console.log($scope.sampleFiles);
            }); // onSampleFilesSelected

            // get the data sources from fileUploadCtrl
            $scope.$on("dataSamplesReceived", function (event, args) {
                $scope.hideMask();
                console.log("dataSamplesReceived: " + args.newDataSamples);
                $scope.model.dataSamples = args.newDataSamples;
                // get a dump of the data samples by uncommenting the following line
                //console.log(angular.toJson($scope.model.dataSamples, true));
                // initialize index used to iterate through inspection of samples
                $scope.currentSampleIndex = -1;
                $scope.wizardStateControl('wizard-inspect-samples');
            }); // ondataSamplesReceived

            $scope.$on("setCurrentSample", function (event, args) {
                console.log("onSetCurrentSample");
                console.log(args);
                console.log(args.sample);
                $scope.currentSample = args.sample;
            }); // onsetCurrentSample

            $scope.$on("setCurrentSchema", function (event, args) {
                console.log("onSetCurrentSchema");
                console.log(args.schema);
                $scope.currentSchema = args.schema;
                Utilities.setSchema($scope.currentSchema);

                $rootScope.test = $scope.currentSchema;
            }); // onsetCurrentSchema

            $scope.$on("setSchemaDomain", function (event, args) {
                console.log("onSetSchemaDomain");
                console.log(args.schemaDomain);
                $scope.schemaDomain = args.schemaDomain;
            }); // onsetCurrentSchema

            $scope.$on("schemaUpdate", function (event, args) {
                console.log("onSchemaUpdate");
                console.log(args.schema);
                $scope.currentSchema = args.schema;
            }); // onSchemaUpdate

            $scope.showCurrentSchema = function () {
                console.log("showCurrentSchema");
                console.log($scope.currentSchema);
            }; // showCurrentSchema

            $scope.discardDataSource = function (currentSampleIndex) {
                $confirm({
                        title: 'Confirm Discard Data Source',
                        text: "Press 'OK' to confirm.",
                        ok: 'OK',
                        cancel: 'Cancel'
                    }
                ).then(function () {
                    console.log("discardDataSource currentSampleIndex: " + currentSampleIndex);
                    console.log($scope.model.dataSamples[currentSampleIndex]);
                    // delete the linked data source and any properties it uniquely introduced
                    angular.forEach(Object.keys($scope.model.properties), function (property) {
                        var linkedDs = $scope.model.properties[property].linkedDs;
                        for (var i = 0; i < linkedDs.length; i++) {
                            if (linkedDs[i] == $scope.model.dataSamples[$scope.currentSampleIndex]) {
                                console.log("Found dataSource linkedDs index: " + i);
                                console.log(linkedDs[i]);
                                // if this is the only linked data souce then it introduced this property
                                // so delete the property
                                if (linkedDs.length == 1) {
                                    console.log("before");
                                    console.log($scope.model.properties);
                                    delete $scope.model.properties[property];
                                    console.log("after");
                                    console.log($scope.model.properties);
                                }
                                linkedDs.splice(i, 1);
                            }
                        }
                    });
                    $scope.model.dataSamples.splice(currentSampleIndex, 1);
                    // decrement the index since we just created a hole
                    $scope.currentSampleIndex -= 1;
                    $scope.wizardStateControl('wizard-inspect-samples');
                }, function (error) {
                    statusCodesFactory.get().$promise.then(function (response) {
                        $confirm(
                            {
                                title: response.failedToDiscardDataSource.title,
                                text: response.failedToDiscardDataSource.title +
                                " (" + error.status + ")",
                                ok: 'OK'
                            },
                           {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                    })
                });
            }; // discardDataSource

            var canvasBase = document.getElementById('panel1base');
            var canvasBar = document.getElementById('panel1bar');
            var canvasLine = document.getElementById('panel1line');
            if (canvasBase||canvasBar||canvasLine){
                var ctx = canvas.getContext('2d');
                ctx.clearRect(0,0, ctx.canvas.width, ctx.canvas.height);
            }

        }]); // schemaWizardCtrl

    schemaWizardApp.directive('singleClick', ['$parse', '$timeout', function ($parse, $timeout) {
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
})();
