(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('sampleDetailsCtrl',
        function ($rootScope, $scope, $resource, $location, $route, $routeParams, sampleData, $confirm, statusCodesFactory) {

            sampleData.$promise.then(function (response) {
                    $rootScope.$broadcast("setCurrentSample", {
                        sample: sampleData
                    })
                }, function (error) {
                    console.log(error);
                    statusCodesFactory.get().$promise.then(function (response) {
                        $confirm(
                            {
                                title: response.gettingDataSamplesFailed.title,
                                text: response.gettingDataSamplesFailed.title +
                                " (" + error.status + ")",
                                ok: 'OK'
                            },
                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                    })
                }
            );
        }); // sampleDetailsCtrl
})();
