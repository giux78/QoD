$(document).ready(function() {
	var userId = $('#userid').val()
	$.ajax({
		url: './mydataset/' + userId,
		async: true,
	    type: "GET",
		success: function(data) {
           console.log(data)
           data.map(function(q){
        	   var query = q.query
        	   var desc  = q.description
        	   var url = q.url
        	   var name = q.name
        	   var htmlQuery = '<hr class="divider"><div class="row"> <div class="form-group"> <div class="col-lg-12"> <label class="col-lg-2 control-label">Name</label> <span class="col-lg-4 label label-info">' + name+'</span> <label class="col-lg-2 control-label"># API CALLS</label> <div class="col-lg-4"> <span class="badge bg-info">0</span> </div> </div> </div> <div class="form-group" > <div class="col-lg-12" style="margin-top : 10px"> <label class="col-lg-2 control-label">URL</label> <div class="col-lg-10"> <input  class="form-control input-sm" id="disabledInput" type="text" name="http://localhost:9001/query/@user.id" placeholder="' + url +'" disabled> </div> </div> </div> <div class="form-group"> <div class="col-lg-12"> <label class="col-lg-2 control-label">SQL Query</label> <label class="col-lg-4 control-label"></label> <label class="col-lg-2 control-label">Description</label> <div class="col-lg-4"> <span class="badge bg-info"></span> </div> </div> </div></div><div class="row"><div class="col-md-6"><textarea style="font-family: Verdana, sans-serif;" class="form-control" rows="6">'+ query +'</textarea></div><div class="col-md-6"> <div style="height: auto;max-height: 150px;overflow-x: hidden;"> <p class="list-group-item-text">'+ desc +'</p><div class="list-group-item-text" ></div> </div></div></div>'
        	   $('#myQueries').prepend(htmlQuery)
           })
           
		},
		error: function(data) {

		}
	});
})