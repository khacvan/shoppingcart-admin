//khi mà cái trang được load lên thì nó sẽ chạy vào ready
//khai báo các event() của thẻ html trong đây
$(document).ready(function(){
    $("#logoutLink").on("click", function(e) {
        e.preventDefault(); // chặn cái sự kiện, cụ thể là gọi xuống controller
        document.logoutForm.submit();
    });

    customizeDropDownMenu()
});

function customizeDropDownMenu() {
    $(".navbar .dropdown").hover(
        function() {
            $(this).find('.dropdown-menu').first().stop(true,true).delay(250).slideDown();

        },
        function(){
            $(this).find('.dropdown-menu').first().stop(true,true).delay(100).slideUp();
        }
    );

    $(".dropdown > a").click(function() {
        location.href = this.href;
    })
}