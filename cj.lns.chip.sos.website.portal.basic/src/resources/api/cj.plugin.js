$.fn.extend({
    /**
     *为插件自定义事件
     * 用法：
     * 一、插件事件定义方法
     * 1.在插件内要产生具体事件的元素上加cjevent属性
     * 2.然后调用该方法将插件内具有cjevent的元素的某个事件绑定到自定义事件上（即参数2)
     * 二、插件事件调用方法
     * 插件.on(customEvent,function(){})
     *
     * @param {Object} eEventName
     * @param {Object} customEventName 格式：actionName(arg1,arg2,...)
     * @param {element} childselect 要在其内查找的选择器
     * @param {boolean} the 是否使用子选择器定位的元素自身,即cjevent声明是否在其自身上。
     *
     在自定义事件中第0个参数是调用该方法的元素，第一个参数是当前被触发的插件内的元素的事件参数，
     第二个参数是当前被触发的插件内的元素
     第三个参数是actionName,第四个参数是arg1,arg2,...分隔的字符串，需要自行split
     */
    cjEventBind : function(eEventName, customEventName, childselect, the) {
        var reg = /^\s*(\w+)\(\s*(.*)\s*\)\s*$/;
        var d = $(this);
        var range = d;
        if (childselect != 'undefined') {
            range = d.find(childselect);
        }
        if ( typeof the !== 'undefined') {
            range.on(eEventName, function(event) {
                var action = $(this).attr('cjevent');
                var arr = reg.exec(action);
                var args = [event, $(this), arr[1], arr[2]];
                d.trigger(customEventName, args);
                //触发插件上已在on中绑定的自定义事件。
            });
        } else {
            range.delegate("[cjevent]", eEventName, function(event) {
                var action = $(this).attr('cjevent');
                var arr = reg.exec(action);
                var args = [event, $(this), arr[1], arr[2]];
                d.trigger(customEventName, args);
                //触发插件上已在on中绑定的自定义事件。
            });
        }
    },
    showStartMenuBackButton : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('>ul>li.third .left');
        the.cjEventBind('click', 'doBack_button', '> ul > li.third .left', true);
        the.on("doBack_button", function(e0, e1, e2, e3, e4) {
            e.css("display", "none");
            the.trigger("doBack", e4);
        });
        e.show();
    },
    showCurrentAppButton : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('.fourth .left');
        the.cjEventBind('click', 'refreshCurrentApp_button', '.fourth .left');
        the.on("refreshCurrentApp_button", function(e0, e1, e2, e3, e4) {
            the.trigger("refreshCurrentApp", e4);
        });
        e.show();
    },
    hideCurrentAppButton : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('.fourth .left');
        e.hide();
    },
    showCurrentMsgTips : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('.third  .msg-tips');
        e.find('a').text(opt);
        the.cjEventBind('click', 'refreshCurrentMsg_button', '.third  .msg-tips', true);
        the.on("refreshCurrentMsg_button", function(e0, e1, e2, e3, e4) {
            e.css('display', 'none');
            the.trigger("refreshCurrentMsgTips", e4);
        });
        e.show();
    },
    enableCyberport : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('.third .right .circle-box');
        e.css('background-color', 'rgba(251, 0, 6, 0.8)');
        e.find('span').text(opt);
        the.trigger("enableCyberport");
        e.show();
    },
     disableCyberport : function(opt) {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        var e = the.find('.third .right .circle-box');
        e.css('background-color', 'rgba(255, 255, 255, 0.8)');
        e.find('span').hide();
        the.trigger("disableCyberport");
        e.show();
    },
    /**
     * 支持的事件：
     * openShortcutPopup,openMySwsPopup,openMyHostPopup,openCyborportPopup
     * showBackButton,doBack
     * showCurrentAppButton,refreshCurrentApp
     * showCurrentMsgTips,refreshCurrentMsgTips
     * enableCyberport,disableCyberport
     * logout
     */
    initStartMenu : function() {
        var the = $(this);
        if (!the.is('.startMenu')) {
            alert('不能在元素' + the.attr('class') + '上调用startMenu');
        }
        the.cjEventBind('mouseenter mouseleave', 'popupIsland', '> ul > li.third .right', true);
        the.on("popupIsland", function(e0, e1, e2, e3, e4) {
            if (e3 !== 'click') {
                return;
            }
            if (e1.type == "mouseenter") {
                //鼠标悬浮
                $(e2).find(".popup-panel").css("display", "block");
                the.trigger("openCyborportPanel", e4);
            } else if (e1.type == "mouseleave") {
                //鼠标离开
                $(e2).find(".popup-panel").css("display", "none");
            }
            
//            var popup = $(this).find("> ul > li.third .right .popup-panel");
//            popup.hover(function() {
//            }, function() {
//                popup.css("display", "none");
//            });
//            $(this).find("> ul > li.third .right").hover(function() {
//
//            }, function() {
//                //鼠标离开
//                popup.css("display", "none");
//            });
//            the.trigger("openCyborportPopup", e4);
//            popup.css("display", "block");

        });
        the.cjEventBind('click', 'logout', '.second>.box>.right');

       // the.cjEventBind('mouseenter mouseleave', 'popupPanel_shortcut', '.fourth .right .app-box');
        $('.fourth>.right>.app-box>li[cjevent]').hover(function(e){
        	var popup= $(this).find(".popup-panel");
        	//添加entered属性是为了避免工具之中包含的元素在更新时毫无道理地响应进入事件问题，该问题会导致多次调用。
        	if('true'==popup.attr('entered')){
        		return ;
        	}
        	popup.css("display", "block");
        	  $.ajax({
                  type : 'post',
                  url : popup.attr('src'),
                  success : function(data) {
                  	popup.html(data);
                  	popup.attr('entered','true');
                  },
                  error : function(e1) {
                      alert(e1.responseText);
                  }
              });
        },function(e){
        	$(this).find(".popup-panel").css("display", "none");
        	var popup= $(this).find(".popup-panel");
        	popup.attr('entered','false');
        });
       
        the.cjEventBind('mouseenter mouseleave', 'popupPanel_mysws', '>ul>li.first', true);
        the.on("popupPanel_mysws", function(e0, e1, e2, e3, e4) {
            if (e1.type == "mouseenter") {
                //鼠标悬浮
                $(e2).find(".popup-panel").css("display", "block");
                the.trigger("openSwsPanel", e4);
            } else if (e1.type == "mouseleave") {
                //鼠标离开
                $(e2).find(".popup-panel").css("display", "none");
            }
        });
    }
});
