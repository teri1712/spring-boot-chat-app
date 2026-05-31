function openPreviewToSubmit(event) {
    document.getElementById("preview-section").style.visibility = "visible";
    const file = event.target.files[0];
    const imagePreview = document.getElementById("image-preview");
    imagePreview.src = URL.createObjectURL(file);

}

function closePreview(event) {
    event.stopPropagation();
    const spinner = document.getElementById("submit-spinner");
    if (spinner.classList.contains("d-none")) {
        document.getElementById("preview-section").style.visibility = "hidden";
        document.getElementById("file-input").value = "";
    }
}

function submitWithAvatar() {
    document.getElementById("avatar-submit").disabled = true;
    const spinner = document.getElementById("submit-spinner");
    spinner.classList.remove("d-none");
    document.getElementById("update-button").click();
}
