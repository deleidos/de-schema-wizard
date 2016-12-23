(function ( angular ) {
    'use strict';

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive( 'matchingTreeGrid', ['$compile', function( $compile ) {
			return {
				restrict: 'A',
				link: function ( scope, element, attrs ) {
                    var matchingTreeGrid = attrs.matchingTreeGrid;
                    var matchingTreeDataId = attrs.matchingTreeDataId;
					var nodeId = attrs.nodeId || 'id';
                    var nodeLabel1 = attrs.nodeLabel1 || 'label1';
                    var nodeLabel2 = attrs.nodeLabel2 || null;
					var nodeChildren = attrs.nodeChildren || 'children';
                    var depth = attrs.treeDepth;
                    var treeColumn = attrs.columnIsTree;
                    var cellMinWidth = attrs.cellMinWidth;
                    var cellMinHeight = attrs.cellMinHeight || "20";
                    var callbackMethod = attrs.callbackMethod || '';
                    var treeCellDirective = attrs.treeCellDirective || 'schemaproperty';
                    var otherTreeData = attrs.otherTreeData || '';
                    var tableCellDirective = attrs.tableCellDirective || 'matchingproperty';
                    var otherTableData = attrs.otherTableData || '';
                    var template =
                        '<div class="tree-table-row" ' +
                             'style="display: block; width: 100%;" ' +
                             'ng-style="!node.' + nodeChildren + ' && { \'min-height\': \'' + cellMinHeight + 'px\' } || { \'min-height\': \'27px\' }" ' +
                             'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }" ' +
                             'data-ng-repeat-start="node in ' + matchingTreeGrid + '">' +
                            '<div class="tree-table-cell" style="display: block; width: 100%;">' +
                                '<span ng-show="' + treeColumn + '" ' +
                                      'style="display: block;" ' +
                                      'ng-style="node.' + nodeId + ' % 2 == 1 && {\'padding-left\': \'' + (depth * 30) + 'px\'} || ' +
                                      'node.' + nodeId + ' % 2 == 0 && {\'padding-left\': \'' + (depth * 30) + 'px\'}"> ' +
                                    '<i class="tree-table-collapsed" ' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + matchingTreeDataId + '.explandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-expanded" ' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + matchingTreeDataId + '.explandCollapse($event, node)"></i>' +
                                    '<i class="normal" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
                                    '<' + treeCellDirective + ' node="{{node}}" ' +
                                                               'node-id="{{node.' + nodeId + '}}"' +
                                                               'node-label1="{{node.' + nodeLabel1 + '}}" ' +
                                                               'node-label2="{{node.' + nodeLabel2 + '}}" ' +
                                                               'callback-method="' + callbackMethod + '" ' +
                                                               'data="' + otherTreeData + '" ' +
                                                               'data-cell-min-width="' + cellMinWidth + '" ' +
                                                               'data-cell-min-height="' + cellMinHeight + '"/> ' +
                                '</span>' +
                                '<span ng-hide="' + treeColumn + '" ' +
                                      'style="display: block; width: ' + cellMinWidth + 'px; margin: 0px; padding-left: 2px; padding-right: 2px;"> ' +
                                    '<i class="tree-table-collapsed" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + matchingTreeDataId + '.explandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-expanded" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + matchingTreeDataId + '.explandCollapse($event, node)"></i>' +
                                    '<i class="normal" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
                                    '<' + tableCellDirective + ' data-ng-hide="node.' + nodeChildren + '.length" ' +
                                                                'node="{{node}}" ' +
                                                                'node-id="{{node.' + nodeId + '}}"' +
                                                                'node-label1="{{node.' + nodeLabel1 + '}}" ' +
                                                                'node-label2="{{node.' + nodeLabel2 + '}}" ' +
                                                                'callback-method="' + callbackMethod + '" ' +
                                                                'data="' + otherTableData + '" ' +
                                                                'data-cell-min-width="' + cellMinWidth + '" ' +
                                                                'data-cell-min-height="' + cellMinHeight + '"/> ' +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="tree-table-row" style="display: block; width: 100%;" data-ng-repeat-end>' +
                            '<div id="{{node.' + nodeId + '}}" ' +
                                 'class="tree-table-cell" style="display: block; width: 100%;" ' +
                                 'data-node-id=' + nodeId + ' ' +
                                 'data-ng-hide="node.collapsed" ' +
                                 'data-matching-tree-grid="node.' + nodeChildren + '" ' +
                                 'data-matching-tree-data-id="' + matchingTreeDataId + '" ' +
                                 'data-node-label1=' + nodeLabel1 + ' ' +
                                 'data-node-label2=' + nodeLabel2 + ' ' +
                                 'data-tree-depth="' + ++depth + '" ' +
                                 'data-node-children=' + nodeChildren + ' ' +
                                 'data-column-is-tree=' + treeColumn + ' ' +
                                 'data-cell-min-width="' + cellMinWidth + '" ' +
                                 'data-cell-min-height="' + cellMinHeight + '" ' +
                                 'data-callback-method="' + callbackMethod + '" ' +
                                 'data-tree-cell-directive="' + treeCellDirective + '" ' +
                                 'data-other-tree-data="' + otherTreeData + '" ' +
                                 'data-table-cell-directive="' + tableCellDirective + '" ' +
                                 'data-other-table-data="' + otherTableData + '">' +
                            '</div>' +
                        '</div>';

					if( matchingTreeDataId && matchingTreeGrid ) {
						if( attrs.angularMatchingTreeGrid ) {
							scope[matchingTreeDataId] = scope[matchingTreeDataId] || {};
							scope[matchingTreeDataId].explandCollapse =
                                scope[matchingTreeDataId].expandCollapse || function( $event, selectedNode ) {
                                    var expandCollapseAll = function(node, collapsed) {
                                        node.collapsed = collapsed;
                                        for (var i = 0, len = node.children.length; i < len; i++) {
                                            expandCollapseAll(node.children[i], collapsed);
                                        }
                                    }
                                    if ($event.shiftKey == true) {
                                        for (var i = 0, len = selectedNode.children.length; i < len; i++) {
                                            selectedNode.children[i].collapsed = !selectedNode.children[i].collapsed;
                                        }
                                    } else if ($event.ctrlKey == true) {
                                        expandCollapseAll(selectedNode, !selectedNode.collapsed);
                                    } else {
                                        selectedNode.collapsed = !selectedNode.collapsed;
                                    }
                                };
						}
                        element.html('').append( $compile( template )( scope ) );
					}
				}
			};
	}]);
})( angular );
