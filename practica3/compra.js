$ (function(){

  var referencia;
  for (referencia in accesorios) {
    var accesorio = accesorios[referencia];
    $("#accesorios").append($('<option>').attr('value', referencia).text(accesorio.etiqueta + '('+accesorio.precio.toFixed(2) + '€)'));

     //$("#accesorios").append('<option value="' + accesorio + '">' + accesorio.etiqueta + '('+accesorio.precio.toFixed(2) + '€)</option>');
  }


  $("#boton").click(function(){
    var seleccionado = $("#accesorios").val();
    var cantidad = $("#cantidad").val();
    var accesorio = accesorios[seleccionado];
    precio_total = (accesorios[seleccionado].precio)*cantidad;
    if($("#"+seleccionado).length > 0){
        $("#" + seleccionado +" td.cantidad").text((parseInt($("#" + seleccionado +" td.cantidad").text())+ parseInt(cantidad)));
        $("#" + seleccionado +" td.subtotal").text((parseInt($("#" + seleccionado +" td.cantidad").text())*(accesorios[seleccionado].precio)).toFixed(2)+ '€')
    }else{
        $("#total").before('<tr id='+seleccionado+'><td>' + accesorio.etiqueta + '</td><td class="cantidad">' + cantidad + "</td><td class='subtotal'>" + precio_total.toFixed(2) + '€' + '</td></tr>');
    }
    $("#totalval").text((parseFloat($("#totalval").text()) + precio_total).toFixed(2)) + '€';

    var tr = $("#" + referencia);
    var tdCantidad = tr.find("td:nth-child(2)");
    var tdPrecio = tr.find("td:nth-child(3)");




  });


});
