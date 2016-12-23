(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.controller('ProgressCtrl',
        function ($scope, $location, $websocket) {
            var websocketURL ="ws://" + $location.host() + ":" +
                               $location.port() + "/schwiz/analytics";
            console.log("websocketURL: " + websocketURL);
            var websocket;

            if($scope.type =1.0 ){
                $scope.dynamic = 1;
                $scope.type = "1%";
            }
            $scope.dynamic = 1;
            $scope.type = "1%";
            $scope.description = "Uploading...";
            $scope.$on("openWebSocket", function(event, args) {
                $scope.sessionId = args.sessionId;
                console.log("onOpenWebSocket $scope.sessionId: " + $scope.sessionId);
                websocket = $websocket(websocketURL);
                websocket.send('{ "sessionId": "' + $scope.sessionId + '" }');

                websocket.onMessage(function (message) {
                    var progressFactor = JSON.parse(message.data)['numerator']
                        / JSON.parse(message.data)['denominator'];
                    $scope.description = JSON.parse(message.data)['description'];
                    //console.log("websocket.onMessage: " + progressFactor + "   " + $scope.description);
                    if (progressFactor > 0 && progressFactor <= 1) {
                        $scope.dynamic = Math.round(progressFactor * 100);
                        $scope.type = $scope.dynamic + "%";
                        $scope.$on("dataSamplesReceived", function() {
                            $scope.dynamic = 1;
                            $scope.type = "1%";
                        });
                        $scope.$on("catalogUpdate", function() {
                            $scope.dynamic = 1;
                            $scope.type = "1%";
                        });
                    }
                }); // onMessage
            }); // onOpenWebSocket

            $scope.$on("closeWebSocket", function(event) {
                try {
                    websocket.close();
                } catch (e) {}
                $scope.dynamic = 1;
                $scope.type = "1%";
                $scope.description = "Uploading...";
                console.log("onCloseWebSocket: close message appears in jetty console");
            }); // onCloseWebSocket

        }); // ProgressCtrl
})();

