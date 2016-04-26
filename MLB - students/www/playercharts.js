var chart;
$(document).ready(function(){
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'batavg'
		},
		title:{
			text: 'Batting Average'
		},
		yAxis:{
			min:0,
			title:{
				text: 'Batting Average'
			}
		}
	});
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'hits'
		},
		title:{
			text: 'Hits'
		},
		yAxis:{
			min:0,
			title:{
				text: '#Hits'
			}
		},
		plotOptions: {
            column: {
                stacking: 'normal'
            }
        }
	});
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'homeruns'
		},
		title:{
			text: 'Home Runs'
		},
		yAxis:{
			min:0,
			title:{
				text: '#Home Runs'
			}
		}
	});
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'gamesplayed'
		},
		title:{
			text: 'Games Played'
		},
		yAxis:{
			min:0,
			title:{
				text: '#Games Played'
			}
		}
	});



	$.get("http://localhost:5555/player.json", {id:playerID, action:'details'}, function(data){
		console.log(data);
		var batavg = [];
		var year = [];
		var hits = [];
		var atbats = [];
		var homeruns = [];
		var gamesplayed = [];
		data.items.forEach(function(item, idx){
			batavg.push(parseFloat(item.battingavg)||0);
			year.push(item.year.toString());
			hits.push(item.hits);
			atbats.push(item.atbats);
			homeruns.push(item.homeruns);
			gamesplayed.push(item.gamesplayed);
		});
		Highcharts.charts.forEach(function(item, index){
			item.xAxis[0].setCategories(year)
		});
		Highcharts.charts[0].addSeries({name:'Average', data:batavg});
		Highcharts.charts[1].addSeries({name:'Hits', data:hits}, false);
		Highcharts.charts[1].addSeries({name:'At Bats', data:atbats});
		Highcharts.charts[2].addSeries({name:'Home Runs', data:homeruns});
		Highcharts.charts[3].addSeries({name:'Games Played', data:gamesplayed});

	}, 'json');
});