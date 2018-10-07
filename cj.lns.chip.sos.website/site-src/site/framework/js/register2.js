$(document).ready(function() {
	var typedli = $('.register>.box>.left>ul>li');
	var hasValue = typedli.children('input');
	
		hasValue.each(function(i, e) {
			if ($(e).val() != '') {
			$(e).siblings('span').hide();
			}else{
				$(e).siblings('span').show();
			}
		});
	
	typedli.children('span').on('click',function(e){
		$(this).hide();
	});
	typedli.children('input').focus(function(e) {
		$(this).siblings('span').hide();
	});
	typedli.children('input').blur(function(e) {
		var input = $(this);
		if (input.val() == '') {
			$(this).siblings('span').show();
		} else {
			input.css('border', 'none');
		}
	});
	$('.register>.box>.left>.op>input').on('click', function(e) {
		var hasValue = typedli.children('input');
		var hasEmpty=false;
		var stop=false;
		hasValue.each(function(i, e) {
			if ($(e).val() == '') {
				$(e).css('border', '1px solid red');
				hasEmpty=true;
			}else{
				if(typeof $(e).parent('li').attr('nd')!='undefined'){
					if(isNaN($(e).val())){
						alert('网盘大小必须输入数字');
						stop=true;
					}
				}else if(typeof $(e).parent('li').attr('home')!='undefined'){
					if(isNaN($(e).val())){
						alert('主存大小必须输入数字');
						stop=true;
					}
				}
			}
		});
		if(stop){
			return;
		}
		if(hasEmpty){
			return;
		}
		var left=$('.register>.box>.left');
		var uid=$('.register > .box > .left>ul>li[uid]>input').val();
		var nick=$('.register > .box > .left>ul>li[nick]>input').val();
		var pwd=$('.register > .box > .left>ul>li[pwd]>input').val();
		var check=$('.register > .box > .left>ul>li[check]>input').val();
		var sex=$('.register > .box > .left>ul>li[sex]>input:checked').val();
		var home=$('.register > .box > .left>ul>li[home]>input').val();
		var nd=$('.register > .box > .left>ul>li[nd]>input').val();
		
		var faceImg=$('.register > .box > .right > .face>img').attr('fn');
		if(pwd!=check){
			alert('输入的确认密码不一致，请重输');
			$('.register > .box > .left>ul>li[check]>input').val('');
			return;
		}
		if(nd>10){
			alert('网盘不能超过10g，俺小公司没那么多钱');
			return;
		}
		if(home>nd/2){
			alert('主空间不能超过网盘的一半，因为还要留一部分作为视窗盘片');
			return;
		}
		nd=parseInt(nd)*1024*1024*1024;
		home=parseInt(home)*1024*1024*1024;
		//注册成功后则隐藏
		var data={
				userCode:uid,
				nickName:nick,
				password:pwd,
				homeSize:home,
				capcity:nd,
				sex:sex,
				faceImg:faceImg
		};
		var url='../pages/registerUser.service';
		$.post(url,data,function(){//注册成功,则模拟登录，在登录逻辑中发现没有视窗则跳到视窗列表界面
			var	url = "../pages/loginService";
			var jqxhr = $.post(
					url,
					{
						account : uid,
						password : pwd
					},
					function(data) {
						data = $.parseJSON(data.replace(/\'/g, "\""));
						if(data.hasDefaultSws=='true'){//尝试访问视窗
							window.location.href = '../?swsid=' + data.defaultSwsId;//视窗的打开就是谁访问这个视窗
						}else{//打开视窗列表
							window.location.href='../auth/listServicews.html?owner='+uid;
						}
						
					})
			.error(function(e) {
				if(e.status!=200){
					alert(e.status+' '+e.statusText);
				}
			});
		}).error(function(e) {
			if(e.status!=200){
				alert(e.status+' '+e.statusText);
			}
		});
		
	});
	
	var options=$('.register>.box>.right>.options>li');
	options.on('click',function(e){
		var src=$(this).children('img').attr('src');
		var fn=$(this).children('img').attr('fn');
		var img=$('.register>.box>.right>.face>img');
		img.attr('src',src);
		img.attr('fn',fn);
	});
});