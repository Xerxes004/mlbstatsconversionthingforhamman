$(document).ready(function(){
	var teamSelector = $("#team-select");

	teamSelector.select2({
		ajax:{
			url:"http://163.11.236.180:5555/team.json?action=search",
			//url:"http://localhost:5555/team.json?action=search",
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
					items.push({id:datum.id, text:datum.name + '\t\t(' + datum.yearfounded + '-' + datum.yearlast + ')'});
				}

				return {
					results:items,
					pagination:{
						more: ((params.page + 1) * 6) < data.count
					}
				};
			},
			cache:true
		},
		placeholder: 'Select Team',
		minimumInputLength:0
	});

	//teamSelector.select2('open');
	teamSelector.on('select2:select', function(){
		console.log('submitting form');
		$("#team-select-form").submit();
	});

	var playerSelector = $("#player-select");
	playerSelector.select2({
		ajax:{
			url:"http://163.11.236.180:5555/player.json?action=search",
			//url:"http://localhost:5555/player.json?action=search",
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
					var datum = di[i];
					var firstlast = (datum.firstgame || datum.lastgame) ? '(' + (datum.firstgame || '') + '-' + (datum.lastgame || '') + ')' : '';

					items.push({id:datum.id, text:datum.name + '  ' + firstlast});
				}
				console.log("Page Num" + params.page);
				return {
					results:items,
					pagination:{
						more: ((params.page + 1) * 6) < data.count
					}
				};
			},
			cache:true
		},
		placeholder: 'Select Player',
		minimumInputLength:0
	});

	playerSelector.on('select2:select', function(){
		console.log('submitting form');
		$("#player-select-form").submit();
	});	
});