    angular.module('treeTableControllers', [])
        .constant("matchConfidenceThreshold", 94)
        .constant("defaultInterpretationMatch", false)
        .controller('treeTableController',
		function($scope, $window, $parse, $timeout, Utils, matchConfidenceThreshold, defaultInterpretationMatch) {

            $scope.model = {};

            $scope.model.dataSamples =
                [
                    {
                        "dsName": "vehicle_position_cluster_schema_examples-1-US-Only",
                        "dsId": "12e0ea91-a5e4-4081-82e9-34770a220327",
                        "dsNumbRecords": 55,
                        "dsContainsStructuredData": true,
                        "dsProfile": {
                            "vehicle_positions.aircraft.air_ground": {
                                "used-in-schema": true,
                                "detailAvg": "1",
                                "detailMin": "1",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "display-name": "air_ground",
                                "struc-type": null,
                                "detailMax": "1",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 1,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [120],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["A"],
                                        "exampleValues": null,
                                        "labels": ["A"]
                                    },
                                    "walkingCount": 120,
                                    "walkingSum": 120,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 120,
                                    "detail-type": "term",
                                    "max-length": 1,
                                    "char-freq-histogram": null,
                                    "min-length": 1
                                },
                                "presence": 1,
/*
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
*/
                                "interpretations": {
                                    "availableOptions": [
                                        {
                                            "iId": null,
                                            "interpretation": "AGL",
                                            "iDomainId": null,
                                            "iName": "AGL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        },
                                        {
                                            "iId": null,
                                            "interpretation": "Unknown",
                                            "iDomainId": null,
                                            "iName": "Unknown",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        }
                                    ],
                                    "selectedOption":
                                        {
                                            "iId": null,
                                            "interpretation": "AGL",
                                            "iDomainId": null,
                                            "iName": "AGL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        },
                                },
                                "exampleValues": ["A"]
                            },
                            "vehicle_positions.latitude": {
                                "used-in-schema": true,
                                "detailAvg": "25.67816",
                                "detailMin": "25.67816",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.lat"
                                }, {
                                    "confidence": 85,
                                    "matching-field": "vehicle_positions.long"
                                }],
                                "detailStdDev": "5.595885009584622",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "119",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "latitude",
                                "struc-type": null,
                                "detailMax": "55.66795",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 38.08432666666667,
                                    "freq-histogram": {
                                        "data": [6, 0, 0, 0, 2, 0, 0, 2, 0, 0, 4, 2, 3, 15, 2, 0, 2, 6, 2, 3, 1, 8, 4, 11, 12, 12, 7, 6, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.latitude",
                                            "longitude-key": "vehicle_positions.longitude",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 40
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 40
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 40
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[25.67815,26.27795)", "[26.27795,26.87775)", "[26.87775,27.47755)", "[27.47755,28.07735)", "[28.07735,28.67715)", "[28.67715,29.27695)", "[29.27695,29.87675)", "[29.87675,30.47655)", "[30.47655,31.07635)", "[31.07635,31.67615)", "[31.67615,32.27595)", "[32.27595,32.87575)", "[32.87575,33.47555)", "[33.47555,34.07535)", "[34.07535,34.67515)", "[34.67515,35.27495)", "[35.27495,35.87475)", "[35.87475,36.47455)", "[36.47455,37.07435)", "[37.07435,37.67415)", "[37.67415,38.27395)", "[38.27395,38.87375)", "[38.87375,39.47355)", "[39.47355,40.07335)", "[40.07335,40.67315)", "[40.67315,41.27295)", "[41.27295,41.87275)", "[41.87275,42.47255)", "[42.47255,43.07235)", "[43.07235,43.67215)", "[43.67215,44.27195)", "[44.27195,44.87175)", "[44.87175,45.47155)", "[45.47155,46.07135)", "[46.07135,46.67115)", "[46.67115,47.27095)", "[47.27095,47.87075)", "[47.87075,48.47055)", "[48.47055,49.07035)", "[49.07035,49.67015)", "[49.67015,50.26995)", "[50.26995,50.86975)", "[50.86975,51.46955)", "[51.46955,52.06935)", "[52.06935,52.66915)", "[52.66915,53.26895)", "[53.26895,53.86875)", "[53.86875,54.46855)", "[54.46855,55.06835)", "[55.06835,55.66815)"],
                                        "exampleValues": null,
                                        "labels": ["[25.6781,26.2779)", "[26.2779,26.8777)", "[26.8777,27.4775)", "[27.4775,28.0773)", "[28.0773,28.6771)", "[28.6771,29.2769)", "[29.2769,29.8767)", "[29.8767,30.4765)", "[30.4765,31.0763)", "[31.0763,31.6761)", "[31.6761,32.2759)", "[32.2759,32.8757)", "[32.8757,33.4755)", "[33.4755,34.0753)", "[34.0753,34.6751)", "[34.6751,35.2749)", "[35.2749,35.8747)", "[35.8747,36.4745)", "[36.4745,37.0743)", "[37.0743,37.6741)", "[37.6741,38.2739)", "[38.2739,38.8737)", "[38.8737,39.4735)", "[39.4735,40.0733)", "[40.0733,40.6731)", "[40.6731,41.2729)", "[41.2729,41.8727)", "[41.8727,42.4725)", "[42.4725,43.0723)", "[43.0723,43.6721)", "[43.6721,44.2719)", "[44.2719,44.8717)", "[44.8717,45.4715)", "[45.4715,46.0713)", "[46.0713,46.6711)", "[46.6711,47.2709)", "[47.2709,47.8707)", "[47.8707,48.4705)", "[48.4705,49.0703)", "[49.0703,49.6701)", "[49.6701,50.2699)", "[50.2699,50.8697)", "[50.8697,51.4695)", "[51.4695,52.0693)", "[52.0693,52.6691)", "[52.6691,53.2689)", "[53.2689,53.8687)", "[53.8687,54.4685)", "[54.4685,55.0683)", "[55.0683,55.6681)"]
                                    },
                                    "min": 25.67816,
                                    "walkingCount": 120,
                                    "walkingSum": 4570.1192,
                                    "max": 55.66795,
                                    "num-distinct-values": "119",
                                    "walkingSquareSum": 177807.5840032646,
                                    "detail-type": "decimal",
                                    "std-dev": 5.595885009584622
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["39.87111", "55.65111", "35.94619", "43.01199", "38.3211", "42.43195", "40.79768", "40.32886", "40.8711", "40.15982", "33.95762", "39.9684", "40.44943", "38.60004", "39.99694", "41.99068", "39.99335", "39.99239", "33.23007", "36.86847", "38.33084", "36.33028", "33.62718", "39.85945", "26.0658", "43.09685", "41.69449", "40.56464", "28.4376", "39.24209", "40.76579", "40.78239", "25.80441", "32.80248", "38.69257", "40.43976", "41.39397", "38.94644", "30.2017", "33.68366", "40.89386", "53.24474", "34.01842", "40.72306", "41.51852", "42.25551", "40.6834", "34.15072", "31.86566", "36.4411"]
                            },
                            "vehicle_positions.aircraft": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "aircraft",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.longitude": {
                                "used-in-schema": true,
                                "detailAvg": "-122.35169",
                                "detailMin": "-122.35169",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.long"
                                }, {
                                    "confidence": 90,
                                    "matching-field": "vehicle_positions.lat"
                                }],
                                "detailStdDev": "36.025620845828435",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "120",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "longitude",
                                "struc-type": null,
                                "detailMax": "140.89445",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": -76.62620275,
                                    "freq-histogram": {
                                        "data": [0, 4, 9, 0, 0, 0, 4, 2, 8, 27, 40, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.latitude",
                                            "longitude-key": "vehicle_positions.longitude",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 40
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 40
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 40
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[-130,-125)", "[-125,-120)", "[-120,-115)", "[-115,-110)", "[-110,-105)", "[-105,-100)", "[-100,-95)", "[-95,-90)", "[-90,-85)", "[-85,-80)", "[-80,-75)", "[-75,-70)", "[-70,-65)", "[-65,-60)", "[-60,-55)", "[-55,-50)", "[-50,-45)", "[-45,-40)", "[-40,-35)", "[-35,-30)", "[-30,-25)", "[-25,-20)", "[-20,-15)", "[-15,-10)", "[-10,-5)", "[-5,0)", "[0,5)", "[5,10)", "[10,15)", "[15,20)", "[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)"],
                                        "exampleValues": null,
                                        "labels": ["[-130.0,-125.0)", "[-125.0,-120.0)", "[-120.0,-115.0)", "[-115.0,-110.0)", "[-110.0,-105.0)", "[-105.0,-100.0)", "[-100.0,-95.0)", "[-95.0,-90.0)", "[-90.0,-85.0)", "[-85.0,-80.0)", "[-80.0,-75.0)", "[-75.0,-70.0)", "[-70.0,-65.0)", "[-65.0,-60.0)", "[-60.0,-55.0)", "[-55.0,-50.0)", "[-50.0,-45.0)", "[-45.0,-40.0)", "[-40.0,-35.0)", "[-35.0,-30.0)", "[-30.0,-25.0)", "[-25.0,-20.0)", "[-20.0,-15.0)", "[-15.0,-10.0)", "[-10.0,-5.0)", "[-5.0,0.0)", "[0.0,5.0)", "[5.0,10.0)", "[10.0,15.0)", "[15.0,20.0)", "[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)"]
                                    },
                                    "min": -122.35169,
                                    "walkingCount": 120,
                                    "walkingSum": -9195.14433,
                                    "max": 140.89445,
                                    "num-distinct-values": "120",
                                    "walkingSquareSum": 860330.4366253795,
                                    "detail-type": "decimal",
                                    "std-dev": 36.025620845828435
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["-75.76062", "-4.98419", "-77.20087", "-74.14777", "-75.56008", "-84.36274", "-74.08418", "-73.90746", "-74.11801", "-75.02926", "-79.08531", "-75.59592", "-73.50629", "-73.5578", "-77.92477", "-83.01981", "-87.77939", "-82.92343", "-79.71548", "-79.70732", "-86.58214", "-78.41139", "-77.04274", "-84.36132", "-75.28095", "-80.12593", "-78.74647", "-87.79577", "-75.33974", "-82.56748", "-84.40372", "-74.1214", "-80.2587", "-96.78906", "-77.9176", "-75.60321", "-76.11297", "-76.12383", "-77.5086", "-96.91946", "-84.30805", "-75.10941", "2.30988", "-83.35294", "-74.63904", "-87.15556", "-92.09462", "-74.00672", "-82.95444", "-82.01193"]
                            },
                            "vehicle_positions.tailnumber": {
                                "used-in-schema": true,
                                "detailAvg": "5",
                                "detailMin": "5",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "0.12801909579781015",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "99",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "tailnumber",
                                "struc-type": null,
                                "detailMax": "6",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 5.983333333333333,
                                    "std-dev-length": 0.12801909579781015,
                                    "freq-histogram": {
                                        "data": [1, 2, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 2, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["N108NN", "N111ZM", "N13227", "N135NN", "N141NN", "N144AN", "N154UW", "N1603", "N174DZ", "N176DZ", "N194AA", "N206FR", "N242MR", "N281AY", "N308UP", "N346AN", "N353AA", "N36444", "N37277", "N37468", "N392MG", "N401YX", "N404YX", "N415EC", "N474FX", "N509NK", "N519UW", "N521VL", "N526JL", "N526VA", "N538UW", "N539UW", "N544UW", "N546UW", "N570UW", "N572EC", "N575UW", "N588NK", "N589NW", "N616NK", "N61DN", "N624NK", "N624VA", "N634JB", "N634VA", "N635VA", "N640JB", "N655JB", "N66814", "N671US", "N711ZX", "N73259", "N75425", "N76062", "N76502", "N76523", "N7716A", "N7725A", "N7728D", "N7730A", "N77431", "N77530", "N775UA", "N779JB", "N812TT", "N816NW", "N821JB", "N823NN", "N827JB", "N8316H", "N8320J", "N8329B", "N836VA", "N838NN", "N839VA", "N848NN", "N859AM", "N8618N", "N8622A", "N8635F", "N863DA", "N8641B", "N8652B", "N870NN", "N875NN", "N890NN", "N895JH", "N9015D", "N9018E", "N914NN", "N917UY", "N93305", "N933FR", "N941FR", "N943JT", "N954NN", "N959NN", "N960NN", "N977AE"],
                                        "exampleValues": null,
                                        "labels": ["N108NN", "N111ZM", "N13227", "N135NN", "N141NN", "N144AN", "N154UW", "N1603", "N174DZ", "N176DZ", "N194AA", "N206FR", "N242MR", "N281AY", "N308UP", "N346AN", "N353AA", "N36444", "N37277", "N37468", "N392MG", "N401YX", "N404YX", "N415EC", "N474FX", "N509NK", "N519UW", "N521VL", "N526JL", "N526VA", "N538UW", "N539UW", "N544UW", "N546UW", "N570UW", "N572EC", "N575UW", "N588NK", "N589NW", "N616NK", "N61DN", "N624NK", "N624VA", "N634JB", "N634VA", "N635VA", "N640JB", "N655JB", "N66814", "N671US", "N711ZX", "N73259", "N75425", "N76062", "N76502", "N76523", "N7716A", "N7725A", "N7728D", "N7730A", "N77431", "N77530", "N775UA", "N779JB", "N812TT", "N816NW", "N821JB", "N823NN", "N827JB", "N8316H", "N8320J", "N8329B", "N836VA", "N838NN", "N839VA", "N848NN", "N859AM", "N8618N", "N8622A", "N8635F", "N863DA", "N8641B", "N8652B", "N870NN", "N875NN", "N890NN", "N895JH", "N9015D", "N9018E", "N914NN", "N917UY", "N93305", "N933FR", "N941FR", "N943JT", "N954NN", "N959NN", "N960NN", "N977AE"]
                                    },
                                    "walkingCount": 120,
                                    "walkingSum": 718,
                                    "num-distinct-values": "99",
                                    "walkingSquareSum": 4298,
                                    "detail-type": "term",
                                    "max-length": 6,
                                    "char-freq-histogram": null,
                                    "min-length": 5
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["N7728D", "N519UW", "N174DZ", "N176DZ", "N135NN", "N914NN", "N959NN", "N838NN", "N779JB", "N624NK", "N8320J", "N111ZM", "N821JB", "N624VA", "N526JL", "N823NN", "N108NN", "N711ZX", "N144AN", "N242MR", "N570UW", "N401YX", "N941FR", "N76502", "N474FX", "N37277", "N960NN", "N616NK", "N8316H", "N8635F", "N392MG", "N575UW", "N890NN", "N93305", "N7730A", "N154UW", "N544UW", "N539UW", "N509NK", "N635VA", "N36444", "N8618N", "N526VA", "N194AA", "N75425", "N8622A", "N61DN", "N76062", "N77530", "N943JT"]
                            },
                            "vehicle_positions.speed": {
                                "used-in-schema": true,
                                "detailAvg": "85.0",
                                "detailMin": "85.0",
                                "main-type": "number",
                                "matching-fields": [],
                                "detailStdDev": "143.04732875481767",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "106",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "speed",
                                "struc-type": null,
                                "detailMax": "979.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 342.85833333333335,
                                    "freq-histogram": {
                                        "data": [1, 1, 3, 10, 6, 5, 3, 6, 7, 5, 5, 4, 2, 6, 2, 4, 5, 17, 11, 6, 5, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[85,105)", "[105,125)", "[125,145)", "[145,165)", "[165,185)", "[185,205)", "[205,225)", "[225,245)", "[245,265)", "[265,285)", "[285,305)", "[305,325)", "[325,345)", "[345,365)", "[365,385)", "[385,405)", "[405,425)", "[425,445)", "[445,465)", "[465,485)", "[485,505)", "[505,525)", "[525,545)", "[545,565)", "[565,585)", "[585,605)", "[605,625)", "[625,645)", "[645,665)", "[665,685)", "[685,705)", "[705,725)", "[725,745)", "[745,765)", "[765,785)", "[785,805)", "[805,825)", "[825,845)", "[845,865)", "[865,885)", "[885,905)", "[905,925)", "[925,945)", "[945,965)", "[965,985)"],
                                        "exampleValues": null,
                                        "labels": ["[85.0,105.0)", "[105.0,125.0)", "[125.0,145.0)", "[145.0,165.0)", "[165.0,185.0)", "[185.0,205.0)", "[205.0,225.0)", "[225.0,245.0)", "[245.0,265.0)", "[265.0,285.0)", "[285.0,305.0)", "[305.0,325.0)", "[325.0,345.0)", "[345.0,365.0)", "[365.0,385.0)", "[385.0,405.0)", "[405.0,425.0)", "[425.0,445.0)", "[445.0,465.0)", "[465.0,485.0)", "[485.0,505.0)", "[505.0,525.0)", "[525.0,545.0)", "[545.0,565.0)", "[565.0,585.0)", "[585.0,605.0)", "[605.0,625.0)", "[625.0,645.0)", "[645.0,665.0)", "[665.0,685.0)", "[685.0,705.0)", "[705.0,725.0)", "[725.0,745.0)", "[745.0,765.0)", "[765.0,785.0)", "[785.0,805.0)", "[805.0,825.0)", "[825.0,845.0)", "[845.0,865.0)", "[865.0,885.0)", "[885.0,905.0)", "[905.0,925.0)", "[925.0,945.0)", "[945.0,965.0)", "[965.0,985.0)"]
                                    },
                                    "min": 85,
                                    "walkingCount": 120,
                                    "walkingSum": 41143,
                                    "max": 979,
                                    "num-distinct-values": "106",
                                    "walkingSquareSum": 1.6561725E7,
                                    "detail-type": "integer",
                                    "std-dev": 143.04732875481767
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["717", "401", "463", "440", "433", "478", "346", "500", "366", "388", "420", "441", "166", "442", "238", "144", "149", "133", "426", "431", "460", "163", "151", "412", "232", "148", "247", "191", "351", "207", "293", "979", "511", "162", "400", "261", "320", "491", "297", "250", "422", "335", "456", "712", "280", "358", "354", "194", "178", "271"]
                            },
                            "cluster_id": {
                                "used-in-schema": true,
                                "detailAvg": "36",
                                "detailMin": "36",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "55",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_id",
                                "struc-type": null,
                                "detailMax": "36",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 36,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["017ccf68-af3a-4824-a461-0a8df1efac59", "080567a9-52d6-4efa-9674-53e004bfb57a", "0abbcf0b-b3d3-49ad-b252-e8ee472ebad1", "0b3be41b-c843-477f-93c8-a83c75720c4a", "0e80dc2e-4508-400b-a8df-102a47e50828", "18d19d30-e1a4-4d40-b9b4-0719410d89f3", "1a7c127d-5ebb-41d2-8ede-edb889a13f7e", "1f9b0f3a-51e5-4163-9525-5511b01bb124", "2aed689e-6dad-4ce9-a964-12d80aae97f0", "36ab88ef-8a5a-416f-be60-86c9036f61ff", "3c17ba7e-9680-4dbb-9c2b-b99fa42c55aa", "3e7b3024-babe-4749-8cc9-2d6e05ca346e", "4144d3da-ca81-4fe8-ac6e-10e440181e8e", "426764c3-afad-4ae4-87e5-317d9dd2b68d", "471c9683-7390-4ab0-aaf7-678e329f22de", "47d36f93-c48b-462c-a769-bf4539a88f93", "5165eb5c-0778-423a-9b05-05c5c537b8fb", "585ebc1d-e359-4c5a-9352-4c305706a0ae", "5d2a032e-5221-4d77-8e46-f554e81a43b4", "6328d6c2-af76-4af2-ba39-aa21bd8fd3be", "63464979-cdff-45b0-9260-b834fc9d2d9d", "63d48759-5895-4dde-b09f-17dbab6b03b1", "65150fb8-10bd-4510-8a46-d7f1b3c6d2c1", "679454f8-d587-4bab-89f0-225c5598bfb9", "69d2f1b6-51fa-4918-8cdf-f782a8128e9a", "6e1c1508-2b98-4452-80d0-42db0b686f2a", "6fc142fd-f82f-4bfe-b58c-b6eb213abca5", "7d65821a-8376-4e98-beaa-f2dcbdef13fb", "7e5566e8-5d3d-438f-a30f-5bf0b57284fc", "7e5ff248-78ff-46b1-9c59-b28eccadbced", "86a0c736-f716-49f5-8ee4-084ccacad98d", "910d69a1-f553-4421-8e73-529323724929", "944731f1-a9f7-4459-8ef7-d1378cb9e60d", "99e7e292-014b-41fa-bb24-33a5ec706400", "a4ccad78-c9f1-4a3d-9ae8-c238501da1ea", "a6e35ba5-ea10-4c1f-882a-cfb244d4b376", "aed7dd99-305b-4d68-b040-e8111e3b64f6", "af9ff2a0-1a45-4918-8346-d60c783f4bad", "b937e927-7066-4a6e-b966-2c4750df8ec5", "ba3ce904-82ce-4e4c-bc6b-a84418cb29c5", "bda24d6d-ef92-4a03-93c6-679167382187", "c52b874f-0047-44b8-98ef-d61bfdfc4ff3", "cd6cbbc5-359d-4070-975c-40eee7a67370", "cdbaecb1-966d-4288-8317-6df727d1530e", "ce8525fa-ac33-49fd-af17-fdf6dbe92f0e", "cf4241c3-1764-489e-b713-b77f19201137", "d4139161-bcf4-48ed-b334-5eb50a3bb71d", "e1c25b75-84b2-4fb5-ae62-b620fbe607be", "e620d55b-2d76-4553-bff0-39ac6ac99758", "f28a9137-b852-42d2-809d-ffebe31033ff", "f5aae067-52e0-47d9-bdb7-b504ac707b12", "fbe214c9-d2aa-48c1-8f3d-3248206b9369", "fedf2833-8791-4145-af46-3ac9996be022", "ff1bd74d-9b31-4306-907b-90dfd0fd3692", "ff2d69f1-069e-48b2-ba9c-6ea43cb0f6ae"],
                                        "exampleValues": null,
                                        "labels": ["017ccf68-af3a-4...", "080567a9-52d6-4...", "0abbcf0b-b3d3-4...", "0b3be41b-c843-4...", "0e80dc2e-4508-4...", "18d19d30-e1a4-4...", "1a7c127d-5ebb-4...", "1f9b0f3a-51e5-4...", "2aed689e-6dad-4...", "36ab88ef-8a5a-4...", "3c17ba7e-9680-4...", "3e7b3024-babe-4...", "4144d3da-ca81-4...", "426764c3-afad-4...", "471c9683-7390-4...", "47d36f93-c48b-4...", "5165eb5c-0778-4...", "585ebc1d-e359-4...", "5d2a032e-5221-4...", "6328d6c2-af76-4...", "63464979-cdff-4...", "63d48759-5895-4...", "65150fb8-10bd-4...", "679454f8-d587-4...", "69d2f1b6-51fa-4...", "6e1c1508-2b98-4...", "6fc142fd-f82f-4...", "7d65821a-8376-4...", "7e5566e8-5d3d-4...", "7e5ff248-78ff-4...", "86a0c736-f716-4...", "910d69a1-f553-4...", "944731f1-a9f7-4...", "99e7e292-014b-4...", "a4ccad78-c9f1-4...", "a6e35ba5-ea10-4...", "aed7dd99-305b-4...", "af9ff2a0-1a45-4...", "b937e927-7066-4...", "ba3ce904-82ce-4...", "bda24d6d-ef92-4...", "c52b874f-0047-4...", "cd6cbbc5-359d-4...", "cdbaecb1-966d-4...", "ce8525fa-ac33-4...", "cf4241c3-1764-4...", "d4139161-bcf4-4...", "e1c25b75-84b2-4...", "e620d55b-2d76-4...", "f28a9137-b852-4...", "f5aae067-52e0-4...", "fbe214c9-d2aa-4...", "fedf2833-8791-4...", "ff1bd74d-9b31-4...", "ff2d69f1-069e-4..."]
                                    },
                                    "walkingCount": 55,
                                    "walkingSum": 1980,
                                    "num-distinct-values": "55",
                                    "walkingSquareSum": 71280,
                                    "detail-type": "term",
                                    "max-length": 36,
                                    "char-freq-histogram": null,
                                    "min-length": 36
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["3c17ba7e-9680-4dbb-9c2b-b99fa42c55aa", "cf4241c3-1764-489e-b713-b77f19201137", "080567a9-52d6-4efa-9674-53e004bfb57a", "fedf2833-8791-4145-af46-3ac9996be022", "6328d6c2-af76-4af2-ba39-aa21bd8fd3be", "0b3be41b-c843-477f-93c8-a83c75720c4a", "36ab88ef-8a5a-416f-be60-86c9036f61ff", "6fc142fd-f82f-4bfe-b58c-b6eb213abca5", "a4ccad78-c9f1-4a3d-9ae8-c238501da1ea", "69d2f1b6-51fa-4918-8cdf-f782a8128e9a", "af9ff2a0-1a45-4918-8346-d60c783f4bad", "585ebc1d-e359-4c5a-9352-4c305706a0ae", "99e7e292-014b-41fa-bb24-33a5ec706400", "910d69a1-f553-4421-8e73-529323724929", "f5aae067-52e0-47d9-bdb7-b504ac707b12", "0abbcf0b-b3d3-49ad-b252-e8ee472ebad1", "1f9b0f3a-51e5-4163-9525-5511b01bb124", "cd6cbbc5-359d-4070-975c-40eee7a67370", "86a0c736-f716-49f5-8ee4-084ccacad98d", "7d65821a-8376-4e98-beaa-f2dcbdef13fb", "b937e927-7066-4a6e-b966-2c4750df8ec5", "e620d55b-2d76-4553-bff0-39ac6ac99758", "6e1c1508-2b98-4452-80d0-42db0b686f2a", "7e5ff248-78ff-46b1-9c59-b28eccadbced", "1a7c127d-5ebb-41d2-8ede-edb889a13f7e", "017ccf68-af3a-4824-a461-0a8df1efac59", "63d48759-5895-4dde-b09f-17dbab6b03b1", "2aed689e-6dad-4ce9-a964-12d80aae97f0", "ff2d69f1-069e-48b2-ba9c-6ea43cb0f6ae", "65150fb8-10bd-4510-8a46-d7f1b3c6d2c1", "0e80dc2e-4508-400b-a8df-102a47e50828", "5165eb5c-0778-423a-9b05-05c5c537b8fb", "e1c25b75-84b2-4fb5-ae62-b620fbe607be", "4144d3da-ca81-4fe8-ac6e-10e440181e8e", "944731f1-a9f7-4459-8ef7-d1378cb9e60d", "3e7b3024-babe-4749-8cc9-2d6e05ca346e", "ba3ce904-82ce-4e4c-bc6b-a84418cb29c5", "18d19d30-e1a4-4d40-b9b4-0719410d89f3", "679454f8-d587-4bab-89f0-225c5598bfb9", "fbe214c9-d2aa-48c1-8f3d-3248206b9369", "47d36f93-c48b-462c-a769-bf4539a88f93", "ce8525fa-ac33-49fd-af17-fdf6dbe92f0e", "471c9683-7390-4ab0-aaf7-678e329f22de", "c52b874f-0047-44b8-98ef-d61bfdfc4ff3", "a6e35ba5-ea10-4c1f-882a-cfb244d4b376", "ff1bd74d-9b31-4306-907b-90dfd0fd3692", "aed7dd99-305b-4d68-b040-e8111e3b64f6", "7e5566e8-5d3d-438f-a30f-5bf0b57284fc", "bda24d6d-ef92-4a03-93c6-679167382187", "5d2a032e-5221-4d77-8e46-f554e81a43b4"]
                            },
                            "vehicle_positions.timestamp": {
                                "used-in-schema": true,
                                "detailAvg": "1442214185",
                                "detailMin": "1442214185",
                                "main-type": "number",
                                "matching-fields": [],
                                "detailStdDev": "4960.274931196411",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "120",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "timestamp",
                                "struc-type": null,
                                "detailMax": "1442231403",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 1.4422271285833333E9,
                                    "freq-histogram": {
                                        "data": [2, 2, 6, 0, 0, 2, 1, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 6, 5, 10, 22, 4, 7, 14, 10, 5, 6, 4],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[1442214185,1442214530)", "[1442214530,1442214875)", "[1442214875,1442215220)", "[1442215220,1442215565)", "[1442215565,1442215910)", "[1442215910,1442216255)", "[1442216255,1442216600)", "[1442216600,1442216945)", "[1442216945,1442217290)", "[1442217290,1442217635)", "[1442217635,1442217980)", "[1442217980,1442218325)", "[1442218325,1442218670)", "[1442218670,1442219015)", "[1442219015,1442219360)", "[1442219360,1442219705)", "[1442219705,1442220050)", "[1442220050,1442220395)", "[1442220395,1442220740)", "[1442220740,1442221085)", "[1442221085,1442221430)", "[1442221430,1442221775)", "[1442221775,1442222120)", "[1442222120,1442222465)", "[1442222465,1442222810)", "[1442222810,1442223155)", "[1442223155,1442223500)", "[1442223500,1442223845)", "[1442223845,1442224190)", "[1442224190,1442224535)", "[1442224535,1442224880)", "[1442224880,1442225225)", "[1442225225,1442225570)", "[1442225570,1442225915)", "[1442225915,1442226260)", "[1442226260,1442226605)", "[1442226605,1442226950)", "[1442226950,1442227295)", "[1442227295,1442227640)", "[1442227640,1442227985)", "[1442227985,1442228330)", "[1442228330,1442228675)", "[1442228675,1442229020)", "[1442229020,1442229365)", "[1442229365,1442229710)", "[1442229710,1442230055)", "[1442230055,1442230400)", "[1442230400,1442230745)", "[1442230745,1442231090)", "[1442231090,1442231435)"],
                                        "exampleValues": null,
                                        "labels": ["[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442230000)", "[1442230000,1442230000)", "[1442230000,1442230000)", "[1442230000,1442230000)", "[1442230000,1442230000)"]
                                    },
                                    "min": 1442214185,
                                    "walkingCount": 120,
                                    "walkingSum": 173067255430,
                                    "max": 1442231403,
                                    "num-distinct-values": "120",
                                    "walkingSquareSum": "249602290853559723328",
                                    "detail-type": "integer",
                                    "std-dev": 4960.274931196411
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["1442227142", "1442227152", "1442227178", "1442227357", "1442227759", "1442227853", "1442227932", "1442228143", "1442228285", "1442228383", "1442228400", "1442228442", "1442228583", "1442228650", "1442228639", "1442228734", "1442228727", "1442228819", "1442228804", "1442228881", "1442228883", "1442228844", "1442228891", "1442228940", "1442228996", "1442228929", "1442229022", "1442229344", "1442229371", "1442229476", "1442229689", "1442229755", "1442229723", "1442229734", "1442229787", "1442229793", "1442229802", "1442229916", "1442230072", "1442229976", "1442230186", "1442230283", "1442230313", "1442230597", "1442230601", "1442230659", "1442230863", "1442230880", "1442231008", "1442231194"]
                            },
                            "cluster_size": {
                                "used-in-schema": true,
                                "detailAvg": "2.0",
                                "detailMin": "2.0",
                                "main-type": "number",
                                "matching-fields": [],
                                "detailStdDev": "0.47062469474708307",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "3",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_size",
                                "struc-type": null,
                                "detailMax": "4.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 2.1818181818181817,
                                    "freq-histogram": {
                                        "data": [47, 6, 2],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["2", "3", "4"],
                                        "exampleValues": null,
                                        "labels": ["2.0", "3.0", "4.0"]
                                    },
                                    "min": 2,
                                    "walkingCount": 55,
                                    "walkingSum": 120,
                                    "max": 4,
                                    "num-distinct-values": "3",
                                    "walkingSquareSum": 274,
                                    "detail-type": "integer",
                                    "std-dev": 0.47062469474708307
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["2", "3", "4"]
                            },
                            "vehicle_positions.aircraft.altitude": {
                                "used-in-schema": true,
                                "detailAvg": "700.0",
                                "detailMin": "700.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 97,
                                    "matching-field": "vehicle_positions.aircraft.alt"
                                }],
                                "detailStdDev": "13970.532877771302",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "72",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "altitude",
                                "struc-type": null,
                                "detailMax": "40000.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 19526.666666666668,
                                    "freq-histogram": {
                                        "data": [7, 5, 8, 3, 4, 1, 5, 3, 1, 3, 5, 1, 2, 2, 1, 1, 3, 1, 1, 0, 2, 1, 1, 1, 0, 2, 0, 2, 0, 1, 0, 3, 4, 1, 1, 2, 0, 0, 0, 3, 1, 0, 10, 4, 5, 2, 2, 12, 2, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[700,1485)", "[1485,2270)", "[2270,3055)", "[3055,3840)", "[3840,4625)", "[4625,5410)", "[5410,6195)", "[6195,6980)", "[6980,7765)", "[7765,8550)", "[8550,9335)", "[9335,10120)", "[10120,10905)", "[10905,11690)", "[11690,12475)", "[12475,13260)", "[13260,14045)", "[14045,14830)", "[14830,15615)", "[15615,16400)", "[16400,17185)", "[17185,17970)", "[17970,18755)", "[18755,19540)", "[19540,20325)", "[20325,21110)", "[21110,21895)", "[21895,22680)", "[22680,23465)", "[23465,24250)", "[24250,25035)", "[25035,25820)", "[25820,26605)", "[26605,27390)", "[27390,28175)", "[28175,28960)", "[28960,29745)", "[29745,30530)", "[30530,31315)", "[31315,32100)", "[32100,32885)", "[32885,33670)", "[33670,34455)", "[34455,35240)", "[35240,36025)", "[36025,36810)", "[36810,37595)", "[37595,38380)", "[38380,39165)", "[39165,39950)", "[39950,40735)"],
                                        "exampleValues": null,
                                        "labels": ["[700.0,1485.0)", "[1485.0,2270.0)", "[2270.0,3055.0)", "[3055.0,3840.0)", "[3840.0,4625.0)", "[4625.0,5410.0)", "[5410.0,6195.0)", "[6195.0,6980.0)", "[6980.0,7765.0)", "[7765.0,8550.0)", "[8550.0,9335.0)", "[9335.0,10120.0)", "[10120.0,10905.0)", "[10905.0,11690.0)", "[11690.0,12475.0)", "[12475.0,13260.0)", "[13260.0,14045.0)", "[14045.0,14830.0)", "[14830.0,15615.0)", "[15615.0,16400.0)", "[16400.0,17185.0)", "[17185.0,17970.0)", "[17970.0,18755.0)", "[18755.0,19540.0)", "[19540.0,20325.0)", "[20325.0,21110.0)", "[21110.0,21895.0)", "[21895.0,22680.0)", "[22680.0,23465.0)", "[23465.0,24250.0)", "[24250.0,25035.0)", "[25035.0,25820.0)", "[25820.0,26605.0)", "[26605.0,27390.0)", "[27390.0,28175.0)", "[28175.0,28960.0)", "[28960.0,29745.0)", "[29745.0,30530.0)", "[30530.0,31315.0)", "[31315.0,32100.0)", "[32100.0,32885.0)", "[32885.0,33670.0)", "[33670.0,34455.0)", "[34455.0,35240.0)", "[35240.0,36025.0)", "[36025.0,36810.0)", "[36810.0,37595.0)", "[37595.0,38380.0)", "[38380.0,39165.0)", "[39165.0,39950.0)", "[39950.0,40735.0)"]
                                    },
                                    "min": 700,
                                    "walkingCount": 120,
                                    "walkingSum": 2343200,
                                    "max": 40000,
                                    "num-distinct-values": "72",
                                    "walkingSquareSum": 6.917598E10,
                                    "detail-type": "integer",
                                    "std-dev": 13970.532877771302
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["16700", "28700", "37000", "32000", "34000", "36000", "38000", "36100", "22100", "35000", "16900", "17500", "9500", "36200", "10700", "19000", "25100", "24200", "32100", "22000", "1900", "2900", "5600", "2300", "8700", "1800", "1600", "3500", "800", "700", "900", "1200", "10300", "4600", "25300", "13900", "13600", "2800", "28000", "8200", "18000", "1300", "1000", "3200", "6800", "26000", "21000", "34800", "34100", "12600"]
                            },
                            "vehicle_positions.heading": {
                                "used-in-schema": true,
                                "detailAvg": "23.0",
                                "detailMin": "23.0",
                                "main-type": "number",
                                "matching-fields": [],
                                "detailStdDev": "86.8073341595564",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "88",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "heading",
                                "struc-type": null,
                                "detailMax": "356.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 185.35833333333332,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 3, 4, 1, 0, 0, 1, 3, 3, 1, 2, 3, 5, 1, 0, 2, 4, 0, 0, 1, 4, 0, 1, 0, 2, 0, 4, 2, 1, 1, 0, 1, 0, 1, 1, 4, 3, 5, 0, 2, 5, 7, 3, 3, 1, 2, 6, 2, 6, 2, 2, 4, 2, 0, 0, 2, 1, 0, 0, 0, 1, 0, 0, 0, 0, 2],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)", "[145,150)", "[150,155)", "[155,160)", "[160,165)", "[165,170)", "[170,175)", "[175,180)", "[180,185)", "[185,190)", "[190,195)", "[195,200)", "[200,205)", "[205,210)", "[210,215)", "[215,220)", "[220,225)", "[225,230)", "[230,235)", "[235,240)", "[240,245)", "[245,250)", "[250,255)", "[255,260)", "[260,265)", "[265,270)", "[270,275)", "[275,280)", "[280,285)", "[285,290)", "[290,295)", "[295,300)", "[300,305)", "[305,310)", "[310,315)", "[315,320)", "[320,325)", "[325,330)", "[330,335)", "[335,340)", "[340,345)", "[345,350)", "[350,355)", "[355,360)"],
                                        "exampleValues": null,
                                        "labels": ["[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)", "[145.0,150.0)", "[150.0,155.0)", "[155.0,160.0)", "[160.0,165.0)", "[165.0,170.0)", "[170.0,175.0)", "[175.0,180.0)", "[180.0,185.0)", "[185.0,190.0)", "[190.0,195.0)", "[195.0,200.0)", "[200.0,205.0)", "[205.0,210.0)", "[210.0,215.0)", "[215.0,220.0)", "[220.0,225.0)", "[225.0,230.0)", "[230.0,235.0)", "[235.0,240.0)", "[240.0,245.0)", "[245.0,250.0)", "[250.0,255.0)", "[255.0,260.0)", "[260.0,265.0)", "[265.0,270.0)", "[270.0,275.0)", "[275.0,280.0)", "[280.0,285.0)", "[285.0,290.0)", "[290.0,295.0)", "[295.0,300.0)", "[300.0,305.0)", "[305.0,310.0)", "[310.0,315.0)", "[315.0,320.0)", "[320.0,325.0)", "[325.0,330.0)", "[330.0,335.0)", "[335.0,340.0)", "[340.0,345.0)", "[345.0,350.0)", "[350.0,355.0)", "[355.0,360.0)"]
                                    },
                                    "min": 23,
                                    "walkingCount": 120,
                                    "walkingSum": 22243,
                                    "max": 356,
                                    "num-distinct-values": "88",
                                    "walkingSquareSum": 5027187,
                                    "detail-type": "integer",
                                    "std-dev": 86.8073341595564
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["236", "231", "133", "313", "207", "206", "272", "276", "217", "213", "273", "92", "143", "161", "162", "33", "256", "134", "264", "261", "218", "227", "27", "35", "307", "234", "233", "267", "241", "94", "260", "274", "39", "246", "238", "205", "211", "109", "90", "254", "89", "74", "270", "164", "170", "239", "240", "355", "356", "242"]
                            },
                            "vehicle_positions": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "vehicle_positions",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.vehicle_type": {
                                "used-in-schema": true,
                                "detailAvg": "8",
                                "detailMin": "8",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "vehicle_type",
                                "struc-type": null,
                                "detailMax": "8",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 8,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [120],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["aircraft"],
                                        "exampleValues": null,
                                        "labels": ["aircraft"]
                                    },
                                    "walkingCount": 120,
                                    "walkingSum": 960,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 7680,
                                    "detail-type": "term",
                                    "max-length": 8,
                                    "char-freq-histogram": null,
                                    "min-length": 8
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["aircraft"]
                            },
                            "vehicle_positions.aircraft.flight_id": {
                                "used-in-schema": true,
                                "detailAvg": "5",
                                "detailMin": "5",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "0.625777294428475",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "99",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "flight_id",
                                "struc-type": null,
                                "detailMax": "7",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 6.341666666666667,
                                    "std-dev-length": 0.625777294428475,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 3, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 3, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL1033", "AAL1035", "AAL1110", "AAL1111", "AAL1399", "AAL1461", "AAL1488", "AAL1500", "AAL1522", "AAL1622", "AAL1772", "AAL18", "AAL1850", "AAL1946", "AAL1982", "AAL205", "AAL219", "AAL2243", "AAL2406", "AAL2476", "AAL2515", "AAL30", "AAL408", "AAL417", "AAL461", "AAL530", "AAL561", "AAL759", "AAL992", "AAL998", "AMX781", "ASH5117", "DAL137", "DAL138", "DAL1617", "DAL295", "DAL37", "DAL454", "DAL584", "DAL81", "FFT109", "FFT1107", "FFT903", "JBU100", "JBU162", "JBU27", "JBU624", "JBU670", "JBU80", "JBU913", "JBU924", "LXJ474", "N242MR", "N392MG", "N415EC", "N572EC", "N61DN", "N812TT", "N895JH", "N977AE", "NKS224", "NKS379", "NKS417", "NKS951", "RPA4309", "RPA4367", "SWA1007", "SWA1053", "SWA153", "SWA2073", "SWA267", "SWA2878", "SWA2965", "SWA2980", "SWA3148", "SWA421", "SWA6366", "SWA977", "UAL1067", "UAL1142", "UAL1201", "UAL1288", "UAL1417", "UAL1582", "UAL1612", "UAL1635", "UAL1900", "UAL1942", "UAL2237", "UAL8128", "UAL924", "UPS208", "VOI422", "VRD114", "VRD170", "VRD321", "VRD420", "VRD67", "VRD761"],
                                        "exampleValues": null,
                                        "labels": ["AAL1033", "AAL1035", "AAL1110", "AAL1111", "AAL1399", "AAL1461", "AAL1488", "AAL1500", "AAL1522", "AAL1622", "AAL1772", "AAL18", "AAL1850", "AAL1946", "AAL1982", "AAL205", "AAL219", "AAL2243", "AAL2406", "AAL2476", "AAL2515", "AAL30", "AAL408", "AAL417", "AAL461", "AAL530", "AAL561", "AAL759", "AAL992", "AAL998", "AMX781", "ASH5117", "DAL137", "DAL138", "DAL1617", "DAL295", "DAL37", "DAL454", "DAL584", "DAL81", "FFT109", "FFT1107", "FFT903", "JBU100", "JBU162", "JBU27", "JBU624", "JBU670", "JBU80", "JBU913", "JBU924", "LXJ474", "N242MR", "N392MG", "N415EC", "N572EC", "N61DN", "N812TT", "N895JH", "N977AE", "NKS224", "NKS379", "NKS417", "NKS951", "RPA4309", "RPA4367", "SWA1007", "SWA1053", "SWA153", "SWA2073", "SWA267", "SWA2878", "SWA2965", "SWA2980", "SWA3148", "SWA421", "SWA6366", "SWA977", "UAL1067", "UAL1142", "UAL1201", "UAL1288", "UAL1417", "UAL1582", "UAL1612", "UAL1635", "UAL1900", "UAL1942", "UAL2237", "UAL8128", "UAL924", "UPS208", "VOI422", "VRD114", "VRD170", "VRD321", "VRD420", "VRD67", "VRD761"]
                                    },
                                    "walkingCount": 120,
                                    "walkingSum": 761,
                                    "num-distinct-values": "99",
                                    "walkingSquareSum": 4873,
                                    "detail-type": "term",
                                    "max-length": 7,
                                    "char-freq-histogram": null,
                                    "min-length": 5
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["SWA977", "AAL461", "DAL37", "DAL81", "AAL2243", "AAL1399", "AAL1488", "AAL205", "JBU27", "NKS379", "SWA2980", "AAL30", "JBU670", "VRD420", "JBU924", "AAL1111", "AAL18", "DAL454", "AAL1110", "N242MR", "AAL1946", "RPA4309", "FFT903", "UAL1201", "LXJ474", "UAL1067", "AAL1500", "NKS224", "SWA2965", "SWA1053", "N392MG", "AAL417", "AAL1035", "ASH5117", "SWA153", "AAL1772", "AAL1850", "AAL561", "NKS951", "VRD321", "UAL1417", "SWA6366", "VRD761", "AAL1622", "UAL1612", "SWA1007", "N61DN", "UAL2237", "UAL1142", "JBU624"]
                            },
                            "vehicle_positions.trip_id": {
                                "used-in-schema": true,
                                "detailAvg": "22",
                                "detailMin": "22",
                                "main-type": "string",
                                "matching-fields": [],
                                "detailStdDev": "3.820549335137844",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "99",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "trip_id",
                                "struc-type": null,
                                "detailMax": "33",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 29.441666666666666,
                                    "std-dev-length": 3.820549335137844,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 3, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 3, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL1033-1442055600-schedule-0001", "AAL1035-1442050800-schedule-0001", "AAL1110-1442053800-schedule-0000", "AAL1111-1442054700-schedule-0000", "AAL1399-1442050800-schedule-0001", "AAL1461-1442055900-schedule-0000", "AAL1488-1442052000-schedule-0001", "AAL1500-1442048400-schedule-0001", "AAL1522-1442105972-airline-0015", "AAL1622-1442052900-schedule-0001", "AAL1772-1442055600-schedule-0001", "AAL18-1441949142-airline-0204", "AAL1850-1442055600-schedule-0003", "AAL1946-1442052000-schedule-0000", "AAL1982-1442041800-schedule-0000", "AAL205-1442052000-schedule-0000", "AAL219-1442105972-airline-0020", "AAL2243-1442050800-schedule-0002", "AAL2406-1442042100-schedule-0000", "AAL2476-1442054700-schedule-0000", "AAL2515-1442056500-schedule-0000", "AAL30-1441949142-airline-0170", "AAL408-1442055600-schedule-0004", "AAL417-1442052000-schedule-0001", "AAL461-1442052900-schedule-0001", "AAL530-1442055600-schedule-0003", "AAL561-1442055600-schedule-0002", "AAL759-1442046000-schedule-0001:2", "AAL992-1442017200-schedule-0001", "AAL998-1442015400-schedule-0000", "AMX781-1442043240-schedule-0000", "ASH5117-1442028610-airline-0024", "DAL137-1442055300-schedule-0000:0", "DAL138-1442021820-schedule-0000:0", "DAL1617-1442042100-schedule-0002", "DAL295-1442012340-schedule-0000", "DAL37-1442023800-schedule-0000:0", "DAL454-1442053740-schedule-0000", "DAL584-1441989900-schedule-0001", "DAL81-1442048700-schedule-0000:0", "FFT109-1442052300-schedule-0000", "FFT1107-1442055900-schedule-0000", "FFT903-1442053800-schedule-0000", "JBU100-1442124954-130-1", "JBU162-1442123451-149-0", "JBU27-1442052600-schedule-0001", "JBU624-1442125854-63-1", "JBU670-1442124351-39-1", "JBU80-1442127651-482-0", "JBU913-1442054100-schedule-0000", "JBU924-1442042640-schedule-0000", "LXJ474-1442179249-504-0", "N242MR-1442195794-157-0", "N392MG-1442219583-156-0", "N415EC-1442195918-68-0", "N572EC-1442162161-dlad-3920754:0", "N61DN-1442193457-104-0", "N812TT-1442219586-52-0", "N895JH-1442219568-21-0", "N977AE-1442223921-57-0", "NKS224-1442055360-schedule-0000", "NKS379-1442053800-schedule-0000", "NKS417-1442053200-schedule-0001", "NKS951-1442056260-schedule-0000", "RPA4309-1442051520-schedule-0000", "RPA4367-1442052300-schedule-0000", "SWA1007-1442055600-schedule-0004", "SWA1053-1442055600-schedule-0004", "SWA153-1442055600-schedule-0000", "SWA2073-1442052000-schedule-0002", "SWA267-1442054700-schedule-0000", "SWA2878-1442050800-schedule-0000", "SWA2965-1442055600-schedule-0004", "SWA2980-1442054100-schedule-0000", "SWA3148-1442056500-schedule-0000", "SWA421-1442055300-schedule-0000", "SWA6366-1442055900-schedule-0000", "SWA977-1442051700-schedule-0000", "UAL1067-1442052000-schedule-0003", "UAL1142-1442050303-airline-0110", "UAL1201-1442051100-schedule-0001", "UAL1288-1441982600-airline-0183", "UAL1417-1442055600-schedule-0001", "UAL1582-1442054100-schedule-0000", "UAL1612-1442053719-airline-0022", "UAL1635-1442057100-schedule-0000", "UAL1900-1442028661-airline-0063", "UAL1942-1441963757-airline-0184", "UAL2237-1442165272-51-0", "UAL8128-1442203695-81-0", "UAL924-1442023200-schedule-0000:0", "UPS208-1442216301-48-0", "VOI422-1442043900-schedule-0002", "VRD114-1442127054-106-1", "VRD170-1442126145-153-0", "VRD321-1442055600-schedule-0004", "VRD420-1442124351-38-1", "VRD67-1442056800-schedule-0000", "VRD761-1442055600-schedule-0000"],
                                        "exampleValues": null,
                                        "labels": ["AAL1033-1442055...", "AAL1035-1442050...", "AAL1110-1442053...", "AAL1111-1442054...", "AAL1399-1442050...", "AAL1461-1442055...", "AAL1488-1442052...", "AAL1500-1442048...", "AAL1522-1442105...", "AAL1622-1442052...", "AAL1772-1442055...", "AAL18-144194914...", "AAL1850-1442055...", "AAL1946-1442052...", "AAL1982-1442041...", "AAL205-14420520...", "AAL219-14421059...", "AAL2243-1442050...", "AAL2406-1442042...", "AAL2476-1442054...", "AAL2515-1442056...", "AAL30-144194914...", "AAL408-14420556...", "AAL417-14420520...", "AAL461-14420529...", "AAL530-14420556...", "AAL561-14420556...", "AAL759-14420460...", "AAL992-14420172...", "AAL998-14420154...", "AMX781-14420432...", "ASH5117-1442028...", "DAL137-14420553...", "DAL138-14420218...", "DAL1617-1442042...", "DAL295-14420123...", "DAL37-144202380...", "DAL454-14420537...", "DAL584-14419899...", "DAL81-144204870...", "FFT109-14420523...", "FFT1107-1442055...", "FFT903-14420538...", "JBU100-14421249...", "JBU162-14421234...", "JBU27-144205260...", "JBU624-14421258...", "JBU670-14421243...", "JBU80-144212765...", "JBU913-14420541...", "JBU924-14420426...", "LXJ474-14421792...", "N242MR-14421957...", "N392MG-14422195...", "N415EC-14421959...", "N572EC-14421621...", "N61DN-144219345...", "N812TT-14422195...", "N895JH-14422195...", "N977AE-14422239...", "NKS224-14420553...", "NKS379-14420538...", "NKS417-14420532...", "NKS951-14420562...", "RPA4309-1442051...", "RPA4367-1442052...", "SWA1007-1442055...", "SWA1053-1442055...", "SWA153-14420556...", "SWA2073-1442052...", "SWA267-14420547...", "SWA2878-1442050...", "SWA2965-1442055...", "SWA2980-1442054...", "SWA3148-1442056...", "SWA421-14420553...", "SWA6366-1442055...", "SWA977-14420517...", "UAL1067-1442052...", "UAL1142-1442050...", "UAL1201-1442051...", "UAL1288-1441982...", "UAL1417-1442055...", "UAL1582-1442054...", "UAL1612-1442053...", "UAL1635-1442057...", "UAL1900-1442028...", "UAL1942-1441963...", "UAL2237-1442165...", "UAL8128-1442203...", "UAL924-14420232...", "UPS208-14422163...", "VOI422-14420439...", "VRD114-14421270...", "VRD170-14421261...", "VRD321-14420556...", "VRD420-14421243...", "VRD67-144205680...", "VRD761-14420556..."]
                                    },
                                    "walkingCount": 120,
                                    "walkingSum": 3533,
                                    "num-distinct-values": "99",
                                    "walkingSquareSum": 105769,
                                    "detail-type": "term",
                                    "max-length": 33,
                                    "char-freq-histogram": null,
                                    "min-length": 22
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["SWA977-1442051700-schedule-0000", "AAL461-1442052900-schedule-0001", "DAL37-1442023800-schedule-0000:0", "DAL81-1442048700-schedule-0000:0", "AAL2243-1442050800-schedule-0002", "AAL1399-1442050800-schedule-0001", "AAL1488-1442052000-schedule-0001", "AAL205-1442052000-schedule-0000", "JBU27-1442052600-schedule-0001", "NKS379-1442053800-schedule-0000", "SWA2980-1442054100-schedule-0000", "AAL30-1441949142-airline-0170", "JBU670-1442124351-39-1", "VRD420-1442124351-38-1", "JBU924-1442042640-schedule-0000", "AAL1111-1442054700-schedule-0000", "AAL18-1441949142-airline-0204", "DAL454-1442053740-schedule-0000", "AAL1110-1442053800-schedule-0000", "N242MR-1442195794-157-0", "AAL1946-1442052000-schedule-0000", "RPA4309-1442051520-schedule-0000", "FFT903-1442053800-schedule-0000", "UAL1201-1442051100-schedule-0001", "LXJ474-1442179249-504-0", "UAL1067-1442052000-schedule-0003", "AAL1500-1442048400-schedule-0001", "NKS224-1442055360-schedule-0000", "SWA2965-1442055600-schedule-0004", "SWA1053-1442055600-schedule-0004", "N392MG-1442219583-156-0", "AAL417-1442052000-schedule-0001", "AAL1035-1442050800-schedule-0001", "ASH5117-1442028610-airline-0024", "SWA153-1442055600-schedule-0000", "AAL1772-1442055600-schedule-0001", "AAL1850-1442055600-schedule-0003", "AAL561-1442055600-schedule-0002", "NKS951-1442056260-schedule-0000", "VRD321-1442055600-schedule-0004", "UAL1417-1442055600-schedule-0001", "SWA6366-1442055900-schedule-0000", "VRD761-1442055600-schedule-0000", "AAL1622-1442052900-schedule-0001", "UAL1612-1442053719-airline-0022", "SWA1007-1442055600-schedule-0004", "N61DN-1442193457-104-0", "UAL2237-1442165272-51-0", "UAL1142-1442050303-airline-0110", "JBU624-1442125854-63-1"]
                            }
                        },
                        "dsFileType": "application/json",
                        "data-sample-id": 4,
                        "dsLastUpdate": null,
                        "dsDescription": null,
                        "dsFileName": "vehicle_position_cluster_schema_examples-1-US-Only.txt",
                        "dsVersion": null,
                        "dsFileSize": 0,
                        "dsStructuredProfile": [
                        {
                            "path": "cluster_id",
                            "field": "cluster_id",
                            "children": [],
                            "id": 1
                        }, {
                            "path": "cluster_size",
                            "field": "cluster_size",
                            "children": [],
                            "id": 2
                        }, {
                            "path": "vehicle_positions",
                            "field": "vehicle_positions",
                            "children": [{
                                "path": "vehicle_positions.latitude",
                                "field": "latitude",
                                "children": [],
                                "id": 4
                            }, {
                                "path": "vehicle_positions.longitude",
                                "field": "longitude",
                                "children": [],
                                "id": 5
                            }, {
                                "path": "vehicle_positions.tailnumber",
                                "field": "tailnumber",
                                "children": [],
                                "id": 6
                            }, {
                                "path": "vehicle_positions.speed",
                                "field": "speed",
                                "children": [],
                                "id": 7
                            }, {
                                "path": "vehicle_positions.timestamp",
                                "field": "timestamp",
                                "children": [],
                                "id": 8
                            }, {
                                "path": "vehicle_positions.heading",
                                "field": "heading",
                                "children": [],
                                "id": 9
                            }, {
                                "path": "vehicle_positions.vehicle_type",
                                "field": "vehicle_type",
                                "children": [],
                                "id": 10
                            }, {
                                "path": "vehicle_positions.trip_id",
                                "field": "trip_id",
                                "children": [],
                                "id": 11
                            }, {
                                "path": "vehicle_positions.aircraft",
                                "field": "aircraft",
                                "children": [{
                                    "path": "vehicle_positions.aircraft.air_ground",
                                    "field": "air_ground",
                                    "children": [],
                                    "id": 13
                                }, {
                                    "path": "vehicle_positions.aircraft.altitude",
                                    "field": "altitude",
                                    "children": [],
                                    "id": 14
                                }, {
                                    "path": "vehicle_positions.aircraft.flight_id",
                                    "field": "flight_id",
                                    "children": [],
                                    "id": 15
                                }],
                                "id": 12
                            }
                            ],
                            "id": 3
                        }],
                        "dsExtractedContentDir": "C:\\Users\\leegc\\AppData\\Local\\Temp\\temp3057174123149834262\\vehicle_position_cluster_schema_examples-1-US-Only-embedded"
                    },
                    {
                        "dsName": "vehicle_position_cluster_schema_examples-2-US-Only",
                        "dsId": "4c4c13b5-dfe9-47af-abd3-9678c47b0fc9",
                        "dsNumbRecords": 55,
                        "dsContainsStructuredData": true,
                        "dsProfile": {
                            "vehicle_positions.lat": {
                                "used-in-schema": true,
                                "detailAvg": "18.69781",
                                "detailMin": "18.69781",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.latitude"
                                }, {
                                    "confidence": 90,
                                    "matching-field": "vehicle_positions.longitude"
                                }],
                                "detailStdDev": "8.310167962782524",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "114",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "lat",
                                "struc-type": null,
                                "detailMax": "56.16934",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 39.15750280701754,
                                    "freq-histogram": {
                                        "data": [6, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 14, 0, 2, 2, 2, 21, 3, 3, 4, 7, 2, 10, 4, 0, 0, 0, 0, 0, 7, 2, 4, 2, 0, 4, 0, 4, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.lat",
                                            "longitude-key": "vehicle_positions.long",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 38
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[18.69780,19.44725)", "[19.44725,20.19670)", "[20.19670,20.94615)", "[20.94615,21.69560)", "[21.69560,22.44505)", "[22.44505,23.19450)", "[23.19450,23.94395)", "[23.94395,24.69340)", "[24.69340,25.44285)", "[25.44285,26.19230)", "[26.19230,26.94175)", "[26.94175,27.69120)", "[27.69120,28.44065)", "[28.44065,29.19010)", "[29.19010,29.93955)", "[29.93955,30.68900)", "[30.68900,31.43845)", "[31.43845,32.18790)", "[32.18790,32.93735)", "[32.93735,33.68680)", "[33.68680,34.43625)", "[34.43625,35.18570)", "[35.18570,35.93515)", "[35.93515,36.68460)", "[36.68460,37.43405)", "[37.43405,38.18350)", "[38.18350,38.93295)", "[38.93295,39.68240)", "[39.68240,40.43185)", "[40.43185,41.18130)", "[41.18130,41.93075)", "[41.93075,42.68020)", "[42.68020,43.42965)", "[43.42965,44.17910)", "[44.17910,44.92855)", "[44.92855,45.67800)", "[45.67800,46.42745)", "[46.42745,47.17690)", "[47.17690,47.92635)", "[47.92635,48.67580)", "[48.67580,49.42525)", "[49.42525,50.17470)", "[50.17470,50.92415)", "[50.92415,51.67360)", "[51.67360,52.42305)", "[52.42305,53.17250)", "[53.17250,53.92195)", "[53.92195,54.67140)", "[54.67140,55.42085)", "[55.42085,56.17030)"],
                                        "exampleValues": null,
                                        "labels": ["[18.6978,19.4472)", "[19.4472,20.1967)", "[20.1967,20.9461)", "[20.9461,21.6956)", "[21.6956,22.4450)", "[22.4450,23.1945)", "[23.1945,23.9439)", "[23.9439,24.6934)", "[24.6934,25.4428)", "[25.4428,26.1923)", "[26.1923,26.9417)", "[26.9417,27.6912)", "[27.6912,28.4406)", "[28.4406,29.1901)", "[29.1901,29.9395)", "[29.9395,30.689)", "[30.689,31.4384)", "[31.4384,32.1879)", "[32.1879,32.9373)", "[32.9373,33.6868)", "[33.6868,34.4362)", "[34.4362,35.1857)", "[35.1857,35.9351)", "[35.9351,36.6846)", "[36.6846,37.4340)", "[37.4340,38.1835)", "[38.1835,38.9329)", "[38.9329,39.6824)", "[39.6824,40.4318)", "[40.4318,41.1813)", "[41.1813,41.9307)", "[41.9307,42.6802)", "[42.6802,43.4296)", "[43.4296,44.1791)", "[44.1791,44.9285)", "[44.9285,45.678)", "[45.678,46.4274)", "[46.4274,47.1769)", "[47.1769,47.9263)", "[47.9263,48.6758)", "[48.6758,49.4252)", "[49.4252,50.1747)", "[50.1747,50.9241)", "[50.9241,51.6736)", "[51.6736,52.4230)", "[52.4230,53.1725)", "[53.1725,53.9219)", "[53.9219,54.6714)", "[54.6714,55.4208)", "[55.4208,56.1703)"]
                                    },
                                    "min": 18.69781,
                                    "walkingCount": 114,
                                    "walkingSum": 4463.95532,
                                    "max": 56.16934,
                                    "num-distinct-values": "114",
                                    "walkingSquareSum": 182670.0566122418,
                                    "detail-type": "decimal",
                                    "std-dev": 8.310167962782524
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["37.64265", "37.54615", "39.93562", "37.64982", "33.96079", "33.97178", "33.80026", "33.67845", "38.02629", "51.28647", "34.01358", "47.57794", "37.40511", "35.66469", "33.98785", "33.6109", "33.97888", "37.89687", "50.99756", "38.33812", "37.73273", "37.57706", "18.70097", "37.55574", "37.53552", "36.14095", "19.29831", "37.73734", "19.3877", "19.45613", "37.54517", "49.33648", "50.09272", "48.08038", "33.94574", "49.26628", "47.84732", "33.67186", "47.77913", "21.25063", "38.91022", "40.81229", "42.33664", "52.52228", "42.4617", "42.38577", "40.93185", "53.12765", "42.75595", "40.90149"]
                            },
                            "vehicle_positions.aircraft.airGround": {
                                "used-in-schema": false,
                                "detailAvg": "1",
                                "detailMin": "1",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.aircraft.air_ground"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "display-name": "airGround",
                                "struc-type": null,
                                "detailMax": "1",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 1,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [114],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["A"],
                                        "exampleValues": null,
                                        "labels": ["A"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 114,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 114,
                                    "detail-type": "term",
                                    "max-length": 1,
                                    "char-freq-histogram": null,
                                    "min-length": 1
                                },
                                "presence": 1,
/*
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
*/
                                "interpretations": {
                                    "availableOptions": [
                                        {
                                            "iId": null,
                                            "interpretation": "MSL",
                                            "iDomainId": null,
                                            "iName": "MSL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        },
                                        {
                                            "iId": null,
                                            "interpretation": "Unknown",
                                            "iDomainId": null,
                                            "iName": "Unknown",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        }
                                    ],
                                    "selectedOption":
                                        {
                                            "iId": null,
                                            "interpretation": "MSL",
                                            "iDomainId": null,
                                            "iName": "MSL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        }
                                },
                                "exampleValues": ["A"]
                            },
                            "vehicle_positions.aircraft.alt": {
                                "used-in-schema": true,
                                "detailAvg": "700.0",
                                "detailMin": "700.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 97,
                                    "matching-field": "vehicle_positions.aircraft.altitude"
                                }],
                                "detailStdDev": "13208.315820046586",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "75",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "alt",
                                "struc-type": null,
                                "detailMax": "47000.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 19821.929824561405,
                                    "freq-histogram": {
                                        "data": [5, 5, 5, 1, 3, 3, 1, 2, 4, 3, 5, 10, 3, 4, 1, 2, 0, 1, 1, 0, 2, 4, 0, 2, 2, 1, 0, 0, 1, 0, 1, 0, 3, 3, 7, 2, 2, 3, 9, 5, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[700,1625)", "[1625,2550)", "[2550,3475)", "[3475,4400)", "[4400,5325)", "[5325,6250)", "[6250,7175)", "[7175,8100)", "[8100,9025)", "[9025,9950)", "[9950,10875)", "[10875,11800)", "[11800,12725)", "[12725,13650)", "[13650,14575)", "[14575,15500)", "[15500,16425)", "[16425,17350)", "[17350,18275)", "[18275,19200)", "[19200,20125)", "[20125,21050)", "[21050,21975)", "[21975,22900)", "[22900,23825)", "[23825,24750)", "[24750,25675)", "[25675,26600)", "[26600,27525)", "[27525,28450)", "[28450,29375)", "[29375,30300)", "[30300,31225)", "[31225,32150)", "[32150,33075)", "[33075,34000)", "[34000,34925)", "[34925,35850)", "[35850,36775)", "[36775,37700)", "[37700,38625)", "[38625,39550)", "[39550,40475)", "[40475,41400)", "[41400,42325)", "[42325,43250)", "[43250,44175)", "[44175,45100)", "[45100,46025)", "[46025,46950)", "[46950,47875)"],
                                        "exampleValues": null,
                                        "labels": ["[700.0,1625.0)", "[1625.0,2550.0)", "[2550.0,3475.0)", "[3475.0,4400.0)", "[4400.0,5325.0)", "[5325.0,6250.0)", "[6250.0,7175.0)", "[7175.0,8100.0)", "[8100.0,9025.0)", "[9025.0,9950.0)", "[9950.0,10875.0)", "[10875.0,11800.0)", "[11800.0,12725.0)", "[12725.0,13650.0)", "[13650.0,14575.0)", "[14575.0,15500.0)", "[15500.0,16425.0)", "[16425.0,17350.0)", "[17350.0,18275.0)", "[18275.0,19200.0)", "[19200.0,20125.0)", "[20125.0,21050.0)", "[21050.0,21975.0)", "[21975.0,22900.0)", "[22900.0,23825.0)", "[23825.0,24750.0)", "[24750.0,25675.0)", "[25675.0,26600.0)", "[26600.0,27525.0)", "[27525.0,28450.0)", "[28450.0,29375.0)", "[29375.0,30300.0)", "[30300.0,31225.0)", "[31225.0,32150.0)", "[32150.0,33075.0)", "[33075.0,34000.0)", "[34000.0,34925.0)", "[34925.0,35850.0)", "[35850.0,36775.0)", "[36775.0,37700.0)", "[37700.0,38625.0)", "[38625.0,39550.0)", "[39550.0,40475.0)", "[40475.0,41400.0)", "[41400.0,42325.0)", "[42325.0,43250.0)", "[43250.0,44175.0)", "[44175.0,45100.0)", "[45100.0,46025.0)", "[46025.0,46950.0)", "[46950.0,47875.0)"]
                                    },
                                    "min": 700,
                                    "walkingCount": 114,
                                    "walkingSum": 2259700,
                                    "max": 47000,
                                    "num-distinct-values": "75",
                                    "walkingSquareSum": 6.468001E10,
                                    "detail-type": "integer",
                                    "std-dev": 13208.315820046586
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["33000", "47000", "10100", "11000", "12900", "34000", "1600", "700", "20300", "20400", "20800", "9000", "36000", "8500", "18200", "17200", "35000", "7300", "37000", "3000", "22000", "2600", "8100", "15000", "1800", "2300", "5600", "5500", "11100", "13700", "4400", "4900", "32000", "39000", "30700", "29200", "12600", "13100", "38000", "10500", "10900", "22100", "10000", "33900", "3100", "23800", "14900", "2000", "1200", "11700"]
                            },
                            "vehicle_positions.aircraft": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "aircraft",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.tailnumber": {
                                "used-in-schema": false,
                                "detailAvg": "6",
                                "detailMin": "6",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }, {
                                    "confidence": 91,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 89,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "tailnumber",
                                "struc-type": null,
                                "detailMax": "6",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 6,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 4, 2, 3, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 2, 1, 1, 1, 1, 3, 1, 4, 3, 1, 1, 1, 1, 5, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 4, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 4, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["N112AN", "N153DL", "N1604R", "N174DZ", "N180UA", "N185DN", "N196UW", "N219CY", "N220FR", "N291AY", "N292AY", "N354AA", "N37470", "N37474", "N38451", "N38950", "N392HA", "N408YX", "N453AS", "N457AS", "N492TA", "N509AY", "N524VA", "N535BC", "N548UW", "N57862", "N580UW", "N623VA", "N626JS", "N629NK", "N634JB", "N641NK", "N641UA", "N647UA", "N66837", "N673US", "N675NW", "N688AA", "N712TW", "N718TW", "N73275", "N75429", "N76054", "N76062", "N762AN", "N7727A", "N7729A", "N77430", "N774UA", "N775JB", "N777UA", "N778UA", "N78005", "N783UA", "N803AL", "N805DN", "N810DN", "N815NW", "N830DN", "N837VA", "N838NN", "N841VA", "N844MH", "N848NN", "N851VA", "N89304", "N928NN", "N931FR", "N935UW", "N943JT", "N947FR", "N952JB", "N959NN", "N987AN"],
                                        "exampleValues": null,
                                        "labels": ["N112AN", "N153DL", "N1604R", "N174DZ", "N180UA", "N185DN", "N196UW", "N219CY", "N220FR", "N291AY", "N292AY", "N354AA", "N37470", "N37474", "N38451", "N38950", "N392HA", "N408YX", "N453AS", "N457AS", "N492TA", "N509AY", "N524VA", "N535BC", "N548UW", "N57862", "N580UW", "N623VA", "N626JS", "N629NK", "N634JB", "N641NK", "N641UA", "N647UA", "N66837", "N673US", "N675NW", "N688AA", "N712TW", "N718TW", "N73275", "N75429", "N76054", "N76062", "N762AN", "N7727A", "N7729A", "N77430", "N774UA", "N775JB", "N777UA", "N778UA", "N78005", "N783UA", "N803AL", "N805DN", "N810DN", "N815NW", "N830DN", "N837VA", "N838NN", "N841VA", "N844MH", "N848NN", "N851VA", "N89304", "N928NN", "N931FR", "N935UW", "N943JT", "N947FR", "N952JB", "N959NN", "N987AN"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 684,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 4104,
                                    "detail-type": "term",
                                    "max-length": 6,
                                    "char-freq-histogram": null,
                                    "min-length": 6
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["N548UW", "N626JS", "N524VA", "N805DN", "N220FR", "N112AN", "N77430", "N37470", "N7727A", "N688AA", "N634JB", "N38950", "N492TA", "N580UW", "N38451", "N673US", "N844MH", "N851VA", "N457AS", "N196UW", "N535BC", "N810DN", "N453AS", "N623VA", "N180UA", "N153DL", "N841VA", "N775JB", "N219CY", "N509AY", "N712TW", "N777UA", "N774UA", "N641UA", "N185DN", "N762AN", "N952JB", "N830DN", "N73275", "N778UA", "N815NW", "N1604R", "N78005", "N76054", "N647UA", "N931FR", "N943JT", "N392HA", "N354AA", "N75429"]
                            },
                            "vehicle_positions.long": {
                                "used-in-schema": true,
                                "detailAvg": "-157.8471",
                                "detailMin": "-157.8471",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.longitude"
                                }, {
                                    "confidence": 85,
                                    "matching-field": "vehicle_positions.latitude"
                                }],
                                "detailStdDev": "53.9195348998481",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "114",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "long",
                                "struc-type": null,
                                "detailMax": "140.46926",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": -77.7208548245614,
                                    "freq-histogram": {
                                        "data": [2, 0, 0, 0, 0, 0, 0, 24, 21, 2, 0, 4, 0, 0, 6, 4, 9, 10, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 4, 4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.lat",
                                            "longitude-key": "vehicle_positions.long",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 38
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[-160,-155)", "[-155,-150)", "[-150,-145)", "[-145,-140)", "[-140,-135)", "[-135,-130)", "[-130,-125)", "[-125,-120)", "[-120,-115)", "[-115,-110)", "[-110,-105)", "[-105,-100)", "[-100,-95)", "[-95,-90)", "[-90,-85)", "[-85,-80)", "[-80,-75)", "[-75,-70)", "[-70,-65)", "[-65,-60)", "[-60,-55)", "[-55,-50)", "[-50,-45)", "[-45,-40)", "[-40,-35)", "[-35,-30)", "[-30,-25)", "[-25,-20)", "[-20,-15)", "[-15,-10)", "[-10,-5)", "[-5,0)", "[0,5)", "[5,10)", "[10,15)", "[15,20)", "[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)"],
                                        "exampleValues": null,
                                        "labels": ["[-160.0,-155.0)", "[-155.0,-150.0)", "[-150.0,-145.0)", "[-145.0,-140.0)", "[-140.0,-135.0)", "[-135.0,-130.0)", "[-130.0,-125.0)", "[-125.0,-120.0)", "[-120.0,-115.0)", "[-115.0,-110.0)", "[-110.0,-105.0)", "[-105.0,-100.0)", "[-100.0,-95.0)", "[-95.0,-90.0)", "[-90.0,-85.0)", "[-85.0,-80.0)", "[-80.0,-75.0)", "[-75.0,-70.0)", "[-70.0,-65.0)", "[-65.0,-60.0)", "[-60.0,-55.0)", "[-55.0,-50.0)", "[-50.0,-45.0)", "[-45.0,-40.0)", "[-40.0,-35.0)", "[-35.0,-30.0)", "[-30.0,-25.0)", "[-25.0,-20.0)", "[-20.0,-15.0)", "[-15.0,-10.0)", "[-10.0,-5.0)", "[-5.0,0.0)", "[0.0,5.0)", "[5.0,10.0)", "[10.0,15.0)", "[15.0,20.0)", "[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)"]
                                    },
                                    "min": -157.8471,
                                    "walkingCount": 114,
                                    "walkingSum": -8860.17745,
                                    "max": 140.46926,
                                    "num-distinct-values": "114",
                                    "walkingSquareSum": 1020054.6171063195,
                                    "detail-type": "decimal",
                                    "std-dev": 53.9195348998481
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["-103.4528", "-121.6429", "-104.39368", "-122.3454", "-116.8985", "-116.89487", "-118.72174", "-118.4465", "-121.79385", "-9.2038", "-118.46026", "-122.34547", "-122.2316", "140.44688", "-118.02478", "-118.38015", "-118.1201", "-118.8012", "-2.74918", "-120.0866", "-120.1534", "-121.3995", "-66.62789", "-121.5846", "-121.70783", "-114.7385", "-67.03642", "-120.1194", "-67.0987", "-67.14649", "-122.1437", "0.73977", "7.53232", "6.04571", "-118.44674", "2.29441", "7.44739", "-118.4166", "7.82391", "-157.8471", "-77.58484", "-73.90588", "-71.01157", "-6.17589", "-85.00085", "-70.73413", "-75.2908", "-1.69448", "-71.22255", "-75.18294"]
                            },
                            "vehicle_positions.speed": {
                                "used-in-schema": false,
                                "detailAvg": "73.0",
                                "detailMin": "73.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.speed"
                                }, {
                                    "confidence": 87,
                                    "matching-field": "vehicle_positions.heading"
                                }],
                                "detailStdDev": "134.21511057415535",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "94",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "speed",
                                "struc-type": null,
                                "detailMax": "919.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 374.3333333333333,
                                    "freq-histogram": {
                                        "data": [1, 0, 0, 0, 2, 0, 2, 4, 6, 2, 1, 4, 3, 2, 1, 4, 5, 0, 8, 2, 5, 5, 7, 4, 3, 2, 13, 7, 6, 6, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[60,75)", "[75,90)", "[90,105)", "[105,120)", "[120,135)", "[135,150)", "[150,165)", "[165,180)", "[180,195)", "[195,210)", "[210,225)", "[225,240)", "[240,255)", "[255,270)", "[270,285)", "[285,300)", "[300,315)", "[315,330)", "[330,345)", "[345,360)", "[360,375)", "[375,390)", "[390,405)", "[405,420)", "[420,435)", "[435,450)", "[450,465)", "[465,480)", "[480,495)", "[495,510)", "[510,525)", "[525,540)", "[540,555)", "[555,570)", "[570,585)", "[585,600)", "[600,615)", "[615,630)", "[630,645)", "[645,660)", "[660,675)", "[675,690)", "[690,705)", "[705,720)", "[720,735)", "[735,750)", "[750,765)", "[765,780)", "[780,795)", "[795,810)", "[810,825)", "[825,840)", "[840,855)", "[855,870)", "[870,885)", "[885,900)", "[900,915)", "[915,930)"],
                                        "exampleValues": null,
                                        "labels": ["[60.0,75.0)", "[75.0,90.0)", "[90.0,105.0)", "[105.0,120.0)", "[120.0,135.0)", "[135.0,150.0)", "[150.0,165.0)", "[165.0,180.0)", "[180.0,195.0)", "[195.0,210.0)", "[210.0,225.0)", "[225.0,240.0)", "[240.0,255.0)", "[255.0,270.0)", "[270.0,285.0)", "[285.0,300.0)", "[300.0,315.0)", "[315.0,330.0)", "[330.0,345.0)", "[345.0,360.0)", "[360.0,375.0)", "[375.0,390.0)", "[390.0,405.0)", "[405.0,420.0)", "[420.0,435.0)", "[435.0,450.0)", "[450.0,465.0)", "[465.0,480.0)", "[480.0,495.0)", "[495.0,510.0)", "[510.0,525.0)", "[525.0,540.0)", "[540.0,555.0)", "[555.0,570.0)", "[570.0,585.0)", "[585.0,600.0)", "[600.0,615.0)", "[615.0,630.0)", "[630.0,645.0)", "[645.0,660.0)", "[660.0,675.0)", "[675.0,690.0)", "[690.0,705.0)", "[705.0,720.0)", "[720.0,735.0)", "[735.0,750.0)", "[750.0,765.0)", "[765.0,780.0)", "[780.0,795.0)", "[795.0,810.0)", "[810.0,825.0)", "[825.0,840.0)", "[840.0,855.0)", "[855.0,870.0)", "[870.0,885.0)", "[885.0,900.0)", "[900.0,915.0)", "[915.0,930.0)"]
                                    },
                                    "min": 73,
                                    "walkingCount": 114,
                                    "walkingSum": 42674,
                                    "max": 919,
                                    "num-distinct-values": "94",
                                    "walkingSquareSum": 1.8027862E7,
                                    "detail-type": "integer",
                                    "std-dev": 134.21511057415535
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["450", "511", "301", "303", "376", "422", "73", "182", "395", "372", "373", "290", "465", "291", "335", "420", "415", "522", "493", "286", "459", "190", "416", "170", "236", "568", "154", "158", "282", "260", "337", "252", "253", "222", "393", "389", "499", "497", "489", "494", "381", "404", "339", "472", "195", "238", "458", "308", "502", "453"]
                            },
                            "cluster_id": {
                                "used-in-schema": false,
                                "detailAvg": "36",
                                "detailMin": "36",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 92,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 91,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "55",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_id",
                                "struc-type": null,
                                "detailMax": "36",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 36,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["04e4aca6-d589-4e33-b717-841594076be7", "075710e3-950c-431e-8496-f63690e499d9", "0bc34b70-94c4-4774-a51f-88be10d6b226", "0c4eb716-4ebb-4c00-86c4-28572b49fafd", "1630bcac-4e03-4e51-9c5e-aa32f9010079", "17512205-80c5-4826-8244-d851a57c20fb", "192e90e4-993c-46bd-883a-cf1e6269326c", "1a13e1ee-e2e9-499c-891f-66eec2273cdc", "1adef694-1be7-4244-a719-84bce48535e6", "34dca0de-b93c-4dd2-88fe-2145a59e08fd", "38333023-07d1-48a0-b9f2-cfaf0553348e", "39eb60a1-7558-4e8d-b2d2-f36628917ea2", "3a2b19f2-1375-47dc-a80e-fef04d7b95c6", "47924c5e-006e-472a-815d-a47415625dbc", "48df1e44-27dc-4f81-8ad0-3c4da50c3112", "4ee04c22-c2b8-4e0d-8ab8-01ffb8919677", "5909039d-f4f7-46db-b9cc-95d06622615e", "5bd73dd2-fc3e-483d-bcec-168c0d8471bd", "5cbe4bcf-29a4-40a9-88c0-3374f23200f6", "6618dfb2-7523-45a1-a47d-3bea537e44f0", "670c6b73-0e48-41ee-b2bb-b1603a5fd69e", "6b7454e5-55a3-48d2-856f-79d0315ca0ab", "6c560bec-90b6-4f45-80a1-7dddbe554598", "6de54bc6-2e16-4843-87b2-47f8a40cfe40", "6f27e02f-0e72-4a90-9512-9749d9c9a871", "7e7d3f40-97f0-497d-9787-b0f3ac46687d", "7eaa24cd-f5a1-4fd1-ad4c-1d39f38246eb", "806f0cc1-e724-41b9-8683-79312e0d0022", "8151d7b7-ea73-46b5-a50d-0c0ca7e88bc8", "948a8a6d-2006-43c0-b279-f3f6abfd78e4", "973f8a24-5fc8-4eb1-ba48-7f155b217647", "98effbec-ea0a-4b4d-9afd-db2ed791ef24", "9b5287b6-ad02-4462-a455-b11b07a37223", "a4923823-9a12-4366-991a-f895c9eeb0f5", "ab6c44eb-4bdf-4d86-af76-e90a5b413152", "ad27928e-a40a-468c-8e4b-db46497bff1a", "b770b2c9-06f3-4d80-afaa-fbad042391c0", "c0755d8a-d5f2-45a9-b67a-f183b298a29c", "c0f9ca18-c790-4bc7-801b-4078539fc24b", "c10355e6-80b2-4fd9-a3f2-129ee93db636", "c32b0b6a-e1bf-4ade-add1-85dd2714e10e", "c7901591-698c-460f-8076-51681ca6f931", "d22bc4b2-4b5c-4ef5-b84d-11940792e316", "db2e100f-d101-48e2-9663-7043cdb0df05", "dc7b1838-0aaa-4459-9e38-23117be3b6ba", "dee33e0f-fcf3-4db0-9e7d-9e48c756d116", "e1b5c65d-f5f4-4b45-b297-eb838f7584d3", "e4f45918-0291-4bf3-a5c5-8580d9d04496", "ef6f9a52-6e22-4c76-a4a9-ab121b70ea65", "f10f509a-4611-4429-bc79-8c4c866d3011", "f59e56fa-d52d-4e85-ad61-102438a20aeb", "f8cfe3ee-209e-4d4c-a47a-3c304e3706f5", "fa15fca5-bb6d-4c1f-a97f-675df1fe312a", "fc89322c-2eae-452e-a33b-52d82a8c172d", "fe6512fb-9f05-4bdc-b8fa-7841bbded81b"],
                                        "exampleValues": null,
                                        "labels": ["04e4aca6-d589-4...", "075710e3-950c-4...", "0bc34b70-94c4-4...", "0c4eb716-4ebb-4...", "1630bcac-4e03-4...", "17512205-80c5-4...", "192e90e4-993c-4...", "1a13e1ee-e2e9-4...", "1adef694-1be7-4...", "34dca0de-b93c-4...", "38333023-07d1-4...", "39eb60a1-7558-4...", "3a2b19f2-1375-4...", "47924c5e-006e-4...", "48df1e44-27dc-4...", "4ee04c22-c2b8-4...", "5909039d-f4f7-4...", "5bd73dd2-fc3e-4...", "5cbe4bcf-29a4-4...", "6618dfb2-7523-4...", "670c6b73-0e48-4...", "6b7454e5-55a3-4...", "6c560bec-90b6-4...", "6de54bc6-2e16-4...", "6f27e02f-0e72-4...", "7e7d3f40-97f0-4...", "7eaa24cd-f5a1-4...", "806f0cc1-e724-4...", "8151d7b7-ea73-4...", "948a8a6d-2006-4...", "973f8a24-5fc8-4...", "98effbec-ea0a-4...", "9b5287b6-ad02-4...", "a4923823-9a12-4...", "ab6c44eb-4bdf-4...", "ad27928e-a40a-4...", "b770b2c9-06f3-4...", "c0755d8a-d5f2-4...", "c0f9ca18-c790-4...", "c10355e6-80b2-4...", "c32b0b6a-e1bf-4...", "c7901591-698c-4...", "d22bc4b2-4b5c-4...", "db2e100f-d101-4...", "dc7b1838-0aaa-4...", "dee33e0f-fcf3-4...", "e1b5c65d-f5f4-4...", "e4f45918-0291-4...", "ef6f9a52-6e22-4...", "f10f509a-4611-4...", "f59e56fa-d52d-4...", "f8cfe3ee-209e-4...", "fa15fca5-bb6d-4...", "fc89322c-2eae-4...", "fe6512fb-9f05-4..."]
                                    },
                                    "walkingCount": 55,
                                    "walkingSum": 1980,
                                    "num-distinct-values": "55",
                                    "walkingSquareSum": 71280,
                                    "detail-type": "term",
                                    "max-length": 36,
                                    "char-freq-histogram": null,
                                    "min-length": 36
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["0c4eb716-4ebb-4c00-86c4-28572b49fafd", "dee33e0f-fcf3-4db0-9e7d-9e48c756d116", "fc89322c-2eae-452e-a33b-52d82a8c172d", "c32b0b6a-e1bf-4ade-add1-85dd2714e10e", "c0755d8a-d5f2-45a9-b67a-f183b298a29c", "806f0cc1-e724-41b9-8683-79312e0d0022", "1adef694-1be7-4244-a719-84bce48535e6", "5cbe4bcf-29a4-40a9-88c0-3374f23200f6", "8151d7b7-ea73-46b5-a50d-0c0ca7e88bc8", "dc7b1838-0aaa-4459-9e38-23117be3b6ba", "47924c5e-006e-472a-815d-a47415625dbc", "973f8a24-5fc8-4eb1-ba48-7f155b217647", "38333023-07d1-48a0-b9f2-cfaf0553348e", "ef6f9a52-6e22-4c76-a4a9-ab121b70ea65", "075710e3-950c-431e-8496-f63690e499d9", "5bd73dd2-fc3e-483d-bcec-168c0d8471bd", "f59e56fa-d52d-4e85-ad61-102438a20aeb", "5909039d-f4f7-46db-b9cc-95d06622615e", "a4923823-9a12-4366-991a-f895c9eeb0f5", "fe6512fb-9f05-4bdc-b8fa-7841bbded81b", "ab6c44eb-4bdf-4d86-af76-e90a5b413152", "6de54bc6-2e16-4843-87b2-47f8a40cfe40", "3a2b19f2-1375-47dc-a80e-fef04d7b95c6", "db2e100f-d101-48e2-9663-7043cdb0df05", "17512205-80c5-4826-8244-d851a57c20fb", "9b5287b6-ad02-4462-a455-b11b07a37223", "948a8a6d-2006-43c0-b279-f3f6abfd78e4", "c7901591-698c-460f-8076-51681ca6f931", "98effbec-ea0a-4b4d-9afd-db2ed791ef24", "f8cfe3ee-209e-4d4c-a47a-3c304e3706f5", "b770b2c9-06f3-4d80-afaa-fbad042391c0", "34dca0de-b93c-4dd2-88fe-2145a59e08fd", "192e90e4-993c-46bd-883a-cf1e6269326c", "7eaa24cd-f5a1-4fd1-ad4c-1d39f38246eb", "0bc34b70-94c4-4774-a51f-88be10d6b226", "6b7454e5-55a3-48d2-856f-79d0315ca0ab", "1630bcac-4e03-4e51-9c5e-aa32f9010079", "e1b5c65d-f5f4-4b45-b297-eb838f7584d3", "4ee04c22-c2b8-4e0d-8ab8-01ffb8919677", "7e7d3f40-97f0-497d-9787-b0f3ac46687d", "6c560bec-90b6-4f45-80a1-7dddbe554598", "fa15fca5-bb6d-4c1f-a97f-675df1fe312a", "1a13e1ee-e2e9-499c-891f-66eec2273cdc", "c10355e6-80b2-4fd9-a3f2-129ee93db636", "6618dfb2-7523-45a1-a47d-3bea537e44f0", "e4f45918-0291-4bf3-a5c5-8580d9d04496", "48df1e44-27dc-4f81-8ad0-3c4da50c3112", "c0f9ca18-c790-4bc7-801b-4078539fc24b", "04e4aca6-d589-4e33-b717-841594076be7", "ad27928e-a40a-468c-8e4b-db46497bff1a"]
                            },
                            "vehicle_positions.timestamp": {
                                "used-in-schema": false,
                                "detailAvg": "1442209381",
                                "detailMin": "1442209381",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "vehicle_positions.timestamp"
                                }],
                                "detailStdDev": "6909.86557231988",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "112",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "timestamp",
                                "struc-type": null,
                                "detailMax": "1442226874",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 1.4422159805701754E9,
                                    "freq-histogram": {
                                        "data": [7, 4, 10, 13, 6, 2, 0, 9, 9, 6, 4, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 3, 12, 10],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[1442209380,1442209730)", "[1442209730,1442210080)", "[1442210080,1442210430)", "[1442210430,1442210780)", "[1442210780,1442211130)", "[1442211130,1442211480)", "[1442211480,1442211830)", "[1442211830,1442212180)", "[1442212180,1442212530)", "[1442212530,1442212880)", "[1442212880,1442213230)", "[1442213230,1442213580)", "[1442213580,1442213930)", "[1442213930,1442214280)", "[1442214280,1442214630)", "[1442214630,1442214980)", "[1442214980,1442215330)", "[1442215330,1442215680)", "[1442215680,1442216030)", "[1442216030,1442216380)", "[1442216380,1442216730)", "[1442216730,1442217080)", "[1442217080,1442217430)", "[1442217430,1442217780)", "[1442217780,1442218130)", "[1442218130,1442218480)", "[1442218480,1442218830)", "[1442218830,1442219180)", "[1442219180,1442219530)", "[1442219530,1442219880)", "[1442219880,1442220230)", "[1442220230,1442220580)", "[1442220580,1442220930)", "[1442220930,1442221280)", "[1442221280,1442221630)", "[1442221630,1442221980)", "[1442221980,1442222330)", "[1442222330,1442222680)", "[1442222680,1442223030)", "[1442223030,1442223380)", "[1442223380,1442223730)", "[1442223730,1442224080)", "[1442224080,1442224430)", "[1442224430,1442224780)", "[1442224780,1442225130)", "[1442225130,1442225480)", "[1442225480,1442225830)", "[1442225830,1442226180)", "[1442226180,1442226530)", "[1442226530,1442226880)"],
                                        "exampleValues": null,
                                        "labels": ["[1442200000,1442200000)", "[1442200000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)"]
                                    },
                                    "min": 1442209381,
                                    "walkingCount": 114,
                                    "walkingSum": 164412621785,
                                    "max": 1442226874,
                                    "num-distinct-values": "112",
                                    "walkingSquareSum": "237118510551210234651",
                                    "detail-type": "integer",
                                    "std-dev": 6909.86557231988
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["1442209381", "1442209422", "1442209488", "1442209683", "1442209795", "1442209995", "1442210186", "1442210281", "1442210280", "1442210277", "1442210411", "1442210564", "1442210481", "1442210488", "1442210655", "1442210716", "1442210722", "1442210861", "1442210873", "1442210919", "1442211371", "1442211957", "1442212012", "1442212054", "1442212154", "1442212351", "1442212384", "1442212400", "1442212442", "1442212452", "1442212603", "1442212745", "1442212776", "1442213043", "1442213219", "1442213233", "1442213321", "1442213349", "1442213396", "1442225635", "1442225648", "1442225660", "1442225743", "1442225703", "1442225942", "1442226128", "1442226267", "1442226295", "1442226306", "1442226319"]
                            },
                            "cluster_size": {
                                "used-in-schema": false,
                                "detailAvg": "2.0",
                                "detailMin": "2.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "cluster_size"
                                }],
                                "detailStdDev": "0.25968830649246727",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "2",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_size",
                                "struc-type": null,
                                "detailMax": "3.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 2.0727272727272728,
                                    "freq-histogram": {
                                        "data": [51, 4],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["2", "3"],
                                        "exampleValues": null,
                                        "labels": ["2.0", "3.0"]
                                    },
                                    "min": 2,
                                    "walkingCount": 55,
                                    "walkingSum": 114,
                                    "max": 3,
                                    "num-distinct-values": "2",
                                    "walkingSquareSum": 240,
                                    "detail-type": "integer",
                                    "std-dev": 0.25968830649246727
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["2", "3"]
                            },
                            "vehicle_positions.heading": {
                                "used-in-schema": false,
                                "detailAvg": "8.0",
                                "detailMin": "8.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.heading"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.speed"
                                }],
                                "detailStdDev": "97.87343036701385",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "71",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "heading",
                                "struc-type": null,
                                "detailMax": "332.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 181.140350877193,
                                    "freq-histogram": {
                                        "data": [1, 0, 1, 1, 1, 0, 0, 1, 2, 0, 1, 1, 0, 3, 0, 4, 2, 11, 2, 4, 5, 7, 0, 3, 3, 4, 1, 0, 1, 0, 0, 1, 1, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 2, 16, 3, 1, 2, 0, 1, 3, 0, 2, 2, 1, 0, 1, 10, 2, 0, 0],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[5,10)", "[10,15)", "[15,20)", "[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)", "[145,150)", "[150,155)", "[155,160)", "[160,165)", "[165,170)", "[170,175)", "[175,180)", "[180,185)", "[185,190)", "[190,195)", "[195,200)", "[200,205)", "[205,210)", "[210,215)", "[215,220)", "[220,225)", "[225,230)", "[230,235)", "[235,240)", "[240,245)", "[245,250)", "[250,255)", "[255,260)", "[260,265)", "[265,270)", "[270,275)", "[275,280)", "[280,285)", "[285,290)", "[290,295)", "[295,300)", "[300,305)", "[305,310)", "[310,315)", "[315,320)", "[320,325)", "[325,330)", "[330,335)", "[335,340)", "[340,345)"],
                                        "exampleValues": null,
                                        "labels": ["[5.0,10.0)", "[10.0,15.0)", "[15.0,20.0)", "[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)", "[145.0,150.0)", "[150.0,155.0)", "[155.0,160.0)", "[160.0,165.0)", "[165.0,170.0)", "[170.0,175.0)", "[175.0,180.0)", "[180.0,185.0)", "[185.0,190.0)", "[190.0,195.0)", "[195.0,200.0)", "[200.0,205.0)", "[205.0,210.0)", "[210.0,215.0)", "[215.0,220.0)", "[220.0,225.0)", "[225.0,230.0)", "[230.0,235.0)", "[235.0,240.0)", "[240.0,245.0)", "[245.0,250.0)", "[250.0,255.0)", "[255.0,260.0)", "[260.0,265.0)", "[265.0,270.0)", "[270.0,275.0)", "[275.0,280.0)", "[280.0,285.0)", "[285.0,290.0)", "[290.0,295.0)", "[295.0,300.0)", "[300.0,305.0)", "[305.0,310.0)", "[310.0,315.0)", "[315.0,320.0)", "[320.0,325.0)", "[325.0,330.0)", "[330.0,335.0)", "[335.0,340.0)", "[340.0,345.0)"]
                                    },
                                    "min": 8,
                                    "walkingCount": 114,
                                    "walkingSum": 20650,
                                    "max": 332,
                                    "num-distinct-values": "71",
                                    "walkingSquareSum": 4832578,
                                    "detail-type": "integer",
                                    "std-dev": 97.87343036701385
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["47", "137", "254", "257", "127", "258", "44", "28", "268", "265", "264", "227", "310", "123", "91", "72", "73", "88", "96", "164", "148", "8", "180", "106", "104", "328", "263", "111", "130", "262", "92", "84", "80", "261", "327", "61", "326", "292", "293", "93", "94", "110", "103", "102", "86", "105", "244", "124", "97", "247"]
                            },
                            "vehicle_positions": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "vehicle_positions",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.vehicle_type": {
                                "used-in-schema": false,
                                "detailAvg": "8",
                                "detailMin": "8",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "vehicle_positions.vehicle_type"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "vehicle_type",
                                "struc-type": null,
                                "detailMax": "8",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 8,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [114],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["aircraft"],
                                        "exampleValues": null,
                                        "labels": ["aircraft"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 912,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 7296,
                                    "detail-type": "term",
                                    "max-length": 8,
                                    "char-freq-histogram": null,
                                    "min-length": 8
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["aircraft"]
                            },
                            "vehicle_positions.aircraft.flight_id": {
                                "used-in-schema": false,
                                "detailAvg": "4",
                                "detailMin": "4",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 92,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "0.6606375573250081",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "flight_id",
                                "struc-type": null,
                                "detailMax": "7",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 6.192982456140351,
                                    "std-dev-length": 0.6606375573250081,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 4, 1, 1, 4, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 5, 4, 1, 1, 1, 1, 2, 1, 1, 1, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 2, 3, 4, 2, 1, 3, 1, 1, 4, 1, 1, 1, 3, 1, 1, 2, 1, 1, 2, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL102", "AAL1107", "AAL1283", "AAL1488", "AAL154D", "AAL185", "AAL205", "AAL219", "AAL2455", "AAL486", "AAL490", "AAL603", "AAL650", "AAL705", "AAL751", "AAL787", "AAL9768", "ABX2201", "ASA384", "ASA552", "ASH3802", "DAL1172", "DAL1896", "DAL2521", "DAL28", "DAL37", "DAL400", "DAL410", "DAL431", "DAL52", "DAL585", "DAL65", "DAL72", "DAL99", "FFT1478", "FFT612", "FFT627", "HAL5", "JBU101", "JBU1248", "JBU19", "JBU624", "N535BC", "N626JS", "NKS409", "NKS696", "RPA4271", "SWA262", "SWA797", "TAI560", "UAL1003", "UAL104", "UAL1122", "UAL1203", "UAL1536", "UAL1608", "UAL1944", "UAL1947", "UAL1975", "UAL2237", "UAL50", "UAL860", "UAL881", "UAL907", "UAL934", "UAL952", "UAL972", "UAL98", "UAL987", "VRD1905", "VRD34", "VRD415", "VRD720", "VRD948"],
                                        "exampleValues": null,
                                        "labels": ["AAL102", "AAL1107", "AAL1283", "AAL1488", "AAL154D", "AAL185", "AAL205", "AAL219", "AAL2455", "AAL486", "AAL490", "AAL603", "AAL650", "AAL705", "AAL751", "AAL787", "AAL9768", "ABX2201", "ASA384", "ASA552", "ASH3802", "DAL1172", "DAL1896", "DAL2521", "DAL28", "DAL37", "DAL400", "DAL410", "DAL431", "DAL52", "DAL585", "DAL65", "DAL72", "DAL99", "FFT1478", "FFT612", "FFT627", "HAL5", "JBU101", "JBU1248", "JBU19", "JBU624", "N535BC", "N626JS", "NKS409", "NKS696", "RPA4271", "SWA262", "SWA797", "TAI560", "UAL1003", "UAL104", "UAL1122", "UAL1203", "UAL1536", "UAL1608", "UAL1944", "UAL1947", "UAL1975", "UAL2237", "UAL50", "UAL860", "UAL881", "UAL907", "UAL934", "UAL952", "UAL972", "UAL98", "UAL987", "VRD1905", "VRD34", "VRD415", "VRD720", "VRD948"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 706,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 4422,
                                    "detail-type": "term",
                                    "max-length": 7,
                                    "char-freq-histogram": null,
                                    "min-length": 4
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["AAL486", "N626JS", "VRD1905", "DAL65", "FFT612", "AAL185", "UAL1975", "UAL1608", "SWA797", "AAL2455", "JBU101", "UAL98", "TAI560", "AAL650", "UAL1536", "DAL72", "DAL28", "VRD948", "ASA552", "AAL603", "N535BC", "DAL1896", "ASA384", "VRD720", "UAL881", "DAL585", "VRD415", "JBU19", "ABX2201", "AAL490", "DAL431", "UAL907", "UAL972", "UAL860", "DAL52", "AAL9768", "JBU1248", "DAL1172", "UAL1122", "UAL987", "DAL400", "DAL410", "UAL50", "UAL104", "UAL952", "FFT1478", "JBU624", "HAL5", "AAL102", "UAL1003"]
                            },
                            "vehicle_positions.trip_id": {
                                "used-in-schema": false,
                                "detailAvg": "21",
                                "detailMin": "21",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 89,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "2.7600232846582933",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "trip_id",
                                "struc-type": null,
                                "detailMax": "33",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 30.473684210526315,
                                    "std-dev-length": 2.7600232846582933,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 4, 1, 1, 4, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 5, 4, 1, 1, 1, 1, 2, 1, 1, 1, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 2, 3, 4, 2, 1, 3, 1, 1, 4, 1, 1, 1, 3, 1, 1, 2, 1, 1, 2, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL102-1442104232-airline-0018", "AAL1107-1442052000-schedule-0004", "AAL1283-1442105972-airline-0124", "AAL1488-1442052000-schedule-0001", "AAL154D-1442174925-104-0", "AAL185-1442021400-schedule-0000", "AAL205-1442052000-schedule-0000", "AAL219-1442105972-airline-0020", "AAL2455-1442026200-schedule-0001", "AAL486-1442022000-schedule-0000", "AAL490-1442019300-schedule-0000", "AAL603-1442027400-schedule-0000", "AAL650-1442121051-77-0", "AAL705-1442048100-schedule-0000:0", "AAL751-1442047500-schedule-0001:0", "AAL787-1442048400-schedule-0000:0", "AAL9768-1442186242-422-0", "ABX2201-1442202969-14-0", "ASA384-1442162853-airline-0121", "ASA552-1442026800-schedule-0000", "ASH3802-1442035474-airline-0046", "DAL1172-1442023500-schedule-0000", "DAL1896-1442025960-schedule-0000", "DAL2521-1442124516-airline-0032", "DAL28-1442013780-schedule-0000:2", "DAL37-1442023800-schedule-0000:0", "DAL400-1442014740-schedule-0000:0", "DAL410-1442014320-schedule-0000:0", "DAL431-1442010600-schedule-0002", "DAL52-1442017500-schedule-0000", "DAL585-1442101843-airline-0123", "DAL65-1442013900-schedule-0000", "DAL72-1442008380-schedule-0000:0", "DAL99-1442047800-schedule-0000:0", "FFT1478-1442037716-airline-0143", "FFT612-1442037642-airline-0084", "FFT627-1442052000-schedule-0001", "HAL5-1442020500-schedule-0000", "JBU101-1442015520-schedule-0000", "JBU1248-1442123148-12-0", "JBU19-1442204799-62-0", "JBU624-1442125854-63-1", "N535BC-1442207829-65-0", "N626JS-1442177358-dlad-3921122:0", "NKS409-1442052900-schedule-0005", "NKS696-1442031460-airline-0023", "RPA4271-1442051100-schedule-0000", "SWA262-1442052000-schedule-0000", "SWA797-1442026200-schedule-0000", "TAI560-1442020200-schedule-0001:0", "UAL1003-1442036600-airline-0118", "UAL104-1442006400-schedule-0000:0", "UAL1122-1441963660-airline-0057", "UAL1203-1442056890-airline-0189", "UAL1536-1442063696-airline-0053", "UAL1608-1441958024-airline-0018", "UAL1944-1442050185-airline-0098", "UAL1947-1442050185-airline-0080", "UAL1975-1441945870-airline-0119", "UAL2237-1442165272-51-0", "UAL50-1442014500-schedule-0000:0", "UAL860-1441948802-airline-0128", "UAL881-1441949447-airline-0061", "UAL907-1441949447-airline-0019:0", "UAL934-1442019300-schedule-0000:0", "UAL952-1442011500-schedule-0001:2", "UAL972-1442012400-schedule-0000:0", "UAL98-1442063714-airline-0073:1", "UAL987-1442013600-schedule-0000:0", "VRD1905-1442025300-schedule-0000", "VRD34-1442124954-194-1", "VRD415-1442016300-schedule-0000", "VRD720-1442019000-schedule-0000", "VRD948-1442030100-schedule-0000"],
                                        "exampleValues": null,
                                        "labels": ["AAL102-14421042...", "AAL1107-1442052...", "AAL1283-1442105...", "AAL1488-1442052...", "AAL154D-1442174...", "AAL185-14420214...", "AAL205-14420520...", "AAL219-14421059...", "AAL2455-1442026...", "AAL486-14420220...", "AAL490-14420193...", "AAL603-14420274...", "AAL650-14421210...", "AAL705-14420481...", "AAL751-14420475...", "AAL787-14420484...", "AAL9768-1442186...", "ABX2201-1442202...", "ASA384-14421628...", "ASA552-14420268...", "ASH3802-1442035...", "DAL1172-1442023...", "DAL1896-1442025...", "DAL2521-1442124...", "DAL28-144201378...", "DAL37-144202380...", "DAL400-14420147...", "DAL410-14420143...", "DAL431-14420106...", "DAL52-144201750...", "DAL585-14421018...", "DAL65-144201390...", "DAL72-144200838...", "DAL99-144204780...", "FFT1478-1442037...", "FFT612-14420376...", "FFT627-14420520...", "HAL5-1442020500...", "JBU101-14420155...", "JBU1248-1442123...", "JBU19-144220479...", "JBU624-14421258...", "N535BC-14422078...", "N626JS-14421773...", "NKS409-14420529...", "NKS696-14420314...", "RPA4271-1442051...", "SWA262-14420520...", "SWA797-14420262...", "TAI560-14420202...", "UAL1003-1442036...", "UAL104-14420064...", "UAL1122-1441963...", "UAL1203-1442056...", "UAL1536-1442063...", "UAL1608-1441958...", "UAL1944-1442050...", "UAL1947-1442050...", "UAL1975-1441945...", "UAL2237-1442165...", "UAL50-144201450...", "UAL860-14419488...", "UAL881-14419494...", "UAL907-14419494...", "UAL934-14420193...", "UAL952-14420115...", "UAL972-14420124...", "UAL98-144206371...", "UAL987-14420136...", "VRD1905-1442025...", "VRD34-144212495...", "VRD415-14420163...", "VRD720-14420190...", "VRD948-14420301..."]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 3474,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 106734,
                                    "detail-type": "term",
                                    "max-length": 33,
                                    "char-freq-histogram": null,
                                    "min-length": 21
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["AAL486-1442022000-schedule-0000", "N626JS-1442177358-dlad-3921122:0", "VRD1905-1442025300-schedule-0000", "DAL65-1442013900-schedule-0000", "FFT612-1442037642-airline-0084", "AAL185-1442021400-schedule-0000", "UAL1975-1441945870-airline-0119", "UAL1608-1441958024-airline-0018", "SWA797-1442026200-schedule-0000", "AAL2455-1442026200-schedule-0001", "JBU101-1442015520-schedule-0000", "UAL98-1442063714-airline-0073:1", "TAI560-1442020200-schedule-0001:0", "AAL650-1442121051-77-0", "UAL1536-1442063696-airline-0053", "DAL72-1442008380-schedule-0000:0", "DAL28-1442013780-schedule-0000:2", "VRD948-1442030100-schedule-0000", "ASA552-1442026800-schedule-0000", "AAL603-1442027400-schedule-0000", "N535BC-1442207829-65-0", "DAL1896-1442025960-schedule-0000", "ASA384-1442162853-airline-0121", "VRD720-1442019000-schedule-0000", "UAL881-1441949447-airline-0061", "DAL585-1442101843-airline-0123", "VRD415-1442016300-schedule-0000", "JBU19-1442204799-62-0", "ABX2201-1442202969-14-0", "AAL490-1442019300-schedule-0000", "DAL431-1442010600-schedule-0002", "UAL907-1441949447-airline-0019:0", "UAL972-1442012400-schedule-0000:0", "UAL860-1441948802-airline-0128", "DAL52-1442017500-schedule-0000", "AAL9768-1442186242-422-0", "JBU1248-1442123148-12-0", "DAL1172-1442023500-schedule-0000", "UAL1122-1441963660-airline-0057", "UAL987-1442013600-schedule-0000:0", "DAL400-1442014740-schedule-0000:0", "DAL410-1442014320-schedule-0000:0", "UAL50-1442014500-schedule-0000:0", "UAL104-1442006400-schedule-0000:0", "UAL952-1442011500-schedule-0001:2", "FFT1478-1442037716-airline-0143", "JBU624-1442125854-63-1", "HAL5-1442020500-schedule-0000", "AAL102-1442104232-airline-0018", "UAL1003-1442036600-airline-0118"]
                            }
                        },
                        "dsFileType": "application/json",
                        "data-sample-id": 5,
                        "dsLastUpdate": null,
                        "dsDescription": null,
                        "dsFileName": "vehicle_position_cluster_schema_examples-2-US-Only.txt",
                        "dsVersion": null,
                        "dsFileSize": 0,
                        "dsStructuredProfile": [
                        {
                            "path": "cluster_id",
                            "field": "cluster_id",
                            "children": [],
                            "id": 1
                        }, {
                            "path": "cluster_size",
                            "field": "cluster_size",
                            "children": [],
                            "id": 2
                        }, {
                            "path": "vehicle_positions",
                            "field": "vehicle_positions",
                            "children": [{
                                "path": "vehicle_positions.lat",
                                "field": "lat",
                                "children": [],
                                "id": 4
                            }, {
                                "path": "vehicle_positions.tailnumber",
                                "field": "tailnumber",
                                "children": [],
                                "id": 5
                            }, {
                                "path": "vehicle_positions.long",
                                "field": "long",
                                "children": [],
                                "id": 6
                            }, {
                                "path": "vehicle_positions.speed",
                                "field": "speed",
                                "children": [],
                                "id": 7
                            }, {
                                "path": "vehicle_positions.timestamp",
                                "field": "timestamp",
                                "children": [],
                                "id": 8
                            }, {
                                "path": "vehicle_positions.heading",
                                "field": "heading",
                                "children": [],
                                "id": 9
                            }, {
                                "path": "vehicle_positions.vehicle_type",
                                "field": "vehicle_type",
                                "children": [],
                                "id": 10
                            }, {
                                "path": "vehicle_positions.trip_id",
                                "field": "trip_id",
                                "children": [],
                                "id": 11
                            }, {
                                "path": "vehicle_positions.aircraft",
                                "field": "aircraft",
                                "children": [{
                                    "path": "vehicle_positions.aircraft.air_ground",
                                    "field": "air_ground",
                                    "children": [],
                                    "id": 13
                                }, {
                                    "path": "vehicle_positions.aircraft.alt",
                                    "field": "alt",
                                    "children": [],
                                    "id": 14
                                }, {
                                    "path": "vehicle_positions.aircraft.flight_id",
                                    "field": "flight_id",
                                    "children": [],
                                    "id": 15
                                }],
                                "id": 12
                            }
                            ],
                            "id": 3
                        }],
                        "dsExtractedContentDir": "C:\\Users\\leegc\\AppData\\Local\\Temp\\temp3057174123149834262\\vehicle_position_cluster_schema_examples-2-US-Only-embedded"
                    },
                    {
                        "dsName": "vehicle_position_cluster_schema_examples-3-US-Only",
                        "dsId": "4c4c13b5-dfe9-47af-abd3-9678c47b0fc9",
                        "dsNumbRecords": 55,
                        "dsContainsStructuredData": true,
                        "dsProfile": {
                            "vehicle_positions.lat": {
                                "used-in-schema": true,
                                "detailAvg": "18.69781",
                                "detailMin": "18.69781",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.latitude"
                                }, {
                                    "confidence": 90,
                                    "matching-field": "vehicle_positions.longitude"
                                }],
                                "detailStdDev": "8.310167962782524",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "114",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "lat",
                                "struc-type": null,
                                "detailMax": "56.16934",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 39.15750280701754,
                                    "freq-histogram": {
                                        "data": [6, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 14, 0, 2, 2, 2, 21, 3, 3, 4, 7, 2, 10, 4, 0, 0, 0, 0, 0, 7, 2, 4, 2, 0, 4, 0, 4, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.lat",
                                            "longitude-key": "vehicle_positions.long",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 38
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[18.69780,19.44725)", "[19.44725,20.19670)", "[20.19670,20.94615)", "[20.94615,21.69560)", "[21.69560,22.44505)", "[22.44505,23.19450)", "[23.19450,23.94395)", "[23.94395,24.69340)", "[24.69340,25.44285)", "[25.44285,26.19230)", "[26.19230,26.94175)", "[26.94175,27.69120)", "[27.69120,28.44065)", "[28.44065,29.19010)", "[29.19010,29.93955)", "[29.93955,30.68900)", "[30.68900,31.43845)", "[31.43845,32.18790)", "[32.18790,32.93735)", "[32.93735,33.68680)", "[33.68680,34.43625)", "[34.43625,35.18570)", "[35.18570,35.93515)", "[35.93515,36.68460)", "[36.68460,37.43405)", "[37.43405,38.18350)", "[38.18350,38.93295)", "[38.93295,39.68240)", "[39.68240,40.43185)", "[40.43185,41.18130)", "[41.18130,41.93075)", "[41.93075,42.68020)", "[42.68020,43.42965)", "[43.42965,44.17910)", "[44.17910,44.92855)", "[44.92855,45.67800)", "[45.67800,46.42745)", "[46.42745,47.17690)", "[47.17690,47.92635)", "[47.92635,48.67580)", "[48.67580,49.42525)", "[49.42525,50.17470)", "[50.17470,50.92415)", "[50.92415,51.67360)", "[51.67360,52.42305)", "[52.42305,53.17250)", "[53.17250,53.92195)", "[53.92195,54.67140)", "[54.67140,55.42085)", "[55.42085,56.17030)"],
                                        "exampleValues": null,
                                        "labels": ["[18.6978,19.4472)", "[19.4472,20.1967)", "[20.1967,20.9461)", "[20.9461,21.6956)", "[21.6956,22.4450)", "[22.4450,23.1945)", "[23.1945,23.9439)", "[23.9439,24.6934)", "[24.6934,25.4428)", "[25.4428,26.1923)", "[26.1923,26.9417)", "[26.9417,27.6912)", "[27.6912,28.4406)", "[28.4406,29.1901)", "[29.1901,29.9395)", "[29.9395,30.689)", "[30.689,31.4384)", "[31.4384,32.1879)", "[32.1879,32.9373)", "[32.9373,33.6868)", "[33.6868,34.4362)", "[34.4362,35.1857)", "[35.1857,35.9351)", "[35.9351,36.6846)", "[36.6846,37.4340)", "[37.4340,38.1835)", "[38.1835,38.9329)", "[38.9329,39.6824)", "[39.6824,40.4318)", "[40.4318,41.1813)", "[41.1813,41.9307)", "[41.9307,42.6802)", "[42.6802,43.4296)", "[43.4296,44.1791)", "[44.1791,44.9285)", "[44.9285,45.678)", "[45.678,46.4274)", "[46.4274,47.1769)", "[47.1769,47.9263)", "[47.9263,48.6758)", "[48.6758,49.4252)", "[49.4252,50.1747)", "[50.1747,50.9241)", "[50.9241,51.6736)", "[51.6736,52.4230)", "[52.4230,53.1725)", "[53.1725,53.9219)", "[53.9219,54.6714)", "[54.6714,55.4208)", "[55.4208,56.1703)"]
                                    },
                                    "min": 18.69781,
                                    "walkingCount": 114,
                                    "walkingSum": 4463.95532,
                                    "max": 56.16934,
                                    "num-distinct-values": "114",
                                    "walkingSquareSum": 182670.0566122418,
                                    "detail-type": "decimal",
                                    "std-dev": 8.310167962782524
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Latitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Latitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["37.64265", "37.54615", "39.93562", "37.64982", "33.96079", "33.97178", "33.80026", "33.67845", "38.02629", "51.28647", "34.01358", "47.57794", "37.40511", "35.66469", "33.98785", "33.6109", "33.97888", "37.89687", "50.99756", "38.33812", "37.73273", "37.57706", "18.70097", "37.55574", "37.53552", "36.14095", "19.29831", "37.73734", "19.3877", "19.45613", "37.54517", "49.33648", "50.09272", "48.08038", "33.94574", "49.26628", "47.84732", "33.67186", "47.77913", "21.25063", "38.91022", "40.81229", "42.33664", "52.52228", "42.4617", "42.38577", "40.93185", "53.12765", "42.75595", "40.90149"]
                            },
                            "vehicle_positions.aircraft.airGround": {
                                "used-in-schema": false,
                                "detailAvg": "1",
                                "detailMin": "1",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.aircraft.air_ground"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "display-name": "airGround",
                                "struc-type": null,
                                "detailMax": "1",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 1,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [114],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["A"],
                                        "exampleValues": null,
                                        "labels": ["A"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 114,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 114,
                                    "detail-type": "term",
                                    "max-length": 1,
                                    "char-freq-histogram": null,
                                    "min-length": 1
                                },
                                "presence": 1,
                                /*
                                 "interpretations": [{
                                 "iId": null,
                                 "interpretation": "Unknown",
                                 "iDomainId": null,
                                 "iName": "Unknown",
                                 "iMatchingNames": null,
                                 "iDescription": null,
                                 "iScript": null,
                                 "iConstraints": null
                                 }],
                                 */
                                "interpretations": {
                                    "availableOptions": [
                                        {
                                            "iId": null,
                                            "interpretation": "MSL",
                                            "iDomainId": null,
                                            "iName": "MSL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        },
                                        {
                                            "iId": null,
                                            "interpretation": "Unknown",
                                            "iDomainId": null,
                                            "iName": "Unknown",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        }
                                    ],
                                    "selectedOption":
                                        {
                                            "iId": null,
                                            "interpretation": "MSL",
                                            "iDomainId": null,
                                            "iName": "MSL",
                                            "iMatchingNames": null,
                                            "iDescription": null,
                                            "iScript": null,
                                            "iConstraints": null
                                        }
                                },
                                "exampleValues": ["A"]
                            },
                            "vehicle_positions.aircraft.alt": {
                                "used-in-schema": true,
                                "detailAvg": "700.0",
                                "detailMin": "700.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 97,
                                    "matching-field": "vehicle_positions.aircraft.altitude"
                                }],
                                "detailStdDev": "13208.315820046586",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "75",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "alt",
                                "struc-type": null,
                                "detailMax": "47000.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 19821.929824561405,
                                    "freq-histogram": {
                                        "data": [5, 5, 5, 1, 3, 3, 1, 2, 4, 3, 5, 10, 3, 4, 1, 2, 0, 1, 1, 0, 2, 4, 0, 2, 2, 1, 0, 0, 1, 0, 1, 0, 3, 3, 7, 2, 2, 3, 9, 5, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[700,1625)", "[1625,2550)", "[2550,3475)", "[3475,4400)", "[4400,5325)", "[5325,6250)", "[6250,7175)", "[7175,8100)", "[8100,9025)", "[9025,9950)", "[9950,10875)", "[10875,11800)", "[11800,12725)", "[12725,13650)", "[13650,14575)", "[14575,15500)", "[15500,16425)", "[16425,17350)", "[17350,18275)", "[18275,19200)", "[19200,20125)", "[20125,21050)", "[21050,21975)", "[21975,22900)", "[22900,23825)", "[23825,24750)", "[24750,25675)", "[25675,26600)", "[26600,27525)", "[27525,28450)", "[28450,29375)", "[29375,30300)", "[30300,31225)", "[31225,32150)", "[32150,33075)", "[33075,34000)", "[34000,34925)", "[34925,35850)", "[35850,36775)", "[36775,37700)", "[37700,38625)", "[38625,39550)", "[39550,40475)", "[40475,41400)", "[41400,42325)", "[42325,43250)", "[43250,44175)", "[44175,45100)", "[45100,46025)", "[46025,46950)", "[46950,47875)"],
                                        "exampleValues": null,
                                        "labels": ["[700.0,1625.0)", "[1625.0,2550.0)", "[2550.0,3475.0)", "[3475.0,4400.0)", "[4400.0,5325.0)", "[5325.0,6250.0)", "[6250.0,7175.0)", "[7175.0,8100.0)", "[8100.0,9025.0)", "[9025.0,9950.0)", "[9950.0,10875.0)", "[10875.0,11800.0)", "[11800.0,12725.0)", "[12725.0,13650.0)", "[13650.0,14575.0)", "[14575.0,15500.0)", "[15500.0,16425.0)", "[16425.0,17350.0)", "[17350.0,18275.0)", "[18275.0,19200.0)", "[19200.0,20125.0)", "[20125.0,21050.0)", "[21050.0,21975.0)", "[21975.0,22900.0)", "[22900.0,23825.0)", "[23825.0,24750.0)", "[24750.0,25675.0)", "[25675.0,26600.0)", "[26600.0,27525.0)", "[27525.0,28450.0)", "[28450.0,29375.0)", "[29375.0,30300.0)", "[30300.0,31225.0)", "[31225.0,32150.0)", "[32150.0,33075.0)", "[33075.0,34000.0)", "[34000.0,34925.0)", "[34925.0,35850.0)", "[35850.0,36775.0)", "[36775.0,37700.0)", "[37700.0,38625.0)", "[38625.0,39550.0)", "[39550.0,40475.0)", "[40475.0,41400.0)", "[41400.0,42325.0)", "[42325.0,43250.0)", "[43250.0,44175.0)", "[44175.0,45100.0)", "[45100.0,46025.0)", "[46025.0,46950.0)", "[46950.0,47875.0)"]
                                    },
                                    "min": 700,
                                    "walkingCount": 114,
                                    "walkingSum": 2259700,
                                    "max": 47000,
                                    "num-distinct-values": "75",
                                    "walkingSquareSum": 6.468001E10,
                                    "detail-type": "integer",
                                    "std-dev": 13208.315820046586
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["33000", "47000", "10100", "11000", "12900", "34000", "1600", "700", "20300", "20400", "20800", "9000", "36000", "8500", "18200", "17200", "35000", "7300", "37000", "3000", "22000", "2600", "8100", "15000", "1800", "2300", "5600", "5500", "11100", "13700", "4400", "4900", "32000", "39000", "30700", "29200", "12600", "13100", "38000", "10500", "10900", "22100", "10000", "33900", "3100", "23800", "14900", "2000", "1200", "11700"]
                            },
                            "vehicle_positions.aircraft": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "aircraft",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.tailnumber": {
                                "used-in-schema": false,
                                "detailAvg": "6",
                                "detailMin": "6",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }, {
                                    "confidence": 91,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 89,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "tailnumber",
                                "struc-type": null,
                                "detailMax": "6",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 6,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 4, 2, 3, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 2, 1, 1, 1, 1, 3, 1, 4, 3, 1, 1, 1, 1, 5, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 4, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 4, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["N112AN", "N153DL", "N1604R", "N174DZ", "N180UA", "N185DN", "N196UW", "N219CY", "N220FR", "N291AY", "N292AY", "N354AA", "N37470", "N37474", "N38451", "N38950", "N392HA", "N408YX", "N453AS", "N457AS", "N492TA", "N509AY", "N524VA", "N535BC", "N548UW", "N57862", "N580UW", "N623VA", "N626JS", "N629NK", "N634JB", "N641NK", "N641UA", "N647UA", "N66837", "N673US", "N675NW", "N688AA", "N712TW", "N718TW", "N73275", "N75429", "N76054", "N76062", "N762AN", "N7727A", "N7729A", "N77430", "N774UA", "N775JB", "N777UA", "N778UA", "N78005", "N783UA", "N803AL", "N805DN", "N810DN", "N815NW", "N830DN", "N837VA", "N838NN", "N841VA", "N844MH", "N848NN", "N851VA", "N89304", "N928NN", "N931FR", "N935UW", "N943JT", "N947FR", "N952JB", "N959NN", "N987AN"],
                                        "exampleValues": null,
                                        "labels": ["N112AN", "N153DL", "N1604R", "N174DZ", "N180UA", "N185DN", "N196UW", "N219CY", "N220FR", "N291AY", "N292AY", "N354AA", "N37470", "N37474", "N38451", "N38950", "N392HA", "N408YX", "N453AS", "N457AS", "N492TA", "N509AY", "N524VA", "N535BC", "N548UW", "N57862", "N580UW", "N623VA", "N626JS", "N629NK", "N634JB", "N641NK", "N641UA", "N647UA", "N66837", "N673US", "N675NW", "N688AA", "N712TW", "N718TW", "N73275", "N75429", "N76054", "N76062", "N762AN", "N7727A", "N7729A", "N77430", "N774UA", "N775JB", "N777UA", "N778UA", "N78005", "N783UA", "N803AL", "N805DN", "N810DN", "N815NW", "N830DN", "N837VA", "N838NN", "N841VA", "N844MH", "N848NN", "N851VA", "N89304", "N928NN", "N931FR", "N935UW", "N943JT", "N947FR", "N952JB", "N959NN", "N987AN"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 684,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 4104,
                                    "detail-type": "term",
                                    "max-length": 6,
                                    "char-freq-histogram": null,
                                    "min-length": 6
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["N548UW", "N626JS", "N524VA", "N805DN", "N220FR", "N112AN", "N77430", "N37470", "N7727A", "N688AA", "N634JB", "N38950", "N492TA", "N580UW", "N38451", "N673US", "N844MH", "N851VA", "N457AS", "N196UW", "N535BC", "N810DN", "N453AS", "N623VA", "N180UA", "N153DL", "N841VA", "N775JB", "N219CY", "N509AY", "N712TW", "N777UA", "N774UA", "N641UA", "N185DN", "N762AN", "N952JB", "N830DN", "N73275", "N778UA", "N815NW", "N1604R", "N78005", "N76054", "N647UA", "N931FR", "N943JT", "N392HA", "N354AA", "N75429"]
                            },
                            "vehicle_positions.long": {
                                "used-in-schema": true,
                                "detailAvg": "-157.8471",
                                "detailMin": "-157.8471",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.longitude"
                                }, {
                                    "confidence": 85,
                                    "matching-field": "vehicle_positions.latitude"
                                }],
                                "detailStdDev": "53.9195348998481",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "114",
                                "detailType": "decimal",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "long",
                                "struc-type": null,
                                "detailMax": "140.46926",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": -77.7208548245614,
                                    "freq-histogram": {
                                        "data": [2, 0, 0, 0, 0, 0, 0, 24, 21, 2, 0, 4, 0, 0, 6, 4, 9, 10, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 4, 4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2],
                                        "region-data": {
                                            "latitude-key": "vehicle_positions.lat",
                                            "longitude-key": "vehicle_positions.long",
                                            "rows": [{
                                                "c": [{
                                                    "v": "United States"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "China"
                                                }, {
                                                    "v": 38
                                                }]
                                            }, {
                                                "c": [{
                                                    "v": "Russia"
                                                }, {
                                                    "v": 38
                                                }]
                                            }],
                                            "cols": [{
                                                "label": "Country"
                                            }, {
                                                "label": "Frequency"
                                            }]
                                        },
                                        "series": "Values",
                                        "type": "map",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[-160,-155)", "[-155,-150)", "[-150,-145)", "[-145,-140)", "[-140,-135)", "[-135,-130)", "[-130,-125)", "[-125,-120)", "[-120,-115)", "[-115,-110)", "[-110,-105)", "[-105,-100)", "[-100,-95)", "[-95,-90)", "[-90,-85)", "[-85,-80)", "[-80,-75)", "[-75,-70)", "[-70,-65)", "[-65,-60)", "[-60,-55)", "[-55,-50)", "[-50,-45)", "[-45,-40)", "[-40,-35)", "[-35,-30)", "[-30,-25)", "[-25,-20)", "[-20,-15)", "[-15,-10)", "[-10,-5)", "[-5,0)", "[0,5)", "[5,10)", "[10,15)", "[15,20)", "[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)"],
                                        "exampleValues": null,
                                        "labels": ["[-160.0,-155.0)", "[-155.0,-150.0)", "[-150.0,-145.0)", "[-145.0,-140.0)", "[-140.0,-135.0)", "[-135.0,-130.0)", "[-130.0,-125.0)", "[-125.0,-120.0)", "[-120.0,-115.0)", "[-115.0,-110.0)", "[-110.0,-105.0)", "[-105.0,-100.0)", "[-100.0,-95.0)", "[-95.0,-90.0)", "[-90.0,-85.0)", "[-85.0,-80.0)", "[-80.0,-75.0)", "[-75.0,-70.0)", "[-70.0,-65.0)", "[-65.0,-60.0)", "[-60.0,-55.0)", "[-55.0,-50.0)", "[-50.0,-45.0)", "[-45.0,-40.0)", "[-40.0,-35.0)", "[-35.0,-30.0)", "[-30.0,-25.0)", "[-25.0,-20.0)", "[-20.0,-15.0)", "[-15.0,-10.0)", "[-10.0,-5.0)", "[-5.0,0.0)", "[0.0,5.0)", "[5.0,10.0)", "[10.0,15.0)", "[15.0,20.0)", "[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)"]
                                    },
                                    "min": -157.8471,
                                    "walkingCount": 114,
                                    "walkingSum": -8860.17745,
                                    "max": 140.46926,
                                    "num-distinct-values": "114",
                                    "walkingSquareSum": 1020054.6171063195,
                                    "detail-type": "decimal",
                                    "std-dev": 53.9195348998481
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Coordinate - Longitude",
                                    "iDomainId": null,
                                    "iName": "Coordinate - Longitude",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["-103.4528", "-121.6429", "-104.39368", "-122.3454", "-116.8985", "-116.89487", "-118.72174", "-118.4465", "-121.79385", "-9.2038", "-118.46026", "-122.34547", "-122.2316", "140.44688", "-118.02478", "-118.38015", "-118.1201", "-118.8012", "-2.74918", "-120.0866", "-120.1534", "-121.3995", "-66.62789", "-121.5846", "-121.70783", "-114.7385", "-67.03642", "-120.1194", "-67.0987", "-67.14649", "-122.1437", "0.73977", "7.53232", "6.04571", "-118.44674", "2.29441", "7.44739", "-118.4166", "7.82391", "-157.8471", "-77.58484", "-73.90588", "-71.01157", "-6.17589", "-85.00085", "-70.73413", "-75.2908", "-1.69448", "-71.22255", "-75.18294"]
                            },
                            "vehicle_positions.speed": {
                                "used-in-schema": false,
                                "detailAvg": "73.0",
                                "detailMin": "73.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.speed"
                                }, {
                                    "confidence": 87,
                                    "matching-field": "vehicle_positions.heading"
                                }],
                                "detailStdDev": "134.21511057415535",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "94",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "speed",
                                "struc-type": null,
                                "detailMax": "919.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 374.3333333333333,
                                    "freq-histogram": {
                                        "data": [1, 0, 0, 0, 2, 0, 2, 4, 6, 2, 1, 4, 3, 2, 1, 4, 5, 0, 8, 2, 5, 5, 7, 4, 3, 2, 13, 7, 6, 6, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[60,75)", "[75,90)", "[90,105)", "[105,120)", "[120,135)", "[135,150)", "[150,165)", "[165,180)", "[180,195)", "[195,210)", "[210,225)", "[225,240)", "[240,255)", "[255,270)", "[270,285)", "[285,300)", "[300,315)", "[315,330)", "[330,345)", "[345,360)", "[360,375)", "[375,390)", "[390,405)", "[405,420)", "[420,435)", "[435,450)", "[450,465)", "[465,480)", "[480,495)", "[495,510)", "[510,525)", "[525,540)", "[540,555)", "[555,570)", "[570,585)", "[585,600)", "[600,615)", "[615,630)", "[630,645)", "[645,660)", "[660,675)", "[675,690)", "[690,705)", "[705,720)", "[720,735)", "[735,750)", "[750,765)", "[765,780)", "[780,795)", "[795,810)", "[810,825)", "[825,840)", "[840,855)", "[855,870)", "[870,885)", "[885,900)", "[900,915)", "[915,930)"],
                                        "exampleValues": null,
                                        "labels": ["[60.0,75.0)", "[75.0,90.0)", "[90.0,105.0)", "[105.0,120.0)", "[120.0,135.0)", "[135.0,150.0)", "[150.0,165.0)", "[165.0,180.0)", "[180.0,195.0)", "[195.0,210.0)", "[210.0,225.0)", "[225.0,240.0)", "[240.0,255.0)", "[255.0,270.0)", "[270.0,285.0)", "[285.0,300.0)", "[300.0,315.0)", "[315.0,330.0)", "[330.0,345.0)", "[345.0,360.0)", "[360.0,375.0)", "[375.0,390.0)", "[390.0,405.0)", "[405.0,420.0)", "[420.0,435.0)", "[435.0,450.0)", "[450.0,465.0)", "[465.0,480.0)", "[480.0,495.0)", "[495.0,510.0)", "[510.0,525.0)", "[525.0,540.0)", "[540.0,555.0)", "[555.0,570.0)", "[570.0,585.0)", "[585.0,600.0)", "[600.0,615.0)", "[615.0,630.0)", "[630.0,645.0)", "[645.0,660.0)", "[660.0,675.0)", "[675.0,690.0)", "[690.0,705.0)", "[705.0,720.0)", "[720.0,735.0)", "[735.0,750.0)", "[750.0,765.0)", "[765.0,780.0)", "[780.0,795.0)", "[795.0,810.0)", "[810.0,825.0)", "[825.0,840.0)", "[840.0,855.0)", "[855.0,870.0)", "[870.0,885.0)", "[885.0,900.0)", "[900.0,915.0)", "[915.0,930.0)"]
                                    },
                                    "min": 73,
                                    "walkingCount": 114,
                                    "walkingSum": 42674,
                                    "max": 919,
                                    "num-distinct-values": "94",
                                    "walkingSquareSum": 1.8027862E7,
                                    "detail-type": "integer",
                                    "std-dev": 134.21511057415535
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["450", "511", "301", "303", "376", "422", "73", "182", "395", "372", "373", "290", "465", "291", "335", "420", "415", "522", "493", "286", "459", "190", "416", "170", "236", "568", "154", "158", "282", "260", "337", "252", "253", "222", "393", "389", "499", "497", "489", "494", "381", "404", "339", "472", "195", "238", "458", "308", "502", "453"]
                            },
                            "cluster_id": {
                                "used-in-schema": false,
                                "detailAvg": "36",
                                "detailMin": "36",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 92,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 91,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "55",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_id",
                                "struc-type": null,
                                "detailMax": "36",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 36,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["04e4aca6-d589-4e33-b717-841594076be7", "075710e3-950c-431e-8496-f63690e499d9", "0bc34b70-94c4-4774-a51f-88be10d6b226", "0c4eb716-4ebb-4c00-86c4-28572b49fafd", "1630bcac-4e03-4e51-9c5e-aa32f9010079", "17512205-80c5-4826-8244-d851a57c20fb", "192e90e4-993c-46bd-883a-cf1e6269326c", "1a13e1ee-e2e9-499c-891f-66eec2273cdc", "1adef694-1be7-4244-a719-84bce48535e6", "34dca0de-b93c-4dd2-88fe-2145a59e08fd", "38333023-07d1-48a0-b9f2-cfaf0553348e", "39eb60a1-7558-4e8d-b2d2-f36628917ea2", "3a2b19f2-1375-47dc-a80e-fef04d7b95c6", "47924c5e-006e-472a-815d-a47415625dbc", "48df1e44-27dc-4f81-8ad0-3c4da50c3112", "4ee04c22-c2b8-4e0d-8ab8-01ffb8919677", "5909039d-f4f7-46db-b9cc-95d06622615e", "5bd73dd2-fc3e-483d-bcec-168c0d8471bd", "5cbe4bcf-29a4-40a9-88c0-3374f23200f6", "6618dfb2-7523-45a1-a47d-3bea537e44f0", "670c6b73-0e48-41ee-b2bb-b1603a5fd69e", "6b7454e5-55a3-48d2-856f-79d0315ca0ab", "6c560bec-90b6-4f45-80a1-7dddbe554598", "6de54bc6-2e16-4843-87b2-47f8a40cfe40", "6f27e02f-0e72-4a90-9512-9749d9c9a871", "7e7d3f40-97f0-497d-9787-b0f3ac46687d", "7eaa24cd-f5a1-4fd1-ad4c-1d39f38246eb", "806f0cc1-e724-41b9-8683-79312e0d0022", "8151d7b7-ea73-46b5-a50d-0c0ca7e88bc8", "948a8a6d-2006-43c0-b279-f3f6abfd78e4", "973f8a24-5fc8-4eb1-ba48-7f155b217647", "98effbec-ea0a-4b4d-9afd-db2ed791ef24", "9b5287b6-ad02-4462-a455-b11b07a37223", "a4923823-9a12-4366-991a-f895c9eeb0f5", "ab6c44eb-4bdf-4d86-af76-e90a5b413152", "ad27928e-a40a-468c-8e4b-db46497bff1a", "b770b2c9-06f3-4d80-afaa-fbad042391c0", "c0755d8a-d5f2-45a9-b67a-f183b298a29c", "c0f9ca18-c790-4bc7-801b-4078539fc24b", "c10355e6-80b2-4fd9-a3f2-129ee93db636", "c32b0b6a-e1bf-4ade-add1-85dd2714e10e", "c7901591-698c-460f-8076-51681ca6f931", "d22bc4b2-4b5c-4ef5-b84d-11940792e316", "db2e100f-d101-48e2-9663-7043cdb0df05", "dc7b1838-0aaa-4459-9e38-23117be3b6ba", "dee33e0f-fcf3-4db0-9e7d-9e48c756d116", "e1b5c65d-f5f4-4b45-b297-eb838f7584d3", "e4f45918-0291-4bf3-a5c5-8580d9d04496", "ef6f9a52-6e22-4c76-a4a9-ab121b70ea65", "f10f509a-4611-4429-bc79-8c4c866d3011", "f59e56fa-d52d-4e85-ad61-102438a20aeb", "f8cfe3ee-209e-4d4c-a47a-3c304e3706f5", "fa15fca5-bb6d-4c1f-a97f-675df1fe312a", "fc89322c-2eae-452e-a33b-52d82a8c172d", "fe6512fb-9f05-4bdc-b8fa-7841bbded81b"],
                                        "exampleValues": null,
                                        "labels": ["04e4aca6-d589-4...", "075710e3-950c-4...", "0bc34b70-94c4-4...", "0c4eb716-4ebb-4...", "1630bcac-4e03-4...", "17512205-80c5-4...", "192e90e4-993c-4...", "1a13e1ee-e2e9-4...", "1adef694-1be7-4...", "34dca0de-b93c-4...", "38333023-07d1-4...", "39eb60a1-7558-4...", "3a2b19f2-1375-4...", "47924c5e-006e-4...", "48df1e44-27dc-4...", "4ee04c22-c2b8-4...", "5909039d-f4f7-4...", "5bd73dd2-fc3e-4...", "5cbe4bcf-29a4-4...", "6618dfb2-7523-4...", "670c6b73-0e48-4...", "6b7454e5-55a3-4...", "6c560bec-90b6-4...", "6de54bc6-2e16-4...", "6f27e02f-0e72-4...", "7e7d3f40-97f0-4...", "7eaa24cd-f5a1-4...", "806f0cc1-e724-4...", "8151d7b7-ea73-4...", "948a8a6d-2006-4...", "973f8a24-5fc8-4...", "98effbec-ea0a-4...", "9b5287b6-ad02-4...", "a4923823-9a12-4...", "ab6c44eb-4bdf-4...", "ad27928e-a40a-4...", "b770b2c9-06f3-4...", "c0755d8a-d5f2-4...", "c0f9ca18-c790-4...", "c10355e6-80b2-4...", "c32b0b6a-e1bf-4...", "c7901591-698c-4...", "d22bc4b2-4b5c-4...", "db2e100f-d101-4...", "dc7b1838-0aaa-4...", "dee33e0f-fcf3-4...", "e1b5c65d-f5f4-4...", "e4f45918-0291-4...", "ef6f9a52-6e22-4...", "f10f509a-4611-4...", "f59e56fa-d52d-4...", "f8cfe3ee-209e-4...", "fa15fca5-bb6d-4...", "fc89322c-2eae-4...", "fe6512fb-9f05-4..."]
                                    },
                                    "walkingCount": 55,
                                    "walkingSum": 1980,
                                    "num-distinct-values": "55",
                                    "walkingSquareSum": 71280,
                                    "detail-type": "term",
                                    "max-length": 36,
                                    "char-freq-histogram": null,
                                    "min-length": 36
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["0c4eb716-4ebb-4c00-86c4-28572b49fafd", "dee33e0f-fcf3-4db0-9e7d-9e48c756d116", "fc89322c-2eae-452e-a33b-52d82a8c172d", "c32b0b6a-e1bf-4ade-add1-85dd2714e10e", "c0755d8a-d5f2-45a9-b67a-f183b298a29c", "806f0cc1-e724-41b9-8683-79312e0d0022", "1adef694-1be7-4244-a719-84bce48535e6", "5cbe4bcf-29a4-40a9-88c0-3374f23200f6", "8151d7b7-ea73-46b5-a50d-0c0ca7e88bc8", "dc7b1838-0aaa-4459-9e38-23117be3b6ba", "47924c5e-006e-472a-815d-a47415625dbc", "973f8a24-5fc8-4eb1-ba48-7f155b217647", "38333023-07d1-48a0-b9f2-cfaf0553348e", "ef6f9a52-6e22-4c76-a4a9-ab121b70ea65", "075710e3-950c-431e-8496-f63690e499d9", "5bd73dd2-fc3e-483d-bcec-168c0d8471bd", "f59e56fa-d52d-4e85-ad61-102438a20aeb", "5909039d-f4f7-46db-b9cc-95d06622615e", "a4923823-9a12-4366-991a-f895c9eeb0f5", "fe6512fb-9f05-4bdc-b8fa-7841bbded81b", "ab6c44eb-4bdf-4d86-af76-e90a5b413152", "6de54bc6-2e16-4843-87b2-47f8a40cfe40", "3a2b19f2-1375-47dc-a80e-fef04d7b95c6", "db2e100f-d101-48e2-9663-7043cdb0df05", "17512205-80c5-4826-8244-d851a57c20fb", "9b5287b6-ad02-4462-a455-b11b07a37223", "948a8a6d-2006-43c0-b279-f3f6abfd78e4", "c7901591-698c-460f-8076-51681ca6f931", "98effbec-ea0a-4b4d-9afd-db2ed791ef24", "f8cfe3ee-209e-4d4c-a47a-3c304e3706f5", "b770b2c9-06f3-4d80-afaa-fbad042391c0", "34dca0de-b93c-4dd2-88fe-2145a59e08fd", "192e90e4-993c-46bd-883a-cf1e6269326c", "7eaa24cd-f5a1-4fd1-ad4c-1d39f38246eb", "0bc34b70-94c4-4774-a51f-88be10d6b226", "6b7454e5-55a3-48d2-856f-79d0315ca0ab", "1630bcac-4e03-4e51-9c5e-aa32f9010079", "e1b5c65d-f5f4-4b45-b297-eb838f7584d3", "4ee04c22-c2b8-4e0d-8ab8-01ffb8919677", "7e7d3f40-97f0-497d-9787-b0f3ac46687d", "6c560bec-90b6-4f45-80a1-7dddbe554598", "fa15fca5-bb6d-4c1f-a97f-675df1fe312a", "1a13e1ee-e2e9-499c-891f-66eec2273cdc", "c10355e6-80b2-4fd9-a3f2-129ee93db636", "6618dfb2-7523-45a1-a47d-3bea537e44f0", "e4f45918-0291-4bf3-a5c5-8580d9d04496", "48df1e44-27dc-4f81-8ad0-3c4da50c3112", "c0f9ca18-c790-4bc7-801b-4078539fc24b", "04e4aca6-d589-4e33-b717-841594076be7", "ad27928e-a40a-468c-8e4b-db46497bff1a"]
                            },
                            "vehicle_positions.timestamp": {
                                "used-in-schema": false,
                                "detailAvg": "1442209381",
                                "detailMin": "1442209381",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "vehicle_positions.timestamp"
                                }],
                                "detailStdDev": "6909.86557231988",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "112",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "timestamp",
                                "struc-type": null,
                                "detailMax": "1442226874",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 1.4422159805701754E9,
                                    "freq-histogram": {
                                        "data": [7, 4, 10, 13, 6, 2, 0, 9, 9, 6, 4, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 3, 12, 10],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[1442209380,1442209730)", "[1442209730,1442210080)", "[1442210080,1442210430)", "[1442210430,1442210780)", "[1442210780,1442211130)", "[1442211130,1442211480)", "[1442211480,1442211830)", "[1442211830,1442212180)", "[1442212180,1442212530)", "[1442212530,1442212880)", "[1442212880,1442213230)", "[1442213230,1442213580)", "[1442213580,1442213930)", "[1442213930,1442214280)", "[1442214280,1442214630)", "[1442214630,1442214980)", "[1442214980,1442215330)", "[1442215330,1442215680)", "[1442215680,1442216030)", "[1442216030,1442216380)", "[1442216380,1442216730)", "[1442216730,1442217080)", "[1442217080,1442217430)", "[1442217430,1442217780)", "[1442217780,1442218130)", "[1442218130,1442218480)", "[1442218480,1442218830)", "[1442218830,1442219180)", "[1442219180,1442219530)", "[1442219530,1442219880)", "[1442219880,1442220230)", "[1442220230,1442220580)", "[1442220580,1442220930)", "[1442220930,1442221280)", "[1442221280,1442221630)", "[1442221630,1442221980)", "[1442221980,1442222330)", "[1442222330,1442222680)", "[1442222680,1442223030)", "[1442223030,1442223380)", "[1442223380,1442223730)", "[1442223730,1442224080)", "[1442224080,1442224430)", "[1442224430,1442224780)", "[1442224780,1442225130)", "[1442225130,1442225480)", "[1442225480,1442225830)", "[1442225830,1442226180)", "[1442226180,1442226530)", "[1442226530,1442226880)"],
                                        "exampleValues": null,
                                        "labels": ["[1442200000,1442200000)", "[1442200000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442210000)", "[1442210000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)", "[1442220000,1442220000)"]
                                    },
                                    "min": 1442209381,
                                    "walkingCount": 114,
                                    "walkingSum": 164412621785,
                                    "max": 1442226874,
                                    "num-distinct-values": "112",
                                    "walkingSquareSum": "237118510551210234651",
                                    "detail-type": "integer",
                                    "std-dev": 6909.86557231988
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["1442209381", "1442209422", "1442209488", "1442209683", "1442209795", "1442209995", "1442210186", "1442210281", "1442210280", "1442210277", "1442210411", "1442210564", "1442210481", "1442210488", "1442210655", "1442210716", "1442210722", "1442210861", "1442210873", "1442210919", "1442211371", "1442211957", "1442212012", "1442212054", "1442212154", "1442212351", "1442212384", "1442212400", "1442212442", "1442212452", "1442212603", "1442212745", "1442212776", "1442213043", "1442213219", "1442213233", "1442213321", "1442213349", "1442213396", "1442225635", "1442225648", "1442225660", "1442225743", "1442225703", "1442225942", "1442226128", "1442226267", "1442226295", "1442226306", "1442226319"]
                            },
                            "cluster_size": {
                                "used-in-schema": false,
                                "detailAvg": "2.0",
                                "detailMin": "2.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "cluster_size"
                                }],
                                "detailStdDev": "0.25968830649246727",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "2",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "cluster_size",
                                "struc-type": null,
                                "detailMax": "3.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 2.0727272727272728,
                                    "freq-histogram": {
                                        "data": [51, 4],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["2", "3"],
                                        "exampleValues": null,
                                        "labels": ["2.0", "3.0"]
                                    },
                                    "min": 2,
                                    "walkingCount": 55,
                                    "walkingSum": 114,
                                    "max": 3,
                                    "num-distinct-values": "2",
                                    "walkingSquareSum": 240,
                                    "detail-type": "integer",
                                    "std-dev": 0.25968830649246727
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["2", "3"]
                            },
                            "vehicle_positions.heading": {
                                "used-in-schema": false,
                                "detailAvg": "8.0",
                                "detailMin": "8.0",
                                "main-type": "number",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.heading"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.speed"
                                }],
                                "detailStdDev": "97.87343036701385",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "number",
                                "detailNumDistinct": "71",
                                "detailType": "integer",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "heading",
                                "struc-type": null,
                                "detailMax": "332.0",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average": 181.140350877193,
                                    "freq-histogram": {
                                        "data": [1, 0, 1, 1, 1, 0, 0, 1, 2, 0, 1, 1, 0, 3, 0, 4, 2, 11, 2, 4, 5, 7, 0, 3, 3, 4, 1, 0, 1, 0, 0, 1, 1, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 2, 16, 3, 1, 2, 0, 1, 3, 0, 2, 2, 1, 0, 1, 10, 2, 0, 0],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["[5,10)", "[10,15)", "[15,20)", "[20,25)", "[25,30)", "[30,35)", "[35,40)", "[40,45)", "[45,50)", "[50,55)", "[55,60)", "[60,65)", "[65,70)", "[70,75)", "[75,80)", "[80,85)", "[85,90)", "[90,95)", "[95,100)", "[100,105)", "[105,110)", "[110,115)", "[115,120)", "[120,125)", "[125,130)", "[130,135)", "[135,140)", "[140,145)", "[145,150)", "[150,155)", "[155,160)", "[160,165)", "[165,170)", "[170,175)", "[175,180)", "[180,185)", "[185,190)", "[190,195)", "[195,200)", "[200,205)", "[205,210)", "[210,215)", "[215,220)", "[220,225)", "[225,230)", "[230,235)", "[235,240)", "[240,245)", "[245,250)", "[250,255)", "[255,260)", "[260,265)", "[265,270)", "[270,275)", "[275,280)", "[280,285)", "[285,290)", "[290,295)", "[295,300)", "[300,305)", "[305,310)", "[310,315)", "[315,320)", "[320,325)", "[325,330)", "[330,335)", "[335,340)", "[340,345)"],
                                        "exampleValues": null,
                                        "labels": ["[5.0,10.0)", "[10.0,15.0)", "[15.0,20.0)", "[20.0,25.0)", "[25.0,30.0)", "[30.0,35.0)", "[35.0,40.0)", "[40.0,45.0)", "[45.0,50.0)", "[50.0,55.0)", "[55.0,60.0)", "[60.0,65.0)", "[65.0,70.0)", "[70.0,75.0)", "[75.0,80.0)", "[80.0,85.0)", "[85.0,90.0)", "[90.0,95.0)", "[95.0,100.0)", "[100.0,105.0)", "[105.0,110.0)", "[110.0,115.0)", "[115.0,120.0)", "[120.0,125.0)", "[125.0,130.0)", "[130.0,135.0)", "[135.0,140.0)", "[140.0,145.0)", "[145.0,150.0)", "[150.0,155.0)", "[155.0,160.0)", "[160.0,165.0)", "[165.0,170.0)", "[170.0,175.0)", "[175.0,180.0)", "[180.0,185.0)", "[185.0,190.0)", "[190.0,195.0)", "[195.0,200.0)", "[200.0,205.0)", "[205.0,210.0)", "[210.0,215.0)", "[215.0,220.0)", "[220.0,225.0)", "[225.0,230.0)", "[230.0,235.0)", "[235.0,240.0)", "[240.0,245.0)", "[245.0,250.0)", "[250.0,255.0)", "[255.0,260.0)", "[260.0,265.0)", "[265.0,270.0)", "[270.0,275.0)", "[275.0,280.0)", "[280.0,285.0)", "[285.0,290.0)", "[290.0,295.0)", "[295.0,300.0)", "[300.0,305.0)", "[305.0,310.0)", "[310.0,315.0)", "[315.0,320.0)", "[320.0,325.0)", "[325.0,330.0)", "[330.0,335.0)", "[335.0,340.0)", "[340.0,345.0)"]
                                    },
                                    "min": 8,
                                    "walkingCount": 114,
                                    "walkingSum": 20650,
                                    "max": 332,
                                    "num-distinct-values": "71",
                                    "walkingSquareSum": 4832578,
                                    "detail-type": "integer",
                                    "std-dev": 97.87343036701385
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["47", "137", "254", "257", "127", "258", "44", "28", "268", "265", "264", "227", "310", "123", "91", "72", "73", "88", "96", "164", "148", "8", "180", "106", "104", "328", "263", "111", "130", "262", "92", "84", "80", "261", "327", "61", "326", "292", "293", "93", "94", "110", "103", "102", "86", "105", "244", "124", "97", "247"]
                            },
                            "vehicle_positions": {
                                "used-in-schema": false,
                                "detailAvg": "n/a",
                                "detailMin": "n/a",
                                "main-type": "object",
                                "matching-fields": [],
                                "detailStdDev": "n/a",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "~",
                                "detailNumDistinct": "n/a",
                                "detailType": "~",
                                "alias-names": null,
                                "interpretation": null,
                                "display-name": "vehicle_positions",
                                "struc-type": "object",
                                "detailMax": "n/a",
                                "attributes": null,
                                "detail": null,
                                "presence": -1,
                                "interpretations": null,
                                "exampleValues": null
                            },
                            "vehicle_positions.vehicle_type": {
                                "used-in-schema": false,
                                "detailAvg": "8",
                                "detailMin": "8",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 100,
                                    "matching-field": "vehicle_positions.vehicle_type"
                                }],
                                "detailStdDev": "0.0",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "1",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "vehicle_type",
                                "struc-type": null,
                                "detailMax": "8",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 8,
                                    "std-dev-length": 0,
                                    "freq-histogram": {
                                        "data": [114],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["aircraft"],
                                        "exampleValues": null,
                                        "labels": ["aircraft"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 912,
                                    "num-distinct-values": "1",
                                    "walkingSquareSum": 7296,
                                    "detail-type": "term",
                                    "max-length": 8,
                                    "char-freq-histogram": null,
                                    "min-length": 8
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["aircraft"]
                            },
                            "vehicle_positions.aircraft.flight_id": {
                                "used-in-schema": false,
                                "detailAvg": "4",
                                "detailMin": "4",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 92,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 86,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "0.6606375573250081",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "flight_id",
                                "struc-type": null,
                                "detailMax": "7",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 6.192982456140351,
                                    "std-dev-length": 0.6606375573250081,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 4, 1, 1, 4, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 5, 4, 1, 1, 1, 1, 2, 1, 1, 1, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 2, 3, 4, 2, 1, 3, 1, 1, 4, 1, 1, 1, 3, 1, 1, 2, 1, 1, 2, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL102", "AAL1107", "AAL1283", "AAL1488", "AAL154D", "AAL185", "AAL205", "AAL219", "AAL2455", "AAL486", "AAL490", "AAL603", "AAL650", "AAL705", "AAL751", "AAL787", "AAL9768", "ABX2201", "ASA384", "ASA552", "ASH3802", "DAL1172", "DAL1896", "DAL2521", "DAL28", "DAL37", "DAL400", "DAL410", "DAL431", "DAL52", "DAL585", "DAL65", "DAL72", "DAL99", "FFT1478", "FFT612", "FFT627", "HAL5", "JBU101", "JBU1248", "JBU19", "JBU624", "N535BC", "N626JS", "NKS409", "NKS696", "RPA4271", "SWA262", "SWA797", "TAI560", "UAL1003", "UAL104", "UAL1122", "UAL1203", "UAL1536", "UAL1608", "UAL1944", "UAL1947", "UAL1975", "UAL2237", "UAL50", "UAL860", "UAL881", "UAL907", "UAL934", "UAL952", "UAL972", "UAL98", "UAL987", "VRD1905", "VRD34", "VRD415", "VRD720", "VRD948"],
                                        "exampleValues": null,
                                        "labels": ["AAL102", "AAL1107", "AAL1283", "AAL1488", "AAL154D", "AAL185", "AAL205", "AAL219", "AAL2455", "AAL486", "AAL490", "AAL603", "AAL650", "AAL705", "AAL751", "AAL787", "AAL9768", "ABX2201", "ASA384", "ASA552", "ASH3802", "DAL1172", "DAL1896", "DAL2521", "DAL28", "DAL37", "DAL400", "DAL410", "DAL431", "DAL52", "DAL585", "DAL65", "DAL72", "DAL99", "FFT1478", "FFT612", "FFT627", "HAL5", "JBU101", "JBU1248", "JBU19", "JBU624", "N535BC", "N626JS", "NKS409", "NKS696", "RPA4271", "SWA262", "SWA797", "TAI560", "UAL1003", "UAL104", "UAL1122", "UAL1203", "UAL1536", "UAL1608", "UAL1944", "UAL1947", "UAL1975", "UAL2237", "UAL50", "UAL860", "UAL881", "UAL907", "UAL934", "UAL952", "UAL972", "UAL98", "UAL987", "VRD1905", "VRD34", "VRD415", "VRD720", "VRD948"]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 706,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 4422,
                                    "detail-type": "term",
                                    "max-length": 7,
                                    "char-freq-histogram": null,
                                    "min-length": 4
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["AAL486", "N626JS", "VRD1905", "DAL65", "FFT612", "AAL185", "UAL1975", "UAL1608", "SWA797", "AAL2455", "JBU101", "UAL98", "TAI560", "AAL650", "UAL1536", "DAL72", "DAL28", "VRD948", "ASA552", "AAL603", "N535BC", "DAL1896", "ASA384", "VRD720", "UAL881", "DAL585", "VRD415", "JBU19", "ABX2201", "AAL490", "DAL431", "UAL907", "UAL972", "UAL860", "DAL52", "AAL9768", "JBU1248", "DAL1172", "UAL1122", "UAL987", "DAL400", "DAL410", "UAL50", "UAL104", "UAL952", "FFT1478", "JBU624", "HAL5", "AAL102", "UAL1003"]
                            },
                            "vehicle_positions.trip_id": {
                                "used-in-schema": false,
                                "detailAvg": "21",
                                "detailMin": "21",
                                "main-type": "string",
                                "matching-fields": [{
                                    "confidence": 99,
                                    "matching-field": "vehicle_positions.trip_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "cluster_id"
                                }, {
                                    "confidence": 93,
                                    "matching-field": "vehicle_positions.aircraft.flight_id"
                                }, {
                                    "confidence": 89,
                                    "matching-field": "vehicle_positions.tailnumber"
                                }],
                                "detailStdDev": "2.7600232846582933",
                                "original-name": null,
                                "merged-into-schema": false,
                                "mainType": "string",
                                "detailNumDistinct": "74",
                                "detailType": "term",
                                "alias-names": null,
                                "interpretation": {
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                },
                                "display-name": "trip_id",
                                "struc-type": null,
                                "detailMax": "33",
                                "attributes": {
                                    "identifier": "Unknown",
                                    "categorical": "Unknown",
                                    "quantitative": "Unknown",
                                    "relational": "Unknown",
                                    "ordinal": "Unknown"
                                },
                                "detail": {
                                    "average-length": 30.473684210526315,
                                    "std-dev-length": 2.7600232846582933,
                                    "freq-histogram": {
                                        "data": [1, 1, 1, 4, 1, 1, 4, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 5, 4, 1, 1, 1, 1, 2, 1, 1, 1, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 2, 3, 4, 2, 1, 3, 1, 1, 4, 1, 1, 1, 3, 1, 1, 2, 1, 1, 2, 1, 1],
                                        "region-data": null,
                                        "series": "Values",
                                        "type": "bar",
                                        "yaxis": "Frequency",
                                        "long-labels": ["AAL102-1442104232-airline-0018", "AAL1107-1442052000-schedule-0004", "AAL1283-1442105972-airline-0124", "AAL1488-1442052000-schedule-0001", "AAL154D-1442174925-104-0", "AAL185-1442021400-schedule-0000", "AAL205-1442052000-schedule-0000", "AAL219-1442105972-airline-0020", "AAL2455-1442026200-schedule-0001", "AAL486-1442022000-schedule-0000", "AAL490-1442019300-schedule-0000", "AAL603-1442027400-schedule-0000", "AAL650-1442121051-77-0", "AAL705-1442048100-schedule-0000:0", "AAL751-1442047500-schedule-0001:0", "AAL787-1442048400-schedule-0000:0", "AAL9768-1442186242-422-0", "ABX2201-1442202969-14-0", "ASA384-1442162853-airline-0121", "ASA552-1442026800-schedule-0000", "ASH3802-1442035474-airline-0046", "DAL1172-1442023500-schedule-0000", "DAL1896-1442025960-schedule-0000", "DAL2521-1442124516-airline-0032", "DAL28-1442013780-schedule-0000:2", "DAL37-1442023800-schedule-0000:0", "DAL400-1442014740-schedule-0000:0", "DAL410-1442014320-schedule-0000:0", "DAL431-1442010600-schedule-0002", "DAL52-1442017500-schedule-0000", "DAL585-1442101843-airline-0123", "DAL65-1442013900-schedule-0000", "DAL72-1442008380-schedule-0000:0", "DAL99-1442047800-schedule-0000:0", "FFT1478-1442037716-airline-0143", "FFT612-1442037642-airline-0084", "FFT627-1442052000-schedule-0001", "HAL5-1442020500-schedule-0000", "JBU101-1442015520-schedule-0000", "JBU1248-1442123148-12-0", "JBU19-1442204799-62-0", "JBU624-1442125854-63-1", "N535BC-1442207829-65-0", "N626JS-1442177358-dlad-3921122:0", "NKS409-1442052900-schedule-0005", "NKS696-1442031460-airline-0023", "RPA4271-1442051100-schedule-0000", "SWA262-1442052000-schedule-0000", "SWA797-1442026200-schedule-0000", "TAI560-1442020200-schedule-0001:0", "UAL1003-1442036600-airline-0118", "UAL104-1442006400-schedule-0000:0", "UAL1122-1441963660-airline-0057", "UAL1203-1442056890-airline-0189", "UAL1536-1442063696-airline-0053", "UAL1608-1441958024-airline-0018", "UAL1944-1442050185-airline-0098", "UAL1947-1442050185-airline-0080", "UAL1975-1441945870-airline-0119", "UAL2237-1442165272-51-0", "UAL50-1442014500-schedule-0000:0", "UAL860-1441948802-airline-0128", "UAL881-1441949447-airline-0061", "UAL907-1441949447-airline-0019:0", "UAL934-1442019300-schedule-0000:0", "UAL952-1442011500-schedule-0001:2", "UAL972-1442012400-schedule-0000:0", "UAL98-1442063714-airline-0073:1", "UAL987-1442013600-schedule-0000:0", "VRD1905-1442025300-schedule-0000", "VRD34-1442124954-194-1", "VRD415-1442016300-schedule-0000", "VRD720-1442019000-schedule-0000", "VRD948-1442030100-schedule-0000"],
                                        "exampleValues": null,
                                        "labels": ["AAL102-14421042...", "AAL1107-1442052...", "AAL1283-1442105...", "AAL1488-1442052...", "AAL154D-1442174...", "AAL185-14420214...", "AAL205-14420520...", "AAL219-14421059...", "AAL2455-1442026...", "AAL486-14420220...", "AAL490-14420193...", "AAL603-14420274...", "AAL650-14421210...", "AAL705-14420481...", "AAL751-14420475...", "AAL787-14420484...", "AAL9768-1442186...", "ABX2201-1442202...", "ASA384-14421628...", "ASA552-14420268...", "ASH3802-1442035...", "DAL1172-1442023...", "DAL1896-1442025...", "DAL2521-1442124...", "DAL28-144201378...", "DAL37-144202380...", "DAL400-14420147...", "DAL410-14420143...", "DAL431-14420106...", "DAL52-144201750...", "DAL585-14421018...", "DAL65-144201390...", "DAL72-144200838...", "DAL99-144204780...", "FFT1478-1442037...", "FFT612-14420376...", "FFT627-14420520...", "HAL5-1442020500...", "JBU101-14420155...", "JBU1248-1442123...", "JBU19-144220479...", "JBU624-14421258...", "N535BC-14422078...", "N626JS-14421773...", "NKS409-14420529...", "NKS696-14420314...", "RPA4271-1442051...", "SWA262-14420520...", "SWA797-14420262...", "TAI560-14420202...", "UAL1003-1442036...", "UAL104-14420064...", "UAL1122-1441963...", "UAL1203-1442056...", "UAL1536-1442063...", "UAL1608-1441958...", "UAL1944-1442050...", "UAL1947-1442050...", "UAL1975-1441945...", "UAL2237-1442165...", "UAL50-144201450...", "UAL860-14419488...", "UAL881-14419494...", "UAL907-14419494...", "UAL934-14420193...", "UAL952-14420115...", "UAL972-14420124...", "UAL98-144206371...", "UAL987-14420136...", "VRD1905-1442025...", "VRD34-144212495...", "VRD415-14420163...", "VRD720-14420190...", "VRD948-14420301..."]
                                    },
                                    "walkingCount": 114,
                                    "walkingSum": 3474,
                                    "num-distinct-values": "74",
                                    "walkingSquareSum": 106734,
                                    "detail-type": "term",
                                    "max-length": 33,
                                    "char-freq-histogram": null,
                                    "min-length": 21
                                },
                                "presence": 1,
                                "interpretations": [{
                                    "iId": null,
                                    "interpretation": "Unknown",
                                    "iDomainId": null,
                                    "iName": "Unknown",
                                    "iMatchingNames": null,
                                    "iDescription": null,
                                    "iScript": null,
                                    "iConstraints": null
                                }],
                                "exampleValues": ["AAL486-1442022000-schedule-0000", "N626JS-1442177358-dlad-3921122:0", "VRD1905-1442025300-schedule-0000", "DAL65-1442013900-schedule-0000", "FFT612-1442037642-airline-0084", "AAL185-1442021400-schedule-0000", "UAL1975-1441945870-airline-0119", "UAL1608-1441958024-airline-0018", "SWA797-1442026200-schedule-0000", "AAL2455-1442026200-schedule-0001", "JBU101-1442015520-schedule-0000", "UAL98-1442063714-airline-0073:1", "TAI560-1442020200-schedule-0001:0", "AAL650-1442121051-77-0", "UAL1536-1442063696-airline-0053", "DAL72-1442008380-schedule-0000:0", "DAL28-1442013780-schedule-0000:2", "VRD948-1442030100-schedule-0000", "ASA552-1442026800-schedule-0000", "AAL603-1442027400-schedule-0000", "N535BC-1442207829-65-0", "DAL1896-1442025960-schedule-0000", "ASA384-1442162853-airline-0121", "VRD720-1442019000-schedule-0000", "UAL881-1441949447-airline-0061", "DAL585-1442101843-airline-0123", "VRD415-1442016300-schedule-0000", "JBU19-1442204799-62-0", "ABX2201-1442202969-14-0", "AAL490-1442019300-schedule-0000", "DAL431-1442010600-schedule-0002", "UAL907-1441949447-airline-0019:0", "UAL972-1442012400-schedule-0000:0", "UAL860-1441948802-airline-0128", "DAL52-1442017500-schedule-0000", "AAL9768-1442186242-422-0", "JBU1248-1442123148-12-0", "DAL1172-1442023500-schedule-0000", "UAL1122-1441963660-airline-0057", "UAL987-1442013600-schedule-0000:0", "DAL400-1442014740-schedule-0000:0", "DAL410-1442014320-schedule-0000:0", "UAL50-1442014500-schedule-0000:0", "UAL104-1442006400-schedule-0000:0", "UAL952-1442011500-schedule-0001:2", "FFT1478-1442037716-airline-0143", "JBU624-1442125854-63-1", "HAL5-1442020500-schedule-0000", "AAL102-1442104232-airline-0018", "UAL1003-1442036600-airline-0118"]
                            }
                        },
                        "dsFileType": "application/json",
                        "data-sample-id": 5,
                        "dsLastUpdate": null,
                        "dsDescription": null,
                        "dsFileName": "vehicle_position_cluster_schema_examples-2-US-Only.txt",
                        "dsVersion": null,
                        "dsFileSize": 0,
                        "dsStructuredProfile": [
                            {
                                "path": "cluster_id",
                                "field": "cluster_id",
                                "children": [],
                                "id": 1
                            }, {
                                "path": "cluster_size",
                                "field": "cluster_size",
                                "children": [],
                                "id": 2
                            }, {
                                "path": "vehicle_positions",
                                "field": "vehicle_positions",
                                "children": [{
                                    "path": "vehicle_positions.lat",
                                    "field": "lat",
                                    "children": [],
                                    "id": 4
                                }, {
                                    "path": "vehicle_positions.tailnumber",
                                    "field": "tailnumber",
                                    "children": [],
                                    "id": 5
                                }, {
                                    "path": "vehicle_positions.long",
                                    "field": "long",
                                    "children": [],
                                    "id": 6
                                }, {
                                    "path": "vehicle_positions.speed",
                                    "field": "speed",
                                    "children": [],
                                    "id": 7
                                }, {
                                    "path": "vehicle_positions.timestamp",
                                    "field": "timestamp",
                                    "children": [],
                                    "id": 8
                                }, {
                                    "path": "vehicle_positions.heading",
                                    "field": "heading",
                                    "children": [],
                                    "id": 9
                                }, {
                                    "path": "vehicle_positions.vehicle_type",
                                    "field": "vehicle_type",
                                    "children": [],
                                    "id": 10
                                }, {
                                    "path": "vehicle_positions.trip_id",
                                    "field": "trip_id",
                                    "children": [],
                                    "id": 11
                                }, {
                                    "path": "vehicle_positions.aircraft",
                                    "field": "aircraft",
                                    "children": [{
                                        "path": "vehicle_positions.aircraft.air_ground",
                                        "field": "air_ground",
                                        "children": [],
                                        "id": 13
                                    }, {
                                        "path": "vehicle_positions.aircraft.alt",
                                        "field": "alt",
                                        "children": [],
                                        "id": 14
                                    }, {
                                        "path": "vehicle_positions.aircraft.flight_id",
                                        "field": "flight_id",
                                        "children": [],
                                        "id": 15
                                    }],
                                    "id": 12
                                }
                                ],
                                "id": 3
                            }],
                        "dsExtractedContentDir": "C:\\Users\\leegc\\AppData\\Local\\Temp\\temp3057174123149834262\\vehicle_position_cluster_schema_examples-2-US-Only-embedded"
                    }
                ];

            $scope.sortDataSamples = function (obj) {
                var sorting_array = [];
                for (var key in obj) {
                    if (obj.hasOwnProperty(key)) {
                        keyLowerCase = (key['toLowerCase'] ? key.toLowerCase() : key);
                        sorting_array.push({"sortKey": keyLowerCase, "originalKey": key});
                    }
                }
                function compare(a, b) {
                    if (a.sortKey < b.sortKey)
                        return -1;
                    if (a.sortKey > b.sortKey)
                        return 1;
                    return 0;
                }

                sorting_array.sort(compare);
                //console.log(sorting_array);
                var sorted_obj = {};
                for (var i = 0; i < sorting_array.length; i++) {
                    //console.log(sorting_array[i]["sortKey"]);
                    //console.log(sorting_array[i]["originalKey"]);
                    //console.log(obj[sorting_array[i]["originalKey"]]);
                    sorted_obj[sorting_array[i]["originalKey"]] = obj[sorting_array[i]["originalKey"]];
                }
                return sorted_obj;
            }; // sortDataSamples
            // sort the data sample profile keys
            for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                $scope.model.dataSamples[i].dsProfile = $scope.sortDataSamples($scope.model.dataSamples[i].dsProfile);
            }
            // save the dataSamples for access via utils
            Utils.setDataSamples($scope.model.dataSamples);
            Utils.setDataSamplesBackup($scope.model.dataSamples);
            console.info("Utils.getDataSamples()");
            console.log(Utils.getDataSamples());

            $scope.model.treeTable = {};
            $scope.confidenceThreshold = matchConfidenceThreshold;
            $scope.interpretationMatch = defaultInterpretationMatch;

            $scope.confidenceValues = {
                selectedConfidenceValue: $scope.confidenceThreshold.toString(),
                availableValues: []
            };
            for (var i = 100; i > 80; i--) {
                $scope.confidenceValues.availableValues.push({value: i});
            }

            $scope.TreeNode = function (name, displayName, strucType) {
                this.field = name;
                this.displayName = displayName;
                this.strucType = strucType;
                this.children;

                //TODO: these methods aren't used
                this.setStrucType = function (sType) {
                    this.strucType = sType;
                };
                this.getStrucType = function () {
                    return this.strucType;
                };
                this.addChild = function (tn) {
                    if (!this.children) this.children = [];
                    this.children.push(tn);
                };
                this.getChildren = function () {
                    return this.children;
                };
            }; // TreeNode

            $scope.buildMatchingNames = function () {
                console.info("buildMatchingNames");
                //console.log("$scope.confidenceThreshold: " + $scope.confidenceThreshold);
                for (var i = 0, ilen = $scope.model.dataSamples.length; i < ilen; i++) {
                    for (var key in $scope.model.dataSamples[i].dsProfile) {
                        var dsProfileObj = $scope.model.dataSamples[i].dsProfile[key];
                        if (dsProfileObj['matching-fields'].length > 0) {
                            dsProfileObj['matching-names'] = {
                                "availableOptions": [],
                                "selectedOption": null
                            };
                            // push the identity element so undo's can be performed
                            dsProfileObj['matching-names'].availableOptions.push(
                                {"id": 0, "name": key});
                            for (j = 0, jlen = dsProfileObj['matching-fields'].length; j < jlen; j++) {
                                //console.log(dsProfileObj['matching-fields'][j]['confidence']);
                                //console.log(dsProfileObj['matching-fields'][j]['matching-field']);
                                if (j == 0 &&
                                    dsProfileObj['matching-fields'][j]['confidence'] >= $scope.confidenceThreshold) {
                                    dsProfileObj['matching-names'].selectedOption =
                                        dsProfileObj['matching-fields'][j]['matching-field'];
                                }
                                dsProfileObj['matching-names'].availableOptions.push(
                                    {
                                        "id": j + 1,
                                        "name": dsProfileObj['matching-fields'][j]['matching-field'] +
                                        ':' +
                                        dsProfileObj['matching-fields'][j]['confidence'] +
                                        "__"
                                    }
                                );
                            }
                        }
                    }
                }
                //console.log("buildMatchingNames $scope.model.dataSamples");
                //console.log(angular.copy($scope.model.dataSamples));
            }; //buildMatchingNames

            $scope.insertTreeTableData = function (ttd, tn, qName) {
                var thisLevelContains = function (arr, el) {
                    for (var i = 0, len = arr.length; i < len; i++) {
                        if (arr[i].field == el.field) return true;
                    }
                    return false;
                };
                //console.group("insertTreeTableData qName: " + qName);
                var qualifiers = qName.split('.');
                var qualifiersHead = qualifiers[0];
                var found = false;
                for (var i = 0, len = ttd.length; i < len; i++) {
                    var fieldQualifiers = ttd[i]['field'].split('.');
                    var lastQualifierInField = fieldQualifiers[fieldQualifiers.length - 1];
                    if (lastQualifierInField == qualifiersHead) {
                        var qNameTail = qName.substr(qualifiersHead.length + 1);
                        if (qNameTail != "") {
                            if (!ttd[i].children) ttd[i].children = [];
                            $scope.insertTreeTableData(ttd[i].children, tn, qNameTail);
                            found = true;
                            break;
                        }
                    }
                }
                // add if not a duplicate
                if (!found && !thisLevelContains(ttd, tn)) {
                    ttd.push(tn);
                }
                //console.info("ttd");
                //console.log(angular.copy(ttd));
                console.groupEnd();
            }; // insertTreeTableData

            $scope.buildTreeTableData = function () {
                console.group("buildTreeTableData");
                $scope.model.dataSamples = Utils.getDataSamples();
                $scope.model.treeTable.data = [];
                var existingProperties = {};
                for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                    //console.log("");
                    console.info("$scope.model.dataSamples[" + i + "].dsProfile");
                    //console.log($scope.model.dataSamples[i].dsProfile);
                    for (property in $scope.model.dataSamples[i].dsProfile) {
                        console.log(property);
                        var tn;
                        var matchedProperty = undefined;
                        if ($scope.model.dataSamples[i].dsProfile[property]['matching-names'] &&
                            $scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption) {
                            if (existingProperties.hasOwnProperty($scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption)) {
                                matchedProperty = $scope.model.dataSamples[i].dsProfile[property]['matching-names'].selectedOption;
                            }
                        }
                        if (matchedProperty == property || matchedProperty == undefined) {
                            if (matchedProperty &&
                                $scope.model.dataSamples[i].dsProfile[property]['matching-names'] &&
                                $scope.model.dataSamples[i].dsProfile[property]['matching-names'][0] &&
                                $scope.model.dataSamples[i].dsProfile[property]['matching-names'][0][id] == 0) {
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = true;
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = false;
                            } else if (matchedProperty) {
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = false;
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = true;
                            } else {
                                if (existingProperties.hasOwnProperty(property)) {
                                    $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = false;
                                    $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = true;
                                } else {
                                    $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = true;
                                    $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = false;
                                }
                            }
                            if (!existingProperties.hasOwnProperty(property) &&
                                !$scope.model.dataSamples[i].dsProfile[property]['original-name']) {
                                existingProperties[property] = $scope.model.dataSamples[i].dsProfile[property];
                            }
                            tn = new $scope.TreeNode(
                                property,
                                $scope.model.dataSamples[i].dsProfile[property]['display-name'],
                                $scope.model.dataSamples[i].dsProfile[property]['struc-type']);
                        } else if (matchedProperty) {
                            // check whether a property whose name matches a matching name has already been processed
                            console.info("got a match\t" + matchedProperty + " with " + property);
                            if ($scope.propSel) delete $scope.propSel;
                            if ($scope.matchSel) delete $scope.matchSel;
                            $scope.propSel = $scope.model.dataSamples[i].dsProfile[property].interpretations.selectedOption;
                            $scope.matchSel = existingProperties[matchedProperty].interpretations.selectedOption;
                            if ($scope.propSel !== $scope.matchSel && $scope.interpretationMatch) {
                                $scope.model.dataSamples[i].dsProfile[property]['used-in-schema'] = true;
                                $scope.model.dataSamples[i].dsProfile[property]['merged-into-schema'] = false;
                                tn = new $scope.TreeNode(
                                    property,
                                    $scope.model.dataSamples[i].dsProfile[property]['display-name'],
                                    $scope.model.dataSamples[i].dsProfile[property]['struc-type']);
                            } else {
                                $scope.model.dataSamples[i].dsProfile[matchedProperty] =
                                    angular.copy($scope.model.dataSamples[i].dsProfile[property]);
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['matching-names'].selectedOption =
                                    {"id": 1, "name": matchedProperty};
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['original-name'] = property;
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['used-in-schema'] = false;
                                $scope.model.dataSamples[i].dsProfile[matchedProperty]['merged-into-schema'] = true;
                                delete $scope.model.dataSamples[i].dsProfile[property];
                                tn = new $scope.TreeNode(
                                    matchedProperty,
                                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['display-name'],
                                    $scope.model.dataSamples[i].dsProfile[matchedProperty]['struc-type']);
                            }
                        }
                        $scope.insertTreeTableData($scope.model.treeTable.data, tn, property);
                    }
                }
                console.log("existingProperties");
                console.log(existingProperties);
                // wrap model.treeTable.data with a root node
                $scope.model.treeTable.data =
                    [{
                        "field": "/",
                        "displayName": "/",
                        "strucType": "object",
                        "children": $scope.model.treeTable.data
                    }];
                //console.info("$scope.model.treeTable.data");
                //console.log($scope.model.treeTable.data);
                console.groupEnd();
            }; // buildTreeTableData

            $scope.buildColumns = function () {
                // NOTE: Directives must be named with all lowercase characters and no punctuation.
                //       This limitation arises because AngularJS requires hyphenated names as attributes
                //       and camel case for the directive name. This workaround eliminates this problem.
                $scope.model.columns = [
                    {
                        'complexCell': 'true',
                        'property1': 'displayName',
                        'property2': 'strucType',
                        'name': 'Schema Property',
                        'callback': 'doCallBack',
                        'tree': {
                            'directive': 'schemaproperty',
                            'data': $scope.model.properties
                        }
                    }
                ];
                for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                    $scope.model.columns.push(
                        {
                            'complexCell': 'true',
                            'property1': 'field',
                            'name': $scope.model.dataSamples[i].dsName,
                            'callback': 'doCallBack',
                            'table': {
                                'directive': 'matchingproperty',
                                'data': i
                            }
                        }
                    )
                }
            }; // buildColumns

            $scope.encodeCellData = function (index, last) {
                //console.info("encodeCellData");
                // data objects must be passed through the layers of directives as encoded JSON strings
                // decoding and restoration as javascript objects must occur in the directive
                for (var i = 0; i < $scope.model.columns.length; i++) {
                    if ($scope.model.columns[i].tree && $scope.model.columns[i].tree.data) {
                        $scope.model.columns[i].tree.data = encodeURI(angular.toJson($scope.model.columns[i].tree.data));
                    }
                    if ($scope.model.columns[i].table && $scope.model.columns[i].table.data) {
                        $scope.model.columns[i].table.data = encodeURI(angular.toJson($scope.model.columns[i].table.data));
                    }
                }
            }; // encodeCellData

            $scope.getColumnId = function (index, last) {
                //console.log(index + "\t" + last);
                if (last) {
                    return 'colN';
                } else {
                    return 'col' + index;
                }
            }; // getColumnId

            $scope.scrollTreeTable = function (scrollAmount) {
                var sTop = document.getElementById('colN').scrollTop -= scrollAmount;
                for (var i = 0; i < $scope.model.columns.length - 1; i++) {
                    document.getElementById('col' + i).scrollTop = sTop;
                }
            }; // scrollTreeTable

            // Chrome & IE
            angular.element($window).bind('mousewheel', function (event) {
                //console.log(event.originalEvent.wheelDelta);
                $scope.scrollTreeTable(event.originalEvent.wheelDelta / 4);
                event.preventDefault();
                event.stopImmediatePropagation();
            });

            // Firefox
            angular.element($window).bind('DOMMouseScroll', function (event) {
                //console.log(event.originalEvent.detail);
                $scope.scrollTreeTable(event.originalEvent.detail * -10);
                event.preventDefault();
                event.stopImmediatePropagation();
            });

            // wait for grid to initialize then add listener for scrolling
            $timeout(function () {
                    document.getElementById('colN').addEventListener('scroll', function (event) {
                        //console.log(event);
                        $scope.scrollTreeTable(0);
                        event.preventDefault();
                        event.stopImmediatePropagation();
                    })
                },
                1000);

            $scope.rebuildModel = function () {
                //console.info("rebuildModel");
                $scope.model.treeTable.lookupTable = [];
                var idNum = 0;
                var traverse = function (subtree, path, parentId, childNum) {
                    subtree.id = ++idNum;
                    subtree.path = path;
                    subtree.parentId = parentId;
                    subtree.childNum = childNum;
                    $scope.model.treeTable.lookupTable.push(subtree);
                    if (subtree.hasOwnProperty('children')) {
                        parentId = idNum;
                        for (var i = 0, len = subtree.children.length; i < len; i++) {
                            traverse(subtree.children[i], subtree.path + '.children[' + childNum + ']', parentId, i);
                        }
                    }
                };
                $scope.model.treeTable.data[0].id = 0;
                $scope.model.treeTable.data[0].path = "$scope.model.treeTable.data[0]";
                $scope.model.treeTable.data[0].parentId = -1;
                $scope.model.treeTable.data[0].childNum = -1;
                $scope.model.treeTable.lookupTable.push($scope.model.treeTable.data[0]);
                for (var i = 0, len = $scope.model.treeTable.data[0].children.length; i < len; i++) {
                    traverse($scope.model.treeTable.data[0].children[i], "$scope.model.treeTable.data[0]", 0, i);
                }
                //console.info("$scope.model.treeTable.data");
                //console.log($scope.model.treeTable.data);
                //$scope.showLookupTable();
            }; // rebuildModel

            $scope.deleteFieldInModel = function (deletionIndex, rebuildAfter) {
                console.info("deleteFieldInModel: " + deletionIndex);
                eval($scope.model.treeTable.lookupTable[deletionIndex].path).children
                    .splice(eval($scope.model.treeTable.lookupTable[deletionIndex].childNum), 1);
                if (rebuildAfter) $scope.rebuildModel();
            }; // deleteFieldInModel

            $scope.showLookupTable = function () {
                console.info("$scope.model.treeTable.data");
                console.log($scope.model.treeTable.data);
                console.log("$scope.model.treeTable.lookupTable");
                console.log("field      id  pId cId  path");
                for (var i = 0, len = $scope.model.treeTable.lookupTable.length; i < len; i++) {
                    console.log(($scope.model.treeTable.lookupTable[i].field + "          ").substr(0, 10) + "\t" +
                        $scope.model.treeTable.lookupTable[i].id + "\t" +
                        $scope.model.treeTable.lookupTable[i].parentId + "\t" +
                        $scope.model.treeTable.lookupTable[i].childNum + "\t" +
                        $scope.model.treeTable.lookupTable[i].path + "\t" +
                        "");
                }
            }; // showLookupTable

            $scope.dragStart = function ($event, parms) {
                console.info("dragStart " + angular.toJson(parms));
                //console.log("$event");
                //console.log($event);
                $scope.draggedNodeIndex = parms.index;
            }; // dragStart

            $scope.dragDrop = function ($event, parms) {
                console.info("dragDrop " + angular.toJson(parms));
                //console.info("$event");
                //console.log($event);
                console.log("$event.shiftKey: " + $event.shiftKey);
                console.log("$event.ctrlKey: " + $event.ctrlKey);

                var retcode = false;

                // don't allow the root node to be dropped
                if ($scope.draggedNodeIndex == 0) return retcode;

                $scope.droppedOnNodeIndex = parms.index;
                console.info("$scope.droppedOnNodeIndex: " + $scope.droppedOnNodeIndex);

                // don't allow the node to be dropped on itself
                if ($scope.draggedNodeIndex == $scope.droppedOnNodeIndex) return retcode;

                var mergeNodes = function (droppedOnNode, draggedNodeCopy) {
                    //$scope.findFieldInModel(draggedNodeCopy.id);
                    //$scope.findFieldInModel(droppedOnNodeCopy.id);
                    //console.log("droppedOnNode.children[childNum].field: " +
                    //            droppedOnNode.children[childNum].field);
                    droppedOnNode.children[childNum].field = draggedNodeCopy.field + " & " + droppedOnNode.children[childNum].field;
                    for (var i = 0, len = draggedNodeCopy.children.length; i < len; i++) {
                        droppedOnNode.children[childNum].children.push(angular.copy(draggedNodeCopy.children[i]));
                    }
                    ;
                };

                var draggedNodeCopy = angular.copy($scope.model.treeTable.lookupTable[$scope.draggedNodeIndex]);
                var droppedOnNodeCopy = angular.copy($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex]);

                var droppedOnNode = eval($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex].path);
                var childNum = eval($scope.model.treeTable.lookupTable[$scope.droppedOnNodeIndex].childNum);
                var emptyNode = {"displayName": "???", "strucType": "object", "children": []};
