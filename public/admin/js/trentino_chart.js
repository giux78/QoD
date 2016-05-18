$(document).ready(function() {
	var userId = $('#userid').val()
//    var hash = window.location.hash.substring(1);
	var add = window.location.href
	var hash = add.substr(add.lastIndexOf('/') + 1)
	
    $.ajax({
		url: '/info_dataset/' + hash,
		async: true,
	    type: "GET",
		success: function(myDataset) {
           console.log(myDataset)
           var title = myDataset.title
           var chartData = myDataset.chart_data
           $('#chart_' + hash).addClass('active')
           if(chartData.length != 0){
        	    chartData.map(function(resourceChart){
        	    	var name = resourceChart.resource_name
        	    	var data = resourceChart.result
        	    	var chartType = resourceChart.chart_type
        	    	if (data.length != 0 && chartType == 'comunita_valle'){
        	    		$('#chart_div').append('<div class="row"><div class="col-md-12"> <section class="panel"> <header class="panel-heading"> <span id="'+name+'_title"></span> <span class="tools pull-right"> <a href="javascript:;" class="fa fa-chevron-down"></a> <a href="javascript:;" class="fa fa-cog"></a> <a href="javascript:;" class="fa fa-times"></a> </span> </header> <div class="panel-body"> <div id="'+name+'_div"> <div id="'+ name + '" style="width:100%;height:400px; text-align: center; margin:0 auto;"> </div> </div> </div> </section></div></div>')
        	    	    graficoTotale(data,'#' + name + '_div' + ' #'+name, name +"_title", name +" per Comunita' di valle")
        	    	}  else if (data.length != 0 && chartType == 'comuni'){
        	    		$('#chart_div').append('<div class="row"><div class="col-md-12"> <section class="panel"> <header class="panel-heading"> <span id="'+name+'_comuni_title"></span> <span class="tools pull-right"> <a href="javascript:;" class="fa fa-chevron-down"></a> <a href="javascript:;" class="fa fa-cog"></a> <a href="javascript:;" class="fa fa-times"></a> </span> </header> <div class="panel-body"> <div id="'+name+'comuni_div"> <div id="'+ name + '_comuni" style="width:100%;height:4500px; text-align: center; margin:0 auto;"> </div> </div> </div> </section></div></div>')
        	    	    graficoTotale(data,'#' + name + 'comuni_div' + ' #'+name+'_comuni', name +"_comuni_title", name +" per Comuni")
        	    	} else if (data.length != 0 && chartType == 'bar_chart'){
        	    		$('#chart_div').append('<div class="row"><div class="col-md-12"> <section class="panel"> <header class="panel-heading"> <span id="'+ name +'_bar_title"></span> <span class="tools pull-right"> <a href="javascript:;" class="fa fa-chevron-down"></a> <a href="javascript:;" class="fa fa-cog"></a> <a href="javascript:;" class="fa fa-times"></a> </span> </header> <div class="panel-body"> <div id="'+name+'_bar_div" style="height:600px"></div> </div> </div> </section></div></div>')
        	    	    barChartMorris(data,name)
        	    	}
        	    })
           }
		},
		error: function(data) {

		}
	});
	
	function get_random_color(str) {
		 var hash = 0;
		  for(var i=0; i < str.length; i++) {
		    hash = str.charCodeAt(i) + ((hash << 3) - hash);
		  }
		  var color = Math.abs(hash).toString(16).substring(0, 6);

		  return "#" + '000000'.substring(0, 6 - color.length) + color;
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
	
	  function barChartMorris(data,resource_name){
			console.log(data)
			$("#" + resource_name + '_bar_title').html(resource_name)
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
				  element: resource_name +'_bar_div',
				  data: dataForChart,
				  xkey: 'y',
				  ykeys: keys,
				  labels: keys
				});
	  }
	
	function graficoTotale(data, divIds, titleId, title){


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

		}
	
})