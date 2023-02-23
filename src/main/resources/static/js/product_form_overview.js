dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function(){
    $("#shortDescription").richText();
    $("#fullDesciption").richText();

    dropdownBrands.change(function(){
        dropdownCategories.empty();
        getCategories();
    })

    getCategoriesForNewForm();
})

function getCategoriesForNewForm(){
    catIdField = $("#categories");
    editMode = false;

    if(catIdField.length){
        editMode = true;
    }

    if(!editMode) getCategories();
}

function getCategories(){
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function(responseJson){
        $.each(responseJson, function(index, category){
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
        })
    })
}
