(function () {

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.service("Globals", function ($rootScope) {
        var detailModels = {
            selected1: null,
            selected2: null,
            dataList: {"data": []},
            detailPanels: {"panel1": [], "panel2": []}
        };
        this.setDetailModels = function (detMdls) {
            detailModels = detMdls;
        };
        this.getDetailModels = function () {
            return detailModels;
        };

        var defaultUserId = "not-authenticated";
        var userId = defaultUserId;
        this.setUserId = function (usrId) {
            userId = usrId;
            $rootScope.$broadcast("userIdChanged", {
                userId: userId
            });
            return userId;
        };
        this.getUserId = function () {
            return userId;
        };
        this.setDefaultUserId = function () {
            userId = defaultUserId;
            $rootScope.$broadcast("userIdChanged", {
                userId: userId
            });
            return userId;
        };
    }); // Globals

    schemaWizardApp.service("Utilities", function () {
        this.showInGenericDetails = function (Globals, profile, property) {
            console.log("showInGenericDetails property: " + property);
            //console.log(profile);
            profile['shown-in-details'] = true;
            var detailModels = Globals.getDetailModels();
            detailModels.detailPanels.panel1 = [];
            detailModels.detailPanels.panel1.push(profile);
            //console.log("detailModels.detailPanels.panel1[0]");
            //console.log(detailModels.detailPanels.panel1[0]);
            detailModels.detailPanels.panel1[0]["dsName"] = null;
            detailModels.detailPanels.panel1[0]["property-name"] = property;
            // set the default viz for the histogram
            if (detailModels.detailPanels.panel1[0].detail['freq-histogram'].type == "map") {
                detailModels.detailPanels.panel1[0].viz = "map";
            } else if(detailModels.detailPanels.panel1[0].detail['detail-type'] == "text") {
                detailModels.detailPanels.panel1[0].viz = "example";
            } else {
                detailModels.detailPanels.panel1[0].viz = "hbc";
            };
            Globals.setDetailModels(detailModels);
        }; // showInGenericDetails

        this.clearGenericDetails = function (Globals) {
            console.log("clearGenericDetails");
            var detailModels = Globals.getDetailModels();
            detailModels.detailPanels.panel1 = [];
        } // clearGenericDetails
    }); // Utilities

})();
