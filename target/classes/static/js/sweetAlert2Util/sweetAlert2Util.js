const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000
});
/**
* ico: success info error warning
*/
function alert2(ico,title){
    Toast.fire({
        icon: ico,
        title: title
    })
}

const Toast2 = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: true,//显示确定按钮
    confirmButtonText: '确定',//确定按钮文本
    showCancelButton: true,//显示取消按钮
    cancelButtonText: '取消'//取消按钮文本
});
/**
* ico: success info error warning
*/
function alert3(ico,title){
    return Toast2.fire({
        icon: ico,
        title: title
    })
}