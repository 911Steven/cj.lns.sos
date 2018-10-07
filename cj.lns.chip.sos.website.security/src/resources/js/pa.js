$('.apply-button').delegate('a[action]','click',function(){
	var action=$(this).attr('action');
	if('all'==action){
		$('.childs-box').empty();
		$.get('./security/applyAllChilds.service',{},function(data){
			
		}).error(function(e){
			alert(e.status+' '+e.responseText);
		});
		return;
	}
	if('custom'==action){
		$.get('./security/applyCustomChilds.html',{},function(data){
			$('.childs-box').html(data);
		}).error(function(e){
			alert(e.status+' '+e.responseText);
		});
		
	}
});

$('ul.security-pa[subject-aq]').delegate('[cjaction]','click',function(){
	var action=$(this).attr('cjaction');
	if(action=='add'){
		var li="<li new='true'><label>问题1:</label><input name='a'><label>答案：</label><input name='q'><span cjaction='del'>X</span></li>";
		$(this).parents('ul.aq').append(li);
		return;
	}
	if(action=='del'){
		var aqid=$(this).parent('li').attr('aqid');
		var thisis=$(this);
		var aqli=$(this).parent('li');
		 var a=aqli.children("input[name='a']");
		 var q=aqli.children("input[name='q']");
		 
		if(typeof a.val()=='undefined'||q.val()==''){
			$(this).parent('li').remove();
			return;
		}
		
		$.get('./security/authAskSet.html',{
			action:'del',
			aqid:aqid,
			a:a.val(),
			q:q.val(),
			current:$('#current').val()
		},function(){
			thisis.parent('li').remove();
		}).error(function(e){
			alert(e.status+' '+e.responseText);
		});
		
		return;
	}
});
$('ul.security-pa[subject-aq]').delegate('ul.aq[aq] input[name]','focusin  focusout',function(e){
	var the=$(this).attr('name');
	var li=$(this).parent('li');
	 if (e.type == "focusin") {
		 var a=li.children("input[name='a']");
		 if(the=='q'){
			 if(typeof a.val()=='undefined'||a.val()==''){
				 a.val('请先填写问题');
			 }
			 return;
		 }
		 if(the=='a'){
			 a.val('');
		 }
		 return;
	 }
	 if(e.type=="focusout"){
		 var a=li.children("input[name='a']");
		 var q=li.children("input[name='q']");
		 if(the=='q'){
			 if(typeof q.val()=='undefined'){
				 q.val('请填写答案');
				return;
			 }
			 if('请先填写问题'==a.val()){
				 return;
			 }
			 //提交
			 var aqid=li.attr('aqid');
			 var action='';
			 if('true'==li.attr('new')){
				 action='new';
			 }else{
				 action='update'
			 }
			 $.get('./security/authAskSet.html',{
					action:action,
					aqid:aqid,
					a:a.val(),
					q:q.val(),
					current:$('#current').val()
				},function(data){
					if(typeof data!='undefined'&&data!=''){
						var ai=$.parseJSON(data);
						li.attr('aqid',ai.id);
					}
					li.removeAttr('new');
				}).error(function(e){
					alert(e.status+' '+e.responseText);
				});
		 }
		return;
	 }
	
});
function selectSubject(e) {
	var opp = $(e).find(".opp>input");
	if (opp.prop('checked')) {
		var url = "./security/setSelectedGroup.html"
		var groupId = $(e).attr('gid');
		var current = $('#current').val();
		$.get(url, {
			action : 'deny',
			groupId : groupId,
			current : current
		}, function() {
			opp.prop('checked', false);
		}).error(function(e) {
			alert(e.status + e.responseText);
		});

	} else {
		var url = "./security/setSelectedGroup.html"
		var groupId = $(e).attr('gid');
		var current = $('#current').val();
		$.get(url, {
			action : 'allow',
			groupId : groupId,
			current : current
		}, function() {
			opp.prop('checked', true);
		}).error(function(e) {
			alert(e.status + e.responseText);
		});

	}
}
function selectRoles(kind) {
	var group = $('.security.bop>.content-panel [subject-group]');
	var user = $('.security.bop>.content-panel [subject-allow]');
	var deny = $('.security.bop>.content-panel [subject-deny]');
	var aq = $('.security.bop>.content-panel [subject-aq]');
	// var apply = $('.security.bop>.content-panel [subject-apply]');
	if (kind == 'allContacts') {
		deny.show();
		group.hide();
		user.hide();
		aq.hide();
	} else if (kind == 'onlySelf') {
		deny.hide();
		group.hide();
		user.hide();
		aq.hide();
	} else if (kind == '_custom') {
		deny.show();
		group.show();
		user.show();
		aq.show();
	} else if (kind == 'mobileContacts') {
		deny.show();
		group.hide();
		user.hide();
		aq.hide();
	} else {
		deny.hide();
		group.hide();
		user.hide();
		aq.hide();
		return;
	}
}
// 默认选中的角色板
var defaultSelRoleCode = $('.security>.content-panel>.subject-sels>a.selected');
selectRoles(defaultSelRoleCode.attr('rcode'));

