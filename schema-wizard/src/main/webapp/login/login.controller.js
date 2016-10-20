(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('loginCtrl',
        function ($rootScope, $scope, $resource, $location, $confirm, $http, $timeout, Globals) {
            var restURL =
                $location.protocol() + "://" +
                $location.host() + ":" +
                $location.port() +
                "/schwiz/rest/login";
            var failedLogin = false;
            var checkIfLoggedIn = false;
            console.log(checkIfLoggedIn);
            $scope.promptLogin = function () {
                if (checkIfLoggedIn === false) {
                    $confirm(
                        {
                            title: "Login to Schema Wizard",
                            ok: 'Login'
                        },
                        {templateUrl: 'login/login.modal.template.html'})
                        .then(function () {
                            $scope.username = document.getElementById('loginUsername').value;
                            $scope.password = document.getElementById('loginPassword').value;
                            var loginCredentials = {username: $scope.username, password: $scope.password};
                            $http({
                            method: 'POST',
                            url: restURL,
                            data: loginCredentials
                            }).then(function successCallback(response) {
                                console.log(response);
                                Globals.setUserId($scope.username);
                                $scope.navigateTo("/catalog");
                                checkIfLoggedIn = true;
                            }, function errorCallback(response) {
                                console.log(response);
                                Globals.setDefaultUserId();
                                checkIfLoggedIn = false;
                                $scope.promptLogin();
                                failedLogin = true;
                                $scope.checkIfFailedLogin = function () {
                                    if (failedLogin === true) {
                                        document.getElementById('incorrect').innerHTML = "Incorrect Username/Password."
                                    }
                                };
                                $timeout($scope.checkIfFailedLogin, 400);
                            });
                        })
                }
            }
            $scope.promptLogin();
        }); // loginCtrl
})();

