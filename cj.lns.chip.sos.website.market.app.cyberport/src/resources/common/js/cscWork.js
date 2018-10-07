(function(){
	$('.csc-work > .footer > .typed > .op > img').on('click',function(e){
		$(this).parents('.typed').siblings('.push-services').toggle();
	});
	
	var area=$('.csc-work > .footer > .typed > .text > .textarea');
	$('.push-services > .list > li').on('click',function(e){
		var developer=$(this).attr('developer');
		var chip=$(this).attr('chip');
		var csckey=$(this).attr('csckey');
		$.post('./cyberportApp/common/cscPusher.service',{developer:developer,chip:chip,csckey:csckey},function(data){
			area.html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	$('.csc-work > .header > .slider > li').hover(function(e){
		$(this).parent('.slider').siblings('.panel').show();
	},function(e){});
	$('.csc-work > .header > .panel').hover(function(e){},function(e){
		$(this).hide();
	});
	$('.csc-work > .header > .panel[item="service"] span[m][csckey]').on('click',function(e){
		var developer=$(this).attr('developer');
		var chip=$(this).attr('chip');
		var csckey=$(this).attr('csckey');
		var cnt=$('.csc-work > .content').first();
		var response=cnt.find('>response').first().clone();
		$.post('./cyberportApp/common/cscPusher.service',{developer:developer,chip:chip,csckey:csckey},function(data){
			$('.csc-work > .content > .response > .body').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	})
})();