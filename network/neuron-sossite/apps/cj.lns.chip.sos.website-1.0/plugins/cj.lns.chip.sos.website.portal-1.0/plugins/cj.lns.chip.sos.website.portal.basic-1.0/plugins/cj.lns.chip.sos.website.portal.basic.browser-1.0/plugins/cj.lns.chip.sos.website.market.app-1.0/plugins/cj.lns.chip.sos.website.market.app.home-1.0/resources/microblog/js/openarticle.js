(function(){
	$('.o-art > .o-box ').undelegate('> .media > ul > .attachment > ul > li','click');
	$('.o-art > .o-box ').delegate('> .media > ul > .attachment > ul > li','click',function(e){
		var file=$(this).attr('path');
		var iframe=document.createElement('iframe');
		$(iframe).attr('src',file);
		$(iframe).attr('style','display:none;');
		$(this).append(iframe);
	});
	$('.o-art > .o-box ').undelegate('> .media > ul > .pics > .items>li','click');
	$('.o-art > .o-box ').delegate('> .media > ul > .pics > .items>li','click',function(e){
		var isMedia=$(this).attr('media');
		var shower=$(this).parent('.items').siblings('.show');
		shower.empty();
		if(typeof isMedia=='undefined'){
			var src = $(this).children('img').attr('src');
			var img="<img src='"+src+"' />"
			shower.append(img);
		}else{
			/////
			var video=$(this).find('video').first().clone();
			shower.append(video);
			shower.find('video').attr('controls','');
		}
	});
	$('.o-art > .o-box ').undelegate('> .media > ul > .pics > .show > img','click');
	var toggle=true;
	$('.o-art > .o-box ').delegate('> .media > ul > .pics > .show > img','click',function(e){
		if(toggle){
		$(this).parent('.show').attr('style','height:400px;');
		toggle=false;
		}else{
			$(this).parent('.show').attr('style','height:200px;');
			toggle=true;
		}
	});
	$('.o-art > .o-box ').undelegate('> .forum > .bottom > .typed','mouseenter mouseleave keyup focus');
	$('.o-art > .o-box ').delegate('> .forum > .bottom > .typed','mouseenter mouseleave keyup focus',function(e){
		if(e.type=='mouseenter'){
			$(this).children('span[label]').hide();
			$(this).find('>span[value]>textarea').focus();
		}else if(e.type=='mouseleave'){
			if(''==$(this).find('>span[value]>textarea').val()){
				$(this).children('span[label]').show();
			}
		}else if(e.type=='keyup'){
			if(e.keyCode=='13'){
				var the=$(this);
				var val=$(this).find('>span[value]>textarea').val();
				var artid=$(this).parents('.o-art[artid]').attr('artid');
				var onsws=$(this).parents('.o-art[artid]').attr('onsws');
				var creator=$(this).parents('.o-art[artid]').attr('creator');
				var url='./swssite/microblog/relatives/entityComments.service';
				
				if(val==''||val=='\n'){
					alert('空的评论');
					return;
				}
				$.post(url,{entity:'myarticle',creator:creator,onsws:onsws,sourceid:artid,action:'comment',to:'@',comment:val,thread:'$'},function(data){
					var obj=$.parseJSON(data);
					//{'commentid':'5741f8ccaf5bc80dbe487031',reviewerId:'user1',reviewerFace:{"nick":"11","head":"1f88b38ad6b8c7b6f7fe9ab7c994550d.jpg","sex":0}}
					the.find('>span[value]>textarea').val('');
					var ul=$('.o-art > .o-box > .forum > .threads');
					var li="<li class='thread'><img title='"+obj.reviewerId+"' src='./resource/ud/"+obj.reviewerFace.head+"?path=home://system/img/faces/&u="+obj.reviewerId+"' >" +
							"<ul class='forum-panel'>" +
							"<li class='ctx' uid='"+obj.reviewerId+"' title='"+obj.reviewerFace.nick+"'>" +
							"<span name>"+obj.reviewerId+"</span>" +
							"<p talk>"+val+"</p>" +
							"</li>" +
							"<li class='op'>" +
							"<span time>"+obj.ctime+"</span>" +
							"<span reply>回复</span>" +
							"<span style='display:none' commentid='"+obj.commentid+"' close>X</span>" +
							"</li>"+
							"</ul>" +
							"</li>";
					ul.append(li);
				}).error(function(e){
					alert(e.responseText);
				});
				
			}
		}
	});
	$('.o-art > .o-box ').undelegate('.forum-panel > .op','mouseenter mouseleave');
	$('.o-art > .o-box ').delegate('.forum-panel > .op','mouseenter mouseleave',function(e){
		if(e.type=='mouseenter'){
			$(this).find('span[close]').show();
		}else{
			$(this).find('span[close]').hide();
		}
	});
	$('.o-art > .o-box ').undelegate('.forum-panel > .op > span[close]','click');
	$('.o-art > .o-box ').delegate('.forum-panel > .op > span[close]','click',function(e){
		var the=$(this);
		var artid=$(this).parents('.o-art[artid]').attr('artid');
		var onsws=$(this).parents('.o-art[artid]').attr('onsws');
		var creator=$(this).parents('.o-art[artid]').attr('creator');
		var commentid=$(this).attr('commentid');
		var url='./swssite/microblog/relatives/entityComments.service';
		$.post(url,{creator:creator,onsws:onsws,sourceid:artid,entity:'myarticle',commentid,action:'delComment'},function(data){
			the.parent('.op').parent('.forum-panel').parent('li').remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-art > .o-box ').undelegate('.forum-panel > .op > span[reply]','click');
	$('.o-art > .o-box ').delegate('.forum-panel > .op > span[reply]','click',function(e){
		var input=$(this).parent('.op').find('input');
		if(input.length>0){
			input.toggle();
			input.focus();
			return;
		}
		$(this).parent('.op').append("<input style='width:100%;padding:4px;'>");
	});
	$('.o-art > .o-box ').undelegate('.forum-panel > .op > input','keyup');
	$('.o-art > .o-box ').delegate('.forum-panel > .op > input','keyup',function(e){
		if(e.keyCode=='13'){
			var input=$(this).parent('.op').find('input');
			var thread=$(this).parents('.thread').find('>.forum-panel>.op>span[commentid]').attr('commentid');
			var the=$(this);
			var val=input.val();
			var artid=$(this).parents('.o-art[artid]').attr('artid');
			var onsws=$(this).parents('.o-art[artid]').attr('onsws');
			var creator=$(this).parents('.o-art[artid]').attr('creator');
			var to='@'+the.parent('.op').siblings('.ctx').attr('uid');
			var url='./swssite/microblog/relatives/entityComments.service';
			
			if(val==''||val=='\n'){
				alert('空的评论');
				return;
			}
			$.post(url,{entity:'myarticle',creator:creator,onsws:onsws,sourceid:artid,action:'comment',to:to,comment:val,thread:thread},function(data){
				input.val('');
				input.hide();
				var obj=$.parseJSON(data);
				//{'commentid':'5741f8ccaf5bc80dbe487031',reviewerId:'user1',reviewerFace:{"nick":"11","head":"1f88b38ad6b8c7b6f7fe9ab7c994550d.jpg","sex":0}}
				var panel=the.parents('li.thread').find('>.forum-panel');
				if(panel.children('.follows').length<1){
					panel.append("<li class='follows'><ul></ul></li>");
				}
				var li="<li><img title='"+obj.reviewerId+"' src='./resource/ud/"+obj.reviewerFace.head+"?path=home://system/img/faces/&u="+obj.reviewerId+"' >" +
						"<ul class='forum-panel'>" +
						"<li class='ctx' uid='"+obj.reviewerId+"'>" +
						"<span name>"+obj.reviewerId+"</span>" +
						"<span symbol>:</span>" +
						"<span to>"+obj.to+"</span>" +
						"<p talk>"+val+"</p>" +
						"</li>" +
						"<li class='op'>" +
						"<span time>"+obj.ctime+"</span>" +
						"<span reply>回复</span>" +
						"<span style='display:none' commentid='"+obj.commentid+"' close>X</span>" +
						"</li>"+
						"</ul>" +
						"</li>";
				$(panel).find(">.follows>ul").append(li);
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	$('.o-art > .o-box ').undelegate('>.op > ul > li[action]','click');
	$('.o-art > .o-box ').delegate('>.op > ul > li[action]','click',function(e){
		var action=$(this).attr('action');
		
		var artid=$(this).parents('.o-art[artid]').attr('artid');
		var onsws=$(this).parents('.o-art[artid]').attr('onsws');
		var creator=$(this).parents('.o-art[artid]').attr('creator');
		var url='./swssite/microblog/relatives/entityComments.service';
		
		switch(action){
		case 'forum':
			$('.o-art > .o-box > .forum > .bottom > .typed').toggle();
			break;
		case 'share':
			$.post(url,{action:'share',creator:creator,onsws:onsws,entity:'myarticle',sourceid:artid},function(data){
				
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		case 'follow':
			$.post(url,{action:'follow',creator:creator,onsws:onsws,entity:'myarticle',sourceid:artid},function(data){
				var obj=$.parseJSON(data);
				if(obj.cancel=='true'){//取消关注
					$('.o-art > .o-box > .op > ul > li[action=follow]').attr('title','我未有关注');
					var ctext=$('.o-art > .o-box > .op > ul > li[action=follow] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					$('.o-art > .o-box > .op > ul > li[action=follow] > span[value]').html('('+(c-1)+')');
				}else{
					$('.o-art > .o-box > .op > ul > li[action=follow]').attr('title','我已关注');
					var ctext=$('.o-art > .o-box > .op > ul > li[action=follow] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					$('.o-art > .o-box > .op > ul > li[action=follow] > span[value]').html('('+(c+1)+')');
				}
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		case 'great':
			$.post(url,{action:'great',creator:creator,onsws:onsws,entity:'myarticle',sourceid:artid},function(data){
				var obj=$.parseJSON(data);
				var great=$('.o-art > .o-box > .forum > .great');
				var faceul=great.children('.faces');
				if(obj.cancel=='true'){//撤消
					great.children('img.g-main').css('border','none');
					faceul.find('>li[face][uid='+obj.reviewerId+']').remove();
					var ctext=$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html('('+(c-1)+')');
				}else{
					great.children('img.g-main').css('border','1px solid #ececec');
					var src=obj.reviewerFace.head;
					var face="<li uid='"+obj.reviewerId+"' face><img src='./resource/ud/"+src+"?path=home://system/img/faces/&u="+obj.reviewerId+"'></li>";
					faceul.append(face);
					var ctext=$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html('('+(c+1)+')');
				}
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		}
	});
	$('.o-art > .o-box ').undelegate('span[face],li[face]','mouseover mouseout');
	$('.o-art > .o-box ').delegate('span[face],li[face]','mouseover mouseout',function(e){
		//弹出用户的face
		if(e.type=='mouseover'){
			
		}else if(e.type=='mouseout'){
			
		}
	});
	$('.o-art > .o-box ').undelegate('> .forum > .bottom > .extends','click');
	$('.o-art > .o-box ').delegate('> .forum > .bottom > .extends','click',function(e){
		alert('----more');
	});
	
	$('.o-art > .o-box ').undelegate('> .forum > .great > img.g-main','click');
	$('.o-art > .o-box ').delegate('> .forum > .great > img.g-main','click',function(e){
		var action=$(this).attr('action');
		var artid=$(this).parents('.o-art[artid]').attr('artid');
		var onsws=$(this).parents('.o-art[artid]').attr('onsws');
		var creator=$(this).parents('.o-art[artid]').attr('creator');
		var url='./swssite/microblog/relatives/entityComments.service';
		$.post(url,{action:'great',creator:creator,onsws:onsws,entity:'myarticle',sourceid:artid},function(data){
			var obj=$.parseJSON(data);
			var great=$('.o-art > .o-box > .forum > .great');
			var faceul=great.children('.faces');
			if(obj.cancel=='true'){//撤消
				great.children('img.g-main').css('border','none');
				faceul.find('>li[face][uid='+obj.reviewerId+']').remove();
				var ctext=$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html();
				ctext=ctext.substring(1,ctext.length-1);
				var c=parseInt(ctext);
				$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html('('+(c-1)+')');
			}else{
				great.children('img.g-main').css('border','1px solid #ececec');
				var src=obj.reviewerFace.head;
				var face="<li uid='"+obj.reviewerId+"' face><img src='./resource/ud/"+src+"?path=home://system/img/faces/&u="+obj.reviewerId+"'></li>";
				faceul.append(face);
				var ctext=$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html();
				ctext=ctext.substring(1,ctext.length-1);
				var c=parseInt(ctext);
				$('.o-art > .o-box > .op > ul > li[action=great] > span[value]').html('('+(c+1)+')');
			}
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	$('.o-art > .o-box ').undelegate('> .desc > span[visits]>span[label]','click');
	$('.o-art > .o-box ').delegate('> .desc > span[visits]>span[label]','click',function(e){
		var the=$(this).parent('span[visits]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.o-art');
		var sourceid=art.attr('artid');
		var entity='myarticle';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
			the.find('div.e-r-v').attr('style','right:auto;left:0;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-art > .o-box ').undelegate('> .desc > span[visits]','mouseleave');
	$('.o-art > .o-box ').delegate('> .desc > span[visits]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	$('.o-art > .o-box ').undelegate('.e-r-v>.more>span[label]','click');
	$('.o-art > .o-box ').delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.o-art');
		var sourceid=art.attr('artid');
		var entity='myarticle';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity,
			skip:skip
			},
		function(data){
				events.append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-art > .o-box ').undelegate('.e-r-v>.events>li[user]','click');
	$('.o-art > .o-box ').delegate('.e-r-v>.events>li[user]','click',function(e){
		var user=$(this).attr('user');
		var box=$(this);
		box.parent('.events').find('.user-face').remove();
		box.append("<div class='user-face' style='right:auto;left:0;'></div>");
		$.get('./servicews/viewUserProfile.service',{user:user},function(data){
			box.find('>.user-face').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-art > .o-box ').undelegate('.e-r-v>.events>li[user]','mouseleave');
	$('.o-art > .o-box ').delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();