(function() {
	$('.l-c > .right > .options > .op > a ').on('click',function(e){
		var the=$(this);
		var panel=the.parents('div.panel[task]');
		var task=panel.attr('task');
		var sid=panel.parents('#session-tasks').find('>li[task='+task+']').attr('sid');
		var url = './security/contactPad.html';
		$.ajax({
			type : 'post',
			url : url,
			success : function(data) {
				$('.l-c > .right .popup-user').remove();
				$('.l-c > .right').append(data);
				$('.popup-user').on(
						'confirm',
						function(e1, selected) {
							var list = $('.desktop .user-list[subject-allow]');
							list.empty();
							var usersStr = "";
							var users = $(selected).children('li.person');
							users.each(function() {
								var img = $(this).find('img.p-h').attr('src');
								var uc = $(this).attr('uc');
								var name = $(this).find('span').html();
								var html = "<li class='user'><img src='" + img
										+ "' plugin><br><span>" + name
										+ "</span></li>";
								list.append(html);
								usersStr += uc + ',';
							});
							if (usersStr == '')
								return;
							
							var url='./contact/inviteUserChatroom.service?users='+usersStr+'&sid='+sid;
							$.get(url,{},function(data){
								$('.l-c > .right > .users').append(data);
							}).error(function(e){
								alert(e.status+' '+e.responseText);
							});
						});
			}
		});
	});
	$('.l-c>.right>.users>li').on('click', function() {
		var task = $(this).parents('div.panel[task]');
		var selUser = task.find('#select-user');

		var sib = selUser.siblings('span[all]');
		var ch = sib.find('input');
		ch.removeAttr("disabled");
		ch.removeAttr("checked");

		var a = selUser.find('>a');
		var uid = $(this).attr('uid');
		a.attr('uid', uid);
		a.html($(this).find('>.intro>.f1').html());
	});
	
	
	$('.l-c>.left>.filter>.set>div>span[all]').on('click', function() {
		var sib = $(this).siblings('#select-user');
		$(this).find('input').attr("disabled", 'disabled');

		var a = sib.find('>a');
		a.removeAttr('uid');
		a.empty();
	});
	$('#session-tasks>.panels>.panel>.ctx .l-c>.left>.filter>.sender>div>#msgInput')
			.on('keyup',
					function(e) {
						var sel = $(this).parents('li.sender').find(
								'input[sel]:checked');
						if (sel.attr('sel') == 'enter') {
							if (e.keyCode == '13' && !e.ctrlKey) {
								var task = $(this).parents('div.panel[task]');
								var sid = $(
										'#session-tasks>li[task='
												+ task.attr('task') + ']')
										.attr('sid');
								var msgbox = task.find('>.l-c>.left>.cnt');
								var selUser = task.find('#select-user>a').attr(
										'uid');
								var sender = task.find(
										'.l-c>.left>.filter>.sender').attr(
										'sender');
								var msgtext=task.find('textarea#msgInput');
								var msg = msgtext.val();
								onSend(sender, sid, selUser, task, msgbox, msg);
								msgtext.val('');
							}
						} else if (sel.attr('sel') == 'ctr+enter') {
							if (e.keyCode == '13' && e.ctrlKey) {
								var task = $(this).parents('div.panel[task]');
								var sid = $(
										'#session-tasks>li[task='
												+ task.attr('task') + ']')
										.attr('sid');
								var msgbox = task.find('>.l-c>.left>.cnt');
								var selUser = task.find('#select-user>a').attr(
										'uid');
								var sender = task.find(
										'.l-c>.left>.filter>.sender').attr(
										'sender');
								var msgtext=task.find('textarea#msgInput');
								var msg = msgtext.val();
								onSend(sender, sid, selUser, task, msgbox, msg);
								msgtext.val('');
							}
						}

					});
	function onSend(sender, sid, toUser, task, msgbox, msg) {
		if(msg==''){
			return;
		}
		var swsid=$('#device').attr('swsid');
		var frame = 'command=push\r\nurl=/pushMsgService/?sid=' + sid
				+ '&sender=' + sender + '&selectUser=' + toUser
				+ '&swsid='+swsid+'\r\nprotocol=peer/1.0\r\n\r\n\r\n' + msg;
		$.ws.sockets.device.send(frame);
	}
})();