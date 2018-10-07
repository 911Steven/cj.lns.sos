$(document).ready(function() {
	$('.portlet[owner]>.box>img,.portlet[owner]>.box>ul>li[name]').on('click',function(e){
		var swsid=$(this).parents('.box').attr('swsid');
		window.location.href='./?swsid='+swsid;
	});
	$('.portlet[owner] > .face > .img>img,.portlet[owner] > .face > .img>p>span[label]').on('click',function(e){
		var menu=$('.startMenu[region]');
		menu.find(".sm-box>li[cjevent='openSwsPanel(currentSws)']> a[cmd]").trigger('mouseenter','posAtOwner');
	});

	$('.portlet[swsviewer] >img,.portlet[swsviewer] >p.name').on('click',function(e){
		var menu=$('.startMenu[region]');
		menu.find(".sm-box>li[cjevent='openSwsPanel(currentSws)']> a[cmd]").trigger('mouseenter');
	});
});