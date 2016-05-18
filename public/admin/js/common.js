$(document).ready(function() {	
	var titles = []

	
	function getSchemaOfTable(tableName, handleData){
		$.ajax({
			url: '/schema/' + tableName,
			async: true,
		    type: "GET",
			success: function(data) {
               console.log(data)
               handleData(data)
			},
			error: function(data) {

			}
		});
	}
	

	
	function getSchemaOfTables(tableNames, handleData){
		var myData = {"schemas" : tableNames}
		$.ajax({
			url: '/schemas',
			async: true,
			dataType: 'json',
		    type: "POST",
		    contentType:'application/json',
		    data : JSON.stringify(myData),
			success: function(data) {
               handleData(data)
			},
			error: function(data) {

			}
		});
	}
	
	
	
	// Async true testare e se non funziona rimettere a false
	$.ajax({
		url: "/datasets",
		dataType: 'json',
		async: true,
		// data: myData,
		success: function(data) {
          console.log(data)
          data.map(function(item){
      	    var id = item.name
            var ht = '<li><a href="#" id="'+id+'">' + item.title + '</a></li>'
            $('#tr_dataset').append(ht)
            $('#trentino_chart').append('<li><a href="/trentino_chart/'+ id+'" id="chart_'+id+'">' + item.title + '</a></li>')
            titles.push(item.title)
           $('body').on('click', '#' + id, function (){ 
           	  var queryId = "query_" + item.name
           	$('#m_title').text(item.title) 
           	var htmlTags = '';
           	for(var i = 0; i < item.tags; i++){
           		htmlTags.append('<span class="label label-default">' + item.tags[i] +'</span>')
           	}
           // item.tags.map(funtion(d){
           // htmlTags.append('<span class="label label-default">' + d
			// +'</span>')
           // })
           	$('#m_tags').html(htmlTags) 
           	var categoria = ""
            var desc = ""
            item.groups.map(function(cat){
            	categoria = categoria + cat.group_title + " "
            	desc = desc + cat.group_desc + " "
            })
            
            var jsonResources = item.resources.filter(function(r){
            	return (r.resource_format === 'JSON')
            })
            
            var html = "";
           	var tableNames = []
            jsonResources.map(function(res){
            // var html = '<ul>'
            	var tableName = res.resource_name.replace(/-/g,'_').replace(/\s/g, '_')
            	tableNames.push(tableName)
            	var descrizioneTabella =  res.resource_description
            	 html = html + '<div class="list-group click"> <a href="#" class="list-group-item"><h4 class="list-group-item-heading">'+ tableName +'</h4><p class="list-group-item-text">'
            	           +descrizioneTabella+'</p><h5>Query : Select * from '+ tableName +'</h5><div class="list-group-item-text" id="schema_'+tableName +'">Schema</div></a></div>'     	
    //     getSchemaOfTable(tableName, function(output){
         // var schema = output.schema //.replace(/|--/g,'</br>')
                // schema = schema.replace(/___/g, " ")
         // var schemaId = '#schema_' + tableName
         // $(schemaId).html(schema)
        //        })
            })
           
              getSchemaOfTables(tableNames, function(output){
                	console.log(output)
                	for (var sc in output.schemas){
                		 console.log(sc)
                		 var ob = output.schemas[sc]
                		 var k = ""
                         for (key in ob){
                        	 k = key
                         }
                		 var schemaId = k
                		 var schema = ob[k]
                		 schema = schema.replace(/___/g, " ")
                		 $('#' + schemaId).html(schema)
                	}
                })
            
           	$('#m_categoria').text(categoria)
           	$('#m_desc').text(desc)
           	$('#m_tab').html(html)
           	$('#modal_dataset').modal('show')

           	// $('#my_sub_dataset').append('<li> <a href="#" id="'+queryId
			// +'">'+ item.title +'</a> </li>')
           })
          })

		},
		error: function(xhr, ajaxOptions, thrownError) {
		// spinner.stop();
		}
	});	
	
	 // $('#m_tab a').click(function(e) {
	 // e.preventDefault();
	 // e.target
	 // })
	   
	
	   $(document).on("click", '.click a', function(e) { 
	        e.preventDefault();
	     // e.target
	        console.log(e.currentTarget.innerHTML)
	        var htmlE = $.parseHTML(e.currentTarget.innerHTML)
	        var query = htmlE[2].innerText.split(":")[1]
	        $('#query_area').html(query)
	        $('#q_tab').html(e.currentTarget.innerHTML)
	      
	        $('#modal_dataset').modal('hide')
		});
	   
	   var userId = $('#m_userId').val()
		$.ajax({
			url: '/mydataset/' + userId,
			async: true,
		    type: "GET",
			success: function(data) {
	           console.log(data)
	           data.map(function(q){
	        	   var query = q.query
	        	   var desc  = q.description
	        	   var url = q.url
	        	   var name = q.name
	        	   var id = q._id.$oid
	        	   var ht = '<li id="'+id+'"><a href="/mydataset2/' + id + '">' + name+ '</a></li>'
	        	   $('#mydataset_2').prepend(ht)
	           })
	           
			},
			error: function(data) {

			}
		});

})