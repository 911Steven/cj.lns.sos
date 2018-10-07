(function(){
	var panel=$('.ser-panel');
	
	panel.find(' > .runtimeEnv > .info > .panel>li').on('click',function(e){
		var the=$(this);
		the.parent('.panel').children('li').removeClass('selected');
		the.addClass('selected');
		if(typeof the.attr('mypub')!='undefined'){
			$.get('./cyberportApp/common/cscMyPublishChip.html',{},function(data){
				panel.find('>.pc-content').html(data);
			}).error(function(e){
				alert(e.responseText);
			});
			return;
		}
		if(typeof the.attr('myinstall')!='undefined'){
			$.get('./cyberportApp/common/cscMyInstallChip.html',{},function(data){
				panel.find('>.pc-content').html(data);
			}).error(function(e){
				alert(e.responseText);
			});
			return;
		}
	});
	panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
	
	panel.find(' > .runtimeEnv > .info > .op > li').on('click',function(e){
		var the=$(this);
		var status=panel.find('> .runtimeEnv > .face > p[status]>span[value]');
		if(typeof the.attr('start')!='undefined'){
			if(the.attr('working')=='true'){
				return;
			}
			status.html('正在开机...');
			the.attr('working','true');
			$.post('./cyberportApp/common/cscRunComputer.service',{
				
			},function(data){
				status.html('已开机');
				the.attr('working','false');
			}).error(function(e){
				the.attr('working','false');
				status.html('开机失败');
				alert(e.responseText);
			});
			return;
		}
		if(typeof the.attr('restart')!='undefined'){
			if(the.attr('working')=='true'){
				return;
			}
			status.html('正在重启...');
			the.attr('working','true');
			$.post('./cyberportApp/common/cscRestartComputer.service',{
				
			},function(data){
				status.html('已开机');
				the.attr('working','false');
			}).error(function(e){
				status.html('重启失败');
				alert(e.responseText);
				the.attr('working','false');
			});
			return;
		}
		if(typeof the.attr('shutdown')!='undefined'){
			if(the.attr('working')=='true'){
				return;
			}
			status.html('正在关机...');
			the.attr('working','true');
			$.post('./cyberportApp/common/cscShutdownComputer.service',{
				
			},function(data){
				status.html('已关机');
				the.attr('working','false');
			}).error(function(e){
				status.html('关机失败');
				alert(e.responseText);
				the.attr('working','false');
			});
			return;
		}
		if(typeof the.attr('destroy')!='undefined'){
			if(the.attr('working')=='true'){
				return;
			}
			if(!confirm("销毁计算机将不可恢复，是否继续？")){
			   return;
			}
			status.html('正在销毁...');
			the.attr('working','true');
			$.post('./cyberportApp/common/cscDestroyComputer.service',{
				
			},function(data){
				status.html('已销毁');
				the.attr('working','false');
			}).error(function(e){
				status.html('销毁失败');
				alert(e.responseText);
				the.attr('working','false');
			});
			return;
		}
	});
	
	function loadCscServiceInfo(atid,tab){
		$.get('./cyberportApp/common/cscMenuAndToolsInfo.html',{atid:atid,tab:tab},function(data){
			var e=panel.find('.scsServiceInfo');
			e.html(data);
			e.show();
		}).error(function(e){
			alert(e.responseText);
		});
	}
	
	panel.undelegate('.inst-panel>.service>ul.business span[m]','click');
	panel.delegate('.inst-panel>.service>ul.business span[m]','click',function(e){
		//alert('---item');
		var atid=$(this).parent('li').attr('atid');
		loadCscServiceInfo(atid,'menu');
	});
	panel.undelegate('.inst-panel > .service > ul.message > li > img,.inst-panel > .service> ul.message span[m]','click');
	panel.delegate('.inst-panel > .service > ul.message > li > img,.inst-panel > .service> ul.message span[m]','click',function(e){
		//alert('---item');
		var atid=$(this).parent('li').attr('atid');
		loadCscServiceInfo(atid,'tool');
	});
	panel.undelegate('.csc-s-info>.bar>span[close]','click');
	panel.delegate('.csc-s-info>.bar>span[close]','click',function(e){
		var ctr=$(this).parents('.csc-s-info');
		ctr.empty();
		ctr.hide();
	});
	panel.undelegate('.mypub > .items > li','mouseenter mouseleave click');
	panel.delegate(' .mypub > .items > li','mouseenter mouseleave click',function(e){
		if(e.type=='mouseenter'){
			$(this).find(">span[del]").show();
		}else if(e.type=='mouseleave'){
			$(this).find(">span[del]").hide();
		}
	});
	panel.undelegate('.mypub > .items > li>span[del]','click');
	panel.delegate(' .mypub > .items > li>span[del]','click',function(e){
		if(!confirm('是否下架芯片?')){
			return;
		}
		var li=$(this).parent('li[docid]');
		var docid=li.attr('docid');
		$.get('./cyberportApp/common/removeChipFromMarket.service',{docid:docid},function(data){
			li.remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	panel.undelegate('.mypub > .op > .buttons > li[pub]','click');
	panel.delegate('.mypub > .op > .buttons > li[pub]','click',function(e){
		panel.find('.pub').show();
	});
	panel.undelegate('.mypub>.pub > span[close]','click');
	panel.delegate('.mypub>.pub > span[close]','click',function(e){
		panel.find('.pub').hide();
	});
	panel.undelegate('.mypub > .pub>.upload>input','change');
	panel.delegate('.mypub > .pub>.upload>input','change',function(e){
		var url = './cyberportApp/common/cscUploadChipToMarket.service';
		var path=panel.find('.mypub > .pub > .path > select').val();
		$.ajaxFileUpload({
			url : url, // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'uploadToMarket',
			dataType : 'json',// 返回值类型 一般设置为json
			data : {path:path},
			success : function(data, status) {
				panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
			},
			error : function(e) {
				alert(e.responseText);
			}
		});
	});
	panel.undelegate('.mypub > .items > li > img,.mypub > .items > li > .face > li[a] > span[label]','click');
	panel.delegate('.mypub > .items > li > img,.mypub > .items > li > .face > li[a] > span[label]','click',function(e){
		var ci=$(this).parents('li').find('>.ci');
		ci.toggle();
	});
	
	panel.undelegate('.inst-panel > .service > ul, .inst-panel > .service > ul>li>ul','mouseenter mouseleave');
	panel.delegate('.inst-panel > .service > ul,.inst-panel > .service > ul>li>ul ','mouseenter mouseleave',function(e){
		$(this).find('>li.op>span[add]').toggle();
	});
	panel.undelegate('.inst-panel > .service > ul>li, .inst-panel > .service > ul>li>ul>li','mouseenter mouseleave');
	panel.delegate('.inst-panel > .service > ul>li,.inst-panel > .service > ul>li>ul>li ','mouseenter mouseleave',function(e){
		$(this).find('>span[del],>span[up],>span[down]').toggle();
	});
	panel.undelegate('.inst-panel > .install > .items > li > img, .inst-panel > .install > .items > li > .face > li[a] > span','click');
	panel.delegate('.inst-panel > .install > .items > li > img, .inst-panel > .install > .items > li > .face > li[a] > span','click',function(e){
		$(this).parents('li.i').find('>.ci').toggle();
	});
	panel.undelegate('.inst-panel > .install > .op > span[show]','click');
	panel.delegate('.inst-panel > .install > .op > span[show]','click',function(e){
		$(this).parents('.op').siblings('.items').toggle();
	});
	panel.undelegate('.inst-panel > .install > .items > li.i','mouseenter mouseleave');
	panel.delegate('.inst-panel > .install > .items > li.i','mouseenter mouseleave',function(e){
		$(this).find('>span[del]').toggle();
	});
	panel.undelegate('.inst-panel > .service>.business li > span[up], .inst-panel > .service>.business li > span[down], .inst-panel > .service>.business li > span[del]','click');
	panel.delegate('.inst-panel > .service>.business li > span[up], .inst-panel > .service>.business li > span[down], .inst-panel > .service>.business li > span[del]','click',function(e){
		
		if(typeof $(this).attr('del')!='undefined'){
			var atid=$(this).parent('li').attr('atid');
			$.get('./cyberportApp/common/cscDelMenu.service',{atid:atid},function(data){
				panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
			}).error(function(e){
				alert(e.responseText);
			});
			
		}else if(typeof $(this).attr('up')!='undefined'){
			var atid=$(this).parent('li').attr('atid');
			$.get('./cyberportApp/common/cscSortMenu.service',{atid:atid,sort:'up'},function(data){
				panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
			}).error(function(e){
				alert(e.responseText);
			});
		}else if(typeof $(this).attr('down')!='undefined'){
			var atid=$(this).parent('li').attr('atid');
			$.get('./cyberportApp/common/cscSortMenu.service',{atid:atid,sort:'down'},function(data){
				panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
			}).error(function(e){
				alert(e.responseText);
			});
		}
		
	});
	panel.undelegate('.inst-panel > .service>.message li > span[del]','click');
	panel.delegate('.inst-panel > .service>.message li > span[del]','click',function(e){
		
		if(typeof $(this).attr('del')!='undefined'){
			var atid=$(this).parent('li').attr('atid');
			$.get('./cyberportApp/common/cscDelTool.service',{atid:atid},function(data){
				panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
			}).error(function(e){
				alert(e.responseText);
			});
			
		}
	});
	panel.undelegate('.inst-panel > .service>.business li.op > span[add]','click');
	panel.delegate('.inst-panel > .service>.business li.op > span[add]','click',function(e){
		//alert('---add');//新增后刷新一次
		var li=$(this).parent('li');
		li.children('input').remove();
		if(li.attr('y')!='yes'){
			li.append("<input title='回车提交'>");
			li.attr('y','yes');
			var input=li.find('>input');
			var atidE=$(this).parents('.s[atid]');
			var atid='-1';//表示根菜单，对于tool无意义
			if(atidE.length>0){
				atid=atidE.attr('atid');
			}
			input.on('keyup',function(e){
				if(e.keyCode=='13'){
					var v=input.val();
					v=v.replace(/[\r\n]/g,"");
					$.post('./cyberportApp/common/cscAddMenu.service',{isTool:'false',parent:atid,name:v},function(data){
						input.remove();
						var obj=$.parseJSON(data);
						panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
					}).error(function(e){
						alert(e.responseText);
					});
				}
			});
		}else{
			li.removeAttr('y');
		}
		
	});
	
	panel.undelegate('.inst-panel > .service>.message li.op > span[add]','click');
	panel.delegate('.inst-panel > .service>.message li.op > span[add]','click',function(e){
		//alert('---add');//新增后刷新一次
		var li=$(this).parent('li');
		li.children('input').remove();
		if(li.attr('y')!='yes'){
			li.append("<input type='text' title='回车提交'>");
			li.append("<input id='tool_icon' name='tool_icon' style='display:block;margin-left:26px;margin-top:10px;' type='file' accept='.svg,.png,.gif,.jpg' title='上传工具图片'>");
			li.attr('y','yes');
			var input=li.find('>input[type=text]');
			input.on('keyup',function(e){
				if(e.keyCode=='13'){
					var v=input.val();
					v=v.replace(/[\r\n]/g,"");
					$.post('./cyberportApp/common/cscAddTool.service',{name:v},function(data){
						input.remove();
						var obj=$.parseJSON(data);
						li.attr('atid',obj.atid);
					}).error(function(e){
						alert(e.responseText);
					});
				}
			});
			var inputfile=li.find('>input[type=file]#tool_icon');
			inputfile.on('change',function(e){
				if(typeof li.attr('atid')=='undefined'){
					alert('请先定义服务工具的名字');
					return;
				}
				var atid=li.attr('atid');
				var url='./cyberportApp/common/cscSetToolIcon.service';
				$.ajaxFileUpload({
					url : url, // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : 'tool_icon',
					dataType : 'json',// 返回值类型 一般设置为json
					data : {atid:atid},
					success : function(data, status) {
						panel.find('> .runtimeEnv > .info > .panel > li.selected').trigger('click');
					},
					error : function(e) {
						alert(e.responseText);
					}
				});
			});
		}else{
			li.removeAttr('y');
		}
	});
	
	panel.undelegate('.pb-panel > .mypub > .items > li > .ci > .op>li[status=install]','click');
	panel.delegate('.pb-panel > .mypub > .items > li > .ci > .op>li[status=install]','click',function(e){
		var marketid=$(this).parents('li[docid]').attr('docid');
		var the=$(this);
		if(the.attr('installed')=='yes'){
			return;
		}
		$.get('./cyberportApp/common/cscInstallChip.service',{marketid:marketid},function(data){
			the.attr('installed','yes');
			the.html('安装成功');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	panel.undelegate('.inst-panel > .install > .items > li > .ci > .op > li','click');
	panel.delegate('.inst-panel > .install > .items > li > .ci > .op > li','click',function(e){
		if($(this).attr('status')=='uninstall'){
			var the=$(this);
			var marketid=$(this).parents('li.i[marketid]').attr('marketid');
			$.get('./cyberportApp/common/cscStopChip.service',{marketid:marketid,neuronFlush:'true'},function(data){
				the.parents('li.i').find('>.face > li[s] > span[status]').html('已停止');
				$.get('./cyberportApp/common/cscUninstallChip.service',{marketid:marketid},function(data){
					the.parents('li.i').remove();
				}).error(function(e){
					alert(e.responseText);
				});
			}).error(function(e){
				alert(e.responseText);
			});
			
		}else if($(this).attr('status')=='run'){
			var the=$(this);
			var marketid=$(this).parents('li.i[marketid]').attr('marketid');
			$.get('./cyberportApp/common/cscRunChip.service',{marketid:marketid},function(data){
				the.parents('li.i').find('>.face > li[s] > span[status]').html('已运行');
			}).error(function(e){
				alert(e.responseText);
			});
		}else if($(this).attr('status')=='stop'){
			var the=$(this);
			var marketid=$(this).parents('li.i[marketid]').attr('marketid');
			$.get('./cyberportApp/common/cscStopChip.service',{marketid:marketid},function(data){
				the.parents('li.i').find('>.face > li[s] > span[status]').html('已停止');
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	
	panel.undelegate('.inst-panel > .install > .items > li > .ci > ul.view > li[contains] > span[get]','click');
	panel.delegate('.inst-panel > .install > .items > li > .ci > ul.view > li[contains] > span[get]','click',function(e){
		var the=$(this);
		var marketid=$(this).parents('li.i[marketid]').attr('marketid');
		var ul=the.parents('li[contains]').find('> ul').first();
		var cli=ul.find('>li').first().clone();
		ul.empty();
		$.get('./cyberportApp/common/cscChipPartLookup.service',{marketid:marketid},function(data){
			var list=$.parseJSON(data);
			for(var i=0;i<list.length;i++){
				var csc=list[i];
				var li=cli.clone();
				li.attr('key',csc.key);
				li.find('>span[label]').html(csc.title);
				var type='';
				switch(csc.type){
				case 'tool':
					type='工具';
					break;
				case 'bflow':
					type='业务流';
					break;
				case 'site':
					type='web3.0网站';
					break;
				}
				li.find('>ul[cinfo]>li[type]>span[value]').html(type);
				li.find('>ul[cinfo]>li[usage]>span[value]').html(csc.usage);
				ul.append(li);
			}
			ul.toggle();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var selectServiceLi=null;
	panel.undelegate('.csc-s-info > .body > ul > li > select','change');
	panel.delegate('.csc-s-info > .body > ul > li > select','change',function(e){
		var the=$(this);
		var showtype=the.find('option:selected').attr('listtype');
		var marketid=the.val();
		if(marketid=='-----'){
			return;
		}
		var service=the.parents('li[chip]').siblings('li[service]');
		var ul=service.find('> ul').first();
		if(selectServiceLi==null){
			selectServiceLi=ul.find('>li').first().clone();
		}
		ul.empty();
		$.get('./cyberportApp/common/cscChipPartLookup.service',{marketid:marketid},function(data){
			var list=$.parseJSON(data);
			for(var i=0;i<list.length;i++){
				var csc=list[i];
				if(showtype=='tool'&&csc.type!='tool'){
					continue;
				}
				if(showtype=='menu'&&csc.type=='tool'){
					continue;
				}
				var li=selectServiceLi.clone();
				li.attr('csckey',csc.key);
				li.attr('csctype',csc.type);
				li.attr('csctitle',csc.title);
				li.find('>span[label]').html(csc.title);
				ul.append(li);
				service.show();
			}
		}).error(function(e){
			alert(e.responseText);
		});
	});
	panel.undelegate('.csc-s-info > .body > ul > li[service] > ul > li>input','click');
	panel.delegate('.csc-s-info > .body > ul > li[service] > ul > li>input','click',function(e){
		var select=panel.find('.csc-s-info > .body > ul > li > select>option:selected');
		var marketid=select.attr('value');
		var tabtype=select.attr('listtype');//当前选项卡，是工具还是菜单
		var csckey=$(this).parent('li').attr('csckey');
		var csctype=$(this).parent('li').attr('csctype');
		var csctitle=$(this).parent('li').attr('csctitle');
		var atid=$(this).parents('.csc-s-info').attr('atid');
		var chip=select.html();
		$.get('./cyberportApp/common/cscMenuAndToolBinder.service',{atid:atid,marketid:marketid,chipname:chip,tabtype:tabtype,csckey:csckey,csctype:csctype,csctitle:csctitle},function(data){
			$('.csc-s-info > .header > .pad > li[info] > span[chip]').html(chip);
			$('.csc-s-info > .header > .pad > li[info] > span[service]').html(csctitle);
			var type='';
			switch(csctype){
			case 'tool':
				type='工具';
				break;
			case 'bflow':
				type='业务流';
				break;
			case 'site':
				type='web3.0网站';
				break;
			}
			$('.csc-s-info > .header > .pad > li[info] > span[csctype]').html('('+type+')');
			$('.csc-s-info > .header > .pad > li[info] > span[csctype]').show();
			var unbind=$('.csc-s-info > .header > p[unbind]');
			unbind.show();
			var cscinfo=$('.csc-s-info');
			cscinfo.attr('bind-marketid',marketid);
			cscinfo.attr('bind-csckey',csckey);
			cscinfo.attr('bind-csctype',csctype);
			$('.inst-panel > .service > ul li[atid='+atid+']>span[m]').attr('style','color: black;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	panel.undelegate('.csc-s-info > .header > p[unbind]','click');
	panel.delegate('.csc-s-info > .header > p[unbind]','click',function(e){
		var atid=$(this).parents('.csc-s-info').attr('atid');
		var tab=$(this).parents('.csc-s-info').attr('tab');
		var the=$(this);
		$.get('./cyberportApp/common/cscMenuAndToolUnbinder.service',{atid:atid,tab:tab},function(data){
			the.hide();
			$('.csc-s-info > .header > .pad > li[info] > span[chip]').html('-');
			$('.csc-s-info > .header > .pad > li[info] > span[service]').html('-');
			$('.csc-s-info > .header > .pad > li[info] > span[csctype]').hide();
			$('.inst-panel > .service > ul li[atid='+atid+']>span[m]').attr('style','color: gray;');
		});
	});
})();