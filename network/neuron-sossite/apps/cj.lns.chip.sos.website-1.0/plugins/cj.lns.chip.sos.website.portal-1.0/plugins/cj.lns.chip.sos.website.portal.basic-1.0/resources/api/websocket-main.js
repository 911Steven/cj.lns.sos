$(document).ready(function() {
	//为什么使用iframe，当浏览器不支持ws时，就要用comet技术，因而采用ajax方式就有跨域问题，而采用iframe后，则可跨采用一致的api（又但是postMessage是html5的技术）
	$.cj.report={};//全局可用
	
	function servicewsListReporter(reporter){
		var swsList=$('#cyberport-popup').find('.cyberport-box > .bottom > ul.others > li.sws[swsid]');
		var swsidStr='';
		swsList.each(function(i,e){
			var swsid=$(e).attr('swsid');
			swsidStr=swsidStr+','+swsid;
		});
		var frame ='command=post\r\nurl=/servicewsListReporter\r\nprotocol=peer/1.0\r\n\r\n\r\n'
		$.ws.sockets.device.send(frame);
	}
	
	function flagMessageReaded(swsid,msgid){
		var frame ='command=flagMessageOneReaded\r\nurl=/mailbox/inbox?swsid='+swsid+'&msgid='+msgid+'&cjtoken=xx\r\nprotocol=peer/1.0\r\n\r\n\r\n'
		$.ws.sockets.device.send(frame);
	}
	function receiveDevice(data) {
		//处理各种消息，消息按地址分为/session,/cyberport,/weibo等
		if('#>login'==data){//上线完成通知
			servicewsListReporter($.cj.report);//完成加载时取其它所有视窗的消息报告
			return;
		}
		var frame=data;
		var url=frame.heads.url;
		if('piggyback'==frame.heads.command){//所有的捎带
			//alert('websocket-main.js 收到互动消息：'+frame.heads.url);
			if('/'==frame.heads.url){
				if(typeof $('#device').attr('peerId')=='undefined'&&frame.heads.peerId!='undefined'){
					devicewebsocket.peerid=frame.heads['peer-id'];
				}
			}
			return;
		}
		if(url=='/'){
			debugger;
		}else if(url.indexOf('/session')==0){//判断会话窗口，有则在窗内显示，无则染色信息港
			var sid=frame.params.sid;
			var swsid=frame.params.swsid;
			var currswsid=$('#device').attr('swsid');
			if(swsid!=currswsid){//在其它视窗图标的tips上加1.
				//****注意：swsid的值并不一定是视窗号，也可能是/home表示用户的主空间则应在用户本身上显示*****之后再实现它
				$.cj.report[swsid]=$.cj.report[swsid]+1;
				var sws=$('#cyberport-popup').find('.cyberport-box > .bottom > ul.others > li.sws[swsid='+swsid+']');
				if(sws.length>0){//因为可能信息港可能没有加载
					var tE=sws.find('.tips');
					var intvalue=$.cj.report[swsid];
					tE.html(intvalue);
					if(intvalue>0){
						tE.show();
					}
				}
				var circle=$('.startMenu .third > .right .circle-box');
				circle.addClass('breathe-btn');
				circle.attr('style','background: rgba(255, 0, 0, 0.8);');
				return;
			}
			var task=$('#session-tasks>li[task][sid='+sid+']');
			if(task.length>0){
				var selectTask=task.attr('task');
				var panel=$('#session-tasks>.panels>.panel[task='+selectTask+']');
				var cnt=panel.find('>.ctx>.l-c>.left>.cnt');
				cnt.append(frame.content);
				cnt.scrollTop( cnt[0].scrollHeight );
				if(panel.is(":hidden")){
					task.css('background','#FFCFBE');
				}
				//标识为已读：
				if('inbox'==frame.params.mailbox){
					flagMessageReaded(swsid,frame.params.msgid);
				}
			}else{//如果不存在会话窗口，则染色信息港
				//$('.startMenu .third > .right .circle-box').animate({'color':'red'},1000);
					//$('.startMenu .third > .right .circle-box').css('background','red');
				var circle=$('.startMenu .third > .right .circle-box');
				circle.addClass('breathe-btn');
				circle.attr('style','background: rgba(255, 0, 0, 0.8);');
				//下面如果有数字统计提示，则显示消息数
				var tips=$('.startMenu .third .msg-tips');
				if(!tips.is(':hidden')){
					var count=$.cj.report[currswsid]+1;
					$.cj.report[swsid]=count;
					tips.children('a').html(count);
				}
			}
		}else if(url.indexOf('/dynamic')==0){//动态服务
			var sid=frame.params.sid;
			var swsid=frame.params.swsid;
			var currswsid=$('#device').attr('swsid');
			if(swsid!=currswsid){//在其它视窗图标的tips上加1.
				//****注意：swsid的值并不一定是视窗号，也可能是/home表示用户的主空间则应在用户本身上显示*****之后再实现它
				$.cj.report[swsid]=$.cj.report[swsid]+1;
				var sws=$('#cyberport-popup').find('.cyberport-box > .bottom > ul.others > li.sws[swsid='+swsid+']');
				if(sws.length>0){//因为可能信息港可能没有加载
					var tE=sws.find('.tips');
					var intvalue=$.cj.report[swsid];
					tE.html(intvalue);
					if(intvalue>0){
						tE.show();
					}
				}
				var circle=$('.startMenu .third > .right .circle-box');
				circle.addClass('breathe-btn');
				circle.attr('style','background: rgba(255, 0, 0, 0.8);');
				return;
			}
			var task=$('.windows > .titleBar[resourceid=application][valueid]');
			var valueid=task.attr('valueid');
			if(task.length>0&&(valueid=='-5'||valueid.indexOf('app://-5')==0)){
				task.find('>a>span[tips]').remove();
				task.find('>a').append("<span tips>"+frame.params.unreadMsgCount+"</span>");
				var hits=$('.home-panel > .title > div > span[kind='+frame.params.appId+']');
				hits.find('span[new]').remove();
				hits.append("<span new style='color:red;'>New</span>");
				//标识为已读：此处注掉原因：只在打开动态主页时将所有标记为已读即可。
				//if('inbox'==frame.params.mailbox){
					//flagMessageReaded(swsid,frame.params.msgid);
				//}
			}else{//如果不存在会话窗口，则染色信息港
				//$('.startMenu .third > .right .circle-box').animate({'color':'red'},1000);
					//$('.startMenu .third > .right .circle-box').css('background','red');
				var circle=$('.startMenu .third > .right .circle-box');
				circle.addClass('breathe-btn');
				circle.attr('style','background: rgba(255, 0, 0, 0.8);');
				//下面如果有数字统计提示，则显示消息数
				var tips=$('.startMenu .third .msg-tips');
				if(!tips.is(':hidden')){
					var count=$.cj.report[currswsid]+1;
					$.cj.report[swsid]=count;
					tips.children('a').html(count);
				}
			}
		}else if(url.indexOf('/cyberport')==0){//只在信息港显示的
			var type=frame.params['type'];
			if(typeof type=='undefined'||type==null){
				type='unkown';
			}
			switch(type){
			case 'reportMyServicewsMsgCount':
				var cnt=frame.content;
				cnt=$.parseJSON(cnt);
				var currswsid=$('#device').attr('swsid');
				var count=cnt[currswsid];
				$.cj.report=cnt;
				if(count>0){
					var circle=$('.startMenu .third > .right .circle-box');
					circle.addClass('breathe-btn');
					circle.attr('style','background: rgba(255, 0, 0, 0.8);');
					var tips=$('.startMenu .third .msg-tips');
					tips.children('a').html(count);
					tips.show();
				}
				break;
			default:
				alert('未确定的消息');
				break;
			}
		}else{
			console.log(frame);
			alert('收到未知消息');
		}
		
	};
	
	
	function openDevice(){
		//打开device
		//var host=window.location.host;
		var devicewebsocket = $.ws.open(
				$('#device').attr('ws'), function(frame) {
					receiveDevice(frame);
					//console.log('device websocket url :'+frame.heads.url);
				},function(e){
					var uid=$('#device').attr('uid');
					var swsid=$('#device').attr('swsid')
					var cjtoken=$('#device').val();
					var frame ='command=login\r\nurl=/loginService/?uid='+uid+'&swsid='+swsid+'&cjtoken='+cjtoken+'\r\ncj-circuit-sync=true\r\ncj-sync-timeout=15000\r\nprotocol=peer/1.0\r\n\r\n\r\n'
					devicewebsocket.send(frame);
					receiveDevice('#>login');
					
					cbox.removeClass('gray-img');
					cbox.find('>span[state]').remove();
					cbox.attr('invalid','false');
					cbox.removeProp('title');
				},function(e){
					$.ws.sockets.device=null;
					//alert('device connector was closed');
					cbox.attr('invalid','true');
					cbox.addClass('gray-img');
					cbox.attr('title','设备状态是：未连接，请点击以尝试建立连接...');
					cbox.find('>span[state]').remove();
					cbox.prepend("<span state>设备未连接</span>");
				},function(e){
					alert('设备通讯错误：'+e);
				});
		return devicewebsocket;
	}
	var cbox=$('.startMenu .third>.right .circle-box ');
	var devicewebsocket =openDevice(cbox);
	cbox.on('click',function(e){
		 if(cbox.attr('invalid')!='true'){
			 return;
		 }
		 //重连设备
		 cbox.find(">span[state]").html('连接中...');
		 $.ws.sockets.device= openDevice(cbox);
	});
	
//	function receiveFs(data) {
//		//console.log(data);
//	}
//	var fswebsocket = $.ws.open(
//			$('#fs').attr('ws'), function(frame) {
//				receiveFs(frame);
//				//console.log('device websocket url :'+frame.heads.url);
//			},function(e){
//				var uid=$('#fs').attr('uid');
//				var swsid=$('#fs').attr('swsid')
//				var peerid=$.ws.sockets.device.peerid;
//				var cjtoken=$('#fs').val();
//				var frame ='command=login\r\nurl=/loginService/?uid='+uid+'&swsid='+swsid+'&cjtoken='+cjtoken+'&peer-id='+peerid+'\r\ncj-circuit-sync=true\r\ncj-sync-timeout=15000\r\nprotocol=FS/1.0\r\n\r\n\r\n'
//				fswebsocket.send(frame);
//				$.ws.sockets.fs=fswebsocket;
//			},function(e){
//				$.ws.sockets.fs=null;
//				//alert('fs connector was closed');
//			},function(e){
//				alert(e);
//			});
	$.ws.sockets={
				device:devicewebsocket
//				fs:fswebsocket
			};
	
	
});