angular.module('treeTableControllers')
.service("Utils", function () {

    var dataSamples;
    this.setDataSamples = function (ds) {
        dataSamples = ds;
    };
    this.getDataSamples = function () {
        return dataSamples;
    };
    this.getDataSample = function (index) {
        return dataSamples[index];
    }

    var dataSamplesBackup;
    this.setDataSamplesBackup = function (ds) {
        dataSamplesBackup = angular.copy(ds);
    };
    this.getDataSamplesBackup = function () {
        return angular.copy(dataSamplesBackup);
    };
    this.getDataSampleBackup = function (index) {
        return dataSamplesBackup[index];
    }});
