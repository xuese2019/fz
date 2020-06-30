//默认首页
toHtml('/home/index');
function toHtml(obj){
    $('#contents').load(root+obj);
}

//注销
function logout(){
    localStorage.clear();
    location.replace(root+"/");
}