$(document).ready(function() {
	
	var currentDatasetUUID = ""

	$('#run_query').click( function(){
		var myData = {}
		$('#excelDataTable').empty();
		var query = $("#query_area").val()
		var dataset = ""
	    myData.query = query
	    myData.dataset_name = dataset
		$.ajax({
			url: '/run_sql',
			dataType: 'json',
			async: true,
		    data: JSON.stringify(myData),
		//	data: myData,
		    type: "POST",
		    contentType:'application/json',
			success: function(data) {
               console.log(data)
               if(data.query){
            	   $('#modal_parametric').modal('show')
               } else {
                 buildHtmlTable(data)
               }
			},
			error: function(data) {

			}
		});
	})
	
		$('#run_query_dataset').click( function(){
		var myData = {}
		$('#excelDataTable').empty();
		var query = $("#query_area").val()
		var path = $('#local_path').val();
	//	path = path.substr(path.lastIndexOf('/') + 1)
		var dataset = ""
	    var format = $('#format').val();
	    myData.query = query
	    myData.dataset_name = dataset
	    myData.path = path
	    myData.format = format
		$.ajax({
			url: '/run_sql_dataset',
			dataType: 'json',
			async: true,
		    data: JSON.stringify(myData),
		//	data: myData,
		    type: "POST",
		    contentType:'application/json',
			success: function(data) {
               console.log(data)
               if(data.query){
            	   $('#modal_parametric').modal('show')
               } else {
                 buildHtmlTable(data)
               }
			},
			error: function(data) {

			}
		});
	})
		
	$('#export_query').click(function(){
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		    return v.toString(16);
		});
		uuid = uuid.replace(/-/g,"")
		var placeholder = $("#q_url").attr('name');
		var query = $("#query_area").val()
		var queryString = ""
		if(query.indexOf('#') > -1){
			var queryArray = query.split(" ")
			var parameters = queryArray.filter(function(elem){
				return (elem.substring( 0, "#".length ) === "#")	
			})

            for(var i in parameters){
            	var par = parameters[i].replace('#',"")
            if(i == 0){
            	queryString += "?" + par + "=valore" 
            } else{
            	queryString +="&" + par + "=valore" 
            }
				
		}

		}
		$("#q_url").attr('placeholder', placeholder + "/" + uuid + queryString);
	    $('#modal_export').modal('show')
	})
	
	$('#save_dataset').click(function(){
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		    return v.toString(16);
		});
		uuid = uuid.replace(/-/g,"")
		currentDatasetUUID = uuid
		var placeholder = $("#q_url_query").attr('name');
		// var placeholderDataset = $("#q_url_dataset").attr('name');
		var query = $("#query_area").val()
	    var datasetQuery = query
		var queryString = ""
		if(query.indexOf('#') > -1){	
		}

		
		$("#q_url_query").attr('placeholder', placeholder + "/" + uuid + queryString);
		$("#export_dataset_query").val(datasetQuery)
	//	$("#q_url_dataset").attr('placeholder', placeholderDataset + "/" + uuid + queryString);
		$('#modal_export_dataset').modal('show')
	})
	
	
