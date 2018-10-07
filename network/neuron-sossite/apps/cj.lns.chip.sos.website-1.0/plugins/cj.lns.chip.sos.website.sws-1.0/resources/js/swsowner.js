(function(){
	var display = $('.sws-cnt-o>.o-pic>img[display]');
	var pos = display.position();
	var top = pos.top;
	var left = pos.left;
	var height = display.height();
	var width = display.width();
	var changeWid = 150;
	var changehei = 150;
	display.hover(function() {
		display.css('top', (top - changehei) + 'px');
		display.height(height + changehei);
		display.css('left', (left - changeWid) + 'px');
		display.width(width + changehei);
	}, function() {
		display.css('top', top + 'px');
		display.height(height);
		display.css('left', left + 'px');
		display.width(width);
	});

	var selImgs = $('.sws-cnt-o>.o-pic>.o-pic-sels>li');
	selImgs.hover(function() {
		var src = $(this).children('img').attr('src');
		display.attr('src', src);
		display.parent('.o-pic').attr('fn',$(this).attr('fn'));
	}, function() {

	});
	
	$('.sws-cnt-o > .o-owner > .o-sections > ul > li > div[sws] > a').on('click',function(e){
		window.location.href='./?swsid='+$(this).attr('swsid');
	});
	
	var sin=$('.sws-cnt-o > .o-pic > .o-sin');
	sin.undelegate('> p','click');
	sin.delegate('> p','click',function(e){
		
		var p=$(this)
		var sin=$(this).parent('.o-sin');
		sin.find('>#sign').remove();
		if(sin.attr('e')!='yes')return;
		var text=p.html();
		if(typeof text=='undefined'){
			text='';
		}
		var copy=p.clone();
		p.remove();
		var editting=false;
		sin.append("<textarea title='回车提交修改内容' id='sign'>"+text+"</textarea>");
		sin.delegate('>#sign','keyup',function(e){
			if(!editting&&e.keyCode=='13'){
				editting=true;
				var val=$(this).val();
				val=val.replace(/\n/g, "");
				if(val=='')	{
					alert('签名为空');
					return;
				}
				if(val.length>56)	{
					alert('签名不能超过28字');
					return;
				}
				$.post('./servicews/viewUserProfile.service',{action:'edit-sign',val:val},function(data){
					var ta=sin.find('>#sign');
					copy.html(ta.val());
					ta.remove();
					sin.append(copy);
				}).error(function(e){
					alert(e.responseText);
				});
			}
		});
	});
	
	var nick=$('.sws-cnt-o > .o-owner > .o-card > ul > li[nick]');
	nick.undelegate('> span[text]','click');
	nick.delegate('> span[text]','click',function(e){
		
		var p=$(this)
		nick=$(this).parents('li[nick]');
		nick.find('>#nick').remove();
		if(nick.attr('e')!='yes')return;
		
		var text=p.html();
		if(typeof text=='undefined'){
			text='';
		}
		var copy=p.clone();
		p.remove();
		var editting=false;
		nick.append("<input title='回车提交修改内容' maxlength='12' value='"+text+"' id='nick'>");
		nick.delegate('>#nick','keyup',function(e){
			if(!editting&&e.keyCode=='13'){
				editting=true;
				var val=$(this).val();
				val=val.replace(/\n/g, "");
				if(val=='')	{
					alert('昵名为空');
					return;
				}
				$.post('./servicews/viewUserProfile.service',{action:'edit-nick',val:val},function(data){
					var ta=nick.find('>#nick');
					copy.html(ta.val());
					ta.remove();
					nick.append(copy);
				}).error(function(e){
					alert(e.responseText);
				});
			}
		});
	});
	
	var briefing=$('.sws-cnt-o > .o-owner > .o-briefing');
	briefing.undelegate(' > pre','click');
	briefing.delegate(' > pre','click',function(e){
		
		var p=$(this)
		briefing=$(this).parents('.o-briefing');
		briefing.find('>#briefing').remove();
		if(briefing.attr('e')!='yes')return;
		
		var text=p.html();
		if(typeof text=='undefined'){
			text='';
		}
		var copy=p.clone();
		p.remove();
		var editting=false;
		briefing.append("<textarea title='回车提交修改内容'  id='briefing'>"+text+"</textarea>");
		briefing.delegate('>#briefing','keyup',function(e){
			if(!editting&&e.keyCode=='13'){
				editting=true;
				var val=$(this).val();
				val=val.replace(/\n/g, "");
				if(val=='')	{
					alert('简介为空');
					return;
				}
				if(val.length>256)	{
					alert('简介不能超过128字');
					return;
				}
				$.post('./servicews/viewUserProfile.service',{action:'edit-briefing',val:val},function(data){
					var ta=briefing.find('>#briefing');
					copy.html(ta.val());
					ta.remove();
					briefing.append(copy);
				}).error(function(e){
					alert(e.responseText);
				});
			}
		});
	});
	
	var shower=$('.sws-cnt-o > .o-pic');
	shower.undelegate('> span[set]','click');
	shower.delegate('> span[set]','click',function(e){
		var fn=$(this).parent('.o-pic').attr('fn');
		var the=$(this);
		the.html('更新中...');
		$.post('./servicews/viewUserProfile.service',{action:'edit-pic',fn:fn},function(data){
			the.html('设置成功，重新登录后生效');
			
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var swslist=$('.sws-cnt-o>.o-owner>.o-sections>ul');
	swslist.undelegate('>li[sws]>a','click');
	swslist.delegate('>li[sws]>a','click',function(e){
		window.location.href='./?swsid='+$(this).attr('swsid');
	});
})();