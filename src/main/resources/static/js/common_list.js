function showDeleteConfirmModal(link, entityName) {

    entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));

    $("#confirmText").text("Detele this " + entityName + " ID "+entityId +'?')
    $("#confirmModal").modal();


    function clearFilter()
    {
        window.location = moduleURL;
    }

}

