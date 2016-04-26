var chart;
$(document).ready(function(){
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'attendance'
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
			renderTo: 'winslosses'
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




	$.get("http://localhost:5555/team.json", {id:teamID, action:'details'}, function(data){
		console.log(data);
		var attendance = [];
		var year = [];
		var wins = [];
		var losses = [];
		data.seasons.forEach(function(item, idx){
			year.push(item.year);
			attendance.push(item.attendance);
			wins.push(item.wins);
			losses.push(item.losses);
		});
		Highcharts.charts.forEach(function(item, index){
			item.xAxis[0].setCategories(year)
		});
		Highcharts.charts[0].addSeries({name:'Average', data:attendance});
		Highcharts.charts[1].addSeries({name:'Average', data:wins}, false);
		Highcharts.charts[1].addSeries({name:'Average', data:losses});
	}, 'json');
});