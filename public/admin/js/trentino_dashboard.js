$(document).ready(function() {
	
	function get_random_color(str) {
		 var hash = 0;
		  for(var i=0; i < str.length; i++) {
		    hash = str.charCodeAt(i) + ((hash << 3) - hash);
		  }
		  var color = Math.abs(hash).toString(16).substring(0, 6);

		  return "#" + '000000'.substring(0, 6 - color.length) + color;
	}
	
	  function plotChartWithoutLabel(data) {		    
		    $.plot($("#all_chart_div #all_chart"), data,
		        {
		            series: {
		                lines: {
		                    show: true,
		                    fill: false
		                },
		                points: {
		                    show: true,
		                    lineWidth: 2,
		                    fill: true,
		                    fillColor: "#ffffff",
		                    symbol: "circle",
		                    radius: 5
		                },
		                shadowSize: 0
		            },
		            grid: {
		                hoverable: true,
		                clickable: true,
		                tickColor: "#f9f9f9",
		                borderWidth: 1,
		                borderColor: "#eeeeee"
		            },
		            colors: ["#79D1CF"],
		            tooltip: true,
		            tooltipOpts: {
		                defaultTheme: false
		            },
		            xaxis: {
		              //  mode: "time"
		            },
		            yaxes: [{
		            }, {
		                position: "right"
		            }]
		        }
		    ); 
		} 

	  function plotChart(dataAndLabel, divIds) {
		   var colors = [] 
		   var charts = dataAndLabel.map(function(item){
		    	data1 = item.data
		    	label1 = item.label
		        if (item.color){
		           colors.push(item.color)	
		        }
		    	return {data : data1, label : label1,  lines: { fill: true} }
		    })
		    
		    $.plot($(divIds), charts,
		        {
		            series: {
		                lines: {
		                    show: true,
		                    fill: false
		                },
		                points: {
		                    show: true,
		                    lineWidth: 2,
		                    fill: true,
		                    fillColor: "#ffffff",
		                    symbol: "circle",
		                    radius: 5
		                },
		                shadowSize: 0
		            },
		            grid: {
		                hoverable: true,
		                clickable: true,
		                tickColor: "#f9f9f9",
		                borderWidth: 1,
		                borderColor: "#eeeeee"
		            },
		            colors: colors, // ["#79D1CF"],
		            tooltip: true,
		            tooltipOpts: {
		                defaultTheme: false
		            },
		            xaxis: {
		              //  mode: "time"
		            },
		            yaxes: [{
		            }, {
		                position: "right"
		            }]
		        }
		    ); 
		} 
	
function attiviPerComune(numeroComune){
	var attivi_data = [];
	var dataAndLabel = {}
	var label = ''
	$.ajax({
		url: "./query/543877899080244/22a0063107804a6eaaa3752abcea6905?codice=" + numeroComune,
		dataType: 'json',
		async: false,
		// data: myData,
		success: function(data) {
			console.log(data)
			data.map(function(item){
				var anno = parseInt(item.anno)
				var valore = parseInt(item.valore)
				label = item.descriz_comu
				attivi_data.push([anno,valore])
				
			})
		//	plotChart(attivi_data, label);          
		},
	    error: function(err){
	    	return []
	    }
	})
	dataAndLabel.data = attivi_data
	dataAndLabel.label = label
	dataAndLabel.color = get_random_color(label)
	return dataAndLabel
}

function graficoTotale(queryUrl, divIds, titleId, title){
	$.ajax({
	//	url: "./query/543877899080244/292f1131090c479e8ea2872cbe8fc582", ./query/543877899080244/075e01059a2d4da79f22c92bb6293381
		url: queryUrl,
		dataType: 'json',
		async: false,
		// data: myData,
		success: function(data) {
		//	console.log(data)
			$("#" + titleId).html(title)
			var arrayComuni = []

			var codComu = _.groupBy(data, function(item){
			//	return item.comu
				return item.descriz
			}) 
      //      console.log(codComu)
            for (var i in codComu){
    			var label = ''
    		    var datas = {}
            	annoComu = []
            	comu = codComu[i]
            	console.log(comu)
            	comu.map(function(item){
            		annoComu.push([parseInt(item.anno),parseInt(item.valore)])
            		label = item.descriz
            	})
            //	arrayComuni.push(annoComu)
            	
            	datas.data = annoComu
            	datas.label = label
            	datas.color = get_random_color(label)
            	arrayComuni.push(datas)
            }
		//	plotChartWithoutLabel(arrayComuni)
			plotChart(arrayComuni, divIds)
		},
	    error: function(err){
	    	return []
	    }
	})
}
//attiviPerComune("32")

$.ajax({
	//	url: "./query/543877899080244/292f1131090c479e8ea2872cbe8fc582", ./query/543877899080244/075e01059a2d4da79f22c92bb6293381
		url: "/query/29daa8da2b664586b7cbd60f42ab7452/ac0f77ade57d478ebb271d885ef083c1",
		dataType: 'json',
		async: false,
		// data: myData,
		success: function(data) {
			console.log(data)
			var dataForChart = []
			var keys = Object.keys(data[0])
			console.log(keys)
			var arrData = data.map(function(item){
			//    var innerKeys = Object.keys(item);
				var barObject = { y : item['Anno']}
				for(var i in keys){
					var key = keys[i];
					if (key != 'Anno'){
						var val = 0.0
						if(item[key] != ''){
							if(item[key].indexOf(",") > -1){
								item[key] = item[key].replace(/,/g, '.')
							}
							val = parseFloat(item[key])
						}
						barObject[key] = parseFloat(val)
					}
				}
				dataForChart.push(barObject)
			})
			for (var i=keys.length-1; i>=0; i--) {
				if (keys[i] === 'Anno') {
					keys.splice(i, 1);
                    break;       
				}
            }
			console.log(dataForChart);
			Morris.Bar({
				  element: 'placeholder',
				  data: dataForChart,
				  xkey: 'y',
				  ykeys: keys,
				  labels: keys
				});
		/*	Morris.Bar({
				  element: 'placeholder',
				  data: [
				    { y: '2006', a: 100, b: 90 },
				    { y: '2007', a: 75,  b: 65 },
				    { y: '2008', a: 50,  b: 40 },
				    { y: '2009', a: 75,  b: 65 },
				    { y: '2010', a: 50,  b: 40 },
				    { y: '2011', a: 75,  b: 65 },
				    { y: '2012', a: 100, b: 90 }
				  ],
				  xkey: 'y',
				  ykeys: ['a', 'b'],
				  labels: ['Series A', 'Series B']
				}); */
			},
        error: function(err){
		    	return []
		    }
		})



graficoTotale('./query/543877899080244/58f9094d5bb14912aade12905df922eb',"#all_chart_div #all_chart", "all_chart_title", "Morti per Comunita' di valle")
graficoTotale('./query/543877899080244/cb14a98151f24121b7d9f0f8eae286f9',"#nati_vivi_div #nati_vivi", "nati_vivi_title", "Nati Vivi per Comunita' di valle")
graficoTotale('./query/543877899080244/b17ae7a2f50e48539d17e5a3c17460d6',"#popolazione_fine_anno_div #popolazione_fine_anno", "popolazione_fine_anno_title", "Popolazione a fine anno per Comunita' di valle")
graficoTotale('./query/29daa8da2b664586b7cbd60f42ab7452/4f36c41152b642b8b88fa5aee179fc4b',"#popolazione_fine_anno_comune_div #popolazione_fine_anno_comune", "popolazione_fine_anno_comune_title", "Popolazione a fine anno per Comune")

var data1 = attiviPerComune("32")
var data2 = attiviPerComune("1")
var data3 = attiviPerComune("3")
var data4 = attiviPerComune("4")
var data5 = attiviPerComune("5")
var data6 = attiviPerComune("6")
var data7 = attiviPerComune("7")
var dataAndLabel = [data1,data2, data3, data4,data5,data6,data7]
plotChart(dataAndLabel, "#attivi_chart_div #attivi_chart") 




})