(function(){
	$('.service-center>span[close]').on('click',function(e){
		$(this).parents('.service-center').hide();
	});
	
	$('.sc-panel>.header>.slider>li').hover(function(e){
		var panels=$('.sc-panel>.header>.panel');
		panels.hide();
		$('.sc-panel>.header>.panel[item='+$(this).attr('item')+']').show();
	},function(e){});
	$('.sc-panel>.header>.panel').hover(function(e){},function(e){
		$(this).hide();
	});
})();