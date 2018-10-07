(function(){
	var main=$('.workbench > ul > li > .main > .main-left[region=portlet]');
	main.undelegate('.portlet[cube] > .cube-tips > ul.statistics > li[col] > span[value]','mouseover mouseout');
	main.delegate('.portlet[cube] > .cube-tips > ul.statistics > li[col] > span[value]','mouseover mouseout',function(e){
		if(e.type=='mouseover'){
			$(this).parent('li[col]').children('.panel').show();
		}else if(e.type=='mouseout'){
			$(this).parent('li[col]').children('.panel').hide();
		}
	});
	main.undelegate('.portlet[cube] > .cube-tips > ul.statistics > li[volume] > span[value]','mouseover mouseout');
	main.delegate('.portlet[cube] > .cube-tips > ul.statistics > li[volume] > span[value]','mouseover mouseout',function(e){
		if(e.type=='mouseover'){
			$(this).parent('li[volume]').children('.panel').show();
		}else if(e.type=='mouseout'){
			$(this).parent('li[volume]').children('.panel').hide();
		}
	});
	main.undelegate('.portlet[cube] > .cube-face > .face','click');
	main.delegate('.portlet[cube] > .cube-face > .face','click',function(e){
		var cube=$('#cube').val();
		if('home'==cube){
			$.cj.App.openApp('appi://site.portal.develop.homespace');
		}else{
			$.cj.App.openApp('appi://site.portal.develop.myspace');
		}
	});
})();