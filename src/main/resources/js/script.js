/* Copier le contenu d'un textarea dans le clipboard */
async function copyToClipboard(idTextArea) {
    var textArea = document.getElementById("form:" + idTextArea);
    if(textArea) {
        try {
            await navigator.clipboard.writeText(textArea.value);
            alert("Contenu copié dans le presse-papier !");
        } catch (err) {
            console.error("Erreur lors de la copie : ", err);
            alert("Erreur lors de la copie");
        }
    }
}

/* Effacer la dernière question et la dernière réponse */
function toutEffacer() {
    document.getElementById("form:question").value = "";
    document.getElementById("form:reponse").value = "";
}