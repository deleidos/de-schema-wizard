(function ( angular ) {
	'use strict';

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive( 'simpleTreeGrid', ['$compile', function( $compile ) {
			return {
				restrict: 'A',
				link: function ( scope, element, attrs ) {
                    var simpleTreeGrid = attrs.simpleTreeGrid;
                    var simpleTreeDataId = attrs.simpleTreeDataId;
                    var linkData = attrs.linkData;
					var nodeId = attrs.nodeId || 'id';
                    var nodeLabel1 = attrs.nodeLabel1 || 'label1';
                    var nodeLabel2 = attrs.nodeLabel2 || null;
					var nodeChildren = attrs.nodeChildren || 'children';
                    var cellMinWidth = attrs.cellMinWidth;
                    var cellMinHeight = attrs.cellMinHeight || "20";
                    var callbackMethod = attrs.callbackMethod || '';
                    var tableCellDirective = attrs.tableCellDirective || 'tablecell';
                    var otherTableData = attrs.otherTableData || '';
                    var template =
                        '<div class="tree-table-row" ' +
                             'style="display: block; width: 100%;" ' +
                             'ng-style="!node.' + nodeChildren + ' && { \'min-height\': \'' + cellMinHeight + 'px\' } || { \'min-height\': \'27px\' }" ' +
                             'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }" ' +
                             'data-ng-repeat-start="node in ' + simpleTreeGrid + '">' +
                            '<div class="tree-table-cell" style="display: block; width: 100%;">' +
                                '<span style="display: block; width: ' + cellMinWidth + 'px; margin: 0px; padding-left: 2px; padding-right: 2px;"> ' +
                                    '<i class="tree-table-collapsed" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + simpleTreeDataId + '.expandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-expanded" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + simpleTreeDataId + '.expandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-normal" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
                                    '<' + tableCellDirective + ' node="{{node}}" ' +
                                                                'node-id="{{node.' + nodeId + '}}" ' +
                                                                'label1="' + nodeLabel1 + '" ' +
                                                                'label2="' + nodeLabel2 + '" ' +
                                                                'node-label1="{{node.' + nodeLabel1 + '}}" ' +
                                                                'node-label2="{{node.' + nodeLabel2 + '}}" ' +
                                                                'callback-method="' + callbackMethod + '" ' +
                                                                'data-other-table-data="' + otherTableData + '" ' +
                                                                'data-link-data="' + linkData + '" ' +
                                                                'data-ng-hide="node.' + nodeChildren + '.length"/> ' +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="tree-table-row" style="display: block; width: 100%;" data-ng-repeat-end>' +
                            '<div id="{{node.' + nodeId + '}}" ' +
                                 'class="tree-table-cell" style="display: block; width: 100%;" ' +
                                 'data-ng-hide="node.collapsed" ' +
                                 'data-node-id=' + nodeId + ' ' +
                                 'data-simple-tree-grid="node.' + nodeChildren + '" ' +
                                 'data-simple-tree-data-id="' + simpleTreeDataId + '" ' +
                                 'data-node-label1=' + nodeLabel1 + ' ' +
                                 'data-node-label2=' + nodeLabel2 + ' ' +
                                 'data-node-children=' + nodeChildren + ' ' +
                                 'data-cell-min-width="' + cellMinWidth + '" ' +
                                 'data-cell-min-height="' + cellMinHeight + '" ' +
                                 'data-callback-method="' + callbackMethod + '" ' +
                                 'data-table-cell-directive="' + tableCellDirective + '" ' +
                                 'data-other-table-data="' + otherTableData + '" ' +
                                 'data-link-data="' + linkData + '"> ' +
                            '</div>' +
                        '</div>';
					if( simpleTreeDataId && simpleTreeGrid ) {
                        element.html('').append( $compile( template )( scope ) );
					}
				}
			};
	}]);
})( angular );
