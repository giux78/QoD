@(username: String)(implicit r: RequestHeader)

$(function() {

    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.RealtimeController.chat(username).webSocketURL()")
    
 /*   var options = {
    	    series: {
    	        lines: { show: true },
    	        points: { show: true }
    	    }
    	}; */
    
    var options = {
            series: {
                lines: {
                    show: true,
                    fill: true
                },
                shadowSize: 0
            },
            yaxis: {
                min: -1000,
                max: 1000,
                ticks: 10
            },
            xaxis: {
                max: 500,
                show: false
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
            }
        }


       
		var d1 = [];
		for (var i = 0; i < 14; i += 0.5) {
			d1.push([i, Math.sin(i)]);
		}

		var d2 = [[0, 3], [4, 8], [8, 5], [9, 13]];

		// A null signifies separate line segments

		var d3 = [[0, 12], [7, 12], null, [7, 2.5], [12, 2.5]];
    
	    var dataToPlot = [d1,d2,d3]
	       
	    
		
	    var plot =$.plot("#reatltime-chart_me #reatltime-chartContainer_me", dataToPlot, options);
               
	    plot.draw()
	    
    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val()}
        ))
        $("#talk").val('')
    }

    var lineaMap = {}
    var arrData = []
    
    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

        // Handle errors
        if(data.error) {
        	console.log(data.error)
            chatSocket.close()

            return
        } else {
          if (data.kind === 'talk'){
           var message = JSON.parse(data.message)
           message.map(function(item){
        	   if(item.late.should_be_index > 0){
        	   var busLinea = item.cod_corsa
        	   var busRitardo = item.late.diff
        	   if(lineaMap[busLinea]){
        		   var ob = lineaMap[busLinea]
        		   if(ob.lenght == 0){
        			   delete lineaMap[busLinea]
        		   } else {
        			   ob.push(busRitardo)
        			   if (ob.length > 10){
        				   ob.pop()
        			   }
        		   }
        	   }else{
        		   var arr = []
        		   arr.push(busRitardo)
        		   lineaMap[busLinea] = arr
        	   }
           }
           })
           }
        
          if ( ! $.isEmptyObject(lineaMap))
          {
        	  // empty array
        	  while(arrData.length > 0) {
        		  arrData.pop();
        		}
        	  dataToPlot = []
        	  $.each( lineaMap, function(i, n){
        		    var points = []
        		    var lineaArr = n
        		    for(var i = 0; i< n.length; i++){
        		    	var point = [i,n[i]]
        		    	points.push(point)
        		    }
        		    dataToPlot.push(points)
        		});
      //    	$.plot("#reatltime-chart_me #reatltime-chartContainer_me", dataToPlot, options);
            $.plot("#reatltime-chart_me #reatltime-chartContainer_me", [], options);
            $.plot("#reatltime-chart_me #reatltime-chartContainer_me", dataToPlot, options);
      //      plot.setOptions(options)
      //    	plot.setData(dataToPlot)
      //    	plot.draw()
          }
        }
      
    }

    
    
    function modify(){
    	dataToPlot = dataToPlot.map(function(arr){
    		if (arr.length > 0){
    			x = arr[arr.length - 1][0]
    			y = arr[arr.length - 1][1]
    			newArr = [x + 1, y]
    			arr.push(newArr)
    		}
    		return arr
    	})
       return dataToPlot
    }
    
    function updatePlot() {
        plot.setData(modify());
        plot.draw();
   //     setTimeout(update, 1000);
    }
    
    setInterval(function() { updatePlot(); }, 1000);
    
    chatSocket.onmessage = receiveEvent

})