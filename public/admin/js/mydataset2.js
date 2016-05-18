$(document).ready(function() {
	var userId = $('#userid').val()
//    var hash = window.location.hash.substring(1);
	var add = window.location.href
	var hash = add.substr(add.lastIndexOf('/') + 1)
	
    $.ajax({
		url: '/personalDataset/' + hash,
		async: true,
	    type: "GET",
		success: function(myDataset) {
           console.log(myDataset)
         //   var myDataset = JSON.parse(data)
            $('#' + hash).addClass('active')
            $('#local_path').val(myDataset.local_path);
            $('#query_area').val(myDataset.query)
            $('#format').val(myDataset.format)
            $('#q_tab').html('<h4>'+myDataset.name+'</h4><h5>'+ myDataset.description+'</h5><h5>'+ myDataset.query+'</h5><p>' + myDataset.schema + '</p>')
		},
		error: function(data) {

		}
	});
	
	$( "#b_export_personal" ).click(function( ) {
		var prova = $( "#f_export" ).serialize()
		var placeholder = $("#q_url").attr('placeholder');
		var description = $("#export_description").val()
		var name = $("#export_name").val()
		var query = $("#query_area").val()
		var type = 'personal'
		var local_path =$('#local_path').val();
		var format = $('#format').val()
	    var path_file = ""
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
	    myData.local_path = local_path
	    myData.format = format
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
	
/*	
	$(window).on('hashchange', function(e){
		window.scrollTo(0, 0);
      var hashOnChange = window.location.hash.substring(1);
    
	$.ajax({
		url: './personalDataset/' + hashOnChange,
		async: true,
	    type: "GET",
		success: function(data) {
           console.log(data)
            var myDataset = JSON.parse(data)
            $('#query_area').val(myDataset.query)
            $('#q_tab').html('<h4>'+myDataset.name+'</h4><h5>'+ myDataset.description+'</h5><h5>'+ myDataset.query+'</h5>')
         
		},
		error: function(data) {

		}
	});
	}); */
	
})