$(document).ready(function() {	
	var resourceHtml =  '<div class="row"> <div class="col-md-12"> <section class="panel"> <div class="panel-body profile-information"> <div class="col-md-12"> <div class="profile-desk"> <h1></h1> <span class="text-muted"></span><br/> <p> </p><br/> <p class="text-center"> </p><br/> </div> </div> </div> </section> </div> <div>'   
    var idItem = {}
    function updataMainPanel(item){
		$("#title").html(item.title)
        var groupsTitle = item.groups.map(function(item){
       	 return item.group_title
        }).join(" ")
        var groupsDesc = item.groups.map(function(item){
       	 return item.group_desc
        }).join(" ")
        $("#sub_title").html(groupsTitle)
        $("#desc").html(groupsDesc)
        var tags = item.tags
        var tagsHtml = ''
        tags.map(function(tag){
       //	 tagsHtml =  tagsHtml + '<span class="label label-info">'+tag+'</span>'
        	tagsHtml =  tagsHtml + '<span class="badge bg-primary">'+tag+'</span>'
        })
        $("#tags").html(tagsHtml)
        var resources = item.resources
        var name = item.name
        resources.map(function(resource){
       	 var r_name = resource.resource_name
       	 var r_format = resource.resource_format
       	 var r_url = resource.resource_url
       	 var r_desc = resource.resource_description
       	 var r_createdCkan = resource.resource_createdOnCkan
       	 var r_createdOpenMemories = resource.resource_createdOn
       	 var r_hadoop = resource.resource_hadoop_path
       	 var hadoopDir = r_hadoop.split("/")[5]
       	 var r_id = resource.resource_id
       	 var r_history = resource.resource_history
       	var fileName = r_hadoop.split("/")[7]
    //	 var fileName = r_id + '.' + r_format

       	 $("#resources").append('<div> <div class="col-md-12"> <section class="panel"> <div class="panel-body profile-information"> <div class="col-md-9"> <div class="profile-desk"> <h1>' + r_name + '</h1> <span class="text-muted">'+ r_format +'</span><br/> <p>'+ r_desc +'</p><br/> <p class="text-center"> </p><br/> </div> </div><div class="col-md-3"> <div class="profile-statistics"> <h4>Download File</h4> <a class="btn btn-primary" href="./downloadFileHadoop/'+hadoopDir+'/' + name  +'/' +fileName +'">Download</a> <h4>Dataset History</h4> <a class="btn btn-info" id="b_h_'+ r_id +'" href="#">See History</a> </div> </div> </div> </section> </div> <div>')
            $('#b_h_'+ r_id).on('click', function(e){
            $('#m_h_title').html(r_name)
            var timelineHtmlMessage = '<div class="msg-time-chat"> <div class="message-body msg-in"> <span class="arrow"></span> <div class="text"> <div class="first">'+ r_createdOpenMemories +'</div> <a href="./downloadFileHadoop/'+hadoopDir+'/' + name +'/' + fileName+ '"><div class="second bg-terques "> Scarica questa versione</div></a> </div> </div> </div>'
           	 if (r_history != null){
           		 r_history.map(function(hist){
           			var h_name = hist.resource_name
           			var h_format =hist.resource_format
           			var h_id = hist.resource_id
           			var date_created = hist.resource_createdOn
           		    var h_hadoop = hist.resource_hadoop_path
           		    var h_hadoopDir = h_hadoop.split("/")[5]
           		   // var h_fileName = h_id + '.' + h_format
           			var h_fileName = h_hadoop.split("/")[7]
           		    timelineHtmlMessage = timelineHtmlMessage + '<div class="msg-time-chat"> <div class="message-body msg-in"> <span class="arrow"></span> <div class="text"> <div class="first">'+ date_created +'</div> <a href="./downloadFileHadoop/'+h_hadoopDir+'/' + name +'/' + h_fileName + '"><div class="second bg-terques "> Scarica questa versione</div></a> </div> </div> </div>'                		 
           		 }) 		 
           	 }
             $("#downloadZip").attr('href','./downloadZip/' + r_id)	
           	 $('#timeline').html(timelineHtmlMessage)
           	 $('#modal_history').modal('show');
            })
        })
	}
	
	$.ajax({
		url: "./openMemoriesDataset",
		dataType: 'json',
		async: false,
		// data: myData,
		success: function(data) {
          console.log(data)
      //    data.map(function(item){
          for (var i in data){
      	    var item = data[i]
        	var id = item.name.replace(/\-/, '')
        	idItem[id] = item
        	var ht = ''
        	if (i == 0){
             ht = '<li class="active"><a href="#" id="open_'+id+'">' + item.title + '</a></li>'
             $("#title").html(item.title)
             var groupsTitle = item.groups.map(function(item){
            	 return item.group_title
             }).join(" ")
             var groupsDesc = item.groups.map(function(item){
            	 return item.group_desc
             }).join(" ")
             $("#sub_title").html(groupsTitle)
             $("#desc").html(groupsDesc)
             var tags = item.tags
             var tagsHtml = ''
             tags.map(function(tag){
            //	 tagsHtml =  tagsHtml + '<span class="label label-info">'+tag+'</span>'
            	 tagsHtml =  tagsHtml + '<span class="badge bg-primary">'+tag+'</span>'
             })
             $("#tags").html(tagsHtml)
             var resources = item.resources
             var name = item.name
             resources.map(function(resource){
            	 var r_name = resource.resource_name
            	 var r_format = resource.resource_format
            	 var r_url = resource.resource_url
            	 var r_desc = resource.resource_description
            	 var r_createdCkan = resource.resource_createdOnCkan
            	 var r_createdOpenMemories = resource.resource_createdOn
            	 var r_id = resource.resource_id
            	 var r_hadoop = resource.resource_hadoop_path
            	 var hadoopDir = r_hadoop.split("/")[5]
            	 var r_history = resource.resource_history
            	// var fileName = r_id + '.' + r_format
            	 var fileName = r_hadoop.split("/")[7]

            	 $("#resources").append('<div> <div class="col-md-12"> <section class="panel"> <div class="panel-body profile-information"> <div class="col-md-9"> <div class="profile-desk"> <h1>' + r_name + '</h1> <span class="text-muted">'+ r_format +'</span><br/> <p>'+ r_desc +'</p><br/> <p class="text-center"> </p><br/> </div> </div><div class="col-md-3"> <div class="profile-statistics"> <h4>Download File</h4> <a class="btn btn-primary" href="./downloadFileHadoop/'+hadoopDir+'/' + name +'/' + fileName +'">Download</a> <h4>Dataset History</h4> <a class="btn btn-info" id="b_h_'+ r_id +'" href="#">See History</a> </div> </div> </div> </section> </div> <div>')
                 $('#b_h_'+ r_id).on('click', function(e){
                	 $('#m_h_title').html(r_name)
                	 var timelineHtmlMessage = '<div class="msg-time-chat"> <div class="message-body msg-in"> <span class="arrow"></span> <div class="text"> <div class="first">'+ r_createdOpenMemories +'</div> <a href="./downloadFileHadoop/'+hadoopDir+'/' + name +'/' + fileName +'"><div class="second bg-terques "> Scarica questa versione</div> </div></a> </div> </div>'
                	 if (r_history != null){
                		 r_history.map(function(hist){
                			var h_name = hist.resource_name
                			var h_id = hist.resource_id
                			var h_format =hist.resource_format
                			var date_created = hist.resource_createdOn
                			var h_hadoop = hist.resource_hadoop_path
                			var h_hadoopDir = h_hadoop.split("/")[5]
                		//	var h_fileName = h_id + '.' + h_format
                			var h_fileName = r_hadoop.split("/")[7]
                		    timelineHtmlMessage = timelineHtmlMessage + '<div class="msg-time-chat"> <div class="message-body msg-in"> <span class="arrow"></span> <div class="text"> <div class="first">'+ date_created +'</div> <a href="./downloadFileHadoop/'+h_hadoopDir+'/' + name  +'/' + h_fileName+ '"><div class="second bg-terques "> Scarica questa versione</div></a> </div> </div> </div>'                		 
                		 }) 		 
                	 }
                	 $("#downloadZip").attr('href','./downloadZip/' + r_id)	
                	 $('#timeline').html(timelineHtmlMessage)
                	 $('#modal_history').modal('show');
                 })
             })
             $('#tr_openMemories').append(ht)
             $('#open_'+id).on('click', function(e){
        			$('#resources').html('')
        			var currentId = e.target.id.split('_')[1]
        			var currentItem = idItem[currentId]
        			updataMainPanel(currentItem)
        		})
        	} else {
        		ht = '<li><a href="#" id="open_'+id+'">' + item.title + '</a></li>'
                $('#tr_openMemories').append(ht)
        		$('#open_'+id).on('click', function(e){
        			$('#resources').html('')
        			var currentId = e.target.id.split('_')[1]
        			var currentItem = idItem[currentId]
        			updataMainPanel(currentItem)
        		})
        	}

          }
      //    })
		},
		error: function(xhr, ajaxOptions, thrownError) {
		// spinner.stop();
		}
	});	

})