//TODO: does emptyNode need to be a TreeNode?
//                var emptyNode = new $scope.TreeNode("placeholder", "???", "object");

                if ($scope.droppedOnNodeIndex == 0) { // move draggedNode to root
                    droppedOnNode.children.push(draggedNodeCopy);
                } else if (droppedOnNode.children[childNum].children) { // droppedOnNode has children
                    if ($event.ctrlKey && draggedNodeCopy.children) { // draggedNode is moved and merged with droppedOnNode
                        mergeNodes(droppedOnNode, draggedNodeCopy);
                        retcode = true;
                    } else { // draggedNode becomes child of droppedOnNode
                        droppedOnNode.children[childNum].children.push(draggedNodeCopy);
                    }
                } else { // draggedNode and droppedOnNode become children of newly created parent node
                    droppedOnNode.children[childNum] = emptyNode;
                    droppedOnNode.children[childNum].children.push(draggedNodeCopy);
                    droppedOnNode.children[childNum].children.push(droppedOnNodeCopy);
                }

                // THE ORDER OF THE REMAINING OPERATIONS IS CRITICAL
                // DO NOT REARRANGE THEM NO MATTER HOW UNINTRUSIVE YOU THINK THAT MIGHT BE
                // copy potentially empty structure node
                var draggedNodeParent = angular.copy(eval($scope.model.treeTable.lookupTable[draggedNodeCopy.parentId]));
                // delete original copy of dragged node
                $scope.deleteFieldInModel($scope.draggedNodeIndex, false);
                // if the remaining child is the one dragged then delete the empty parent structure
                if ((draggedNodeParent.strucType == "object" || draggedNodeParent.strucType == "array") &&
                    draggedNodeParent.children && draggedNodeParent.children.length == 1) {
                    $scope.deleteFieldInModel(draggedNodeParent.id, false);
                }

                // refresh structures
                $scope.rebuildModel();

                return retcode;
            }; // dragDrop

            $scope.changeNodeLabel = function (parms) {
                console.info("changeNodeLabel " + angular.toJson(parms));
                var changeNode = eval($scope.model.treeTable.lookupTable[parms.index]);
                changeNode.field = parms.newName;
                /*TODO: correct?*/
                changeNode['displayName'] = parms.newName;
                $scope.rebuildModel();
            }; // changeNodeLabel

            $scope.changeNodeStruc = function (parms) {
                console.info("changeNodeStruc " + angular.toJson(parms));

                // don't allow the root node to be changed
                if (parms.index == 0) return;

                var changeNode = eval($scope.model.treeTable.lookupTable[parms.index]);
                if (parms.currentStruc == 'object') {
                    changeNode.strucType = 'array';
                } else if (parms.currentStruc == 'array') {
                    if (parms.hasChildren) {
                        changeNode.strucType = 'object';
                    } else {
                        changeNode.strucType = undefined;
                    }
                } else if (changeNode.mainType == undefined) {
                    changeNode.strucType = 'object';
                } else {
                    changeNode.strucType = 'array';
                }
                $scope.rebuildModel();
            }; // changeNodeStruc

            $scope.previouslyShownInDetails1;
            $scope.showInDetails1 = function ($event, parms) {
                console.info("showInDetails1");
                console.log(parms['dataSource'].dsProfile[parms['property']]);
                if ($scope.previouslyShownInDetails1) $scope.previouslyShownInDetails1['shown-in-details1'] = false;
                parms['dataSource'].dsProfile[parms['property']]['shown-in-details1'] = true;
                $scope.previouslyShownInDetails1 = parms['dataSource'].dsProfile[parms['property']];
            }; // showInDetails1

            $scope.previouslyShownInDetails2;
            $scope.showInDetails2 = function ($event, parms) {
                console.info("showInDetails2");
                console.log(parms['dataSource'].dsProfile[parms['property']]);
                if ($scope.previouslyShownInDetails2) $scope.previouslyShownInDetails2['shown-in-details2'] = false;
                parms['dataSource'].dsProfile[parms['property']]['shown-in-details2'] = true;
                $scope.previouslyShownInDetails2 = parms['dataSource'].dsProfile[parms['property']];
            }; // showInDetails2

            $scope.changeInterpretation = function ($event, parms) {
                console.info("changeInterpretation");
                console.log(parms['dataSource']['dsName']);
                console.log(parms['property']);
                var splits = parms['dataSource'].dsProfile[parms['property']]['interpretations'].selectedOption['name'].split(':');
                console.log(splits[0]);
            }; // changeInterpretation

            $scope.changeMatchedProperty = function ($event, parms) {
                console.group("changeMatchedProperty");
                // find out which data sample this event refers to
                var dsIndex = -1;
                for (var i = 0, len = $scope.model.dataSamples.length; i < len; i++) {
                    if ($scope.model.dataSamples[i].dsName == parms['dataSource']['dsName']) {
                        dsIndex = i;
                        break;
                    }
                }
                //console.log("dsIndex: " + dsIndex);
                //console.log("parms['property']: " + parms['property']);
                var splits = parms['dataSource'].dsProfile[parms['property']]['matching-names'].selectedOption['name'].split(':');
                var matchedProperty = splits[0];
                var matchedConfidence = splits[1];
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty] =
                    angular.copy($scope.model.dataSamples[dsIndex].dsProfile[parms['property']]);
                $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['field'] = matchedProperty;
                if (!matchedConfidence) {
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['used-in-schema'] = true;
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['merged-into-schema'] = false;
                } else {
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['used-in-schema'] = false;
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['merged-into-schema'] = true;
                }

                // keep the display-name set to the original name
                if ($scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] != null) {
                    // returning a node to its original place so set original-name to original value which is null
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] = null;
                } else {
                    $scope.model.dataSamples[dsIndex].dsProfile[matchedProperty]['original-name'] = parms['property'];
                }
                delete $scope.model.dataSamples[dsIndex].dsProfile[parms['property']];
                Utils.setDataSamples($scope.model.dataSamples);

                $scope.buildTreeTableData();
                $scope.rebuildModel($scope.model.treeTable.data);
                console.groupEnd();
            }; // changeMatchedProperty

            $scope.doCallBack = function ($event, callback, parms) {
                console.info("doCallBack callback: " + callback);
                //console.log($event);
                //console.log("doCallBack parms: " + angular.toJson(parms));
                switch (callback) {
                    case "dragStart":
                        $scope.dragStart($event, parms);
                        break;
                    case "dragDrop":
                        return $scope.dragDrop($event, parms);
                        break;
                    case "changeNodeLabel":
                        $scope.changeNodeLabel(parms);
                        break;
                    case "showInDetails1":
                        $scope.showInDetails1($event, parms);
                        break;
                    case "showInDetails2":
                        $scope.showInDetails2($event, parms);
                        break;
                    case "changeInterpretation":
                        $scope.changeInterpretation($event, parms);
                        break;
                    case "changeMatchedProperty":
                        $scope.changeMatchedProperty($event, parms);
                        break;
                }
            }; // doCallBack

            $scope.init = function () {
//TODO: this needs to get passed $scope.modifySchemaMode = true;
                $scope.buildMatchingNames();
                $scope.buildTreeTableData();
                $scope.buildColumns();
                $scope.encodeCellData();
                $timeout($scope.rebuildModel, $scope.model.treeTable.data, 600);
            }; //init
            $scope.init();

            $scope.repeatMatching = function (interpretationMatch) {
                console.info("repeatMatching interpretationMatch: " + interpretationMatch);
                $scope.model.dataSamples = Utils.getDataSamplesBackup();
                Utils.setDataSamples($scope.model.dataSamples);
                $scope.confidenceThreshold = $scope.confidenceValues.selectedConfidenceValue;
                if (interpretationMatch) $scope.interpretationMatch = interpretationMatch;
                $scope.init();
            }; // repeatMatching

            $scope.removeDs = function (index) {
                console.info("removeDs index: " + index);
                //console.log($scope.model.dataSamples.length);
                $scope.model.dataSamples = Utils.getDataSamplesBackup();
                $scope.model.dataSamples.splice(index - 1, 1);
                //console.log($scope.model.dataSamples.length);
                Utils.setDataSamples($scope.model.dataSamples);
                Utils.setDataSamplesBackup(Utils.getDataSamplesBackup().splice(index - 1, 1));
                //console.log(Utils.getDataSamplesBackup());
                $scope.init();
            }; // removeDs
