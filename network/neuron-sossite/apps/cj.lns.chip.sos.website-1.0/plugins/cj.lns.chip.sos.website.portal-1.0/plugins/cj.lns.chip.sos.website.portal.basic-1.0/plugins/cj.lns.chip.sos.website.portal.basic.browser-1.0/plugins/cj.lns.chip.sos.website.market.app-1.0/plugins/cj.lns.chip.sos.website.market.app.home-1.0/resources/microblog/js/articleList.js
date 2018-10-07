(function() {
	$('.art-panel>.more').on('click', function(e) {
		var categoryId='';
		var selected=$('.windows > .titleBar > .menu > ul > li[selected]');
		var cmd='';
		var limit=10;
		var the=$(this);
		var skipE=$(this).attr('skip');
		var skip=limit;
		if(typeof skipE!='undefined'&&skipE!=''){
			skip=skipE;
		}
		if(selected.length==0){
			cmd='./swssite/microblog/myarticle.html?skip='+skip+'&limit='+limit;
		}else{
			cmd=selected.attr('cmd')+'&skip='+skip+'&limit='+limit;
		}
		$.get(cmd,{},function(html){
			var div=document.createElement('div');
			$(div).html(html);
			var lis=$(div).find('.art-panel>.art-ul>li');
			if(lis.length<1){
				$('.art-panel>.more').remove();
				return;
			}
			$('.art-panel>.art-ul').append(lis);
			the.attr('skip',(parseInt(skip)+parseInt(limit)));
		});
	});
	$('.art-panel>.art-ul').undelegate('>.article>.body>.title','click');
	$('.art-panel>.art-ul').delegate('>.article>.body>.title','click',function(e){
		var id=$(this).parents('.article').attr('id');
		var cmd='./swssite/microblog/openArticle.html?id='+id;
		$.cj.App.openApp(cmd);
	});
	$('.art-panel>.art-ul').undelegate('> .article > .body > .op > ul > li[action]','click');
	$('.art-panel>.art-ul').delegate('> .article > .body > .op > ul > li[action]','click',function(e){
		var article=$(this).parents('.article');
		var id=$(this).parents('.article').attr('id');
		var cmd='./swssite/microblog/doArticle.html?id='+id;
		var action=$(this).attr('action');
		switch(action){
		case 'del':
			break;
		default://如果是持有人则为打开，是访问者则评论或分享
			$.cj.App.openApp('./swssite/microblog/openArticle.html?id='+id);
			break;
		}
		
		$.get(cmd,{id:id,action:action},function(e){
			article.remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	$('.art-panel>.art-ul').undelegate('> .article > .body > .second > span[visits]>span[label]','click');
	$('.art-panel>.art-ul').delegate('> .article > .body > .second > span[visits]>span[label]','click',function(e){
		var the=$(this).parent('span[visits]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.article');
		var sourceid=art.attr('id');
		var entity='myarticle';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
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
	$('.art-panel>.art-ul').undelegate('> .article > .body > .second > span[visits]','mouseleave');
	$('.art-panel>.art-ul').delegate('> .article > .body > .second > span[visits]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	$('.art-panel>.art-ul').undelegate('.e-r-v>.more>span[label]','click');
	$('.art-panel>.art-ul').delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.article');
		var sourceid=art.attr('id');
		var entity='myarticle';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
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
	$('.art-panel>.art-ul').undelegate('.e-r-v>.events>li[user]','click');
	$('.art-panel>.art-ul').delegate('.e-r-v>.events>li[user]','click',function(e){
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
	$('.art-panel>.art-ul').undelegate('.e-r-v>.events>li[user]','mouseleave');
	$('.art-panel>.art-ul').delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();