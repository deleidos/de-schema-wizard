(function () {
    var schemaWizardApp = angular.module('schemaWizardApp');
    schemaWizardApp.directive('validNumber', function() {
        return {
            require: '?ngModel',
            link: function(scope, element, attrs, ngModelCtrl) {
                if(!ngModelCtrl) {
                    return;
                }

                ngModelCtrl.$parsers.push(function(val) {
                    if (angular.isUndefined(val)) {
                        var val = '';
                    }

                    var clean = val.replace(/[^-0-9\.]/g, '');
                    var negativeCheck = clean.split('-');
                    var decimalCheck = clean.split('.');
                    if(!angular.isUndefined(negativeCheck[1])) {
                        negativeCheck[1] = negativeCheck[1].slice(0, negativeCheck[1].length);
                        clean =negativeCheck[0] + '-' + negativeCheck[1];
                        if(negativeCheck[0].length >= 0) {
                            clean =negativeCheck[0];
                        }

                    }

                    if(!angular.isUndefined(decimalCheck[1])) {
                        decimalCheck[1] = decimalCheck[1].slice(0,2);
                        clean =decimalCheck[0] + '.' + decimalCheck[1];
                    }

                    if (val !== clean) {
                        ngModelCtrl.$setViewValue(clean);
                        ngModelCtrl.$render();
                    }
                    return clean;
                });

                element.bind('keypress', function(event) {
                    if(event.keyCode === 32) {
                        event.preventDefault();
                    }
                });
            }
        };
    });
    //end validation directive

    schemaWizardApp.directive('ngEnter', function($document) {
        return {
            scope: {
                ngEnter: "&"
            },
            link: function(scope, element, attrs) {
                var enterWatcher = function(event) {
                    if (event.which === 13) {
                        scope.ngEnter();
                        scope.$apply();
                        event.preventDefault();
                        $document.unbind("keydown keypress", enterWatcher);
                    }
                };
                $document.bind("keydown keypress", enterWatcher);
            }
        }
    });
    // end enter button modal directive
    schemaWizardApp.directive('modaldraggable', function ($document) {
        "use strict";
        return function (scope, element) {
            var startX = 0,
                startY = 0,
                x = 650,
                y = 0;
            element= angular.element(document.getElementsByClassName("modal-dialog"));
            console.log("added directive");
            element.css({
                position: 'fixed',
                cursor: 'move'
            });

            element.on('mousedown', function (event) {
                // Prevent default dragging of selected content
                event.preventDefault();
                startX = event.screenX - x;
                startY = event.screenY - y;
                $document.on('mousemove', mousemove);
                $document.on('mouseup', mouseup);
            });

            function mousemove(event) {
                y = event.screenY - startY;
                x = event.screenX - startX;
                element.css({
                    top: y + 'px',
                    left: x + 'px'
                });
            }

            function mouseup() {
                $document.unbind('mousemove', mousemove);
                $document.unbind('mouseup', mouseup);
            }
        };
    });
    // end draggable modal Directive

})();
