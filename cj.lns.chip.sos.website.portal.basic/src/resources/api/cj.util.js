if ( typeof $.cj == 'undefined')
    $.cj = {};
if ( typeof $.cj.util == 'undefined')
    $.cj.util = {};
$.parameter = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}
$.cj.util = {
    //滚动条是否到了文档底部
    isScrollDocEnd : function() {
        var totalheight = $(window).height() + $(window).scrollTop();
        return $(document).height() <= totalheight ? true : false;
    },
    onScrollWinBottom:function(){
    	$(window).scroll(function(e){
  		　　var scrollTop = $(this).scrollTop();
  		　　var docHeight = $(document).height();
  		　　var windowHeight = window.document.body.clientHeight;
  		　　if(scrollTop + windowHeight >= docHeight-60){
  		　　　　alert("you are in the bottom");
  		　　}
  		});
    },
    getObjInPart : function(path) {
        //debugger;
        var arr = path.split('.');
        var tmp = $;
        for (var i = 0; i < arr.length; i++) {
            tmp = tmp[arr[i]];
            if (!tmp)
                return;
        }
        return tmp;
    },
    orient : function() {
        if (window.orientation == 90 || window.orientation == -90) {
            //ipad、iphone竖屏；Andriod横
            orientation = 'landscape';
            return false;
        } else if (window.orientation == 0 || window.orientation == 180) {
            //ipad、iphone横屏；Andriod竖
            orientation = 'portrait';
            return false;
        }
    }
};
