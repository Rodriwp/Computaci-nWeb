$(document).ready(function() {
    console.log("ready!");
    $("input[name='movie'][value='2']").prop("checked", true);


});
function displaySelection() {
  console.log($("input[name='movie']:checked").val());
};
