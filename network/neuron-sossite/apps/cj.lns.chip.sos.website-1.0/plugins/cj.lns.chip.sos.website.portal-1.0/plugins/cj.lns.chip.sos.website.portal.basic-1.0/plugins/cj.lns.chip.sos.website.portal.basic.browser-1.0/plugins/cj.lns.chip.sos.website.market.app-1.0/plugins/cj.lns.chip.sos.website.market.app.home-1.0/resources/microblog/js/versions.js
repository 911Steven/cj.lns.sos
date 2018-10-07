(function(){
	var cnt=$('.o-product > .main > .content');
	cnt.undelegate('.v-panel > .releases > .release > .stress > li[issues]','click');
	cnt.delegate('.v-panel > .releases > .release > .stress > li[issues]','click',function(e){
		$(this).children('.panel').toggle();
	});
	cnt.undelegate('.v-panel > .releases > .release > .title>span[value]','click');
	cnt.delegate('.v-panel > .releases > .release > .title>span[value]','click',function(e){
		var file=$(this).parents('li.release').attr('file');
		var iframe="<iframe style='display:none' src='"+file+"'><iframe>";
		var pid=$('.o-product').attr('pid');
		var releaseid=$(this).parents('.release').attr('releaseid');
		var the=$(this);
		$.post('./swssite/microblog/editProduct.service',{pid:pid,releaseid:releaseid,editor:'product.versions.downloads'},function(data){
			the.append(iframe);
			var v=the.parents('.release').find('> .stress > li[dowloads] > span[value]');
			var i=parseInt(v.html())+1;
			v.html(i+'');
		});
		
	});
	
	cnt.undelegate('.v-panel > .releases > .release','mouseover mouseout');
	cnt.delegate('.v-panel > .releases > .release','mouseover mouseout',function(e){
		if(e.type=='mouseover'){
			$(this).find('>span[remove]').show();
			$(this).find('>span[edit]').show();
		}if(e.type=='mouseout'){
			$(this).find('>span[remove]').hide();
			$(this).find('>span[edit]').hide();
		}
	});
	cnt.undelegate('.v-panel > .releases > .release>span[remove]','click');
	cnt.delegate('.v-panel > .releases > .release>span[remove]','click',function(e){
		var pid=$('.o-product').attr('pid');
		var releaseid=$(this).parent('.release').attr('releaseid');
		var the=$(this).parent('.release');
		$.post('./swssite/microblog/editProduct.service',{pid:pid,releaseid:releaseid,editor:'product.versions.del'},function(data){
			the.remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	//下载
	var plist=$('.o-product');
	plist.undelegate('.v-panel > .releases > .release > .stress > li[dowloads] > span[label]','mouseenter');
	plist.delegate('.v-panel > .releases > .release > .stress > li[dowloads] > span[label]','mouseenter',function(e){
		var the=$(this).parent('li[dowloads]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.o-product');
		var release=$(this).parents('.release');
		var sourceid=art.attr('pid');
		var releaseid=release.attr('releaseid');
		var entity='myproduct';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			releaseid:releaseid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
			the.find('div.e-r-v').attr('style','right:auto;left:0;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('.v-panel > .releases > .release > .stress > li[dowloads]','mouseleave');
	plist.delegate('.v-panel > .releases > .release > .stress > li[dowloads]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.o-product');
		var release=$(this).parents('.release');
		var releaseid=release.attr('releaseid');
		var sourceid=art.attr('pid');
		var entity='myproduct';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			releaseid:releaseid,
			kind:kind,
			sourceid:sourceid,
			entity:entity,
			skip:skip
			},
		function(data){
				events.append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	plist.undelegate('.e-r-v>.events>li[user]','click');
	plist.delegate('.e-r-v>.events>li[user]','click',function(e){
		var user=$(this).attr('user');
		var box=$(this);
		box.parent('.events').find('.user-face').remove();
		box.append("<div class='user-face' style='right:auto;left:0;'></div>");
		$.get('./servicews/viewUserProfile.service',{user:user},function(data){
			box.find('>.user-face').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('.e-r-v>.events>li[user]','mouseleave');
	plist.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();