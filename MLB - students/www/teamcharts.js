var chart;
$(document).ready(function(){
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'attendance'
		},
		title:{
			text: 'Attendance'
		},
		yAxis:{
			min:0,
			title:{
				text: null
			}
		}
	});
	Highcharts.chart({
		chart:{
			type: 'column',
			renderTo: 'winslosses'
		},
		title:{
			text: 'Wins-Losses'
		},
		yAxis:{
			min:0,
			title:{
				text: null
			}
		},
		plotOptions: {
            column: {
                stacking: 'normal'
            }
        }
	});

	$.get("http://163.11.236.180:5555/team.json", {id:teamID, action:'details'}, function(data){
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
		Highcharts.charts[0].addSeries({name:'Attendance', data:attendance});
		Highcharts.charts[1].addSeries({name:'Losses', data:losses}, false);
		Highcharts.charts[1].addSeries({name:'Wins', data:wins});
	}, 'json');
});