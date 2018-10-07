(function(){
	$('.sceneset > .content-panel > .wall > .pics>ul>li').on('click',function(e){
		var file=$(this).attr('file');
		var src=$(this).find('>img').attr('src');
		$(this).parents('.wall').find(' > .shower > div[box] > img').attr('src',src);
		$(this).parents('.wall').find(' > .shower > div[box]').attr('file',file);
	});
	$('.sceneset > .content-panel > .wall > .shower > div[box] > span[set]').on('click',function(e){
		
		$.get('./servicews/components/setScene.service',{
			action:'setWall',
			wall:$(this).parent('div[file]').attr('file'),
			scene:$(this).parents('.sceneset').attr('scene')
		},function(data){
			alert('成功，刷新桌面后生效');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.bop > .content-panel > .themes > .theme').on('click',function(e){
		var check=$(this).find('input:radio');
		$.get('./servicews/components/setScene.service',{
			action:'setTheme',
			theme:$(this).attr('theme'),
			scene:$(this).parents('.sceneset').attr('scene')
		},function(data){
			check.prop('checked',true);
			alert('成功，刷新桌面后生效');
		}).error(function(e){
			alert(e.responseText);
		});
		
	});
})();