/*
    <div
        data-angular-tree-table="true"          // the treetableview directive
        data-tree-table-id="treeTable"          // each tree's unique id
        data-tree-model="treeTable.data"        // the tree model on $scope
        data-node-id="id"                       // each node's id
        data-link-data=linkData                 // external data reference
        data-node-label1="columnName1"          // each node's 1st label
        data-node-label2="columnName2"          // each node's 2nd label
        data-node-children="children"           // each node's children
        data-tree-depth="0"                     // the depth of the tree at each level
        data-column-is-tree=""                  // render column as tree
        data-cell-min-width=""                  // the width of the collumn header
        data-callback-method="callback"         // callback method
        data-tree-cell-directive="directive"    // directive for node (tree) cells
        data-other-tree-data="data"             // other data for the tree directive
        data-table-cell-directive="directive"   // directive for node (table) cells
        data-other-table-data="data">           // other data for the table directive
     </div>
*/
(function ( angular ) {
	'use strict';

    var schemaWizardApp = angular.module('schemaWizardApp');

    schemaWizardApp.directive( 'simpleTreeTableGrid', ['$compile', function( $compile ) {
			return {
				restrict: 'A',
				link: function ( scope, element, attrs ) {
					var treeTableId = attrs.treeTableId;
					var simpleTreeTableGrid = attrs.simpleTreeTableGrid;
                    var linkData = attrs.linkData;
					var nodeId = attrs.nodeId || 'id';
                    var nodeLabel1 = attrs.nodeLabel1 || 'label1';
                    var nodeLabel2 = attrs.nodeLabel2 || null;
					var nodeChildren = attrs.nodeChildren || 'children';
                    var cellMinWidth = attrs.cellMinWidth;
                    var tableCellDirective = attrs.tableCellDirective || 'simplecell';
                    var template =
                        '<div class="tree-table-row" style="display: block; width: 100%;" data-ng-repeat-start="node in ' + simpleTreeTableGrid + '">' +
                            '<div class="tree-table-cell" style="display: block; width: 100%;">' +
                                '<span style="display: block; width: 100%; min-width:' + cellMinWidth + 'px; margin: 0px; padding-left: 4px; padding-right: 4px;" ' +
                                      'ng-class="{ \'stripe-even\': node.' + nodeId + ' % 2 == 0, \'stripe-odd\': node.' + nodeId + ' % 2 == 1 }"> ' +
                                    '<i class="tree-table-collapsed" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.expandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-expanded" style="background-image: none;"' +
                                       'data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" ' +
                                       'data-ng-click="' + treeTableId + '.expandCollapse($event, node)"></i>' +
                                    '<i class="tree-table-normal" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
                                    '<' + tableCellDirective + ' node="{{node}}" ' +
                                                                'node-id="{{node.' + nodeId + '}}" ' +
                                                                'label1="' + nodeLabel1 + '" ' +
                                                                'label2="' + nodeLabel2 + '" ' +
                                                                'node-label1="{{node.' + nodeLabel1 + '}}" ' +
                                                                'node-label2="{{node.' + nodeLabel2 + '}}" ' +
                                                                'data-link-data="' + linkData + '" ' +
                                                                'data-ng-hide="node.' + nodeChildren + '.length"/> ' +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="tree-table-row" style="display: block; width: 100%;" data-ng-repeat-end>' +
                            '<div id="{{node.' + nodeId + '}}" ' +
                                 'class="tree-table-cell" style="display: block; width: 100%;" ' +
                                 'data-node-id=' + nodeId + ' ' +
                                 'data-ng-hide="node.collapsed" ' +
                                 'data-tree-table-id="' + treeTableId + '" ' +
                                 'data-tree-table-grid="node.' + nodeChildren + '" ' +
                                 'data-link-data="' + linkData + '" ' +
                                 'data-node-label1=' + nodeLabel1 + ' ' +
                                 'data-node-label2=' + nodeLabel2 + ' ' +
                                 'data-node-children=' + nodeChildren + ' ' +
                                 'data-cell-min-width="' + cellMinWidth + 'px" ' +
                                 'data-table-cell-directive="' + tableCellDirective + '"> ' +
                            '</div>' +
                        '</div>';

                    if( treeTableId && simpleTreeTableGrid ) {
                        if( attrs.angularSimpleTreeTable ) {
                            scope[treeTableId] = scope[treeTableId] || {};
                            scope[treeTableId].expandCollapse =
                                scope[treeTableId].expandCollapse || function( selectedNode ) {
                                    selectedNode.collapsed = !selectedNode.collapsed;
                                };
                        }
                        element.html('').append( $compile( template )( scope ) );
                    }
				}
			};
	}]);
})( angular );
