let pageSize = 10;
let pageNow = 0;

//加载产品
findProduct();

page(0);

function pageUpOrDow(p){
    if(p > 0){
        pageNow = pageNow - 1;
    }else{
        pageNow = pageNow + 1;
    }
    pageNow = pageNow < 0 ? 0 : pageNow;
    page(pageNow);
}

function page(pageNow){
    this.pageNow = pageNow;
    $.ajax({
        url:root+"/api/stock/stock/"+pageSize+"/"+pageNow,
        dataType:"json",
        data:$('#stock-search-form').serialize(),
        type:"POST",
        beforeSend:function(){
            $("#datas").find('tr').remove();
        },
        success:function(req){
            if(req.length <= 0 && pageNow > 0){
                pageUpOrDow(1);
            }else{
                $(req).each(function(i,e){
                    $(e.stockInfo).each(function(i2,e2){
                        var r = ttr((i+i2+1),e2,e.name);
                        $("#datas").append(r);
                    });
                });
            }
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function ttr(i,e,n){
    return "<tr>"+
            "<td>"+i+"</td>"+
            "<td>"+n+"</td>"+
            "<td>"+e.specifications+"</td>"+
            "<td>"+e.stock+"</td>"+
            "<td>"+
//                "<button type=\"button\" class=\"btn btn-danger btn-xs\" onclick=\"remove('"+e.id+"')\">删除</button>&nbsp;&nbsp;"+
                "<button type=\"button\" class=\"btn btn-warning btn-xs\" onclick=\"openEditView('"+e.id+"')\">修改</button>&nbsp;&nbsp;"+
            "</td>"+
            "</tr>";
}
function save(){
    $.ajax({
        url:root+"/api/stock/stock",
        dataType:"text",
        data:$('#add-stock-form').serialize(),
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#add-stock-model-open-button").click();
            $("#add-stock-form")[0].reset();
            page(0);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function openEditView(e){
    $("#id-edit").val(e);
    $("#edit-stock-model-open-button").click();
    $.ajax({
        url:root+"/api/stock/stock/"+e,
        dataType:"json",
        type:"GET",
        beforeSend:function(){
        },
        success:function(req){
            $("#productId-edit").val(req.productId);
            $("#specifications-edit").val(req.specifications);
            $("#stock-edit").val(req.stock);
        },
        complete:function(){
        },
        error:function(e){
            console.log(e);
        }
    });
}
function edit(){
    $.ajax({
        url:root+"/api/stock/stock/"+$("#id-edit").val(),
        dataType:"text",
        data:$('#edit-stock-form').serialize(),
        type:"PUT",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#edit-stock-model-open-button").click();
            $("#edit-stock-form")[0].reset();
            $("#img-edit-img").attr("src","");
            page(pageNow);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function findProduct(){
    $.ajax({
        url:root+"/api/product/product/0/0",
        dataType:"json",
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            $(req).each(function(i,e){
                $(".product").append("<option value=\""+e.id+"\">"+e.name+"</option>");
            });
        },
        complete:function(){
        },
        error:function(e){
        }
    });
}
