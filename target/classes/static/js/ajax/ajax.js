$(document).ajaxSend(function(event, jqxhr, settings) {
  jqxhr.setRequestHeader('auth', localStorage.getItem("auth"))
})
$(document).ajaxError(function(event,xhr,options,exc){
    if(xhr.status == 'undefined'){
        return;
    }
    switch(xhr.status){
        case 500:
            if(xhr.responseText === "logout"){
                alert("登录已过期");
                location.replace(root+"/");
                return;
            }
        case 403:
            // 未授权异常
            alert("系统拒绝：您没有访问权限。");
            break;
        case 404:
            alert("您访问的资源不存在。");
            break;
    }
});