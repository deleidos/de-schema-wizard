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
     </div>
*/
(function ( angular ) {
	'use strict';
	angular.module( 'angularSimpleTreeTableGrid', [] )
		.directive( 'simpleTreeTableModel', ['$compile', function( $compile ) {
			return {
				restrict: 'A',
				link: function ( scope, element, attrs ) {
					var treeTableId = attrs.treeTableId;
					var simpleTreeTableModel = attrs.simpleTreeTableModel;
					var nodeId = attrs.nodeId || 'id';
                    var nodeLabel1 = attrs.nodeLabel1 || 'label1';
                    var nodeLabel2 = attrs.nodeLabel2 || null;
					var nodeChildren = attrs.nodeChildren || 'children';
                    var depth = attrs.treeDepth;
                    var treeColumn = attrs.columnIsTree;
                    var cellMinWidth = attrs.cellMinWidth;
                    var template =
                        '<div class="row" style="display: block; width: 100%;" data-ng-repeat-start="node in ' + simpleTreeTableModel + '">' +
                            '<div class="cell" style="display: block; width: 100%;">' +
                                '<span ng-hide="' + treeColumn + '" ' +
                                      'style="display: block; min-width: ' + cellMinWidth + 'px; margin: 0px; padding-left: 4px; padding-right: 4px;" ' +
                                      'ng-style="node.' + nodeId + '% 2 == 1 && {\'background\': \'white\'} || ' +
                                                'node.' + nodeId + '% 2 == 0 && {\'background\': \'azure\'}"> ' +
                                    '<i class="collapsed" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
                                    '<i class="expanded" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.explandCollapse(node)"></i>' +
                                    '<i class="normal" style="background-image: none;" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
                                    '{{node.' + nodeLabel1 + '}} ' +
                                    '{{node.' + nodeLabel2 + '}} ' +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="row" style="display: block; width: 100%;" data-ng-repeat-end>' +
                            '<div id="{{node.' + nodeId + '}}" ' +
                                 'class="cell" style="display: block; width: 100%;" ' +
                                 'data-node-id=' + nodeId + ' ' +
                                 'data-ng-hide="node.collapsed" ' +
                                 'data-tree-table-id="' + treeTableId + '" ' +
                                 'data-simple-tree-table-model="node.' + nodeChildren + '" ' +
                                 'data-node-label1=' + nodeLabel1 + ' ' +
                                 'data-node-label2=' + nodeLabel2 + ' ' +
                                 'data-tree-depth="' + ++depth + '" ' +
                                 'data-node-children=' + nodeChildren + ' ' +
                                 'data-column-is-tree=' + treeColumn + ' ' +
                                 'data-cell-min-width="' + cellMinWidth + 'px"> ' +
                            '</div>' +
                        '</div>';

					if( treeTableId && simpleTreeTableModel ) {
						if( attrs.angularSimpleTreeTable ) {
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
