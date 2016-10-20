(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('wizardFinalizeCtrl',
        function($rootScope, $scope, $resource, $location, $route, $routeParams, schemaData, statusCodesFactory, $confirm) {

            schemaData.$promise.then(function (response) {
                console.log("wizardFinalizeCtrl");
                console.log(schemaData);
                $rootScope.$broadcast("schemaUpdate", {
                    schema: schemaData
                });
                }, function (error) {
                    console.log(error);
                    statusCodesFactory.get().$promise.then(function (response) {
                        $confirm(
                            {
                                title: response.failedToFinalizeWizard.title,
                                text: response.failedToFinalizeWizard.title +
                                " (" + error.status + ")",
                                ok: 'OK'
                            },
                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                    })
                }
            );
        }); // wizardFinalizeCtrl
})();
