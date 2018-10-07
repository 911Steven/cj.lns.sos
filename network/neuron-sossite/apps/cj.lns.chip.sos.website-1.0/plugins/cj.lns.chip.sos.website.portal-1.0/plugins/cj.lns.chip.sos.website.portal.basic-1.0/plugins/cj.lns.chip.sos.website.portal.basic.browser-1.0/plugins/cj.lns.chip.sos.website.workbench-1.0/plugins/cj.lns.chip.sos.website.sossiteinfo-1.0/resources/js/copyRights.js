$(document).ready(function() {
	
	$('.copyright>span[label]').hover(function(e){
		$('.copyright').attr('style','-moz-border-radius: 0;-webkit-border-radius: 0;border-radius:0;display:block;');
		$('.copyright>.detail').show();
	});
	$('.copyright').hover(function(e){},function(e){
		$('.copyright>.detail').hide();
		$('.copyright').attr('style','display: inline-block; -moz-border-radius: 0 15px 0 0;-webkit-border-radius: 0 15px 0 0; border-radius:0 15px 0 0; ');
	});
});