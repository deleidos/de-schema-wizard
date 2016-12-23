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
        this.setRole = function (roles) {
            userRole = roles;
            $rootScope.$broadcast("userRoleChanged", {
                userRole: userRole
            });
            return userRole;
        };
        this.getRole = function () {
            return userRole;
        };
        this.setName = function (name) {
            userName = name;
            $rootScope.$broadcast("use name has changed", {
                userName: name
            });
            return userName;
        };
        this.getName = function () {
            return userName;
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
            }
            Globals.setDetailModels(detailModels);
        }; // showInGenericDetails

        this.clearGenericDetails = function (Globals) {
            console.log("clearGenericDetails");
            var detailModels = Globals.getDetailModels();
            detailModels.detailPanels.panel1 = [];
        }; // clearGenericDetails

        var dataSamples;
        this.setDataSamples = function (ds) {
            dataSamples = ds;
        };
        this.getDataSamples = function () {
            return dataSamples;
        };
        this.getDataSample = function (index) {
            return dataSamples[index];
        };

        var dataSamplesBackup;
        this.setDataSamplesBackup = function (ds) {
            dataSamplesBackup = angular.copy(ds);
        };
        this.getDataSamplesBackup = function () {
            return angular.copy(dataSamplesBackup);
        };
        this.getDataSampleBackup = function (index) {
            return dataSamplesBackup[index];
        };

        var schema;
        this.setSchema = function (s) {
            schema = s;
        };
        this.getSchema = function () {
            return schema;
        };

        var modifySchemaMode = false;
        this.setModifySchemaMode = function (msm) {
            modifySchemaMode = msm;
        };
        this.getModifySchemaMode = function () {
            return modifySchemaMode;
        };
    }); // Utilities
})();
