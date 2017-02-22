(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('userCtrl', ['$rootScope', '$scope', '$resource',
        '$location', '$confirm', '$http', '$timeout', 'Globals', 'Idle',
        'guidedTourStepFactory', '$cookies', 'uiTourService',
        function ($rootScope, $scope, $resource, $location, $confirm, $http, $timeout, Globals, Idle, guidedTourStepFactory, $cookies, TourService) {

            guidedTourStepFactory.get()
                .$promise.then(function (response) {
                    console.log(response)
                $rootScope.tourInformation = response;
                $scope.userTour = $rootScope.tourInformation.userTour;
            });
            if ($scope.path == "/userPage") {
                if ($cookies.get('schwiz.tours.userPage') !== "visited") {
                    $timeout(function () {
                        TourService.getTourByName('catalog').startAt('700');
                    }, 2500);
                    $cookies.put('schwiz.tours.userPage', "visited");
                }
            }

            $scope.name = Globals.getUserId();
            $scope.currentUserRole = Globals.getRole();
            $scope.currentUserName = Globals.getName();
            $scope.questionsList1 = [];
            $scope.questionsList2 = [];
            $scope.questionThree = [];
            if ($scope.currentUserRole == "admin") {
                $scope.getUserList = function () {
                    var restURL =
                        $location.protocol() + "://" +
                        $location.host() + ":" +
                        $location.port() +
                        "/schwiz/rest/users";

                    $http({
                        method: 'GET',
                        url: restURL,
                        data: ""
                    }).then(function successCallback(response) {
                        $scope.userPackage = response.data.userPackage;
                        for (x = 0; x < $scope.userPackage.length; x++) {
                            if ($scope.userPackage[x].userName == Globals.getUserId()) {
                                $scope.userRole = $scope.userPackage[x].userRole;
                            }
                        }
                    }, function errorCallback(response) {
                        console.log("Could not get user list");

                    })
                }
            }
            else {
                $scope.getUserList = function () {
                    //make user package single user
                    $scope.userList = {
                        "userPackage": [{
                            firstName: $scope.currentUserName,
                            userRole: $scope.currentUserRole,
                            userName: $scope.name
                        }]
                    };
                    $scope.userPackage = $scope.userList.userPackage;
                }
            }
            $scope.getUserList();
            $scope.newUser = false;


            // create new user
            $scope.createNewUser = function () {
                $scope.newUser = true;
                $scope.clearDataFromSaveUser();
                $rootScope.$broadcast("transformTable", {});


                $scope.$watch(function () {
                        return $scope.role && document.getElementById('newName').value && document.getElementById('newUsername').value
                            && document.getElementById('newPassword').value
                    },
                    function () {

                        if ($scope.role != "" && document.getElementById('newPassword').value != "" && document.getElementById('newName').value != ""
                            && document.getElementById('newUsername').value != "") {
                            document.getElementById('saveButton').disabled = false;
                        }
                        else {
                            document.getElementById('saveButton').disabled = true;
                        }
                    });
            };
            $scope.saveNewUser = function () {
                $scope.savedName = document.getElementById('newName').value;
                $scope.savedUserName = document.getElementById('newUsername').value;
                $scope.savedUserRole = document.querySelector('input[name="roleTypeNew"]:checked').value.toLowerCase();
                $scope.savedPassword = document.getElementById('newPassword').value;

                $scope.newUserCredentials = {
                    firstName: $scope.savedName,
                    userName: $scope.savedUserName,
                    userRole: $scope.savedUserRole,
                    password: $scope.savedPassword
                };
                $scope.newUser = false;
                var restURL =
                    $location.protocol() + "://" +
                    $location.host() + ":" +
                    $location.port() +
                    "/schwiz/rest/createUser";

                $http({
                    method: 'POST',
                    url: restURL,
                    data: $scope.newUserCredentials
                }).then(function successCallback(response) {

                    //update user catalog
                    $scope.getUserList();


                }, function errorCallback(response) {
                    console.log("User was not added");

                });

            };
            // cancel new user creation
            $scope.cancelNewUser = function () {
                $scope.newUser = false;
                $scope.clearDataFromSaveUser();
            };

            //delete user
            $scope.deleteUser = function (username) {

                var restURL =
                    $location.protocol() + "://" +
                    $location.host() + ":" +
                    $location.port() +
                    "/schwiz/rest/deleteUser";

                if (username == $scope.name) {
                    $confirm(
                        {
                            username: username,
                            title: 'You cannot delete yourself',
                            text: 'You cannot delete yourself from the user catalog',
                            ok: 'OK',
                            cancel: 'Cancel'
                        }
                    ).then(function () {

                    })

                }
                else {
                    $confirm(
                        {
                            username: username,
                            title: 'Are you sure you want to Delete?',
                            text: 'Are you sure you want to delete ' + username + '?',
                            ok: 'OK',
                            cancel: 'Cancel'
                        }
                    ).then(function () {

                        $http({
                            method: 'DELETE',
                            url: restURL,
                            data: username
                        }).then(function successCallback(response) {
                            $scope.getUserList();
                            //get catalog of users


                        }, function errorCallback(response) {
                            console.log("User was not deleted");

                        });

                    })
                }

            };

            //modify list of users
            $scope.modifyUserList = function (name, username, role) {
                $scope.isCurrentUser = "";
                if (username == $scope.name) {
                    $scope.isCurrentUser = true;
                }
                else {
                    $scope.isCurrentUser = false;
                }
                var restURL =
                    $location.protocol() + "://" +
                    $location.host() + ":" +
                    $location.port() +
                    "/schwiz/rest/security/questions";

                $http({
                    method: 'POST',
                    url: restURL,
                    data: {userName: username}
                }).then(function successCallback(response) {
                    $scope.questionsList1 = response.data.securityQuestion1;
                    $scope.questionsList2 = response.data.securityQuestion2;
                    $scope.questionsList3 = response.data.securityQuestion3;
                    $confirm(
                        {
                            name: name,
                            username: username,
                            role: role,
                            sec1: $scope.questionsList1,
                            sec2: $scope.questionsList2,
                            sec3: $scope.questionsList3,
                            currentUser: $scope.isCurrentUser,
                            title: 'Modify User',
                            ok: 'OK',
                            cancel: 'Cancel'
                        },
                        {templateUrl: 'login/user.page.modify.user.modal.template.html'}
                    ).then(function () {
                            $scope.modifiedUsername = document.getElementById('username').value;
                            $scope.modifiedName = document.getElementById('name').value;
                            $scope.modifiedPassword = document.getElementById('password').value;

                            $scope.securityQuestionOne = document.getElementById("q1q").value;
                            $scope.securityQuestionOneAnswer = document.getElementById("q1").value;
                            $scope.securityQuestionTwo = document.getElementById('q2q').value;
                            $scope.securityQuestionTwoAnswer = document.getElementById('q2').value;
                            $scope.securityQuestionThree = document.getElementById('q3q').value;
                            $scope.securityQuestionThreeAnswer = document.getElementById('q3').value;
                            try {

                                //query selector will null out if nothing is checked needs to be inside try/catch block
                                $scope.modifiedRole = document.querySelector('input[name="roleType"]:checked').value.toLowerCase();
                            }
                            catch (e) {
                                $scope.modifiedRole = "";
                            }

                            $scope.updateUserData = {};
                            $scope.updateUserData.userName = $scope.modifiedUsername;

                            if ($scope.modifiedName != "") {
                                $scope.updateUserData.firstName = $scope.modifiedName;
                                $scope.currentUserName = $scope.modifiedName;
                            }
                            if ($scope.modifiedPassword != "") {
                                $scope.updateUserData.password = $scope.modifiedPassword;
                            }
                            if ($scope.modifiedRole != "") {
                                $scope.updateUserData.userRole = $scope.modifiedRole;
                            }

                            if ($scope.securityQuestionOneAnswer && $scope.securityQuestionTwoAnswer && $scope.securityQuestionThreeAnswer != "") {
                                console.log("update security questions")
                                var restURL =
                                    $location.protocol() + "://" +
                                    $location.host() + ":" +
                                    $location.port() +
                                    "/schwiz/rest/modify/user/questions";
                                $http({
                                    method: 'POST',
                                    url: restURL,
                                    data: {
                                        userName: username,
                                        questions: {
                                            securityQuestion1: $scope.securityQuestionOne,
                                            securityQuestion2: $scope.securityQuestionTwo,
                                            securityQuestion3: $scope.securityQuestionThree
                                        },
                                        answers: {
                                            securityQuestion1Answer: $scope.securityQuestionOneAnswer,
                                            securityQuestion2Answer: $scope.securityQuestionTwoAnswer,
                                            securityQuestion3Answer: $scope.securityQuestionThreeAnswer
                                        }
                                    }
                                }).then(function successCallback(response) {


                                }, function errorCallback(response) {
                                    console.log("User was not modified");
                                });
                            }

                            var restURL =
                                $location.protocol() + "://" +
                                $location.host() + ":" +
                                $location.port() +
                                "/schwiz/rest/updateUser";

                            $http({
                                method: 'POST',
                                url: restURL,
                                data: $scope.updateUserData
                            }).then(function successCallback(response) {
                                // grab list of users
                                $scope.getUserList();
                            }, function errorCallback(response) {
                                console.log("User was not modified");
                            });
                        }
                    )
                }, function errorCallback(response) {
                    console.log("Could not get question list");

                });
            };

            $scope.clearDataFromSaveUser = function () {
                document.getElementById('newName').value = "";
                document.getElementById('newUsername').value = "";
                document.getElementById('newPassword').value = "";
                document.getElementById('newPasswordConfirm').value = "";
                document.getElementById('newUserForm').reset();
                $scope.role = "";
            }


        }]); // userCtrl
})();

