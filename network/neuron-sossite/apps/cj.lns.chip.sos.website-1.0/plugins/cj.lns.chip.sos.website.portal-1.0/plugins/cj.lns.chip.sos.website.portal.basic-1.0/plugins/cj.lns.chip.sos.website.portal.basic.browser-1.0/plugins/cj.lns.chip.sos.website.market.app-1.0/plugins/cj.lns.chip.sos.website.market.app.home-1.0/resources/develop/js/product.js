(function(){
	$('.my-product>.list>.product').hover(function(e){
		$(this).children('span[close]').show();
	},function(){
		$(this).children('span[close]').hide();
	});
	$('.my-product>.list>.product>span[close]').on('click',function(e){
		var the=$(this);
		var pid=the.parent('.product').attr('pid');
		$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.entitiy.delAllInfo'},function(data){
			the.parent('.product').remove();
		}).error(function(e){
			alert(e.responseText);
		})
	});
	$('.my-product > .list > .product > .abstract > pre > span[extends]').on('click',function(e){
		var pid=$(this).parents('.product').attr('pid');
		$.get('./swssite/develop/getProductMenu.html?menu=abstract',{pid:pid},function(data){
			$('.my-product > .list > .product > .abstract>pre').remove();
			$('.my-product > .list > .product > .abstract').prepend(data);
		}).error(function(e){
			alert(e.responseText);
		})
	});
	$('.my-product > .list > .product > .face > img').on('click',function(e){
		var pid=$(this).parents('.product').attr('pid');
		var cmd='./swssite/develop/openProduct.html?id='+pid;
		$.cj.App.openApp(cmd);
	});
	
	var plist=$('.my-product > .list');
	plist.undelegate('> .product > .face > p[visitor]>span','click');
	plist.delegate('> .product > .face > p[visitor]>span','click',function(e){
		var the=$(this).parent('p[visitor]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.product');
		var sourceid=art.attr('pid');
		var entity='myproduct';
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
	plist.undelegate('> .product > .face > p[visitor]','mouseleave');
	plist.delegate('> .product > .face > p[visitor]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.product');
		var sourceid=art.attr('pid');
		var entity='myproduct';
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