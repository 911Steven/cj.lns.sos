$.cj={};
//应用程序的打开
$.cj.App={
	refreshBind:function(bar){
		$(bar).children('a').on('click',function(e){
			$.cj.App.open($(bar).attr('tool'),$(bar).attr('resourceId'),$(bar).attr('valueId'));
			$(this).siblings('.menu>ul>li[cmd]').removeAttr('selected');
		});
	},
	menuBind:function(bar){
		$(bar).find('>.menu>ul>li').on('click',function(e){
			var the =$(this);
			the.siblings('li').removeAttr('selected');
			var cmd=the.attr('cmd');
			if(cmd.indexOf('app://')==0||cmd.indexOf('appi://')==0){
				$.cj.App.open('toolbar.win.app','application',cmd);
				the.attr('selected','');
			}else{
				$.get(cmd,{},function(html){
					$('.desktop>.windows > .win-content').html(html);
					the.attr('selected','');
				}).error(function(e){
					alert(e.responseText);
				});
			}
		});
	},
	renderPortal:function(bar){
		var left=$(bar).attr('pleft');
		var ml=$('.main > .main-left');
		var mlused=ml.attr('used');
		var right=$(bar).attr('pright');
		var mr=$('.main > .main-right');
		var mrused=mr.attr('used');
		
		var showLeft=false;
		if(left!=''&&typeof left!='undefined'){
			showLeft=true;
			$.get(left,{},function(html){
				ml.children("style[portlet='"+left+"']").remove();
				ml.children("script[portlet='"+left+"']").remove();
				
				var tmp = document.createElement("div");
				$(tmp).append(html);
				var style=$(tmp).find('style');
				style.attr('portlet',left);
				var lets=$(tmp).find('>.portlet');
				if(style.length>0){
					ml.prepend(style);
				}
				var script=$(tmp).find('script');
				script.attr('portlet',left);
				if(script.length>0){
					ml.prepend(script);
				}
				lets.attr('type','app');
				ml.append(lets);
			}).error(function(e){
				alert(e.responseText);
			});
		}
		if(mlused!='empty'||showLeft){
			if(ml.is(':hidden')){
				$('.main>.main-box>.desktop').css('margin-left','200px');
				ml.show();
			}
		}else{
			$('.main > .main-left[region=portlet]').hide();
			$('.main > .main-box > .desktop[region=desktop]').css('margin-left','0');
		}
		
		var showRight=false;
		if((right!=''&&typeof right!='undefined')){
			showRight=true;
			$.get(right,{},function(html){
				mr.children("style[portlet='"+right+"']").remove();
				mr.children("script[portlet='"+right+"']").remove();
				var tmp = document.createElement("div");
				$(tmp).append(html);
				var style=$(tmp).find('style');
				style.attr('portlet',right);
				var lets=$(tmp).find('>.portlet');
				if(style.length>0){
					mr.prepend(style);
				}
				var script=$(tmp).find('script');
				script.attr('portlet',right);
				if(script.length>0){
					mr.prepend(script);
				}
				lets.attr('type','app');
				mr.append(lets);
			}).error(function(e){
				alert(e.responseText);
			});
		}
		if(mrused!='empty'||showRight){
			if(mr.is(':hidden')){
				$('.main>.main-box>.desktop').css('margin-right','200px');
				mr.show();
			}
		}else{
			$('.main > .main-right[region=portlet]').hide();
			$('.main > .main-box > .desktop[region=desktop]').css('margin-right','0');
		}
	},
	openApp:function(cmd){
		if(cmd.indexOf('app://')==0||cmd.indexOf('appi://')==0){
			$.cj.App.open('toolbar.win.app','application',cmd);
		}else{
			$.get(cmd,{},function(html){
				$('.desktop>.windows > .win-content').html(html);
			}).error(function(e){
				alert(e.responseText);
			});
		}
	},
	open:function(tool,resourceId,valueId){
		$.post('./servicews/openWin/',
			{
				tool:tool,
				resourceId:resourceId,
				valueId:valueId
			},
			function(html) {
				$('.main > .main-left>.portlet[type=app]').remove();
				$('.main > .main-right>.portlet[type=app]').remove();
				//$('.main>.main-box>.desktop').css('margin-right','220');
				//$('.main > .main-right').hide();
				$('.desktop').html(html);
				var titlebar=$('.windows > .titleBar');
				titlebar.attr('tool',tool);
				titlebar.attr('resourceId',resourceId);
				titlebar.attr('valueId',valueId);
				
				$.cj.App.renderPortal(titlebar);
				$.cj.App.refreshBind(titlebar);
				$.cj.App.menuBind(titlebar);
				
			}
		).
		error(function(e) {
			alert(e.status+' '+e.responseText);
		});
	}	
};
$(document).ready(function() {
	 $.ajaxSetup ({ cache: false ,contentType: "application/x-www-form-urlencoded; charset=utf-8"});
	 var defaultAppId=$('.desktop[region=desktop]').attr('defaultAppId');
	 //默认应用不能是普通应用，只能是：toolbar.win.app位置的应用
	 if(typeof defaultAppId!='undefined'&&defaultAppId!=''){
		 $.cj.App.open('toolbar.win.app','application',defaultAppId);
	 }
	 $('.desktop[region=desktop]').delegate('>.windows > .titleBar > .provider-view','click',function(e){
		var appId= $(this).parents('.titleBar').attr('valueId');
		if(appId.indexOf('app://')==0){
			appId=appId.substring(6,appId.length);
		}
		$.get('./servicews/setDefaultApp.service',{appId:appId},function(data){
			alert('成功设为默认主页');
		}).error(function(e){
			alert(e.responseText);
		});
	 });
	 $('.desktop[region=desktop]').delegate('>.windows > .titleBar > .provider-resize','click',function(e){
		 	var resize=$(this).attr('resize');
		 	var desktop=$('.main > .main-box > .desktop[region=desktop]');
			var left=$('.main > .main-left[region=portlet]');
			var right=$('.main > .main-right[region=portlet]');
		 	if(typeof resize=='undefined'||'mini'==resize){
				left.hide();
				right.hide();
				desktop.css('margin-left','0');
				desktop.css('margin-right','0');
				resize='max';
		 	}else{
		 		desktop.css('margin-left','200px');
				desktop.css('margin-right','200px');
				left.show();
				right.show();
		 		resize='mini';
		 	}
		 	$(this).attr('resize',resize);
		 });
});
