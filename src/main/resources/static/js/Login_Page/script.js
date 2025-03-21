document.getElementById("Login_Button").addEventListener("click", function () {

let user = document.getElementById("Username_Input").value;
let pass = document.getElementById("Password_Input").value;


if(user.trim() == "Usuário" && pass.trim() == "Senha"){
    console.log("Login Efetuado");
}
});
