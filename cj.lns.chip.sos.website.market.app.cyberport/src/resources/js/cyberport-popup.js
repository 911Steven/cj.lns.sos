(function(){
	$('.cyberport-box>.content>.right>.apps>.app:first-child>.box').toggle();
	$('.cyberport-box').undelegate('>.content>.right>.apps>.app>.head', 'click');
	$('.cyberport-box').delegate('>.content>.right>.apps>.app>.head', 'click',
			function(e) {
		e.stopPropagation();
				$(this).parent().children('.box').toggle();
				return false;
			});
	
	$('.cyberport-box>.content>.left>.cyb-sws>.session').on('click', function(e) {
		e.stopPropagation();
		var apps = $('.cyberport-box>.content>.right>.apps');
		apps.html('loadding...');
		$.get("./cyberportApp/popup/getInboxSessionViewer.html", {}, function(data) {
			apps.html(data);
			var report = $('#report_msg');
			var tips = $('.cyberport-box>.content>.left>.cyb-sws>ul.tips');
			tips.find('>li[inbox]>p').html(report.attr('inbox'));
			tips.find('>li[outbox]>p').html(report.attr('outbox'));
			tips.find('>li[drafts]>p').html(report.attr('drafts'));
			tips.find('>li[trash]>p').html(report.attr('trash'));
		});
		return false;
	});
	
	$('.cyberport-box>.content>.left>.cyb-sws>ul.tips').undelegate('>li', 'click');
	$('.cyberport-box>.content>.left>.cyb-sws>ul.tips').delegate('>li', 'click',
			function(e) {
		e.stopPropagation();
				var url = "./cyberportApp/popup/getMessages.html";
				var action = '';
				if (typeof $(this).attr('inbox') != 'undefined') {
					action = 'inbox';
				} else if (typeof $(this).attr('outbox') != 'undefined') {
					action = 'outbox';
				} else if (typeof $(this).attr('drafts') != 'undefined') {
					action = 'drafts';
				} else if (typeof $(this).attr('trash') != 'undefined') {
					action = 'trash';
				}
				
				var limit=10;
				var skip=0;
				var messages = $('.cyberport-box>.content>.right>.apps');
				messages.html("loadding...");
				$('.cyberport-box > .content > .right ').on('scroll', function(){
						var msg_list = $(this);
						if ( msg_list.height() + msg_list[0].scrollTop >= msg_list[0].scrollHeight - 60 ) { 
							var messages = $('.cyberport-box>.content>.right>.apps');
							$.get(url, {
								'action' : action,
								'skip':skip,
								'limit':limit
							}, function(data) {
								messages.append(data);
								skip=skip+limit;
							});
						} 
				});
				
				$.get(url, {
					'action' : action,
					'skip':skip,
					'limit':limit
				}, function(data) {
					messages.html(data);
					skip=skip+limit;
				});
				return false;
			});
	var apps=$('.cyberport-box>.content>.right>.apps');
	apps.undelegate('>.app>.box>.item', 'click');
	apps.delegate('>.app>.box>.item', 'click',
			function(e) {
				e.stopPropagation();
				var sid=$(this).attr('sid');
				var title=$(this).find('>.detail>li[ltitle]');
				var url = "./servicews/session/openSession.html";
				var appCode= $(this).parents('li.app[appCode]').attr('appCode');
				//var messages = $('.cyberport-box>.content>.right>.apps');
				//messages.html("loadding...");
				var the=$(this);
				$.get(url, {
					'sid' : sid
				}, function(data) {
					if(appCode=='system'){
						var box=the.parents('ul.apps');
						box.empty();
						box.html(data);
					}else if(appCode=='dynamic'){//打开相应模块
						var obj=$.parseJSON(data);
						var dynamicurl='app://-5?filterById='+obj.appId;
						$.cj.App.openApp(dynamicurl);
					}else{
					the.showWin(data,null,{title:title.html(),sid:sid});
					}
				});
				return false;
			});
	apps.undelegate('>.app>.box>.item', 'mouseenter mouseleave');
	apps.delegate('>.app>.box>.item', 'mouseenter mouseleave',
			function(e) {
		e.stopPropagation();
		if(e.type=='mouseenter'){
			$(e.currentTarget).find('>span.del').show();
		}else if(e.type=='mouseleave'){
			$(e.currentTarget).find('>span.del').hide();
		}
		return false;
	});
	apps.undelegate('>.app>.box>.item>span.del','click');
	apps.delegate('>.app>.box>.item>span.del','click',function(e){
		e.stopPropagation();
		var li=$(this).parent('li.item');
		var sid=li.attr('sid');
		var url = "./servicews/session/deleteSession.service";
		var the=$(this);
		$.get(url, {
			'sid' : sid
		}, function(data) {
			the.parent('li.item').remove();//如果是remove会导致此区域重加载，会执行此脚本文件，而此文件又导致重装页面，因此会死循环
			
		});
		return false;
	});
	
	var tips=$('.cyberport-box > .bottom > ul.others > li.sws[swsid]');
	tips.each(function(i,e){
		var swsid=$(e).attr('swsid');
		var count=$.cj.report[swsid];
		if(typeof count!='undefined'&&count!=0){
			var t=$(e).find('.tips');
			t.html(count);
			t.show();
		}
	});
	tips.on('click',function(e){
		window.location.href='./?swsid='+$(this).attr('swsid');
	});
})();