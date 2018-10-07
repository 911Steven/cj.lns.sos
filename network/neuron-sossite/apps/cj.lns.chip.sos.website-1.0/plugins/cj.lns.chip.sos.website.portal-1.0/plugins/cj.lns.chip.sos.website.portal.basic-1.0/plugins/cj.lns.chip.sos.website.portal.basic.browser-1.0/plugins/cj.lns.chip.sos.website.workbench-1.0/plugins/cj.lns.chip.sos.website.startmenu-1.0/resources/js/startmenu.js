$(document).ready(function() {
	var menu=$('.startMenu[region]');

	menu.undelegate('>.sm-box','mouseleave');
	menu.delegate('>.sm-box','mouseleave',function(e){
		$(this).find('div.popup-panel').hide();
		return false;
	});
	
	menu.undelegate('>.sm-box a[cmd]','mouseenter');
	menu.delegate('>.sm-box a[cmd]','mouseenter',function(e,arg1){
		menu.find('div.popup-panel').hide();
		var pop=$(this).siblings('div.popup-panel');
		var id=pop.attr('id');
		switch(id){
		case 'cyberport-popup':
			 var circle=$('.startMenu .third > .right .circle-box');
			 if(circle.attr('invalid')=='true'){
				 return ;
			 }
			 $.ajax({
	             type : 'post',
	             url : './cyberportApp/popup/cyberportPopup.html',
	             success : function(data) {
	            	 circle.removeClass('breathe-btn');
	            	 circle.attr('style','background:rgba(255,255,255,0.8);');
	            	 var tips=$('.startMenu .third .msg-tips');
	            	 tips.hide();
	                 pop.html(data);
	                 pop.show();
	             },
	             error : function(e) {
	             	 alert(e.responseText);
	             }
	         });
			break;
		case 'sws-popup':
			 $.ajax({
		            type : 'post',
		            url : './servicews/components/swsPopup.html',
		            success : function(data) {
		                pop.html(data);
		                pop.show();
		                if('posAtOwner'==arg1){
		                	menu.find('.sws-panel>.sws-tabs>div.sws-owner').trigger('click');
		                }
		            },
		            error : function(e) {
		            	 alert(e.responseText);
		            }
		        });
			break;
		default:
			var reg=/toolbar.win.\w+/;
			if(reg.test(id)){
				 $.ajax({
	                  type : 'post',
	                  url : pop.attr('src'),
	                  success : function(data) {
	                	pop.html(data);
	                  	pop.attr('entered','true');
	                  	pop.show();
	                  },
	                  error : function(e1) {
	                      alert(e1.responseText);
	                  }
	              });
				
			}
			break;
		}
		
		return false;
	});
});