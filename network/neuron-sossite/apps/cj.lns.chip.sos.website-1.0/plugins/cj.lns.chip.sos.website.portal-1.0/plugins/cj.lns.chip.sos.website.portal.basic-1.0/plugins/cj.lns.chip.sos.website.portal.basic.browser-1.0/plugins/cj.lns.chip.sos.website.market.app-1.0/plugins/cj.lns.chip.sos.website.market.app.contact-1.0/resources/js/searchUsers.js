(function(){
	var start=0;
	var max=4;//如果第一页超出了DIV自身的高度，则会自动滚动直到结束，因此要设小
	var realFetch=0;//实际每次取的量
	var userul=$('.su-list[result]').first();
	var userli=userul.children('li').first().clone();
	userul.empty();
	
	function fetchPage(){
		if(max<1){
			return;
		}
		var uval=$('.su-panel>.su-sea>.su-input>input');
		if(uval.val()==''||typeof uval.val()=='undefined'){
			uval.attr('style','border:1px solid red;');
			uval.focus(function(){uval.removeAttr('style');});
		}
		var onval=$(".su-panel>.su-sea>.su-cond>span>input[type='checkbox']:checked").val();
		var sexval=$(".su-panel>.su-sea>.su-cond>span>input[type='radio']:checked").val();
		var ucode=uval.val()=='用户名'?'':uval.val();
		$.get('./contact/findUsers.service',{
			userCode:ucode,
			online:onval,
			sex:sexval,
			start:start,
			max:max
		},function(d){
			var cnt=$(this).parents('.ug-g-list');
			var users=$.parseJSON(d);
			if(users.length==0){
				max=0;
			}else{
				start+=users.length;
			}
			for(var i=0;i<users.length;i++){
				var user=users[i];
				userli=userli.clone();
				var name=user.userCode;
				userli.find('ul>li[field]>span[nickname]').html(name);
				var sex='-';
				if(user.sex==0){
					sex='男';
				}else if(user.sex==1){
					sex='女';
				}
				userli.find('ul>li[field]>span[state]>label[sex]').html(sex);
				userli.attr('title','cj号：'+user.userCode);
				userli.attr('uid',user.id);
				if(typeof user.head!='undefined'){
					var src='./resource/ud/'+user.head+'?path=home://system/img/faces&u='+user.userCode;
					userli.children('img').attr('src',src);
				}
				if(typeof user.signatureText=='undefined'){
					user.signatureText='&nbsp;';
				}
				userli.find('ul>li[text]').html(user.signatureText);
				userul.append(userli);
			}
		}).error(function(e){
			alert(e.status+' '+e.responseText);
		});
	}
	var result = $('.ug-g-list');
	var nScrollHight = 0; // 滚动距离总长(注意不是滚动条的长度)
	var nScrollTop = 0; // 滚动到的当前位置
	var nDivHight = result.height();
	result.on('scroll', function() {
		nScrollHight = $(this)[0].scrollHeight;
		nScrollTop = $(this)[0].scrollTop;
		if (nScrollTop + nDivHight - nScrollHight >= -20) {
			// 取下页
			if(max>0)
			fetchPage();
		}
	});
	$('.su-panel>.su-sea>.su-input>input').focus(function(){
		if($(this).val()=='用户名'){
			$(this).val('');
		}
	});
	
	$('#bu-search').on('click',function(e){
		userul.empty();
		fetchPage();
		
	});
	userul.delegate('li','click',function(e){
		var the=$(this);
		var uid=the.attr('uid');
		var src=the.children('img').attr('src');
		if(typeof uid=='undefined'){
			return;
		}
		$.get('./contact/viewUser.html',{
			uid:uid
		},function(d){
			$('.ug-panel').html(d);
			$('.u-pic > ul > li.u-profile > img').attr('src',src);
		}).error(function(e){
			alert(e.status+' '+e.responseText);
		});
	});
})();
