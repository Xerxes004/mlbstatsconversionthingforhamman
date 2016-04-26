var chart;
$(document).ready(function(){
	// Load the fonts
	Highcharts.createElement('link', {
	   href: 'https://fonts.googleapis.com/css?family=Signika:400,700',
	   rel: 'stylesheet',
	   type: 'text/css'
	}, null, document.getElementsByTagName('head')[0]);

	Highcharts.theme = {
	   colors: ["#f45b5b", "#8085e9", "#8d4654", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
	      "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
	   chart: {
	      backgroundColor: null,
	      style: {
	         fontFamily: "Signika, serif"
	      }
	   },
	   title: {
	      style: {
	         color: 'black',
	         fontSize: '16px',
	         fontWeight: 'bold'
	      }
	   },
	   subtitle: {
	      style: {
	         color: 'black'
	      }
	   },
	   tooltip: {
	      borderWidth: 0
	   },
	   legend: {
	      itemStyle: {
	         fontWeight: 'bold',
	         fontSize: '13px'
	      }
	   },
	   xAxis: {
	      labels: {
	         style: {
	            color: '#6e6e70'
	         }
	      }
	   },
	   yAxis: {
	      labels: {
	         style: {
	            color: '#6e6e70'
	         }
	      }
	   },
	   plotOptions: {
	      series: {
	         shadow: true
	      },
	      candlestick: {
	         lineColor: '#404048'
	      },
	      map: {
	         shadow: false
	      }
	   },

	   // Highstock specific
	   navigator: {
	      xAxis: {
	         gridLineColor: '#D0D0D8'
	      }
	   },
	   rangeSelector: {
	      buttonTheme: {
	         fill: 'white',
	         stroke: '#C0C0C8',
	         'stroke-width': 1,
	         states: {
	            select: {
	               fill: '#D0D0D8'
	            }
	         }
	      }
	   },
	   scrollbar: {
	      trackBorderColor: '#C0C0C8'
	   },

	   // General
	   background2: '#E0E0E8'

	};

	// Apply the theme
	Highcharts.setOptions(Highcharts.theme);
	
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