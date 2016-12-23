/*
    <div
        data-angular-tree-table="true"          // the treetableview directive
        data-tree-table-id="treeTable"          // each tree's unique id
        data-tree-model="treeTable.data"        // the tree model on $scope
        data-node-id="id"                       // each node's id
        data-node-label1="columnName"           // each node's label1
        data-node-children="children"           // each node's children
        data-tree-depth="0"                     // the depth of the tree at each level
        data-column-is-tree"                    // render column as tree
        data-cell-min-width=""                  // the width of the collumn header
        data-callback-method="callback"         // callback method
        data-tree-cell-directive="directive"    // directive for node (tree) cells
        data-other-tree-data="data"             // other data for the tree directive
        data-table-cell-directive="directive"   // directive for node (table) cells
        data-other-table-data="data>"           // other data for the table directive
     </div>
*/
(function ( angular ) {
	'use strict';
	angular.module( 'angularTreeTableGrid', [] )
		.directive( 'treeTableModel', ['$compile', function( $compile ) {
			return {
				restrict: 'A',
				link: function ( scope, element, attrs ) {
					var treeTableId = attrs.treeTableId;
					var treeTableModel = attrs.treeTableModel;
					var nodeId = attrs.nodeId || 'id';
                    var nodeLabel1 = attrs.nodeLabel1 || 'label1';
                    var nodeLabel2 = attrs.nodeLabel2 || null;
					var nodeChildren = attrs.nodeChildren || 'children';
                    var depth = attrs.treeDepth;
                    var treeColumn = attrs.columnIsTree;
                    var cellMinWidth = attrs.cellMinWidth;
                    var cellMinHeight = attrs.cellMinHeight;
<!-- TODO: add to code base for this diretive and simple version -->
var cellMinHeight = attrs.cellMinHeight || "20";
                    var callbackMethod = attrs.callbackMethod || '';
                    var treeCellDirective = attrs.treeCellDirective || 'nodelabel';
                    var otherTreeData = attrs.otherTreeData || '';
                    var tableCellDirective = attrs.tableCellDirective || 'nodelabel';
                    var otherTableData = attrs.otherTableData || '';
                    var template =
                        '<div class="tree-table-row" ' +
                             'style="display: block; width: 100%;" ' +
<!-- TODO: make height and striping changes in code base for this directive and simple version -->
                             'ng-style="!node.' + nodeChildren + ' && { \'min-height\': \'' + cellMinHeight + 'px\' } || { \'min-height\': \'27px\' }" ' +
                             'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }" ' +
                             'data-ng-repeat-start="node in ' + treeTableModel + '">' +
                            '<div class="tree-table-cell" style="display: block; width: 100%;">' +
                                '<span ng-show="' + treeColumn + '" ' +
                                      'style="display: block;" ' +
                                      'ng-style="node.' + nodeId + ' % 2 == 1 && {\'padding-left\': \'' + (depth * 30) + 'px\'} || ' +
                                      'node.' + nodeId + ' % 2 == 0 && {\'padding-left\': \'' + (depth * 30) + 'px\'}"> ' +
/*                                      'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }"> ' +*/
                                    '<i class="tree-table-collapsed" ' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
                                    '<i class="tree-table-expanded" ' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
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
/*TODO: horizontal padding reduced*/
                                      'style="display: block; width: ' + cellMinWidth + 'px; margin: 0px; padding-left: 2px; padding-right: 2px;"> ' +
/*TODO: now striping row rather than cell                                      'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }"> ' +*/
                                    '<i class="tree-table-collapsed" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
                                    '<i class="tree-table-expanded" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
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
                                 'data-tree-table-id="' + treeTableId + '" ' +
                                 'data-tree-table-model="node.' + nodeChildren + '" ' +
                                 'data-node-label1=' + nodeLabel1 + ' ' +
                                 'data-node-label2=' + nodeLabel2 + ' ' +
                                 'data-tree-depth="' + ++depth + '" ' +
                                 'data-node-children=' + nodeChildren + ' ' +
                                 'data-column-is-tree=' + treeColumn + ' ' +
/*TODO:remove px                        'data-cell-min-width="' + cellMinWidth + 'px" ' +*/
                                 'data-cell-min-width="' + cellMinWidth + '" ' +
                                 'data-cell-min-height="' + cellMinHeight + '" ' +
                                 'data-callback-method="' + callbackMethod + '" ' +
                                 'data-tree-cell-directive="' + treeCellDirective + '" ' +
                                 'data-other-tree-data="' + otherTreeData + '" ' +
                                 'data-table-cell-directive="' + tableCellDirective + '" ' +
                                 'data-other-table-data="' + otherTableData + '">' +
                            '</div>' +
                        '</div>';

					if( treeTableId && treeTableModel ) {
						if( attrs.angularTreeTable ) {
							scope[treeTableId] = scope[treeTableId] || {};
							scope[treeTableId].explandCollapse =
								scope[treeTableId].explandCollapse || function( selectedNode ) {
									selectedNode.collapsed = !selectedNode.collapsed;
    							};
						}
                        element.html('').append( $compile( template )( scope ) );
					}
				}
			};
	}]);
})( angular );
