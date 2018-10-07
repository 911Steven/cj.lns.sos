(function(){
	$('.menu-panel li[downable]').hover(function(){
		$(this).find('.downbox').show();
		$(this).children('a').addClass('downbox-owner');
	},function(){
		if('true'==$(this).find('.downbox').attr('keepshow')){
			return;
		}
		$(this).find('.downbox').hide();
		$(this).children('a').removeClass('downbox-owner');
	});
	$('.menu-task>ul>li[desktop]').on('click',function(){
		$(".workbench [region='portlet']").toggle();
		$(".workbench [region='desktop']").toggle();
		$(".workbench [region='sosSiteInfo']").toggle();
	});
	
	var tasks=$('#session-tasks');
	tasks.delegate('.op','click',function(e){
		var panel=$(this).parent('div.panel');
		panel.hide();
		var tool=tasks.find('>li[task='+panel.attr('task')+']');
    	tool.css('background',"none");
    	return false;
	});
	tasks.delegate('>li[task]','click',function(e){
		tasks.find('>.panels>.panel[task]').hide();
		var win =tasks.find('>.panels>.panel[task='+$(this).attr('task')+']');
		win.show();
		var tools=tasks.find('>li[task]');
		tools.css('background',"none");
		tools.css('border-bottom',"1px solid #f5f5f5");
		var tool=tasks.find('>li[task='+$(this).attr('task')+']');
    	tool.css('background',"#f5f5f5");
		return false;
	});
	tasks.delegate('>li[task]>span.close','click',function(e){
		var li=$(this).parent('li[task]');
		var win =tasks.find('>.panels>.panel[task='+li.attr('task')+']');
		win.find('>.ctx').empty();
		win.hide();
		li.removeAttr('sid');
		li.find('>span[title]').empty();
		li.hide();
		return false;
	});
	tasks.find('>li[task]').hover(function(){
		$(this).find('>span.close').toggle();
	});
})();

(function($) {
    $.fn.extend({
        showWin: function(html,task,session) {
        	var tasks=$('#session-tasks');
        	if(typeof session!='undefined'&&session!=null){
        		var the=tasks.find('>li[task][sid='+session.sid+']');
	        	if(the.length>0){
	        		tasks.find('>.panels>.panel[task]').hide();
	        		var win =tasks.find('>.panels>.panel[task='+the.attr('task')+']');
	        		win.show();
	        		var tools=tasks.find('>li[task]');
	        		tools.css('background',"none");
	        		tools.css('border-bottom',"1px solid #f5f5f5");
	        		var tool=tasks.find('>li[task='+the.attr('task')+']');
	            	tool.css('background',"#f5f5f5");
	            	the.show();
	        		return false;
	        	}
        	}
        	if(tasks.find('>li[task]:hidden').length<1){
        		alert('已打开了4个任务，省了吧，要么关闭个再来。');
        		return;
        	}
        	var panels=tasks.find('>.panels>.panel[task]');
        	panels.hide();
        	var tools =tasks.find('>li[task]');
        	var selTask=task;
        	if(typeof selTask =='undefined'||selTask==null){
        		tools.each(function(i,e){
        			if('none'==$(e).css('display')){
        				selTask=i+1;
        				return false;
        			}
        		});
        		if((typeof selTask =='undefined')||selTask==null){
        			var pos=tasks.attr('curr');
        			if(typeof pos =='undefined'){
        				pos=0;
        			}
        			if(pos>=4){
        				pos=0;
        			}
        			selTask=pos++;
        			tasks.attr('curr',selTask);
        		}
        	}
        	tools.css('background',"none");
        	tools.css('border-bottom',"1px solid #f5f5f5");
        	
        	var tool =tasks.find('>li[task='+selTask+']');
        	tool.css('background',"#f5f5f5");
        	tool.find('>span[title]').html(session.title);
        	tool.attr('sid',session.sid);
        	tool.show();
        	
        	var win =tasks.find('>.panels>.panel[task='+selTask+']');
        	var doc=win.find('>.ctx');
        	doc.html(html);
        	doc.show();
        	win.show();
        }
    });
    $('.menu-panel .downbox > div.style-logout > ul>li').on('click',function(e){
    	window.location.href='./?swsid='+$(this).attr('swsid');
    });
    $('.menu-panel > .menu-tray > ul > li[login]').on('click',function(e){
    	window.location.href='./public/login.html';
    });
    $('.menu-panel > .menu-tray > ul > li[logout]').on('click',function(e){
    	$.get('./public/logout.html',{},function(){
    		window.location.href='./?swsid='+$.parameter('swsid');
    	});
    });
    $('.menu-panel > .menu-sos > ul > li[about]').on('click',function(e){
    	window.location.href='./pages/about/index.html';
    });
    $('.menu-panel > .menu-sos > ul > li[officalsite]').on('click',function(e){
    	window.location.href='./?swsid='+$(this).attr('officalsite');
    });
    $('.menu-panel > .menu-sos > ul > li[home]').on('click',function(e){
    	window.location.href='/';
    });
    $('.menu-panel > .menu-sos > ul > li[basicsite]').on('click',function(e){
    	$.get('./servicews/getBasicSwsidBy.service',{swsid:$(this).attr('basicsite')},function(data){
    		var obj=$.parseJSON(data);
    		window.location.href='./?swsid='+obj.id;
    	}).error(function(e){
    		alert(e.responseText);
    	});
    	
    });
    $('.menu-panel > .menu-task > ul.m-task-left > .service>.downbox>li').on('click',function(e){
    	if($(this).is('.scenter')){
    		return;
    	}
    	$(this).parent('.downbox').attr('keepshow','true');
    	var scenter=$(this).siblings('.scenter').find('.service-center');
    	scenter.show();
    	var cnt=scenter.find('>.content');
    	cnt.html('加载中...');
    	var url='./servicews/components/serviceCenterPortal.html';
    	var type='';
    	if(typeof $(this).attr('common')!='undefined'){
    		type='common';
    	}
    	if(typeof $(this).attr('basic')!='undefined'){
    		type='basic';
    	}
    	$.post(url,{type:type},function(data){
    		cnt.html(data);
    	}).error(function(e){
    		alert(e.responseText);
    	})
    });
    $('.menu-panel > .menu-task > ul.m-task-left > .service >.downbox> .scenter>.service-center>span[close]').on('click',function(e){
    	var downbox=$(this).parents('.downbox');
    	downbox.hide();
    	downbox.siblings('a').removeClass('downbox-owner');
    	downbox.attr('keepshow','false');
    });
    $('.menu-panel > .menu-task > ul.m-task-left > .service >a').on('click',function(e){
    	if(!$(this).is('.downbox-owner')){
    		return;
    	}
    	var service=$(this).parent('.service');
    	service.find('.service-center').hide();
    	var downbox=service.find('.downbox');
    	downbox.hide();
    	downbox.siblings('a').removeClass('downbox-owner');
    	downbox.attr('keepshow','false');
    });
})(jQuery); 
