var treeTableApp = angular.module('treeTableApp', [
	'treeTableControllers',
    'angularTreeTableGrid',
    'angularSimpleTreeTableGrid',
    'dndLists',
    'ui.bootstrap'
]);

var theModel = {};
treeTableApp.value("model", theModel);

treeTableApp.run(function ($http, model) {
    var num = -1;
	function initModel(arr, depth, path, parentId) {
		angular.forEach(arr, function(node) {
			node.id = ++num;
		    node.parentId = parentId;
			if (node.children && node.children.length > 0) {
				// nodes with children are folders so initially collapse them
				node.collapsed = false; // true=collapse all nodes; false=expand all nodes
				path = node.path + node.label;
				initModel(node.children, depth + 1, path, num);
			}
		})
	}; // initModel

	$http.get("data/" +
/*                "location"*/
/*                "simple"*/
                "pbs"
/*                "fgcms-roads-all"*/
/*                "trip"*/
/*                "tree-table"*/
/*                "asdi-heirarchical"*/
                + ".json")
    .then(function (response) {
			model.data = response.data;
//			initModel(model.data, 0, "", 0);
	})
}); // run
