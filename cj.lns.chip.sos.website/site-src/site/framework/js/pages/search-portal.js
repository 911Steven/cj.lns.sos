$(document).ready(function(){
	$('.topbar > ul > .login').on('click',function(e){
		window.location.href='./public/login.html';
	});
	$('.topbar > ul > .mine').on('click',function(e){
		window.location.href='./public/register2.html';
	});
	$('.content > .topbar > ul.right>.me').on('click',function(e){
		window.location.href='./?swsid='+$(this).attr('swsid');
	});
	$('.content > .banner > .b-center > .logo').on('click',function(e){
		window.location.href="./";
	});
	var menuli=$('.content > .banner > .b-center > .systems > .menu > .mlist > li');
	menuli.hover(function(){
		$(this).parent('.mlist').find('>li').removeClass('tab-selected');
		$(this).addClass('tab-selected');
		var swsid=$(this).attr('swsid');
		var tabs=$('.content > .abstract > .a-tabs');
		tabs.find(">li.tab-show").removeClass('tab-show');
		tabs.find(">li[swsid="+swsid+']').addClass('tab-show');
	},function(){
		
	});
	menuli.on('click',function(e){
		window.location.href="./?swsid="+$(this).attr('swsid');
	})
});