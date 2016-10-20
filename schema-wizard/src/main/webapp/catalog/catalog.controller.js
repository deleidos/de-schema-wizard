(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('catalogCtrl', [ '$rootScope', '$scope', '$resource',
        '$location', '$route', '$routeParams', '$timeout', '$confirm', '$cookies', '$sce',
        'LogoPage', 'DomainInformation', 'UploadParameters', 'Server', 'domainResource', 'version',
        'session', 'catalogData', 'tabHistoryFactory', 'statusCodesFactory', 'myModals', 'uiTourService',
        'schemaResource','sampleDataResource',
        function ($rootScope, $scope, $resource, $location, $route, $routeParams, $timeout,
                  $confirm, $cookies, $sce, LogoPage, DomainInformation, UploadParameters, Server,
                  domainResource, version, session, catalogData, tabHistoryFactory, statusCodesFactory,
                  myModals, TourService, schemaResource, sampleDataResource) {

            if (tabHistoryFactory.getPrevTab() == 2) {
                tabHistoryFactory.setPrevTab(2);
            } else if (tabHistoryFactory.getPrevTab() == 3) {
                tabHistoryFactory.setPrevTab(3);
            } else {
                tabHistoryFactory.setPrevTab(1);
            }
            $scope.logoPageOpacity = 1.0;
            $scope.fadeLogoPage = function () {
                $rootScope.$broadcast("transformTable", {});
                //console.log("fadeLogoPage");
                //console.log("$scope.logoPageOpacity: " + $scope.logoPageOpacity);
                $scope.logoPageOpacity -= 0.1;
                if ($scope.logoPageOpacity < 0) {
                    document.getElementById('logoPage').style.display = "none";
                    document.getElementById('catalogPage').style.opacity = 1.0;
                } else {
                    document.getElementById('logoPage').style.opacity =
                        $scope.logoPageOpacity;
                    document.getElementById('catalogPage').style.opacity =
                        1 - $scope.logoPageOpacity;
                    $timeout($scope.fadeLogoPage, 75);
                }
            } // fadeLogoPage

            $scope.hideLogoPage = function () {
                //console.log("hideLogoPage");
                document.getElementById('catalogPage').style.opacity = 0.0;
                document.getElementById('catalogPage').style.display = "block";
                $timeout($scope.fadeLogoPage, 200);
            } // hideLogoPage

            if (LogoPage.isFirstTime()) {
                $timeout($scope.hideLogoPage, 2000);
            } else {
                document.getElementById('logoPage').style.display = "none";
                document.getElementById('catalogPage').style.opacity = 1.0;
                document.getElementById('catalogPage').style.display = "block";
            }
            console.log("catalogCtrl");
            console.log("catalogData");
            console.log(catalogData);
            $scope.version = version;
            $scope.catalog = {};
            $scope.catalog.schemaCatalog = catalogData.schemaCatalog;
            $scope.catalog.dataSamplesCatalog = catalogData.dataSamplesCatalog;
            $scope.catalog.domainsCatalog = catalogData.domainsCatalog;
            DomainInformation.setCount($scope.catalog.domainsCatalog.length);

            session.$promise.then(function (response) {
                Server.setSessionId(session.sessionId);
                // having resolved this $promise is the closest point
                // for timing the launch of the catalog tour
                // start the catalog tour if it hasn't been seen before
                if ($cookies.get('schwiz.tours.catalog') !== "visited") {
                    //console.log(TourService.getTourByName('catalog'));
                    $timeout(function () { TourService.getTourByName('catalog').startAt(0); }, 2500);
                }
            }, function (error) {
                statusCodesFactory.get()
                    .$promise.then(function (response) {
                    $confirm(
                        {
                            title: response.sessionIdFailed.title + error.status,
                            text: response.sessionIdFailed.message,
                            ok: 'OK'
                        },
                        {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                });
            });
            UploadParameters.setDomains(catalogData.domainsCatalog);
            $scope.newDomain = null;

            $scope.removeSchema = function (schema) {
                $confirm({
                        title: 'Confirm Delete Schema',
                        text: "Confirm the request to delete this schema.",
                        ok: 'OK',
                        cancel: 'Cancel'
                    }
                ).then(function () {
                    console.log("deleting schema "+schema.sName+" Id:"+ schema.sId);
                    schemaResource.remove(
                        {
                            schemaId:  schema.sId
                        }
                    ).$promise.then(function (response) {
                            console.log("removeSchema response");
                            console.log(response);
                            for (var i = 0; i < $scope.catalog.schemaCatalog.length; i++) {
                                if ($scope.catalog.schemaCatalog[i].sId === schema.sId) {
                                    $scope.catalog.schemaCatalog.splice(i, 1);
                                    break;
                                }
                            }
                        }, function (error) {
                            console.log("error.status: " + error.status);
                            statusCodesFactory.get()
                                .$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.deleteSchemaFailed.title + error.status,
                                        text: response.deleteSchemaFailed.message,
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    )
                })
            };// delete schema

            $scope.removeDataSample = function (sample) {
                console.log(sample)
                $confirm({
                        title: 'Confirm Delete Data Sample',
                        text: "Confirm the request to delete this data sample.",
                        ok: 'OK',
                        cancel: 'Cancel'
                    }
                ).then(function () {
                    console.log("deleting data sample "+sample.dsName+" Id:"+ sample.dsId);
                    sampleDataResource.remove(
                        {
                            sampleId:  sample.dsId
                        }
                    ).$promise.then(function (response) {
                            console.log("removeDataSample response");
                            console.log(response);
                            for (var i = 0; i < $scope.catalog.dataSamplesCatalog.length; i++) {
                                if ($scope.catalog.dataSamplesCatalog[i].dsId === sample.dsId) {
                                    $scope.catalog.dataSamplesCatalog.splice(i, 1);
                                    break;
                                }
                            }
                        }, function (error) {
                            console.log("error.status: " + error.status);
                            statusCodesFactory.get()
                                .$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.removeDataSampleFailed.title + error.status,
                                        text: response.removeDataSampleFailed.message,
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    )
                })
            };// remove data sample

            $scope.createNewDomain = function () {
                console.log("create new domain");
                $scope.newDomain =
                {
                    "dId": null,
                    "dName": null,
                    "dVersion": "1.0",
                    "dLastUpdate": new Date(),
                    "dDescription": "",
                    "dInterpretations": []
                }
                $timeout($scope.focusDname, 300);
            }; // createNewDomain

            $scope.focusDname = function () {
                document.getElementById('newDomainName').focus();
            }; // focusIname

            $scope.saveNewDomain = function () {
                console.log("save new domain");
                domainResource.save({
                    domainId: null,
                    data: $scope.newDomain
                }).$promise.then(function (response) {
                        console.log("saveNewDomain response");
                        console.log(response);
                        // the returnValue has quotes around it, strip them off
                        console.log(response.returnValue);
                        $scope.newDomain.dId = response.returnValue;
                        console.log("$scope.newDomain");
                        console.log($scope.newDomain);
                        $scope.catalog.domainsCatalog.push(angular.copy($scope.newDomain));
                        DomainInformation.setCount($scope.catalog.domainsCatalog.length);
                        $scope.newDomain = null;
                    }, function (error) {
                        console.log("error.status: " + error.status);
                        statusCodesFactory.get()
                            .$promise.then(function (response) {
                            $confirm(
                                {
                                    title: response.saveNewDomainFailed.title + error.status,
                                    text: response.saveNewDomainFailed.message,
                                    ok: 'OK'
                                },
                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                        })
                    }
                )
            }; // saveNewDomain

            $scope.launchDomainHelp = function() {
                $scope.emptyDomainTitle = 'Create New Domain';
                var baseUrl = 'assets/help/Content/Products/';
                $scope.emptyDomainUrl = $sce.trustAsResourceUrl(baseUrl + 'Domains/Create Domain.htm');
                var modal = myModals.alert('generic', $scope.emptyDomainTitle, 'small', $scope.emptyDomainUrl);
                modal.result.then(function (data) {

                }, function (data) {

                })
            };
            $scope.cancelNewDomain = function () {
                $scope.newDomain = null;
            }; // cancelNewDomain

            $scope.removeDomain = function (domain) {
                $confirm(
                    {
                        title: 'Confirm Delete Domain',
                        text: "Confirm the request to delete this domain.",
                        ok: 'OK',
                        cancel: 'Cancel'
                    }
                ).then(function () {
                    console.log("deleting domain: " + domain.dName + "(" + domain.dId + ")");
                    var domainIdJson = {};
                    domainIdJson.dId = domain.dId;
                    domainResource.remove(
                        {
                            data: domainIdJson
                        }
                    ).$promise.then(function (response) {
                            console.log("removeDomain response");
                            console.log(response);
                            for (var i = 0; i < $scope.catalog.domainsCatalog.length; i++) {
                                if ($scope.catalog.domainsCatalog[i].dId === domain.dId) {
                                    $scope.catalog.domainsCatalog.splice(i, 1);
                                    break;
                                }
                            }
                            DomainInformation.setCount($scope.catalog.domainsCatalog.length);

                        }, function (error) {
                            console.log("error.status: " + error.status);
                            statusCodesFactory.get()
                                .$promise.then(function (response) {
                                $confirm(
                                    {
                                        title: response.deleteDomainFailed.title + error.status,
                                        text: response.deleteDomainFailed.message,
                                        ok: 'OK'
                                    },
                                    {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                            })
                        }
                    )
                })
            };// delete domain
        }]); // catalogCtrl

    schemaWizardApp.controller('tabCtrl',
        function ($rootScope, $scope, $location, tabHistoryFactory) {
            // set the default tab for the application
            this.tab = tabHistoryFactory.getPrevTab();

            this.selectTab = function (setTab) {
                this.tab = setTab;
                tabHistoryFactory.setPrevTab(setTab);
                $rootScope.$broadcast("transformTable", {});
            };
            this.isSelected = function (checkTab) {
                return this.tab === checkTab;
            };

            $scope.that = this;

            $scope.$on("selectTab", function (event, args) {
                console.log("onSelectTab tabnumber: " + args.tabNumber);
                $scope.that.selectTab(args.tabNumber);
            }); // onSelectTab
        }); // tabCtrl
})();
