var hanler=$('.content > .h-zw > .h-center');
	hanler.undelegate('.portlet[type=develop]>.d-pics>.d-items>li','click');
	hanler.delegate('.portlet[type=develop]>.d-pics>.d-items>li','click',function(e){
		var show=$('.portlet[type=develop]>.d-pics>.d-show');
		show.find('>img').attr('src',$(this).find('>img').attr('src'));
		show.find('.title').html($(this).find('>.title').html());
	});