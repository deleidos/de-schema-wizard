/**
 * @ngdoc controller
 * @name schemaWizardApp
 * @description This is the only module used throughout the schema wizard app, under it lies every controller, etc..
 *
 **/

(function () {
    //For IE
    try {
        var resizeInterpretationEvent = new Event('resizeInterpretation');
    }
    catch (e) {
        console.log(e)
    }

    var schemaWizardApp =
        angular.module('schemaWizardApp', [
            'ui.bootstrap',
            'ngAnimate',
            'ngCookies',
            'ngResource',
            'ngRoute',
            'ngSanitize',
            'ngWebSocket',
            'googlechart',
            'chart.js',
            'ngCsv',
            'dndLists',
            'angular-confirm',
            'anguFixedHeaderTable',
            'bm.uiTour',
            'shagstrom.angular-split-pane',
            'ngIdle'
        ])
            .constant("baseUrl", "/schwiz/")
            .constant("version", "3.0.0")

            .service('authInterceptor', function ($location, $window, $q) {
                var service = this;
                service.responseError = function (response) {
                    /*                    if (response.status == 401 || response.status == 403){*/
                    if (response.status == 403) {
                        console.log("############### Not Logged On or Session Timed Out ###############");
                        $location.path("/log0n");
                        $window.location.reload();
                    } else {
                        return $q.reject(response);
                    }
                };
            })

            .config(['$httpProvider', function ($httpProvider) {
                $httpProvider.interceptors.push('authInterceptor');
            }])
            // these two configs are for ng-idle
            .config(['KeepaliveProvider', 'IdleProvider', function (KeepaliveProvider, IdleProvider) {
                //time before idle in seconds
                //60 minutes
                IdleProvider.idle(3600);
                //amount of time for countdown  before timeout
                //10 minutes
                IdleProvider.timeout(600);
                //This specifies how often the Keepalive event is triggered and the request is issued.
                KeepaliveProvider.interval(1);
            }])
            .config(['TitleProvider', function (TitleProvider) {
                TitleProvider.enabled(false); // it is enabled by default
            }])

            .factory("LogoPage", function () {
                var alreadyUsed = false;
                return {
                    isFirstTime: function () {
                        if (!alreadyUsed) {
                            alreadyUsed = true;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            })

            .factory("objectEmptyFactory", function () {
                return {
                    isObjectEmpty: function (object) {
                        for (var key in object
                            ) {
                            if (object.hasOwnProperty(key)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            })

            .factory("DomainInformation", function () {
                var NumberOfDomains = 0;
                return {
                    setCount: function (count) {
                        NumberOfDomains = count;
                    },
                    getCount: function () {
                        return NumberOfDomains;
                    },
                    isEmpty: function () {
                        return NumberOfDomains === 0;
                    }
                }
            })

            .factory("UploadParameters", function () {
                var schemaDomain = "";
                var schemaTolerance = "";
                var domainsArray = [];
                return {
                    setSchemaDomain: function (schemaDomain) {
                        this.schemaDomain = schemaDomain;
                    },
                    setSchemaTolerance: function (schemaTolerance) {
                        this.schemaTolerance = schemaTolerance;
                    },
                    setDomains: function (domainsArray) {
                        this.domainsArray = domainsArray;
                    },
                    get: function () {
                        return {
                            "schemaDomain": this.schemaDomain,
                            "schemaTolerance": this.schemaTolerance,
                            "domainsArray": this.domainsArray
                        }
                    }
                }
            })

            .factory("Server", function () {
                var sessionId = "";
                return {
                    setSessionId: function (sessionId) {
                        this.sessionId = sessionId;
                    },
                    getSessionId: function () {
                        return {"sessionId": this.sessionId}
                    }
                }
            })

            .factory("schwizResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/sessionId",
                    {},
                    {getSessionId: {method: "GET"}}
                );
            })

            .factory("catalogResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/catalog",
                    {},
                    {getCatalog: {method: "GET"}}
                );
            })

            .factory("schemaResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/schema/:schemaId",
                    {schemaId: "@schemaId"},
                    {getSchema: {method: "GET"}}
                );
            })

            .factory("sampleDataResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/sampleData/:sampleId",
                    {sampleId: "@sampleId"},
                    {getSampleData: {method: "GET"}}
                );
            })

            .factory("domainResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/domain",
                    {
                        domainId: "@domainId",
                        data: "@data"
                    },
                    {update: {method: "PUT"}}
                )
            })

            .factory("interpretationResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/:domainId/interpretation/:interpretationId",
                    {
                        domainId: "@domainId",
                        interpretationId: "@interpretationId",
                        data: "@data"
                    },
                    {update: {method: "PUT"}}
                )
            })

            .factory("pythonValidateResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/python/validate/:interpretationId",
                    {
                        interpretationId: "@interpretationId"
                    },
                    {validate: {method: "GET"}}
                )
            })

            .factory("pythonTestResource", function ($resource, baseUrl) {
                return $resource(baseUrl + "rest/python/test/:interpretationId",
                    {
                        interpretationId: "@interpretationId"
                    },
                    {test: {method: "GET"}}
                )
            })

            .factory('tabHistoryFactory', function () {
                var tabHistory = {
                    setPrevTab: function (tab) {
                        tabHistory.prevTab = tab;
                    },
                    getPrevTab: function () {
                        return tabHistory.prevTab;
                    }
                };
                return tabHistory;
            })

            .factory('statusCodesFactory', function ($resource) {
                return $resource('schema-wizard/schema-wizard.messages-rest.json')
            })

            .factory('guidedTourStepFactory', function ($resource) {
                return $resource('schema-wizard/schema-wizard.messages-tour.json')
            })

            .config(['ChartJsProvider', function (ChartJsProvider) {
                ChartJsProvider.setOptions({
                    scale : {
                        ticks : {
                            beginAtZero : true
                        }
                    }
                });
            }])

            .config(["$routeProvider", "$locationProvider", "$compileProvider", 'TourConfigProvider',
                function ($routeProvider, $locationProvider, $compileProvider, TourConfigProvider) {
                    //$locationProvider.html5Mode(true);
                    $compileProvider.aHrefSanitizationWhitelist(/^\s*(|blob|):/);

                    TourConfigProvider.set('scrollOffset', 50);
                    TourConfigProvider.set('onStart', function () {
                        console.log('Started Tour');
                    });

                    $routeProvider.when("/login", {
                        templateUrl: "login/login.html",
                        controller: "loginCtrl"
                    })

                    .when("/catalog", {
                        templateUrl: "catalog/catalog.lists.html",
                        controller: "catalogCtrl",
                        resolve: {
                            session: function ($route, schwizResource) {
                                return schwizResource.getSessionId();
                            },
                            catalogData: function ($route, catalogResource) {
                                return catalogResource.getCatalog().$promise;
                            }
                        }
                    })

                    .when("/schema/:schemaId", {
                        templateUrl: "catalog/catalog.hierarchical.schema.details.html",
                        controller: "hierarchicalSchemaDetailsCtrl",
                        resolve: {
                            schemaData: function ($route, schemaResource) {
                                return schemaResource.getSchema(
                                    {schemaId: $route.current.params.schemaId.slice(1)});
                            }
                        }
                    })

                    .when("/sampleData/:sampleId", {
                        templateUrl: "catalog/catalog.hierarchical.sample.details.html",
                        controller: "hierarchicalSampleDetailsCtrl",
                        resolve: {
                            sampleData: function ($route, sampleDataResource) {
                                return sampleDataResource.getSampleData(
                                    {sampleId: $route.current.params.sampleId.slice(1)});
                            }
                        }
                    })

                    .when("/:domainName/:domainId/interpretations", {
                        templateUrl: "catalog/catalog.domains.interpretations.html",
                        controller: "interpretationCtrl",
                        resolve: {
                            interpretationData: function ($route, interpretationResource) {
                                return interpretationResource.get(
                                    {domainId: $route.current.params.domainId.slice(1)});
                            },
                            domainName: function ($route) {
                                return $route.current.params.domainName.slice(1);
                            },
                            domainId: function ($route) {
                                return $route.current.params.domainId.slice(1);
                            }
                        }
                    })

                    .when("/wizardUploadSamples", {
                        templateUrl: "wizard/wizard.upload.samples.html"
                    })

                    .when("/wizardInspectSamples", {
                        templateUrl: "wizard/wizard.hierarchical.inspect.samples.html",
                        controller: "hierarchicalGenericCtrl"
                    })

                    .when("/wizardMatchFields", {
                        templateUrl: "wizard/wizard.hierarchical.match.fields.html",
                        controller: "hierarchicalMatchingCtrl"
                    })

                    .when("/wizardFinalizeSchema", {
                        templateUrl: "wizard/wizard.hierarchical.finalize.schema.html",
                        controller: "hierarchicalFinalizeCtrl"
                    })

                    .when("/wizardSave", {
                        templateUrl: "wizard/wizard.save.html"
                    })

                    .when("/blank", {
                        templateUrl: "schema-wizard/schema-wizard.blank.html"
                    })

                    .when("/userPage", {
                        templateUrl: "login/user.page.html"
                    })

                    .otherwise({
                        redirectTo: "/login"
                    })
                }])
            .run(['uiTourService', function (TourService) {
                TourService.createDetachedTour('myDetachedTour', {backdrop: true});
                //TourService.createDetachedTour('interpretationsTour');
            }])
})();

