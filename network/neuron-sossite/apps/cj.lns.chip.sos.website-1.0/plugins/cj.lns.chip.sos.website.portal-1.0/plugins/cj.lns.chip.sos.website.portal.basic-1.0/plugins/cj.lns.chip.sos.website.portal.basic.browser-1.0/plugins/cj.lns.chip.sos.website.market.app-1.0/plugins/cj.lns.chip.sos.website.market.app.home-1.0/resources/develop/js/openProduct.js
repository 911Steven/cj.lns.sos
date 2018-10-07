(function(){
	var menu=$('.o-product > .main > .menu');
	//下载最新发布版
	$('.o-product > .title > .show > .release > span[value]').on('click',function(e){
		var file=$(this).parents('.show').attr('file');
		var iframe="<iframe style='display:none' src='"+file+"'><iframe>";
		var pid=$('.o-product').attr('pid');
		var releaseid=$(this).parents('.show').attr('releaseid');
		var the=$(this);
		$.post('./swssite/develop/editProduct.service',{pid:pid,releaseid:releaseid,editor:'product.versions.downloads'},function(data){
			the.append(iframe);
			var v=the.parents('.show').find('> .box > li[download] > span[value]');
			var i=parseInt(v.html())+1;
			v.html(i+'');
		}).error(function(e){
			alert(e.reponoseText);
		});
		
	});
	$('.o-product > .title > .show > .box > li[issue]>span').on('click',function(){
		var the=$(this).parent('li[issue]');
		the.attr('style','position:relative;');
		var pid=$('.o-product').attr('pid');
		var releaseid=$(this).parents('.show').attr('releaseid');
		$.post('./swssite/develop/editProduct.service',{pid:pid,releaseid:releaseid,editor:'product.versions.releaseIssue'},function(data){
			the.append(data);
		}).error(function(e){
			alert(e.reponoseText);
		});
		return false;
	});
	menu.undelegate('>li','click');
	menu.delegate('>li','click',function(e){
		var m=$(this).attr('m');
		var pid=$('.o-product').attr('pid');
		var the=$(this);
		$.get('./swssite/develop/getProductMenu.html',{menu:m,pid:pid},function(data){
			menu.find('>li.menu-selected').removeClass('menu-selected');
			the.addClass('menu-selected');
			$('.o-product > .main > .content').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	//menu.find('>li.menu-selected').trigger('click');
	$('.o-product').undelegate('[editable]','click');
	$('.o-product').delegate('[editable]','click',function(e){
		var editor=$(this).attr('editable');
		showEditor(editor,$(this));
	});
	$('.o-product').undelegate('.editor>span[close]','click');
	$('.o-product').delegate('.editor>span[close]','click',function(e){
		$(this).parent('.editor').remove();
	});
	$('.o-product>.main>.op').css('display','block');
	
	function showEditor(editor,theE){
		switch(editor){
		case 'product.logo':
			if( $(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('必须先输入产品英文名');
				return;
			}
			var pid=$('.o-product').attr('pid');
			theE.attr('title','点击上传文件');
			theE.css('cursor','pointer');
			theE.parent().find('div.editor').remove();
			theE.parent().append("<div class='editor' style='position:absolute;left:10px;bottom:40px;'><input type='file' id='logfile' name='logfile' ><span close>X</span></div>")
			theE.parent().find('#logfile').on('change', function(file) {
				if ($(this).val() == '') {
					return false;
				}
				if(typeof theE.parent().find('div.editor').attr('fn')!='undefined'){
					alert('已经上传了文件:'+theE.find('div.editor').attr('fn'));
					return;
				}
				$.ajaxFileUpload({
						url : './swssite/develop/uploadProductLogo.service',
						secureuri : false,
						fileElementId : 'logfile',
						dataType : 'json',
						data : {pid:pid},
						success : function(data, status) {
							theE.parent().find('div.editor').remove();
							theE.attr('src',data.src);
						},
						error : function(data, status, e) {
							alert(e);
						}
					});
			});
			break;
		case 'product.enname':
			theE.attr('title','点击编译英文名');
			theE.css('cursor','pointer');
			theE.parent().find('.editor').remove();
			theE.parent().append("<div class='editor' title='回车即提交' style='position:absolute;left:10px;bottom:40px;'><input enname type='text' value='"+theE.html()+"' ><span close>X</span></div>")
			theE.parent().undelegate('div.editor>input[enname]','keyup');
			theE.parent().delegate('div.editor>input[enname]','keyup',function(e){
				if(e.keyCode=='13'){
					var val=$(this).val();
					var pid=$('.o-product').attr('pid');
					if(val==''){
						alert('产品英文名称为空');
						return;
					}
					$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.enname',value:val},function(data){
						theE.parent().find('.editor').remove();
						theE.html(val);
						theE.attr('has','true');
						var obj=$.parseJSON(data);
						$('.o-product').attr('enname',val);
						$('.o-product').attr('pid',obj.id);
					}).error(function(e){
						alert(e.responseText);
					});
				}
			});
			
			break;
		case 'product.cnname':
			theE.attr('title','点击编译中文名');
			theE.css('cursor','pointer');
			if($(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('必须先输入产品英文名');
				return;
			}
			theE.parent().find('div.editor').remove();
			theE.parent().append("<div class='editor' title='回车即提交' style='position:absolute;left:10px;bottom:20px;'><input cnname type='text' value='"+theE.html()+"' ><span close>X</span></div>")
			theE.parent().undelegate('div.editor>input[cnname]','keyup');
			theE.parent().delegate('div.editor>input[cnname]','keyup',function(e){
				if(e.keyCode=='13'){
					var val=$(this).val();
					if(val==''){
						alert('产品中文名称为空');
						return;
					}
					var pid=$('.o-product').attr('pid');
					$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.cnname',value:val},function(data){
						theE.parent().find('.editor').remove();
						theE.html(val);
					}).error(function(e){
						alert(e.responseText);
					});
				}
			});
			break;
		}
	}
	$('.o-product > .main > .content').undelegate('.p-list > .p-docs > .doc > ul > li[cnt] > span[extends]','click');
	$('.o-product > .main > .content').delegate('.p-list > .p-docs > .doc > ul > li[cnt] > span[extends]','click',function(e){
		var pid=$('.o-product').attr('pid');
		var doc=$(this).parents('.doc[artid]');
		var artid=doc.attr('artid');
		var the=doc.find('> ul > li[cnt]>pre');
		var thebut=$(this);
		$.post('./swssite/develop/editProduct.service',{pid:pid,artid:artid,editor:'product.docs.getCnt'},function(data){
			the.html(data);
			thebut.remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-product > .main > .content').undelegate('.p-list > .p-docs > .doc>span[remove]','click');
	$('.o-product > .main > .content').delegate('.p-list > .p-docs > .doc>span[remove]','click',function(e){
		var pid=$('.o-product').attr('pid');
		var artid=$(this).parent('.doc').attr('artid');
		var the=$(this).parent('.doc');
		$.post('./swssite/develop/editProduct.service',{pid:pid,artid:artid,editor:'product.docs.del'},function(data){
			the.remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	$('.o-product > .main > .content').undelegate('.p-list > .p-docs > .doc>span[edit]','click');
	$('.o-product > .main > .content').delegate('.p-list > .p-docs > .doc>span[edit]','click',function(e){
		var pid=$('.o-product').attr('pid');
		var doc=$(this).parent('.doc');
		var artid=doc.attr('artid');
		var theE=$('.o-product > .main > .content');
		theE.find('div.editor').remove();
		var title=doc.find('> ul > li[title]').html();
		$.post('./swssite/develop/editProduct.service',{pid:pid,artid:artid,editor:'product.docs.getCnt'},function(data){
			theE.prepend("<div class='editor' style='left:10px;bottom:20px;top:10px;right:10px;'><p><span></span><input title value='"+title+"'></p><textarea content type='text'  >"+data+"</textarea><span close>关闭</span>" +
					"<span save>保存</span>" +
					"</div>");
			theE.undelegate('.editor > span[save]','click');
			theE.delegate('.editor > span[save]','click',function(e){
				var val=theE.find('.editor>textarea').val();
				var title=theE.find('.editor input[title]').val();
				if(title==''){
					alert('标题为空');
					return;
				}
				if(val==''){
					alert('内容为空');
					return;
				}
				var pid=$('.o-product').attr('pid');
				$.post('./swssite/develop/editProduct.service',{pid:pid,artid:artid,editor:'product.docs.edit',title:title,value:val},function(data){
					theE.find('.editor').remove();
					doc.find('> ul > li[title]').html(title);
					doc.find('> ul > li[cnt] > pre').html(val);
				}).error(function(e){
					alert(e.responseText);
				});
			});
		}).error(function(e){
			alert(e.responseText);
		});
		
		
	});
	$('.o-product > .main > .content').undelegate('.p-list > .p-docs > .doc','mouseover mouseout');
	$('.o-product > .main > .content').delegate('.p-list > .p-docs > .doc','mouseover mouseout',function(e){
		if(e.type=='mouseover'){
			$(this).find('>span[remove]').show();
			$(this).find('>span[edit]').show();
		}if(e.type=='mouseout'){
			$(this).find('>span[remove]').hide();
			$(this).find('>span[edit]').hide();
		}
	});
	$('.o-product').undelegate(' > .main > .op > ul > li[editor]','click');
	$('.o-product').delegate(' > .main > .op > ul > li[editor]','click',function(e){
		var m=menu.find('>li.menu-selected').attr('m');
		var theE=$('.o-product > .main > .content');
		switch(m){
		case 'abstract':
			if( $(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('必须先输入产品英文名');
				return;
			}
			theE.find('div.editor').remove();
			theE.append("<div class='editor' style='position:absolute;left:10px;bottom:20px;top:10px;right:10px;'><textarea content type='text'  >"+theE.children('pre').html()+"</textarea><span close>关闭</span>" +
					"<span save>保存</span>" +
					"</div>");
			theE.undelegate('.editor > span[save]','click');
			theE.delegate('.editor > span[save]','click',function(e){
				var val=theE.find('.editor>textarea').val();
				if(val==''){
					alert('内容为空');
					return;
				}
				var pid=$('.o-product').attr('pid');
				$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.abstract',value:val},function(data){
					theE.find('.editor').remove();
					theE.children('pre').html(val);
				}).error(function(e){
					alert(e.responseText);
				});
			});
			break;
		case 'docs':
			var enname=$(".o-product > .title > .logo > p[editable='product.enname']").html();
			if( $(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('先指定产品英文名');
				return;
			}
			theE.find('div.editor').remove();
			theE.prepend("<div class='editor' style='left:10px;bottom:20px;top:10px;right:10px;'><p><span>标题</span><input title></p><textarea content type='text'  ></textarea><span close>关闭</span>" +
					"<span save>保存</span>" +
					"</div>");
			theE.undelegate('.editor > span[save]','click');
			theE.delegate('.editor > span[save]','click',function(e){
				var val=theE.find('.editor>textarea').val();
				var title=theE.find('.editor input[title]').val();
				if(title==''){
					alert('标题为空');
					return;
				}
				if(val==''){
					alert('内容为空');
					return;
				}
				var pid=$('.o-product').attr('pid');
				$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.docs',title:title,value:val},function(data){
					theE.find('.editor').remove();
					//theE.children('pre').html(val);
					var url='./swssite/develop/getProductMenu.html?menu=docs&pid='+pid;
					$.get(url,{},function(html){
						$('.o-product > .main > .content').html(html);
					}).error(function(e){
						alert(e.responseText);
					});
				}).error(function(e){
					alert(e.responseText);
				});
			});
			
			break;
		case 'versions':
			var enname=$(".o-product > .title > .logo > p[editable='product.enname']").html();
			if( $(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('先指定产品英文名');
				return;
			}
			theE.find('div.editor').remove();
			theE.prepend("<div class='editor' style='left:10px;bottom:20px;top:10px;right:10px;'><p><span>程序</span><input type='file' id='jarfile' name='jarfile' accept='.rar,.zip,.gz,.jar,.exe,.bin,.so,.java,.js' file></p><textarea content type='text'  >Functions&Issues</textarea><span close>关闭</span>" +
					"<span save>保存</span>" +
					"</div>");
			var pid=$('.o-product').attr('pid');
			theE.find('#jarfile').on('change', function(file) {
				if ($(this).val() == '') {
					return false;
				}
				if(typeof theE.find('div.editor').attr('fn')!='undefined'){
					alert('已经上传了文件:'+theE.find('div.editor').attr('fn'));
					return;
				}
				theE.find('div.editor>p>span').html("waitting...");
				$.ajaxFileUpload({
					url : './swssite/develop/uploadProduct.service', // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : 'jarfile',
					dataType : 'json',// 返回值类型 一般设置为json
					data : {},
					success : function(data, status) {
						theE.find('div.editor').attr('fn',data.fn);
						theE.find('div.editor').attr('rfn',data.rfn);
						theE.find('div.editor>p>span').html('完成');
					},
					error : function(data, status, e) {
						alert(e);
					}
				});
			});
			theE.undelegate('.editor > span[save]','click');
			theE.delegate('.editor > span[save]','click',function(e){
				var filename=theE.find('#jarfile').val();
				if(filename==''){
					alert('未上传程序文件');
					return;
				}
				var val=theE.find('.editor>textarea').val();
				if(val==''){
					alert('必须说明此版本发布的功能和问题');
					return;
				}
				$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.versions',fn:theE.find('div.editor').attr('fn'),rfn:theE.find('div.editor').attr('rfn'),value:val},function(data){
					var url='./swssite/develop/getProductMenu.html?menu=versions&pid='+pid;
					$.get(url,{},function(html){
						$('.o-product > .main > .content').html(html);
					}).error(function(e){
						alert(e.responseText);
					});
				}).error(function(e){
					alert(e.responseText);
				});
			});
			break;
		case 'license':
			var enname=$(".o-product > .title > .logo > p[editable='product.enname']").html();
			if( $(".o-product > .title > .logo > p[editable='product.enname']").attr('has')!='true'){
				alert('先指定产品英文名');
				return;
			}
			theE.find('div.editor').remove();
			var cnt=$('.o-product > .main > .content > pre').html();
			theE.prepend("<div class='editor' style='left:10px;bottom:20px;top:10px;right:10px;'><p><span>声明</span></p><textarea content type='text'  >"+cnt+"</textarea><span close>关闭</span>" +
					"<span save>保存</span>" +
					"</div>");
			var pid=$('.o-product').attr('pid');
			theE.undelegate('.editor > span[save]','click');
			theE.delegate('.editor > span[save]','click',function(e){
				var val=theE.find('.editor>textarea').val();
				if(val==''){
					alert('内容为空');
					return;
				}
				var pid=$('.o-product').attr('pid');
				$.post('./swssite/develop/editProduct.service',{pid:pid,editor:'product.license',value:val},function(data){
					theE.find('.editor').remove();
					var url='./swssite/develop/getProductMenu.html?menu=license&pid='+pid;
					$.get(url,{},function(html){
						$('.o-product > .main > .content').html(html);
					}).error(function(e){
						alert(e.responseText);
					});
				}).error(function(e){
					alert(e.responseText);
				});
			});
			break;
		}
	});
	
	//来访
	var plist=$('.o-product');
	plist.undelegate('> .title > .show > .box > li[visitor] > span','mouseenter');
	plist.delegate('> .title > .show > .box > li[visitor] > span','mouseenter',function(e){
		var the=$(this).parent('li[visitor]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.o-product');
		var sourceid=art.attr('pid');
		var entity='myproduct';
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
			the.find('div.e-r-v').attr('style','right:0;left:auto;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('> .title > .show > .box > li[visitor]','mouseleave');
	plist.delegate('> .title > .show > .box > li[visitor]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.o-product');
		var sourceid=art.attr('pid');
		var entity='myproduct';
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
	
	plist.undelegate('.e-r-v>.events>li[user]','click');
	plist.delegate('.e-r-v>.events>li[user]','click',function(e){
		var user=$(this).attr('user');
		var box=$(this);
		box.parent('.events').find('.user-face').remove();
		box.append("<div class='user-face' style='right:0;left:auto;'></div>");
		$.get('./servicews/viewUserProfile.service',{user:user},function(data){
			box.find('>.user-face').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('.e-r-v>.events>li[user]','mouseleave');
	plist.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
	
	//下载
	plist.undelegate('> .title > .show > .box > li[download] > span','mouseenter');
	plist.delegate('> .title > .show > .box > li[download] > span','mouseenter',function(e){
		var the=$(this).parent('li[download]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.o-product');
		var sourceid=art.attr('pid');
		var release=$(this).parents('.show');
		var releaseid=release.attr('releaseid');
		var entity='myproduct';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			releaseid:releaseid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
			the.find('div.e-r-v').attr('style','right:0;left:auto;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('> .title > .show > .box > li[download]','mouseleave');
	plist.delegate('> .title > .show > .box > li[download]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.o-product');
		var sourceid=art.attr('pid');
		var release=$(this).parents('.show');
		var releaseid=release.attr('releaseid');
		var entity='myproduct';
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			releaseid:releaseid,
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
	
	plist.undelegate('.e-r-v>.events>li[user]','click');
	plist.delegate('.e-r-v>.events>li[user]','click',function(e){
		var user=$(this).attr('user');
		var box=$(this);
		box.parent('.events').find('.user-face').remove();
		box.append("<div class='user-face' style='right:0;left:auto;'></div>");
		$.get('./servicews/viewUserProfile.service',{user:user},function(data){
			box.find('>.user-face').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('.e-r-v>.events>li[user]','mouseleave');
	plist.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();