$(document).ready(function(){
	$("#team-select").select2({
		ajax:{
			url:"http://localhost:5555/team.json?action=search",
			dataType:'json',
			delay:250,
			data: function(params){
				return {
					name: params.term || '',
					page: params.page || 0
				};
			},
			processResults: function(data, params){
				params.page = params.page || 0;
				var items = [];
				var di = data.items;
				console.log(di);
				for(var i = 0; i < di.length; i++){
					var datum = di[i]
					items.push({id:datum.name, text:datum.name + '\t\t(' + datum.yearfounded + '-' + datum.yearlast + ')'});
				}
				return {
					results:items,
					pagination:{
						more: ((params.page + 1) * 10) < data.count
					}
				};
			},
			cache:true
		},
		minimumInputLength:1
	});
});