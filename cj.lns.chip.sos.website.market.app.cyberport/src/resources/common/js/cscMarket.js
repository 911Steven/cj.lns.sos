(function(){
	var market=$('.market > .list');
	
	function loadCscChipInfo(marketid){
		$.get('./cyberportApp/common/cscChipInfo.html',{marketid:marketid},function(data){
			var e=$('.market').find('.scsChipInfo');
			e.html(data);
			e.show();
		}).error(function(e){
			alert(e.responseText);
		});
	}
	
	market.undelegate('>.zdxz > ul > li','click');
	market.delegate('>.zdxz > ul > li','click',function(e){
		var marketid=$(this).attr('marketid');
		loadCscChipInfo(marketid);
	});
	market.undelegate('> .table > li>img,> .table > li>.face>li>span','click');
	market.delegate('> .table > li>img,> .table > li>.face>li>span','click',function(e){
		var marketid=$(this).parents('li[marketid]').attr('marketid');
		loadCscChipInfo(marketid);
	});
	
	$('.market').undelegate('.cinfo > span[close]','click');
	$('.market').delegate('.cinfo > span[close]','click',function(e){
		var ctr=$(this).parents('.scsChipInfo');
		ctr.empty();
		ctr.hide();
	})
	$('.market').undelegate('.cinfo > .op>li[status=install]','click');
	$('.market').delegate('.cinfo > .op>li[status=install]','click',function(e){
		if($(this).attr('installed')=='yes')return;
		//安装芯片
		var the=$(this);
		var marketid=$(this).attr('marketid');
		$.get('./cyberportApp/common/cscInstallChip.service',{marketid:marketid},function(data){
			the.attr('installed','yes');
			the.html('安装成功');
		}).error(function(e){
			alert(e.responseText);
		});
	})
	$('.market>.category > ul > li').on('click',function(e){
		$(this).parent('ul').children('li').removeClass('cat-selected');
		$(this).addClass('cat-selected');
		var path=$(this).attr('path');
		$.get('./cyberportApp/common/cscMarketChips.html',{path:path},function(data){
			$('#ctgy-cnt').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.market>.category > ul > li.cat-selected').trigger('click');
	
	var category=$('.main>.main-right[region=portlet]');
	category.undelegate('.csc-category > .table > li','click');
	category.delegate('.csc-category > .table > li','click',function(e){
		var marketid=$(this).attr('marketid');
		loadCscChipInfo(marketid);
	});
	
	$('.market').undelegate('> .list>.table > .cimore','click');
	$('.market').delegate('> .list >.table> .cimore','click',function(e){
		var path=$(this).attr('path');
		var skip=$(this).attr('skip');
		var the=$(this);
		$.get('./cyberportApp/common/cscMarketChips.html',{path:path,skip:skip},function(data){
			the.remove();
			$('.market > .list > .table').append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
})();