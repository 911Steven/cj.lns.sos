$(document).ready(function() {
	$('.sws-win>.center>.kind>.bar>.op>a').on('click',function(e){
		var kind=$(this).parents('.kind');
		var kv='';
		if(typeof kind.attr('psws')!='undefined'){
			kv='csws';
		}else if(typeof kind.attr('csws')!='undefined'){
			kv='bsws';
		}else if(typeof kind.attr('bsws')!='undefined'){
			kv='ssws';
		}else{
			alert('不支持的视窗类型');
		}
		var url='../auth/applyServicews.html';
		$.get(url,{
			kind:kv
		},function(data){
			//返回新申请的视窗id，这里再取出来显示
			var popup=$('.popup');
			popup.children('.box').html(data);
			popup.show();
		});
	});
	$('.popup').on('click',function(e){
		if($(e.target).hasClass('popup')){
		$(this).hide();
		}
	});
	$('.sws-win > .right > .op>span[enter]>a').on('click',function(e){
		var checked=$('.sws-win > .right > .op>span[setDefault]>input').prop('checked');
		var swsid=$(this).parents('.op').attr('select-swsid');
		if(checked){
			var owner=$(this).parents('.op').attr('select-owner');
			//ajax到后台，如果成功则成功不成功都进入视窗
			$.post('../public/setDefaultSws.html',{owner:owner,swsid:swsid},function(e){
				window.location.href = '../?swsid=' + swsid;
			}).error(function(e){
				alert('error:'+e.responseText);
			});
			return;
		}
		window.location.href = '../?swsid=' + swsid;
	});
	$('.center').undelegate('>.kind>.list>li[swsid]','click');
	$('.center').delegate('>.kind>.list>li[swsid]','click',function(e){
		var swsid=$(this).attr('swsid');
		var url='../servicews/getServicewsBody2.service';
		$.get(url,{swsid:swsid},function(data){
			data=$.parseJSON(data);
			var sws=eval(data);
			$('.sws-win > .right > .op').attr('select-swsid',sws.swsid);
			$('.sws-win > .right > .op').attr('select-owner',sws.owner);
			var face=$('.sws-win > .right > .face');
			face.children('img').attr('src',sws.faceImg);
			face.children('p').html(sws.swsName);
			
			var detail=$('.sws-win > .right > .detail');
			detail.find('li[owner]>span[value]').html(sws.owner);
			detail.find('li[swsid]>span[value]').html(sws.swsid);
			if(sws.capacity==-1){
				detail.find('li[capacity]>span[value]').html('不限');
				detail.find('li[capacity]>span[unit]').html('&nbsp;')
			}else{
				detail.find('li[capacity]>span[value]').html((sws.capacity/1024/1024/1024).toFixed(6));
			}
			detail.find('li[useSpace]>span[value]').html((sws.useSpace/1024/1024/1024).toFixed(6));
			detail.find('li[dataSize]>span[value]').html((sws.dataSize/1024/1024/1024).toFixed(6));
			var extra=detail.find('>.extra>ul');
			extra.empty();
			var level='个人视窗';
			switch(sws.level){
			case 0:
				level='超级视窗';
				break;
			case 1:
				level='基础视窗';
				var li='<li platform><span label>平台：</span><span value>'+sws.extra.platform+'</span><span unit>&nbsp</span></li>';
				extra.append(li);
				break;
			case 2:
				level='公共视窗';
				console.log(sws.extra);
				if(typeof sws.extra.crop=='undefined'){
					sws.extra.crop='&nbsp;';
				}
				if(typeof sws.extra.address=='undefined'){
					sws.extra.address='&nbsp;';
				}
				if(typeof sws.extra.home=='undefined'){
					sws.extra.home='&nbsp;';
				}
				var li='<li crop><span label>公司/组织：</span><span value>'+sws.extra.crop+'</span><span unit>&nbsp</span></li>';
				extra.append(li);
				li='<li address><span label>地址：</span><span value>'+sws.extra.address+'</span><span unit>&nbsp</span></li>';
				extra.append(li);
				li='<li home><span label>主页：</span><span value>'+sws.extra.home+'</span><span unit>&nbsp</span></li>';
				extra.append(li);
				break;
			case 3:
				level='个人视窗';
				if(typeof sws.extra.hobby=='undefined'){
					sws.extra.home='&nbsp;';
				}
				var li='<li hobby><span label>兴趣/爱好：</span><span value>'+sws.extra.hobby+'</span><span unit>&nbsp</span></li>';
				extra.append(li);
				break;
			}
			detail.find('li[level]>span[value]').html(level);
			var intro="<li intro><span label>简介：</span><p>"+sws.extra.intro+"</p></li>"
			extra.append(intro);
			
			if(detail.parent('.right').is(':hidden')){
				detail.parent('.right').show();
			}
		}).error(function(e){
			alert('error:'+e.responseText);
		});
	});
});