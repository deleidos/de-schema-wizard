/**
 * @ngdoc controller
 * @name Login Controller
 * @description This is controller used in Schema Wizard to handle all of the login operations.
 *

 **/

(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('loginCtrl',
        function ($rootScope, $scope, $resource, $location, $confirm, $http, $timeout, Globals, Idle) {
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
            };
            $scope.logout();
            Idle.unwatch();
            var restURL =
                $location.protocol() + "://" +
                $location.host() + ":" +
                $location.port() +
                "/schwiz/rest/login";
            var failedLogin = false;
            var checkIfLoggedIn = false;
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
                                console.log(response)
                                Globals.setUserId($scope.username);
                                Globals.setRole(response.data.userRole);
                                Globals.setName(response.data.firstName)
                                // set security questions, if not set before
                                if (response.data.createdSecurityQuestions == false) {
                                    $rootScope.$broadcast('did-not-set-security-questions');
                                }
                                $scope.navigateTo("/catalog");
                                checkIfLoggedIn = true;
                            }, function errorCallback(response) {
                                Globals.setDefaultUserId();
                                checkIfLoggedIn = false;
                                $scope.promptLogin();
                                failedLogin = true;
                                $scope.checkIfFailedLogin = function () {
                                    if (failedLogin === true) {
                                        document.getElementById('incorrect').innerHTML = "Incorrect Username/Password."
                                    }
                                };
                                $timeout($scope.checkIfFailedLogin, 500);
                            });
                        })
                }
            }
            $scope.promptLogin();
        }); // loginCtrl
})();