$( "#b_export" ).click(function( ) {
	var prova = $( "#f_export" ).serialize()
	var placeholder = $("#q_url").attr('placeholder');
	var description = $("#export_description").val()
	var name = $("#export_name").val()
	var query = $("#query_area").val()
	var type = 'normal'
    if(query.indexOf('#') > -1){
    	type = 'parametric'
    }
	console.log(description + " " + placeholder)
	var myData = {}
    myData.placeholder = placeholder
    myData.description = description
    myData.query = query
    myData.type = type
    myData.name = name
	$.ajax({
		url: '/export_query',
		dataType: 'json',
		async: true,
	    data: JSON.stringify(myData),
	//	data: myData,
	    type: "POST",
	    contentType:'application/json',
		success: function(data) {
		    $('#modal_export').modal('hide')
		},
		error: function(data) {
			$('#modal_export').modal('hide')
		}
	});
	
});
	
	$( "#b_export_dataset" ).click(function( ) {
		var prova = $( "#f_export_dataset" ).serialize()
		var placeholder = $("#q_url_query").attr('placeholder');
		var description = $("#export_dataset_description").val()
		var name = $("#export_dataset_name").val()
		var query = $("#query_area").val()
		var type = 'dataset'
	    var uuid = currentDatasetUUID
		console.log(description + " " + placeholder)
		var myData = {}
	    myData.placeholder = placeholder
	    myData.name = name
	    myData.description = description
	    myData.query = query
	    myData.type = type
	    myData.uuid = currentDatasetUUID
		$.ajax({
			url: '/export_dataset',
			dataType: 'json',
			async: true,
		    data: JSON.stringify(myData),
		//	data: myData,
		    type: "POST",
		    contentType:'application/json',
			success: function(data) {
			    $('#modal_export').modal('hide')
			},
			error: function(data) {
				$('#modal_export').modal('hide')
			}
		}); 
		
	});
	
	
	$( "#add_dataset" ).click(function( ) {
		$('#modal_add_dataset').modal('show')
		
	})
	
	function buildHtmlTable(myList) {
		
	    var columns = addAllColumnHeaders(myList);

	    for (var i = 0 ; i < myList.length ; i++) {
	        var row$ = $('<tr/>');
	        for (var colIndex = 0 ; colIndex < columns.length ; colIndex++) {
	            var cellValue = myList[i][columns[colIndex]];

	            if (cellValue == null) { cellValue = ""; }

	            row$.append($('<td/>').html(cellValue));
	        }
	        $("#excelDataTable").append(row$);
	    }
	}

	// Adds a header row to the table and returns the set of columns.
	// Need to do union of keys from all records as some records may not contain
	// all records
	function addAllColumnHeaders(myList)
	{
	    var columnSet = [];
	    var headerTr$ = $('<tr/>');

	    for (var i = 0 ; i < myList.length ; i++) {
	        var rowHash = myList[i];
	        for (var key in rowHash) {
	            if ($.inArray(key, columnSet) == -1){
	                columnSet.push(key);
	                headerTr$.append($('<th/>').html(key));
	            }
	        }
	    }
	    $("#excelDataTable").append(headerTr$);

	    return columnSet
	}
    $("#wizard").steps({
        headerTag: "h2",
        bodyTag: "section",
        transitionEffect: "slideLeft",
        onStepChanging: function (event, currentIndex, newIndex) { 
        	if (currentIndex == 0 && newIndex == 1){
        	  if(! $("#term_cond").is(':checked')){
        		  $("#alert_step1").show()
        		  return false;
        	  }
        	} else if(currentIndex == 1 && newIndex == 2){
          	  var nameHere = $('#add_dataset_name').val()
        	  var descHere = $('#add_dataset_description').val()
        	  if (nameHere == "" || descHere == ""){
        		  $("#alert_step2").show()
        		  return false;
        	  }
    	      
            } else if (currentIndex == 2 && newIndex == 3){
	 
            }
        	console.log(newIndex)
        	return true;    	
        },
        onFinished: function (event, currentIndex)
            {
        	  var name = $('#add_dataset_name').val()
        	  var desc = $('#add_dataset_description').val()
        	  var userId = $('#m_userId').val()
      		  //grab all form data  
      		  var formData = new FormData($(this)[0]);
      		  formData.append("name",name)
      		  formData.append("desc", desc)
      		  formData.append("user_id", userId)
      		  var uploadFile = document.getElementById("datasetfile").files[0]
      		  formData.append("dataset", uploadFile);
      		 
      		  $.ajax({
      		    url: '/upload',
      		    type: 'POST',
      		    data: formData,
      		    contentType: 'multipart/form-data',
      		    async: false,
      		    cache: false,
      		    contentType: false,
      		    processData: false,
      		    success: function (returndata) {
      		      alert(returndata);
      		    }
      		  });

            }
       
    });

	
})