// 当选择角色时后台清除了自定义项，而界面还保留了设置，要么清除，要么再次选中自定义时将历史整体提交
// 由于实现起来较费时，先做清除。
function memoCustomTab() {
	var group = $('.security.bop>.content-panel [subject-group]');
	// var selectedGroups=group.find('.opp>input:checked');
	group.find('.opp>input:checked').prop('checked', false);
	$('.security.bop>.content-panel [subject-allow]').empty();
}
var customHistory = {
	have : false,
	current : {},
	allowGroups : [

	],
	allowContacts : [],
	denyContacts : [],
	questions : []
}
function selectSubjectKind(e) {
	var sels = $('.sp-panel.subject-sels>a');
	sels.removeAttr('class');
	var the = $(e);
	the.attr('class', 'selected');
	var kind = the.attr('rcode');
	if ('_custom' == kind) {// 自定义不是角色因此不需要向后台提交，避免以下的默认提交动作
		selectRoles(kind);
		return;
	}
	var url = "./security/setSelectedRole.html"
	var roleId = $(the).attr('rid');
	var current = $('#current').val();
	$.get(url, {
		roleId : roleId,
		roleCode : kind,
		current : current
	}, function() {
		selectRoles(kind);
		if ('_custom' != kind) {// 如果不是选中的自定义项，则记录自定义面板，以供再次被选择时恢复。
			memoCustomTab();
		}
	}).error(function(e) {
		alert(e.status + e.responseText);
	});
}

function selectAllowUsers() {
	var url = './security/contactPad.html';
	$.ajax({
		type : 'post',
		url : url,
		success : function(data) {
			$('.desktop .popup-user').remove();
			$('.desktop').append(data);
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
						var current = $('#current').val();
						$.get("./security/setSelectedContact.html", {
							action : 'allow',
							users : usersStr,
							current : current
						}, function() {

						}).error(function(e) {
							alert('error:' + e.status + ' ' + e.responseText);
						});
					});
		},
		error : function(e) {
			alert('error:' + e.responseText);
		}
	});
}
function selectDenyUsers() {
	var url = './security/contactPad.html';
	$.ajax({
		type : 'post',
		url : url,
		success : function(data) {
			$('.desktop .popup-user').remove();
			$('.desktop').append(data);
			$('.popup-user').on(
					'confirm',
					function(e1, selected) {
						var list = $('.desktop .user-list[subject-deny]');
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
						var current = $('#current').val();
						$.get("./security/setSelectedContact.html", {
							action : 'deny',
							users : usersStr,
							current : current
						}, function() {

						}).error(function(e) {
							alert('error:' + e.status + ' ' + e.responseText);
						});
					});
		},
		error : function(e) {
			alert('error:' + e.responseText);
		}
	});
}