
    document.getElementById("signInLink").onclick = function () {

    var dropdown = document.getElementById("dropdownMenu");
    dropdown.style.display = (dropdown.style.display === "block") ? "none" : "block";

}

    // Close dropdown if clicked outside
    window.onclick = function (event) {


    dropdown.style.display = "none";

}


