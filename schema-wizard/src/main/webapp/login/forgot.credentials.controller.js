/**
 * @ngdoc directive
 * @name Forgot Credentials  Controller
 * @description This is controller used in Schema Wizard to handle all of the forgot Credentials operations. It utilizes various HTML templates
 * in order to guide the user through either resetting thier username by providing their full name, or resetting their password by providing their username.
 *

 **/
(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('forgotCtrl',
        function ($rootScope, $scope, $resource, $location, $confirm, $http, $timeout, Globals, Idle, $window) {
            $scope.forgotTypeList = ['username', 'password'];
            $scope.forgotType = "";
            $scope.userName = "";
            //created because Schema Wizard Controller has not been loaded yet
            $rootScope.navigateToLogin = function () {
                $rootScope.$broadcast('dismissModal');
                $location.path("/logOn");
                $window.location.reload();
            };
            $scope.setForgotCredentialType = function (type) {
                if (type == 'username') {
                    $scope.forgotType = "username";
                    $scope.$watch(function () {
                            return document.getElementById('enterName').value;
                        },
                        function () {

                            if (document.getElementById('enterName').value != "") {
                                document.getElementById('submitButtonSelectType').disabled = false;
                            }
                            else {
                                document.getElementById('submitButtonSelectType').disabled = true;
                            }
                        });
                }
                else if (type == 'password') {
                    $scope.forgotType = "password";
                    $scope.$watch(function () {
                            return document.getElementById('enterUserName').value;
                        },
                        function () {

                            if (document.getElementById('enterUserName').value != "") {
                                document.getElementById('submitButtonSelectType').disabled = false;
                            }
                            else {
                                document.getElementById('submitButtonSelectType').disabled = true;
                            }
                        });
                }
            };
            $scope.forgotCredentialsLink = function () {
                $rootScope.$broadcast('dismissModal');
                $confirm(
                    {
                        title: "Forgot Credentials",
                        ok: 'Submit',
                        cancel: "Cancel"
                    },
                    {templateUrl: 'login/forgot.credentials.selectType.template.html'})
                    .then(function () {

                            try {
                                $scope.userFullName = document.getElementById('enterName').value;
                                $scope.userName = document.getElementById('enterUserName').value;
                            }
                            catch (e) {

                            }
                            //get username
                            if ($scope.userFullName != "") {
                                var restURL =
                                    $location.protocol() + "://" +
                                    $location.host() + ":" +
                                    $location.port() +
                                    "/schwiz/rest/recover/username";
                                $http({
                                    method: 'POST',
                                    url: restURL,
                                    data: {firstName: $scope.userFullName}
                                }).then(function successCallback(response) {
                                    if (angular.equals(response.data, {})) {
                                        $confirm(
                                            {
                                                title: "User does not exist",
                                                ok: 'Return to Login',
                                                text: 'There is no username associated with that name'
                                            },
                                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                            .then(function () {
                                                $scope.navigateToLogin();
                                            })
                                    }
                                    else {
                                        if (response.data.userName != "" || response.data.userName != null) {
                                            $scope.retrievedUsername = response.data.username;
                                            $confirm(
                                                {
                                                    title: "Username",
                                                    text: 'Your username is: ' + $scope.retrievedUsername,
                                                    ok: 'OK'
                                                },
                                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                                .then(function () {
                                                    $scope.navigateToLogin();

                                                })


                                        }
                                        else {
                                            $confirm(
                                                {
                                                    title: "User does not exist",
                                                    ok: 'Return to Login',
                                                    text: 'There is no username associated with that name'
                                                },
                                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                                .then(function () {
                                                    $scope.navigateToLogin();
                                                })
                                        }
                                    }
                                }), function errorCallback(response) {
                                    //error call back for failed POST-forgot username



                                    $confirm(
                                        {
                                            title: "Something went wrong!",
                                            ok: 'Return to Login',
                                            text: 'Your information did not match what we have, or there was an internal error.'
                                        },
                                        {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                        .then(function () {
                                            $scope.navigateToLogin();
                                        })
                                }
                            }
                            if ($scope.userName != "") {
                                var restURL =
                                    $location.protocol() + "://" +
                                    $location.host() + ":" +
                                    $location.port() +
                                    "/schwiz/rest/recover/questions";
                                $http({
                                    method: 'POST',
                                    url: restURL,
                                    data: {userName: $scope.userName}
                                }).then(function successCallback(response) {
                                        if (angular.equals(response.data, {})) {
                                            $confirm(
                                                {
                                                    title: "Your security questions have not been set",
                                                    text: 'Your security questions have not been set, contact your administrator.',
                                                    ok: 'Submit',
                                                    cancel: "Cancel"
                                                },
                                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                                .then(function () {
                                                    $scope.navigateToLogin();
                                                })
                                        }

                                        else {
                                            $scope.questionOneModal = response.data.questions.securityQuestion1;
                                            $scope.questionTwoModal = response.data.questions.securityQuestion2;
                                            $scope.questionThreeModal = response.data.questions.securityQuestion3;
                                            $scope.$watch(function () {
                                                    return document.getElementById('answer0').value && document.getElementById('answer1').value && document.getElementById('answer2').value;
                                                },
                                                function () {

                                                    if (document.getElementById('answer0').value != "" && document.getElementById('answer1').value != "" && document.getElementById('answer2').value != "") {
                                                        document.getElementById('questionSubmit').disabled = false;
                                                    }
                                                    else {
                                                        document.getElementById('questionSubmit').disabled = true;
                                                    }
                                                });
                                            $confirm(
                                                {
                                                    title: "Answer Security Questions",
                                                    questionOne: $scope.questionOneModal,
                                                    questionTwo: $scope.questionTwoModal,
                                                    questionThree: $scope.questionThreeModal,
                                                    ok: 'Submit',
                                                    cancel: "Cancel"
                                                },
                                                {templateUrl: 'login/forgot.credentials.displayQuestions.template.html'})
                                                .then(function () {
                                                    // send answered questions
                                                    $scope.questionOne = document.getElementById('question1').innerHTML;
                                                    $scope.questionTwo = document.getElementById('question2').innerHTML;
                                                    $scope.questionThree = document.getElementById('question3').innerHTML;

                                                    $scope.questionOneAnswer = document.getElementById('answer1').value;
                                                    $scope.questionTwoAnswer = document.getElementById('answer2').value;
                                                    $scope.questionThreeAnswer = document.getElementById('answer3').value;
                                                    var restURL =
                                                        $location.protocol() + "://" +
                                                        $location.host() + ":" +
                                                        $location.port() +
                                                        "/schwiz/rest/submit/questions";
                                                    $scope.questionsResponse = {
                                                        userName: $scope.userName,
                                                        questions: {
                                                            securityQuestion1: $scope.questionOne,
                                                            securityQuestion2: $scope.questionTwo,
                                                            securityQuestion3: $scope.questionThree
                                                        },
                                                        answers: {
                                                            securityQuestion1Answer: $scope.questionOneAnswer,
                                                            securityQuestion2Answer: $scope.questionTwoAnswer,
                                                            securityQuestion3Answer: $scope.questionThreeAnswer
                                                        }
                                                    }
                                                    $http({
                                                        method: 'POST',
                                                        url: restURL,
                                                        data: $scope.questionsResponse
                                                    }).then(function successCallback(response) {
                                                        $scope.generatedPassword = response.data['password'];
                                                        if (response.data.verification == false) {
                                                            $confirm(
                                                                {
                                                                    title: "Questions did not match",
                                                                    ok: 'OK',
                                                                    text: 'Your security question responses, do not match what we have on record'
                                                                },
                                                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'}
                                                            )
                                                                .then(function () {
                                                                    $scope.navigateToLogin();
                                                                })
                                                        }
                                                        else {
                                                            $confirm(
                                                                {
                                                                    title: "Reset Password",
                                                                    ok: 'OK',
                                                                    text: 'System generated password: ' + $scope.generatedPassword
                                                                },
                                                                {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'}
                                                            )
                                                                .then(function () {
                                                                    $scope.navigateToLogin();
                                                                })
                                                        }


                                                    }, function errorCallback(response) {
                                                        //failed POST-send security questions
                                                        $confirm(
                                                            {
                                                                title: "Something went wrong!",
                                                                ok: 'Return to Login',
                                                                text: 'Your information did not match what we have, or there was an internal error. Try again!'
                                                            },
                                                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                                            .then(function () {

                                                                $scope.navigateToLogin();


                                                            })

                                                    })
                                                })


                                        }
                                    }
                                    ,
                                    function errorCallback(response) {
                                        //failed POST-get security questions
                                        $confirm(
                                            {
                                                title: "Something went wrong!",
                                                ok: 'Return to Login',
                                                text: 'Your information did not match what we have, or there was an internal error.'
                                            },
                                            {templateUrl: 'schema-wizard/schema-wizard.confirm.template.html'})
                                            .then(function () {

                                                $scope.navigateToLogin();


                                            })
                                    }
                                )

                            }
                        }
                    )

            }
        })
    ; // forgotCredentials Controller
})
();