/* ***************************************** temporary methods ************************************************************************* */
            {
                //console.info("UUID: " + uuid.v4());

                $scope.findFieldInModel = function (lookupIndex) {
                    console.info("findFieldInModel");
                    console.log($scope.model.treeTable.lookupTable[lookupIndex]);
                    //console.log($scope.model.treeTable.data[0]);
                }; // findFieldInModel

                $scope.replaceFieldInModel = function (lookupIndex) {
                    console.info("replaceFieldInModel");
                    $scope.findFieldInModel(lookupIndex);
                    var parentNode = eval($scope.model.treeTable.lookupTable[lookupIndex].path);
                    var childNum = eval($scope.model.treeTable.lookupTable[lookupIndex].childNum);
                    var newNode = {"field": "???", "children": []};
                    parentNode.children[childNum] = newNode;
                    parentNode.children[childNum].children.push(angular.copy($scope.model.treeTable.lookupTable[3]));
                    parentNode.children[childNum].children.push(angular.copy($scope.model.treeTable.lookupTable[11]));
                    $scope.rebuildModel();
                }; // replaceFieldInModel

                $scope.addFieldToModel = function (lookupIndex) {
                    console.info("addFieldToModel");
                    $scope.findFieldInModel(lookupIndex);
                    var parentNode = eval($scope.model.treeTable.lookupTable[lookupIndex].path);
                    var childNum = eval($scope.model.treeTable.lookupTable[lookupIndex].childNum);
                    var newNode = {"field": "zeta", "mainType": "number", "detailType": "integer"};
                    if (parentNode.id == 0) {
                        parentNode.children.push(newNode);
                    } else if (!parentNode.children[childNum].children) {
                        parentNode.children[childNum].children = [];
                        $scope.findFieldInModel(lookupIndex);
                        parentNode.children[childNum].children.push(newNode);
                    }
                    $scope.rebuildModel();
                }; // addFieldToModel

                $scope.changeModel = function () {
                    $scope.model.treeTable.data =
                        [
                            {
                                "field": "alpha", "mainType": "~", "detailType": "~", "children": [
                                {"field": "a1", "mainType": "number", "detailType": "integer"},
                                {
                                    "field": "a2", "mainType": "number", "detailType": "integer", "children": [
                                    {"field": "a2a", "mainType": "number", "detailType": "integer"},
                                    {"field": "a2b", "mainType": "number", "detailType": "integer"}
                                ]
                                },
                                {"field": "a3", "mainType": "number", "detailType": "integer"}
                            ]
                            },
                            {"field": "beta", "mainType": "number", "detailType": "integer"},
                            {"field": "delta", "mainType": "number", "detailType": "integer"},
                            {"field": "epsilon", "mainType": "number", "detailType": "integer"}
                        ]
                    /*
                     { "field" : "gamma", "mainType" : "number", "detailType" : "integer", "children" : [
                     { "field" : "g1", "mainType" : "number", "detailType" : "integer" },
                     { "field" : "g2", "mainType" : "number", "detailType" : "integer" }
                     ] },
                     */
                    $scope.rebuildModel();
                }; // changeModel
            }
        }
	); // treeTableController

treeTableApp.directive('singleClick', ['$parse', '$timeout', function ($parse, $timeout) {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            var fn = $parse(attr['singleClick']);
            var clicks = 0, timer = null;
            element.on('click', function (event) {
                clicks++;  //count clicks
                if (clicks === 1) {
                    timer = $timeout(function () {
                        fn(scope, {$event: event});
                        clicks = 0;         //after action performed, reset counter
                    }, 300);
                } else {
                    $timeout.cancel(timer);
                    clicks = 0;             //after action performed, reset counter
                }
            });
        }
    };
}]); // singleClick
