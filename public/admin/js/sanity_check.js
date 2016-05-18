$(document).ready(function() {
	$.ajax({
		url: '/expired_dataset_resource',
		async: true,
	    type: "GET",
		success: function(data) {
           console.log(data)
        //   var dataJson = JSON.parse(data)
           data.map(function(dataset){
        	   var created_date = dataset.modified
        	   var aggiornamento = dataset.Aggiornamento
        	   var expiration = dataset.expiration
        	   $('#body_res').append( '<tr> <td><a href="#">'+dataset.title+'</a></td> <td >'+ created_date +'</td> <td>'+aggiornamento+'</td> <td><span class="label label-danger label-large">'+expiration+'</span></td>  </tr>')
           })      
		},
		error: function(data) {

		}
	});
	
	$.ajax({
		url: '/expired_dataset',
		async: true,
	    type: "GET",
		success: function(data) {
           console.log(data)
        //   var dataJson = JSON.parse(data)
           data.map(function(dataset){
        	   var created_date = dataset.modified
        	   var aggiornamento = dataset.Aggiornamento
        	   var expiration = dataset.expiration
        	   $('#body_agg').append( '<tr> <td><a href="#">'+dataset.title+'</a></td> <td >'+ created_date +'</td> <td>'+aggiornamento+'</td> <td><span class="label label-danger label-large">'+expiration+'</span></td>  </tr>')
           })      
		},
		error: function(data) {

		}
	});
	
	$.ajax({
		url: '/without_category_dateset',
		async: true,
	    type: "GET",
		success: function(data) {
           console.log(data)
           var dataJson = JSON.parse(data)
           dataJson.map(function(dataset){

        	   $('#body_cat').append( '<tr> <td><a href="#">'+dataset.title+'</a></td>  </tr>')
           })      
		},
		error: function(data) {

		}
	});
})